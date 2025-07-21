package de.safti.skriptclient.api.synatxes.expression;

import io.github.syst3ms.skriptparser.lang.TriggerContext;

public interface Removable {

    void remove(Object[] delta, TriggerContext context);

}
