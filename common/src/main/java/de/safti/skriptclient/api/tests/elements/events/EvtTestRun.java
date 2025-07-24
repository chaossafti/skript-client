package de.safti.skriptclient.api.tests.elements.events;

import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.pattern.PatternBundleBuilder;
import de.safti.skriptclient.api.synatxes.AbstractEvent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EvtTestRun extends AbstractEvent {

    public static final PatternBundle PATTERNS = PatternBundleBuilder.builder()
            .newPattern()
                .literal("test run")
                .build()
            .build();

    public record TestRunContext(Set<String> reachedPoints) implements TriggerContext {


        @Override
        public String getName() {
            return "test trigger context";
        }
    }

    static {
        SkriptRegistry.registerEvent(EvtTestRun.class, TestRunContext.class, PATTERNS);
    }

    @Override
    public boolean validate(int matchedPattern, ParseContext context) {
        return true;
    }

    @Override
    public boolean check(@NotNull TriggerContext triggerContext) {
        return true;
    }

    @Override
    public @NotNull PatternBundle getPatternBundle() {
        return PATTERNS;
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return "test run";
    }
}
