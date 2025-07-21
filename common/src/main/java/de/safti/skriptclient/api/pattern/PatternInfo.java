package de.safti.skriptclient.api.pattern;

import de.safti.skriptclient.api.pattern.PatternBundleBuilder.ExpressionArgumentInfo;
import de.safti.skriptclient.api.pattern.PatternBundleBuilder.RegexArgumentInfo;
import de.safti.skriptclient.api.pattern.arguments.ExpressionPatternArgument;
import de.safti.skriptclient.api.pattern.arguments.RegexPatternArgument;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.Type;

import java.util.*;

public record PatternInfo(List<ExpressionArgumentInfo> expressionArguments,
                          List<RegexArgumentInfo> regexArguments,
                          String pattern) {


    public ResolvedPattern resolve(Expression<?>[] expressions, ParseContext context) {

        // resolve the expression arguments
        List<ExpressionPatternArgument<?>> resolvedExpressionArguments = new ArrayList<>();
        for (int i = 0, expressionArgumentsSize = expressionArguments.size(); i < expressionArgumentsSize; i++) {
            ExpressionArgumentInfo argInfo = expressionArguments.get(i);
            var argument = createArgument(argInfo.name(), expressions[i], argInfo.type());

            resolvedExpressionArguments.add(argument);
        }

        // resolve the regex arguments
        List<RegexPatternArgument> resolvedRegexArguments = new ArrayList<>();
        for (int i = 0; i < regexArguments.size(); i++) {
            RegexArgumentInfo argInfo = regexArguments.get(i);
            RegexPatternArgument argument = new RegexPatternArgument(argInfo.name(), context, i);
            resolvedRegexArguments.add(argument);
        }

        // convert expression arguments into a Map<String, ExprArg>
        Map<String, ExpressionPatternArgument<?>> expressionMap = new HashMap<>();
        for (ExpressionPatternArgument<?> argument : resolvedExpressionArguments) {
            expressionMap.put(argument.getName(), argument);
        }

        // same for regexes
        Map<String, RegexPatternArgument> regexMap = new HashMap<>();
        for (RegexPatternArgument argument : resolvedRegexArguments) {
            regexMap.put(argument.getName(), argument);
        }

        return new ResolvedPattern(this, expressionMap, regexMap);
    }

    private <T> ExpressionPatternArgument<T> createArgument(String name, Expression<?> expression, Type<T> type) {
        @SuppressWarnings("unchecked")
        Expression<T> expressionT = (Expression<T>) expression;
        return new ExpressionPatternArgument<>(name, type, expressionT);
    }

}
