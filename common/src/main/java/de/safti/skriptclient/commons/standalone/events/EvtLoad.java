package de.safti.skriptclient.commons.standalone.events;

import de.safti.skriptclient.SkriptClient;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.script.Script;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: replace with new element api, once it's done
public class EvtLoad extends SkriptEvent {

    public record ScriptLoadContext(Script script) implements TriggerContext {

        @Override
            public String getName() {
                return "script load event";
            }
        }


    static {
        SkriptClient.INSTANCE.getRegistry()
                .newEvent(EvtLoad.class, "load")
                .setHandledContexts(ScriptLoadContext.class)
                .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext triggerContext) {
        return true;
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull ParseContext parseContext) {
        return true;
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return "on script load";
    }
}
