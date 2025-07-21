package de.safti.skriptclient.api.synatxes;

import de.safti.skriptclient.api.synatxes.expression.*;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractExpression<T> implements Expression<T>, PatternSupportingSyntaxElement {
    private Expression<?>[] parsedExpressions;


    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        parsedExpressions = expressions;
        return validatePattern(parseContext.getLogger()) && validate(matchedPattern, parseContext);
    }

    @Override
    public @NotNull Expression<?>[] getExpressions() {
        return parsedExpressions;
    }

    @Override
    public final void change(@NotNull TriggerContext context, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        switch (changeMode) {
            case SET -> castThisToOrThrow(Settable.class).set(changeWith, context);
            case ADD -> castThisToOrThrow(Addable.class).add(changeWith, context);
            case REMOVE -> castThisToOrThrow(Removable.class).remove(changeWith, context);
            case DELETE -> castThisToOrThrow(Deletable.class).delete(context);
            case RESET -> castThisToOrThrow(Resettable.class).reset(context);
            case REMOVE_ALL -> castThisToOrThrow(AllRemovable.class).removeAll(context);
        }
    }



    @Override
    public final Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        Set<ChangeMode> supportedChangeModes = supportedChangeModes();
        if(!supportedChangeModes.contains(mode)) {
            return Optional.empty();
        }

        return Optional.ofNullable(getSupportedValues());
    }


    @Nullable
    protected abstract Class<?>[] getSupportedValues();

    private <C> C castThisToOrThrow(Class<C> clazz) {
        if(clazz.isInstance(this)) {
            //noinspection unchecked
            return (C) this;
        }

        throw new UnsupportedOperationException();
    }

    protected Set<ChangeMode> supportedChangeModes() {
        Set<ChangeMode> result = new HashSet<>();

        if(this instanceof Addable) {
            result.add(ChangeMode.ADD);
        }

        if(this instanceof AllRemovable) {
            result.add(ChangeMode.REMOVE_ALL);
        }

        if(this instanceof Deletable) {
            result.add(ChangeMode.DELETE);
        }

        if(this instanceof Removable) {
            result.add(ChangeMode.REMOVE);
        }

        if(this instanceof Resettable) {
            result.add(ChangeMode.RESET);
        }

        if(this instanceof Settable) {
            result.add(ChangeMode.SET);
        }

        return result;
    }
}
