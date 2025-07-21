package de.safti.skriptclient.fabric.elements.events;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.docs.annotations.Author;
import de.safti.skriptclient.commons.elements.events.EvtConnect;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.jetbrains.annotations.NotNull;

@Author("notSafti")
public class EvtConnectFabric extends EvtConnect {

    static {
        SkriptRegistry.registerEvent(EvtConnectFabric.class, ConnectContext.class, PATTERNS);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ConnectContext context = new ConnectContext(handler);
            SkriptClient.INSTANCE.getEventManager()
                    .callEvent(EvtConnectFabric.class, context);
        });
    }


    @Override
    public boolean validate(int matchedPattern, ParseContext context) {
        return true;
    }

    @Override
    public boolean check(@NotNull TriggerContext triggerContext) {
        return true;
    }
}
