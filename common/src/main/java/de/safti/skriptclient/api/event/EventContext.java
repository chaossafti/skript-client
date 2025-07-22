package de.safti.skriptclient.api.event;

import de.safti.skriptclient.api.event.interfaces.EventRedirector;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EventContext<E> implements TriggerContext {
    private final String eventName;
    private final Map<String, Object> values;

    protected EventContext(String eventName, @NotNull Map<String, Object> values) {
        this.eventName = eventName;
        this.values = values;
    }

    public static <E> EventContext<E> of(String eventName, List<String> keys, Object[] data, @Nullable E event,  @Nullable EventRedirector<E> eventRedirector) {
        if (keys.size() != data.length)
            throw new IllegalArgumentException("Mismatch between keys and data");

        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), data[i]);
        }

        if(eventRedirector != null) {
            return new CancellableEventContext<>(eventName, map, event, eventRedirector);
        }

        return new EventContext<>(eventName, map);
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

