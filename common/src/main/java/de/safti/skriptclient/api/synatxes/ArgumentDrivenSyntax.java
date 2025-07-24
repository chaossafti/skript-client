package de.safti.skriptclient.api.synatxes;

import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.pattern.ResolvedPattern;
import de.safti.skriptclient.api.pattern.arguments.ExpressionPatternArgument;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

/**
 * Base interface for any custom, argument driven syntaxes.
 * <p>
 * Don't directly implement this. Use the already inplace abstract classes instead.
 *
 * @see AbstractEffect
 * @see AbstractExpression
 * @see AbstractEvent
 */
public interface ArgumentDrivenSyntax {

    @SuppressWarnings("unchecked")
    default  <A extends ExpressionPatternArgument<AT>, AT> ExpressionPatternArgument<AT> getArgument(String name) {
        return ((A) getPersonalPattern().expressionArguments().get(name));
    }

    // TODO: look up SyntaxManager and SkriptRegistry to provide a default implementation
    @NotNull
    PatternBundle getPatternBundle();

    ResolvedPattern getPersonalPattern();

    boolean validate(int matchedPattern, ParseContext context);

}
