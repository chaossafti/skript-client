package de.safti.skriptclient.commons.standalone.events;

import io.github.syst3ms.skriptparser.lang.TriggerContext;

public interface CancellableTriggerContext extends TriggerContext {

    void setCancelled(boolean cancelState);

    boolean isCancelled();

}
