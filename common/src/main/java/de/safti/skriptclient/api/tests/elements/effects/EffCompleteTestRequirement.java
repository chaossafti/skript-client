package de.safti.skriptclient.api.tests.elements.effects;

import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.pattern.PatternBundleBuilder;
import de.safti.skriptclient.api.pattern.arguments.ExpressionPatternArgument;
import de.safti.skriptclient.api.synatxes.AbstractEffect;
import de.safti.skriptclient.api.tests.elements.events.EvtTestRun;
import de.safti.skriptclient.logging.runtime.RuntimeLogger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.SkriptRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EffCompleteTestRequirement extends AbstractEffect {
    private static final String NAME = "name";

    public static final PatternBundle PATTERNS = PatternBundleBuilder.builder()
            .registerExpressionArgument(NAME, String.class, false)

            .newPattern()
                .literal("complete [test ]point")
                .argument(NAME)
                .build()
            .build();

    static {
        SkriptRegistry.registerEffect(EffCompleteTestRequirement.class, PATTERNS);
    }

    @Override
    protected void execute(@NotNull TriggerContext context, RuntimeLogger runtimeLogger) {
        if(!(context instanceof EvtTestRun.TestRunContext runContext)) {
            throw new SkriptRuntimeException("Invalid context: " + context.getClass());
        }

        ExpressionPatternArgument<String> nameArg = getArgument(NAME);
        runContext.reachedPoints().add(nameArg.resolveOrGo(context));

    }

    @Override
    public boolean validate(int matchedPattern, ParseContext context) {
        if(!(context.getParserState().getCurrentEvent() instanceof EvtTestRun evtTestRun)) {
            context.getLogger().error("complete test requirement effect can only be used in a test run event!", ErrorType.SEMANTIC_ERROR);
            return false;
        }

        return true;
    }

    @Override
    public @NotNull PatternBundle getPatternBundle() {
        return PATTERNS;
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return "complete " + getArgument(NAME).resolveOrGo(triggerContext);
    }
}
