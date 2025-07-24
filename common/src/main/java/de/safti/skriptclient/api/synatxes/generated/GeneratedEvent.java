package de.safti.skriptclient.api.synatxes.generated;

import de.safti.skriptclient.commons.standalone.events.CancellableSkriptEvent;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.event.SkriptEventManager;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratedEvent extends CancellableSkriptEvent {
    private final String eventName;
    private final boolean isCancellable;

    public GeneratedEvent(String eventName, boolean isCancellable) {
        this.eventName = eventName;
        this.isCancellable = isCancellable;
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
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return eventName;
    }

    @Override
    public void register(@NotNull Trigger trigger, SkriptEventManager eventManager) {
        this.eventHandler = eventManager.registerTrigger(eventName, trigger, this::check);
    }

    @Override
    public void unregister() {
        if (this.eventHandler != null) {
            this.eventHandler.getAttachedEventManager().removeEventHandler(eventName, this.eventHandler);
            this.eventHandler = null;
        }
    }

    @Override
    public boolean isCancellable() {
        return isCancellable;
    }
}
