package de.safti.skriptclient.api.synatxes.expression;

import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

public interface Deletable extends ChangerFunction {

    void delete(TriggerContext context);

    @Override
    default @NotNull ChangeMode asChangeMode() {
        return ChangeMode.DELETE;
    }
}
