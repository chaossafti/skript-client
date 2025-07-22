package de.safti.skriptclient.api.synatxes.expression.property;

import io.github.syst3ms.skriptparser.lang.TriggerContext;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Handles ChangeMode.SET, ChangeMode.ADD and ChangeMode.REMOVE
 *
 * @see io.github.syst3ms.skriptparser.types.changers.ChangeMode
 */
public interface PropertyChanger<T, H> {

    void chanceBy(TriggerContext context, H holder, T delta);

    void changeBy(TriggerContext context, H holder, T[] delta);

    boolean isSingle();


    @Contract(value = "_ -> new", pure = true)
    static <T, H> @NotNull PropertyChanger<T, H> createSingle(BiConsumer<H, T> changerConsumer) {
        return new PropertyChanger<>() {


            @Override
            public void chanceBy(TriggerContext context, H holder, T delta) {
                changerConsumer.accept(holder, delta);
            }

            @Override
            public void changeBy(TriggerContext context, H holder, T[] delta) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isSingle() {
                return true;
            }
        };
    }

    @Contract(value = "_ -> new", pure = true)
    static <T, H> @NotNull PropertyChanger<T, H> createSingle(TriConsumer<TriggerContext, H, T> function) {
        return new PropertyChanger<>() {
            @Override
            public void chanceBy(TriggerContext context, H holder, T value) {
                function.accept(context, holder, value);
            }

            @Override
            public void changeBy(TriggerContext context, H holder, T[] delta) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isSingle() {
                return true;
            }
        };
    }


    @Contract(value = "_ -> new", pure = true)
    static <T, H> @NotNull PropertyChanger<T, H> createPlural(BiConsumer<H, T> changerConsumer) {
        return new PropertyChanger<>() {


            @Override
            public void chanceBy(TriggerContext context, H holder, T delta) {
                changerConsumer.accept(holder, delta);
            }

            @Override
            public void changeBy(TriggerContext context, H holder, T[] delta) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isSingle() {
                return false;
            }
        };
    }

    @Contract(value = "_ -> new", pure = true)
    static <T, H> @NotNull PropertyChanger<T, H> createPlural(TriConsumer<TriggerContext, H, T[]> function) {
        return new PropertyChanger<>() {


            @Override
            public void chanceBy(TriggerContext context, H holder, T delta) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void changeBy(TriggerContext context, H holder, T[] delta) {
                function.accept(context, holder, delta);
            }

            @Override
            public boolean isSingle() {
                return false;
            }
        };
    }

}
