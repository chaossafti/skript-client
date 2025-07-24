package de.safti.skriptclient.api.event;


import de.safti.skriptclient.api.event.interfaces.EventRedirector;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.EventResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class EventWrapperExtension {

    private static final Logger log = LoggerFactory.getLogger(EventWrapperExtension.class);

    /**
     * @param event         The event to register
     * @param listenerClass the class of the listener
     * @param <L>           The Listener type
     * @return An eventRedirector that will define the consumer consuming the invoked WrappedFabricEvent
     */
    public static <L> WrappedEventRedirector wrap(Event<L> event, Class<L> listenerClass, Class<?> resultClass, boolean isCancelable, int... supportedArguments) {
        // TODO: maybe this this is cleanable by
        //  extracting Consumer<WrappedEvent> into an interface
        //  and passing it as a method argument

        // the EventRedirector will set the event consumer used for this event
        // it's essentially a completable future
        // TODO: get rid of the isCancelable parameter
        //  by checking the return value of the event method using reflection
        AtomicReference<WrappedEventRedirector> eventRedirectorRef = new AtomicReference<>(isCancelable ? new CancellableWrappedEventRedirector(resultClass) : new WrappedEventRedirector(resultClass));

        // the event consumer will in the end invoke SkriptEventManager#call
        // check it out in EventRegistration#register
        AtomicReference<Consumer<WrappedEvent>> eventConsumerRef = eventRedirectorRef.get().getEventConsumerRef();



        // Create a dynamic proxy of listener interface
        // this proxy will listen for when the event is called, wrap all arguments into a WrappedFabricEvent
        // and dispatch the WrappedFabricEvent to the eventConsumer stored in eventConsumerRef
        L listenerProxy = listenerClass.cast(Proxy.newProxyInstance(
                listenerClass.getClassLoader(),
                new Class[]{listenerClass},
                (proxy, method, args) -> {
                    // make sure to not override anything other than what we need to override
                    if(method.getDeclaringClass().equals(Object.class)) return method.invoke(proxy, args);


                    Class<?> returnType = method.getReturnType();
                    boolean hasResult = returnType.equals(CompoundEventResult.class);

                    if(args.length < supportedArguments.length) {
                        log.warn("provided argument length is smaller than amount of supported argument!");
                        log.warn("ListenerClass: {}", listenerClass);
                    }

                    // consume the event with the provided consumer
                    // essentially, this creates a TriggerContext
                    // and calls an event using the SkriptEventManager
                    WrappedEvent wrappedEvent = new WrappedEvent(copySelectedIndices(args, supportedArguments), isCancelable, hasResult, resultClass);
                    eventConsumerRef.get().accept(wrappedEvent);
                    Object resultObject = wrappedEvent.getResultObject();

                    // simple, cancelled events
                    if(returnType.equals(EventResult.class)) return wrappedEvent.result;

                    // advanced canceled events; These support event results
                    if(returnType.equals(CompoundEventResult.class)) {
                        if(resultObject != null) {
                            return CompoundEventResult.interruptTrue(resultObject);
                        }

                        return CompoundEventResult.pass();
                    }

                    // return nothing
                    if(returnType.equals(Void.TYPE) || method.getReturnType().equals(Void.class)) return null;

                    // something invalid
                    throw new IllegalStateException("event Method wants " + returnType + " as return type. Listener: " + listenerClass);
                }
        ));

        // register our proxy listener
        event.register(listenerProxy);

        // return our redirector
        return eventRedirectorRef.get();
    }

    public static Object[] copySelectedIndices(Object[] source, int[] indices) {
        Object[] result = new Object[indices.length];
        for (int i = 0; i < indices.length; i++) {
            result[i] = source[indices[i]];
        }
        return result;
    }


    public static final class WrappedEvent {
        private final Object[] values;
        private final boolean isCancellable;
        private final boolean hasResult;
        private EventResult result = EventResult.pass();

        @Nullable
        private final Class<?> resultClass;
        @Nullable
        private Object resultObject;


        public WrappedEvent(Object[] values, boolean isCancellable, boolean hasResult, @Nullable Class<?> resultClass) {
            this.values = values;
            this.isCancellable = isCancellable;
            this.hasResult = hasResult;
            this.resultClass = resultClass;
        }

        public boolean isCancellable() {
            return isCancellable;
        }

        public Object[] values() {
            return values;
        }

        public void setResult(@Nullable EventResult result) {
            this.result = result;
        }


        public boolean isHasResult() {
            return hasResult;
        }

        @Nullable
        public EventResult getResult() {
            return result;
        }


        public @Nullable Object getResultObject() {
            return resultObject;
        }

        public void setResultObject(@Nullable Object resultObject) {
            if(resultClass == null) {
                throw new IllegalStateException("Tried setting result object, but no result was expected.");
            }

            if(resultObject == null) {
                this.resultObject = null;
                return;
            }

            if(!resultClass.isInstance(resultObject)) {
                throw new IllegalArgumentException("expected result object of type " + resultClass.getCanonicalName() + ", but got " + resultObject.getClass().getCanonicalName());
            }

            this.resultObject = resultObject;
        }
    }

    public static class CancellableWrappedEventRedirector extends WrappedEventRedirector {

        public CancellableWrappedEventRedirector(@Nullable Class<?> resultClass) {
            super(resultClass);
        }

        @Override
        public boolean isCancellable() {
            return true;
        }

        @Override
        public void acceptResult(WrappedEvent event, Object o) {
            event.setResultObject(o);
        }
    }

    public static class WrappedEventRedirector implements EventRedirector<WrappedEvent> {
        private final AtomicReference<Consumer<WrappedEvent>> eventConsumerRef = new AtomicReference<>();
        @Nullable
        private final Class<?> resultClass;

        public WrappedEventRedirector(@Nullable Class<?> resultClass) {
            this.resultClass = resultClass;
        }

        @Override
        public void onEvent(Consumer<WrappedEvent> eventConsumer) {
            eventConsumerRef.set(eventConsumer);
        }

        @Override
        public boolean isCancellable() {
            return false;
        }

        @Override
        public @Nullable Class<?> getResultClass() {
            return resultClass;
        }

        @Override
        public @NotNull BiConsumer<WrappedEvent, Boolean> cancelSetter() {
            return (architecturyEvent, cancelState) -> architecturyEvent.result = cancelState ? EventResult.interruptFalse() : EventResult.pass();
        }

        @Override
        public @NotNull Function<WrappedEvent, Boolean> cancelGetter() {
            return architecturyEvent -> architecturyEvent.result.interruptsFurtherEvaluation();
        }

        public AtomicReference<Consumer<WrappedEvent>> getEventConsumerRef() {
            return eventConsumerRef;
        }
    }


    private EventWrapperExtension() {

    }


}

