package de.safti.skriptclient.api.synatxes;

import de.safti.skriptclient.api.pattern.Pattern;
import de.safti.skriptclient.api.pattern.PatternArgument;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PatternSupportingSyntaxElement {

    default boolean validatePattern(SkriptLogger logger) {
        List<PatternArgument<?>> patternArguments = getPattern().patternArguments();

        if(getExpressions().length != patternArguments.size()) {
            logger.error("Provided expression list unequal argument list; This is a Skript client error. Please report it to the Github issue tracker!", ErrorType.EXCEPTION);
            // TODO: better logging of this? it most likely wont happen anyway
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    default  <A extends PatternArgument<AT>, AT> PatternArgument<AT> getArgument(int index) {
        Expression<AT> expression = (Expression<AT>) getExpressions()[index];
        return ((A) getPattern().patternArguments().get(index)).copyWithExpression(expression);
    }


    @NotNull
    Expression<?>[] getExpressions();

    @NotNull
    Pattern getPattern();

    boolean validate(int matchedPattern, ParseContext context);

}
