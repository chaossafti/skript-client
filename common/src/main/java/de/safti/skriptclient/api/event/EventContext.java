package de.safti.skriptclient.api.event;

import de.safti.skriptclient.api.event.interfaces.EventRedirector;
import de.safti.skriptclient.commons.standalone.events.CancellableTriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EventContext<E> implements TriggerContext, CancellableTriggerContext {
    private final String eventName;
    private final Map<String, Object> values;
    private final E event;
    @Nullable
    private final EventRedirector<E> eventRedirector;

    protected EventContext(String eventName, @NotNull Map<String, Object> values, E event, @Nullable EventRedirector<E> eventRedirector) {
        this.eventName = eventName;
        this.values = values;
        this.event = event;
        this.eventRedirector = eventRedirector;
    }

    public static <E> EventContext<E> simple(String eventName, List<String> keys, Object[] data, @Nullable E event, @Nullable EventRedirector<E> eventRedirector) {
        if (keys.size() != data.length)
            throw new IllegalArgumentException("Mismatch between keys and data");

        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), data[i]);
        }

        return new EventContext<>(eventName, map, event, eventRedirector);
    }

    public static <E, R> ResultingEventContext<E, R> resulting(String eventName, List<String> keys, Object[] data, @Nullable E event, @Nullable EventRedirector<E> eventRedirector, Class<R> resultClass, @Nullable R initialResult) {
        if (keys.size() != data.length)
            throw new IllegalArgumentException("Mismatch between keys and data");

        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), data[i]);
        }

        return new ResultingEventContext<>(eventName, map, event, eventRedirector, resultClass, initialResult);
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
    public boolean isCancellable() {
        return eventRedirector != null;
    }

    @Override
    public String toString() {
        return "EventContext{name=" + eventName + ", values=" + values + '}';
    }

    @Override
    public String getName() {
        return eventName;
    }

    @Override
    public void setCancelled(boolean cancelState) {
        if(eventRedirector == null) throw new UnsupportedOperationException();

        eventRedirector.cancelSetter().accept(event, cancelState);
    }

    @Override
    public boolean isCancelled() {
        if(eventRedirector == null) throw new UnsupportedOperationException();

        return eventRedirector.cancelGetter().apply(event);
    }
}

