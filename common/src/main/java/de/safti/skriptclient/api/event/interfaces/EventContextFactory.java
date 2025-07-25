package de.safti.skriptclient.api.event.interfaces;

/**
 * @param <E> The event class to extract values from
 */
@FunctionalInterface
public interface EventContextFactory<E> {
    EventContextData create(E event);
}

