package de.safti.skriptclient.api.pattern;

import de.safti.skriptclient.api.pattern.arguments.ExpressionPatternArgument;
import de.safti.skriptclient.api.pattern.arguments.RegexPatternArgument;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record ResolvedPattern(PatternInfo patternInfo,
                              Map<String, ExpressionPatternArgument<?>> expressionArguments,
                              Map<String, RegexPatternArgument> regexArguments) {

    @NotNull
    public <A> A getArgument(String name) throws IllegalArgumentException {
        //noinspection unchecked
        return (A) expressionArguments.get(name);
    }

    public RegexPatternArgument getRegexArgument(String name) {
        return regexArguments.get(name);
    }

    /**
     * Regex arguments will only ever be used in init.
     * Keeping them around after the init call won't help us
     * as the init method isn't ever called again.
     * To save us some memory, we can clear the regex arguments.
     * <p>
     * After calling this method, regex arguments will be unaccessible.
     */
    @ApiStatus.Internal
    public void close() {
        // regex arguments will only ever be used in init
        // keeping them around after the init call won't help us
        // as init isn't ever called again
        // to save memory we can clear the regex arguments
        regexArguments.clear();
    }
}
