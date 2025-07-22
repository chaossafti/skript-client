package de.safti.skriptclient.commons.standalone.events;

import io.github.syst3ms.skriptparser.lang.SkriptEvent;

public abstract class CancellableSkriptEvent extends SkriptEvent {
    protected boolean isCancelled;

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
