package de.safti.skriptclient.api.pattern;

import java.util.ArrayList;
import java.util.List;

public class PatternBuilder {
    private final PatternBundleBuilder parentBuilder;
    private final StringBuilder stringBuilder = new StringBuilder();

    private final List<PatternBundleBuilder.ExpressionArgumentInfo> expressionArguments = new ArrayList<>();
    private final List<PatternBundleBuilder.RegexArgumentInfo> regexArguments = new ArrayList<>();

    PatternBuilder(PatternBundleBuilder parentBuilder) {
        this.parentBuilder = parentBuilder;
    }

    public PatternBuilder literal(String str) {
        if(!stringBuilder.isEmpty()) stringBuilder.append(" ");

        stringBuilder.append(str);
        return this;
    }

    public PatternBuilder regex(String name) {
        if(!stringBuilder.isEmpty()) stringBuilder.append(" ");

        PatternBundleBuilder.RegexArgumentInfo regexInfo = parentBuilder.getRegexArgumentInfo(name);
        stringBuilder.append("<")
                .append(regexInfo)
                .append(">");

        regexArguments.add(regexInfo);

        return this;
    }


    public PatternBuilder argument(String name) {
        if(!stringBuilder.isEmpty()) stringBuilder.append(" ");

        PatternBundleBuilder.ExpressionArgumentInfo argumentInfo = parentBuilder.getExpressionArgumentInfo(name);
        if(argumentInfo == null) {
            throw new IllegalArgumentException("Argument with name " + name + " has not been registered! use SyntaxPatternInfoBuilder#registerArgument to register them.");
        }

        stringBuilder.append(argumentInfo.getPatternArgumentString());
        expressionArguments.add(argumentInfo);

        return this;
    }

    public PatternBundleBuilder build() {
        parentBuilder.addIncompletePattern(this);
        return parentBuilder;
    }

    List<PatternBundleBuilder.ExpressionArgumentInfo> getExpressionArguments() {
        return expressionArguments;
    }

    List<PatternBundleBuilder.RegexArgumentInfo> getRegexArguments() {
        return regexArguments;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
