package de.safti.skriptclient.commons.standalone.expressions;

import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.pattern.PatternBundleBuilder;
import de.safti.skriptclient.api.pattern.arguments.ExpressionPatternArgument;
import de.safti.skriptclient.api.synatxes.AbstractExpression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprLiteralComponent extends AbstractExpression<Component> {
    private static final String STRING = "string";


    public static final PatternBundle PATTERNS = PatternBundleBuilder.builder()
            .registerExpressionArgument(STRING, String.class, false)

            .newPattern()
                .literal("[text[-| ]]component of")
                .argument(STRING)
                .build()
            .build();


    static {
        SkriptRegistry.registerExpression(ExprLiteralComponent.class, Component.class, true, PATTERNS);
    }


    @Override
    public boolean validate(int matchedPattern, ParseContext context) {
        return true;
    }

    @Override
    public Component[] getValues(@NotNull TriggerContext triggerContext) {
        ExpressionPatternArgument<String> stringArg = getArgument(STRING);
        String str = stringArg.resolveOrGo(triggerContext);
        return new Component[] {net.minecraft.network.chat.Component.literal(str)};
    }


    @Override
    public @NotNull PatternBundle getPatternBundle() {
        return PATTERNS;
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        ExpressionPatternArgument<String> stringArg = getArgument(STRING);
        return "text component of " + stringArg.resolveOrGo(triggerContext);
    }
}
