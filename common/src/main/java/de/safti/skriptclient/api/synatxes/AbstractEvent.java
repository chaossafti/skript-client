package de.safti.skriptclient.api.synatxes;

import de.safti.skriptclient.api.pattern.ResolvedPattern;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEvent extends SkriptEvent implements ArgumentDrivenSyntax {
    private ResolvedPattern personalPattern;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.personalPattern = getPatternBundle().resolve(matchedPattern, expressions, parseContext);
        return validate(matchedPattern, parseContext);
    }

    @Override
    public ResolvedPattern getPersonalPattern() {
        return personalPattern;
    }
}
