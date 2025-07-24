package de.safti.skriptclient.api.tests.elements.events;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.tests.elements.effects.EffRegisterTestRequirement;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.SyntaxElement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

// TODO: intermediary Structure class between SkriptEvent and SyntaxElement
//  that when registered doesn't prepend "on" before the pattern
public class EvtTestRequirements extends SkriptEvent {

    public record TestRequirementsContext(Set<String> requiredPoints) implements TriggerContext {

        public TestRequirementsContext() {
            this(new HashSet<>());
        }

        @Override
        public String getName() {
            return "test requirements";
        }
    }


    static {
        SkriptClient.INSTANCE.getRegistry()
                .newEvent(EvtTestRequirements.class, "test requirements")
                .register();
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull ParseContext parseContext) {
        return true;
    }

    @Override
    public boolean check(@NotNull TriggerContext triggerContext) {
        return true;
    }

    @Override
    protected boolean isRestrictingExpressions() {
        return true;
    }

    @Override
    protected Set<Class<? extends SyntaxElement>> getAllowedSyntaxes() {
        return Set.of(EffRegisterTestRequirement.class);
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return "event requirements";
    }
}
