package de.safti.skriptclient.api.event;

import de.safti.skriptclient.api.event.EventWrapperExtension.WrappedEvent;
import de.safti.skriptclient.api.event.interfaces.EventContextData;
import de.safti.skriptclient.api.event.interfaces.EventRedirector;

import java.util.*;

public final class EventBuilder {
    private static final Map<String, EventBuilder> ACTIVE_BUILDERS = new HashMap<>();

    private final String eventName;
    private String[] pattern;
    private final LinkedHashMap<String, Class<?>> namedValues = new LinkedHashMap<>();

    private EventBuilder(String eventName) {
        this.eventName = eventName;
    }

    public static EventBuilder create(String eventName) {
        if (ACTIVE_BUILDERS.containsKey(eventName))
            throw new IllegalStateException("Event already defined: " + eventName);
        EventBuilder builder = new EventBuilder(eventName);
        ACTIVE_BUILDERS.put(eventName, builder);
        return builder;
    }

    public static <E> EventRegistration<E> init(String eventName, Class<E> modEventClass) {
        EventBuilder builder = ACTIVE_BUILDERS.remove(eventName);
        if (builder == null)
            throw new IllegalStateException("EventBuilder not defined for: " + eventName);
        return new EventRegistration<>(builder, modEventClass);
    }

    public EventBuilder pattern(String... patterns) {
        this.pattern = patterns;
        return this;
    }

    public EventBuilder eventValue(String name, Class<?> typeClass) {
        if (namedValues.containsKey(name))
            throw new IllegalArgumentException("Duplicate event value name: " + name);

        namedValues.put(name, typeClass);
        return this;
    }

    public <E> EventRegistration<E> enterRegistrationStage(Class<E> eventClass) {
        return init(eventName, eventClass);
    }

    /**
     * Similar to {@link #enterRegistrationStage(Class)}, but the class is predefined as {@link WrappedEvent}.
     * A contextValueFactory is also defined.
     *
     * @return An even registrator for this event builder
     */
    public EventRegistration<WrappedEvent> enterWrappedRegistrationStage() {
        return init(eventName, WrappedEvent.class)
                .contextValuesFactory(event -> event::values);
    }

    public String getEventName() {
        return eventName;
    }

    public String[] getPattern() {
        return pattern;
    }

    public List<String> getValueNames() {
        return List.copyOf(namedValues.keySet());
    }

    public Map<String, Class<?>> getNamedValues() {
        return namedValues;
    }

    public <E> EventContext<E> createContext(EventContextData data, E event, EventRedirector<E> redirector) {
        List<String> keys = getValueNames();
        Object[] values = data.values();

        if (keys.size() != values.length)
            throw new IllegalArgumentException("Mismatch: keys=" + keys.size() + ", values=" + values.length + " Event: " + eventName);

        if(redirector.getResultClass() != null) {
            return EventContext.resulting(eventName, keys, values, event, redirector, redirector.getResultClass(), null);
        }

        return EventContext.simple(eventName, keys, values, event, redirector);
    }
}
