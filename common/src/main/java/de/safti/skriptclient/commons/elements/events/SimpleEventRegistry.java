package de.safti.skriptclient.commons.elements.events;

import de.safti.skriptclient.api.event.EventBuilder;
import net.minecraft.client.multiplayer.ClientPacketListener;

/**
 * This class is used to build events using {@link de.safti.skriptclient.api.event.EventBuilder}
 */
public class SimpleEventRegistry {
    public static final String CONNECT = "connect";

    static {
        EventBuilder.create(CONNECT)
                .pattern("connect [the [a] server]")
                .eventValue("connection", ClientPacketListener.class);

    }

}
