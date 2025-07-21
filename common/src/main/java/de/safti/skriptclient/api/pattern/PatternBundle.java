package de.safti.skriptclient.api.pattern;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

import java.util.Arrays;

/**
 * A class used for storing pattern infos.
 * Mainly used for registering.
 */
public record PatternBundle(PatternInfo[] patternInfos) {

    public ResolvedPattern resolve(int matchedPattern, Expression<?>[] expressions, ParseContext context) {
        PatternInfo info = patternInfos[matchedPattern];
        return info.resolve(expressions, context);
    }

    public String[] extractPatternStrings() {
        return Arrays.stream(patternInfos)
                .map(PatternInfo::pattern)
                .toArray(String[]::new);
    }


}
