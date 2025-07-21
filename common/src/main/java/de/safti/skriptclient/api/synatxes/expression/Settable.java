package de.safti.skriptclient.api.synatxes.expression;

import io.github.syst3ms.skriptparser.lang.TriggerContext;

public interface Settable {

    void set(Object[] delta, TriggerContext context);

}
