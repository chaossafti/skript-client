package de.safti.skriptclient.api.synatxes.expression;

import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

public interface ChangerFunction {

    @NotNull
    ChangeMode asChangeMode();
}
