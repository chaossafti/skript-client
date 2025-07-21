package de.safti.skriptclient.api.synatxes;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEvent extends SkriptEvent implements PatternSupportingSyntaxElement {
    private Expression<?>[] parsedExpressions;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, ParseContext parseContext) {
        this.parsedExpressions = expressions;

        return validatePattern(parseContext.getLogger()) && validate(matchedPattern, parseContext);
    }

    @Override
    public @NotNull Expression<?>[] getExpressions() {
        return parsedExpressions;
    }
}
