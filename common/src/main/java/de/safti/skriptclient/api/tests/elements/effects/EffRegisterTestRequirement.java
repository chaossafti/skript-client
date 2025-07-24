package de.safti.skriptclient.api.tests.elements.effects;

import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.pattern.PatternBundleBuilder;
import de.safti.skriptclient.api.pattern.arguments.ExpressionPatternArgument;
import de.safti.skriptclient.api.synatxes.AbstractEffect;
import de.safti.skriptclient.api.tests.elements.events.EvtTestRequirements;
import de.safti.skriptclient.api.tests.elements.events.EvtTestRun;
import de.safti.skriptclient.logging.runtime.RuntimeLogger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EffRegisterTestRequirement extends AbstractEffect {
    private static final String NAME = "name";

    public static final PatternBundle PATTERNS = PatternBundleBuilder.builder()
            .registerExpressionArgument(NAME, String.class, false)

            .newPattern()
                .literal("reach point")
                .argument(NAME)
                .build()
            .build();

    static {
        SkriptRegistry.registerEffect(EffRegisterTestRequirement.class, PATTERNS);
    }

    @Override
    public boolean validate(int matchedPattern, ParseContext context) {
        ParserState state = context.getParserState();
        SkriptLogger logger = context.getLogger();

        if(!(state.getCurrentEvent() instanceof EvtTestRequirements)) {
            logger.error("test requirements must be registered in a test requirements register event!", ErrorType.SEMANTIC_ERROR);
            return false;
        }

        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext context, RuntimeLogger runtimeLogger) {
        if(!(context instanceof EvtTestRun.TestRunContext(Set<String> reachedPoints))) {
            runtimeLogger.error("Unsupported trigger context!", ErrorType.EXCEPTION);
            return;
        }

        ExpressionPatternArgument<String> nameArg = getArgument(NAME);
        reachedPoints.add(nameArg.resolveOrGo(context));

    }

    @Override
    public @NotNull PatternBundle getPatternBundle() {
        return PATTERNS;
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        ExpressionPatternArgument<String> nameArg = getArgument(NAME);
        return "test requirement " + (triggerContext != null ? nameArg.resolveOrGo(triggerContext) : "");
    }
}
