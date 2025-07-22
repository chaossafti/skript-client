package de.safti.skriptclient.api.event.interfaces;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @param <E> The event class
 */
public interface EventRedirector<E> {
    void onEvent(Consumer<E> event);

    BiConsumer<E, Boolean> cancelSetter();

    Function<E, Boolean> cancelGetter();

}

