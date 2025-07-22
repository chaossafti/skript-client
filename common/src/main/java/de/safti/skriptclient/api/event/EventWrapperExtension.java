package de.safti.skriptclient.api.event;


import de.safti.skriptclient.api.event.interfaces.EventRedirector;
import dev.architectury.event.Event;
import dev.architectury.event.EventResult;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class EventWrapperExtension {

    /**
     * @param event         The event to register
     * @param listenerClass the class of the listener
     * @param <L>           The Listener type
     * @return An eventRedirector that will define the consumer consuming the invoked WrappedFabricEvent
     */
    public static <L> EventRedirector<WrappedEvent> redirector(Event<L> event, Class<L> listenerClass, int... supportedArguments) {
        // the EventRedirector will set the event consumer used for this event
        // it's essentially a completable future
        AtomicReference<WrappedEventRedirector> eventRedirectorRef = new AtomicReference<>(new WrappedEventRedirector());

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

                    // call the event
                    WrappedEvent wrappedEvent = new WrappedEvent(copySelectedIndices(args, supportedArguments));
                    eventConsumerRef.get().accept(wrappedEvent);

                    // return the event result only if the methods signature allows it.
                    return method.getReturnType().equals(EventResult.class) ? wrappedEvent.result : null;
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
        private EventResult result;

        public WrappedEvent(Object[] values) {
            this.values = values;
        }

        public Object[] values() {
            return values;
        }

        public void setResult(EventResult result) {
            this.result = result;
        }

        public EventResult getResult() {
            return result;
        }
    }

    public static final class WrappedEventRedirector implements EventRedirector<WrappedEvent> {
        private final AtomicReference<Consumer<WrappedEvent>> eventConsumerRef = new AtomicReference<>();

        @Override
        public void onEvent(Consumer<WrappedEvent> eventConsumer) {
            eventConsumerRef.set(eventConsumer);
        }

        @Override
        public BiConsumer<WrappedEvent, Boolean> cancelSetter() {
            return (architecturyEvent, cancelState) -> architecturyEvent.result = cancelState ? EventResult.interruptFalse() : EventResult.pass();
        }

        @Override
        public Function<WrappedEvent, Boolean> cancelGetter() {
            return architecturyEvent -> architecturyEvent.result.interruptsFurtherEvaluation();
        }

        public AtomicReference<Consumer<WrappedEvent>> getEventConsumerRef() {
            return eventConsumerRef;
        }
    }


    private EventWrapperExtension() {

    }


}

