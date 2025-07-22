package de.safti.skriptclient.api.synatxes.expression.property;

import io.github.syst3ms.skriptparser.lang.TriggerContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface PropertyGetter<T, H> {

    @NotNull
    T getSingle(TriggerContext context, H holder);

    @NotNull
    T[] getArray(TriggerContext context, H holder);

    boolean isSingle();


    static <T, H> PropertyGetter<T, H> createSingle(Function<H, T> function) {
        return new PropertyGetter<>() {
            @Override
            public @NotNull T getSingle(TriggerContext context, H holder) {
                return function.apply(holder);
            }

            @Override
            public T @NotNull [] getArray(TriggerContext context, H holder) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isSingle() {
                return true;
            }
        };
    }

    static <T, H> PropertyGetter<T, H> createPlural(Function<H, T[]> function) {
        return new PropertyGetter<>() {
            @Override
            public @NotNull T getSingle(TriggerContext context, H holder) {
                throw new UnsupportedOperationException();
            }

            @Override
            public @NotNull T[] getArray(TriggerContext context, H holder) {
                return function.apply(holder);
            }

            @Override
            public boolean isSingle() {
                return false;
            }
        };
    }


}
