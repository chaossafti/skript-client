package de.safti.skriptclient.api.event.interfaces;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @param <E> The event class
 */
public interface EventRedirector<E> {
    void onEvent(Consumer<E> event);

    boolean isCancellable();

    @Nullable
    Class<?> getResultClass();

    default void acceptResult(E event, Object o) {
        throw new UnsupportedOperationException();
    }


    @NotNull
    BiConsumer<E, Boolean> cancelSetter();

    @NotNull
    Function<E, Boolean> cancelGetter();

}

