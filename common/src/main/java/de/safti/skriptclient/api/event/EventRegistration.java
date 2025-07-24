package de.safti.skriptclient.api.event;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.event.interfaces.EventContextData;
import de.safti.skriptclient.api.event.interfaces.EventContextFactory;
import de.safti.skriptclient.api.event.interfaces.EventRedirector;
import de.safti.skriptclient.api.synatxes.generated.GeneratedEvent;
import de.safti.skriptclient.api.synatxes.generated.GeneratedResultingEvent;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.EventResult;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.function.Supplier;

// TODO: register event listener only if a SkriptEvent is registered
public final class EventRegistration<E> {
    private final EventBuilder builder;
    private final Class<E> modEventClass;

    private EventContextFactory<E> contextFactory;
    private EventRedirector<E> eventRedirector;

    public EventRegistration(EventBuilder builder, Class<E> modEventClass) {
        this.builder = builder;
        this.modEventClass = modEventClass;
    }

    public EventRegistration<E> contextValuesFactory(EventContextFactory<E> factory) {
        this.contextFactory = factory;
        return this;
    }

    public EventRegistration<E> redirector(EventRedirector<E> redirector) {
        this.eventRedirector = redirector;
        return this;
    }

    /**
     * Utility method to call {@link #redirector(EventRedirector)} with an {@link EventRedirector}
     * created from {@link EventWrapperExtension#wrap(Event, Class, Class, boolean, int...)} using the arguments in this method.
     * The resultClass argument is replaced with null.
     *
     * @param event The event.
     * @param listenerClass The listener class
     * @param supportedArguments the supported arguments of the listener class to clone
     * @param <L> The Listener class
     * @return  this
     * @see EventWrapperExtension#wrap(Event, Class, Class, boolean, int...)
     */
    public <L> EventRegistration<E> wrappedRedirector(Event<L> event, Class<L> listenerClass, int... supportedArguments) {
        if(!modEventClass.isAssignableFrom(EventWrapperExtension.WrappedEvent.class)) {
            throw new IllegalStateException("EventRegistration is not of event WrappedEvent!");
        }

        // This is a safe cast because of the condition above.
        //noinspection unchecked
        return redirector((EventRedirector<E>) EventWrapperExtension.wrap(event, listenerClass, null, isCancellable(listenerClass), supportedArguments));
    }

    /**
     * Utility method to call {@link #redirector(EventRedirector)} with an {@link EventRedirector}
     * created from {@link EventWrapperExtension#wrap(Event, Class, Class, boolean, int...)} using the arguments in this method.
     *
     * @param event The event.
     * @param listenerClass The listener class
     * @param supportedArguments the supported arguments of the listener class to clone
     * @param <L> The Listener class
     * @return  this
     * @see EventWrapperExtension#wrap(Event, Class, Class, boolean, int...)
     */
    public <L> EventRegistration<E> wrappedRedirector(Event<L> event, Class<L> listenerClass, Class<?> resultClass, int... supportedArguments) {
        if(!modEventClass.isAssignableFrom(EventWrapperExtension.WrappedEvent.class)) {
            throw new IllegalStateException("EventRegistration is not of event WrappedEvent!");
        }

        // This is a safe cast because of the condition above.
        //noinspection unchecked
        return redirector((EventRedirector<E>) EventWrapperExtension.wrap(event, listenerClass, resultClass, isCancellable(listenerClass), supportedArguments));
    }

    private <L> boolean isCancellable(Class<L> listenerClass) {
        Method[] declaredMethods = listenerClass.getDeclaredMethods();
        Method eventMethod;
        // TODO: cleanup. Remove if spam and replace with loop or recursive method.
        outerIf:
        if(declaredMethods.length != 1) {
            // try looking up the super class
            Class<?>[] superInterfaces = listenerClass.getInterfaces();
            if(superInterfaces.length >= 1) {
                declaredMethods = superInterfaces[0].getDeclaredMethods();
                if(declaredMethods.length == 1) {
                    eventMethod = declaredMethods[0];
                    break outerIf;
                }
            }

            throw new IllegalStateException("Listener class " + listenerClass + " has a declared method count unequal to 1! Could not determine a cancellable-state. Amount: " + declaredMethods.length);
        } else eventMethod = declaredMethods[0];

        // this works because architectury events are cancellable by returning an EventResult (simply cancel or go on)
        // OR a CompoundEventResult (cancel and set changed value or go on)
        Class<?> returnType = eventMethod.getReturnType();
        return returnType.equals(EventResult.class) || returnType.equals(CompoundEventResult.class);
    }

    public void register() {
        if(contextFactory == null || eventRedirector == null)
            throw new IllegalStateException("Both contextFactory and redirector must be defined.");


        Supplier<GeneratedEvent> supplier = () ->
                eventRedirector.getResultClass() != null ?
                new GeneratedResultingEvent<>(builder.getEventName(), eventRedirector.getResultClass()) :
                new GeneratedEvent(builder.getEventName(), eventRedirector.isCancellable());

        // Register event
        SkriptRegistry.registerEvent(supplier, builder.getPattern());

        // Register values
        for (var entry : builder.getNamedValues().entrySet()) {
            String valueName = entry.getKey();
            Class<?> type = entry.getValue();

            registerEventValue(type, valueName);
        }


        eventRedirector.onEvent(event -> {
            // whenever our generic event is called, we should redirect it to the SkriptEventManager


            // create the trigger context
            EventContextData data = contextFactory.create(event);
            EventContext<E> context = builder.createContext(data, event, eventRedirector);



            // dispatch the event to the SkriptEventManager
            SkriptClient.INSTANCE.getEventManager()
                    .callEvent(builder.getEventName(), context);

            // the event redirector wants to know the final event result
            if(context instanceof ResultingEventContext<?,?> resultingEventContext) {
                eventRedirector.acceptResult(event, resultingEventContext.getResult());
            }

        });
    }

    @SuppressWarnings("unchecked")
    private <T1> void registerEventValue(Class<T1> c, String valueName) {
        // TODO: multi-value expressions

        SkriptRegistry.registerEventValue(
                EventContext.class,
                c,
                true,
                valueName,
                ctx -> {
                    T1[] arr = (T1[]) Array.newInstance(c, 1); // only of length one because this doesn't support multi-value event values yet
                    T1 value = (T1) ctx.get(valueName);
                    arr[0] = value;
                    return arr;
                });
    }

}

