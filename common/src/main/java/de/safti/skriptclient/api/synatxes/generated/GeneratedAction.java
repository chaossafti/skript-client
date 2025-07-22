package de.safti.skriptclient.api.synatxes.generated;

import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class GeneratedAction<T> extends Effect {
    private final String friendlyNAme;
    private final Consumer<T> consumer;

    private Expression<T> tExpression;

    public GeneratedAction(String friendlyNAme, Consumer<T> consumer) {
        this.friendlyNAme = friendlyNAme;
        this.consumer = consumer;
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull ParseContext parseContext) {
        tExpression = (Expression<T>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext triggerContext) {
        Optional<? extends T> opt = tExpression.getSingle(triggerContext);
        if(opt.isEmpty()) {
            return;
        }

        consumer.accept(opt.get());
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return friendlyNAme;
    }
}
