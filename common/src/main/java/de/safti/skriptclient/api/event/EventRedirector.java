package de.safti.skriptclient.api.event;

import java.util.function.Consumer;

/**
 * @param <E> The event class to register.
 */
@FunctionalInterface
public interface EventRedirector<E> {
    void onEvent(Consumer<E> event);
}

