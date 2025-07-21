package de.safti.skriptclient.api.event;

import io.github.syst3ms.skriptparser.lang.TriggerContext;

import java.util.*;

public final class EventContext implements TriggerContext {
    private final String eventName;
    private final Map<String, Object> values;

    private EventContext(String eventName, Map<String, Object> values) {
        this.eventName = eventName;
        this.values = Map.copyOf(values); // make it immutable
    }

    public static EventContext of(String eventName, List<String> keys, Object[] data) {
        if (keys.size() != data.length)
            throw new IllegalArgumentException("Mismatch between keys and data");

        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), data[i]);
        }

        return new EventContext(eventName, map);
    }

    public Object get(String name) {
        return values.get(name);
    }

    public String getEventName() {
        return eventName;
    }

    public Set<String> keys() {
        return values.keySet();
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    @Override
    public String toString() {
        return "EventContext{name=" + eventName + ", values=" + values + '}';
    }

    @Override
    public String getName() {
        return eventName;
    }
}

