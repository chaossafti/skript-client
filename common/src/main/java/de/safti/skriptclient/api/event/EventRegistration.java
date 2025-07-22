package de.safti.skriptclient.api.event;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.event.interfaces.EventContextData;
import de.safti.skriptclient.api.event.interfaces.EventContextFactory;
import de.safti.skriptclient.api.event.interfaces.EventRedirector;
import dev.architectury.event.Event;
import io.github.syst3ms.skriptparser.types.Type;

import java.lang.reflect.Array;

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
     * created from {@link EventWrapperExtension#redirector(Event, Class, int...)} using the arguments in this method.
     *
     * @param event The event.
     * @param listenerClass The listener class
     * @param supportedArguments the supported arguments of the listener class to clone
     * @param <L> The Listener class
     * @return  this
     * @see EventWrapperExtension#redirector(Event, Class, int...)
     */
    public <L> EventRegistration<E> wrappedRedirector(Event<L> event, Class<L> listenerClass, int... supportedArguments) {
        if(!modEventClass.isAssignableFrom(EventWrapperExtension.WrappedEvent.class)) {
            throw new IllegalStateException("EventRegistration is not of event WrappedEvent!");
        }

        // This is a safe cast because of the condition above.
        //noinspection unchecked
        return redirector((EventRedirector<E>) EventWrapperExtension.redirector(event, listenerClass, supportedArguments));
    }

    public void register() {
        if(contextFactory == null || eventRedirector == null)
            throw new IllegalStateException("Both contextFactory and redirector must be defined.");

        // Register event
        SkriptRegistry.registerEvent(() -> new GeneratedEvent(builder.getEventName()), builder.getPattern());

        // Register values
        for (var entry : builder.getNamedValues().entrySet()) {
            String valueName = entry.getKey();
            Type<?> type = entry.getValue();

            registerEventValue(type, valueName);
        }


        eventRedirector.onEvent(event -> {
            EventContextData data = contextFactory.create(event);
            EventContext<E> context = builder.createContext(data, event, eventRedirector);

            SkriptClient.INSTANCE.getEventManager()
                    .callEvent(builder.getEventName(), context);
        });
    }

    private <T1> void registerEventValue(Type<T1> type, String valueName) {
        // TODO: multi-value expressions

        SkriptRegistry.registerEventValue(
                EventContext.class,
                type.getTypeClass(),
                true,
                valueName,
                ctx -> {
                    Class<T1> clazz = type.getTypeClass();
                    T1[] arr = (T1[]) Array.newInstance(clazz, 1); // only of length one because this doesn't support multi-value event values yet
                    T1 value = (T1) ctx.get(valueName);
                    arr[0] = value;
                    return arr;
                });
    }

}

