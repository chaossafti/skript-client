package de.safti.skriptclient.api.event;

import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;

import java.util.*;

public final class EventBuilder {
    private static final Map<String, EventBuilder> ACTIVE_BUILDERS = new HashMap<>();

    private final String eventName;
    private String pattern;
    private final LinkedHashMap<String, Type<?>> namedValues = new LinkedHashMap<>();

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

    public static <T> EventRegistration<T> init(String eventName, Class<T> modEventClass) {
        EventBuilder builder = ACTIVE_BUILDERS.remove(eventName);
        if (builder == null)
            throw new IllegalStateException("EventBuilder not defined for: " + eventName);
        return new EventRegistration<>(builder, modEventClass);
    }

    public EventBuilder pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public EventBuilder eventValue(String name, Class<?> typeClass) {
        if (namedValues.containsKey(name))
            throw new IllegalArgumentException("Duplicate event value name: " + name);
        Optional<Type<?>> typeOpt = (Optional<Type<?>>) TypeManager.getByClass(typeClass);
        if (typeOpt.isEmpty())
            throw new IllegalArgumentException("Unknown type: " + typeClass);
        namedValues.put(name, typeOpt.get());
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public String getPattern() {
        return pattern;
    }

    public List<String> getValueNames() {
        return List.copyOf(namedValues.keySet());
    }

    public Map<String, Type<?>> getNamedValues() {
        return namedValues;
    }

    public EventContext createContext(EventContextData data) {
        List<String> keys = getValueNames();
        Object[] values = data.values();

        if (keys.size() != values.length)
            throw new IllegalArgumentException("Mismatch: keys=" + keys.size() + ", values=" + values.length);

        return EventContext.of(eventName, keys, values);
    }
}
