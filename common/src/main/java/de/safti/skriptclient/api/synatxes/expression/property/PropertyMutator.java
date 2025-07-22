package de.safti.skriptclient.api.synatxes.expression.property;

import io.github.syst3ms.skriptparser.lang.TriggerContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles ChangeMode.RESET, ChangeMode.DELETE and ChangeMode.REMOVE_ALL
 * @see io.github.syst3ms.skriptparser.types.changers.ChangeMode
 *
 */
public interface PropertyMutator<H> {

    void mutate(TriggerContext context, H holder);

    boolean isSingle();


    @Contract(value = "_ -> new", pure = true)
    static <H> @NotNull PropertyMutator<H> createSingle(Consumer<H> mutatorConsumer) {
        return new PropertyMutator<>() {


            @Override
            public void mutate(TriggerContext context, H holder) {
                mutatorConsumer.accept(holder);
            }

            @Override
            public boolean isSingle() {
                return true;
            }
        };
    }

    @Contract(value = "_ -> new", pure = true)
    static <H> @NotNull PropertyMutator<H> createSingle(BiConsumer<TriggerContext, H> function) {
        return new PropertyMutator<>() {
            @Override
            public void mutate(TriggerContext context, H holder) {
                function.accept(context, holder);
            }

            @Override
            public boolean isSingle() {
                return true;
            }
        };
    }


    @Contract(value = "_ -> new", pure = true)
    static <H> @NotNull PropertyMutator<H> createPlural(Consumer<H> changerConsumer) {
        return new PropertyMutator<>() {


            @Override
            public void mutate(TriggerContext context, H holder) {
                changerConsumer.accept(holder);
            }

            @Override
            public boolean isSingle() {
                return false;
            }
        };
    }

    @Contract(value = "_ -> new", pure = true)
    static <H> @NotNull PropertyMutator<H> createPlural(BiConsumer<TriggerContext, H> function) {
        return new PropertyMutator<>() {


            @Override
            public void mutate(TriggerContext context, H holder) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isSingle() {
                return false;
            }
        };
    }

}
