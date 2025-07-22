package de.safti.skriptclient.commons.standalone.types;

import de.safti.skriptclient.api.SkriptRegistry;
import net.minecraft.client.multiplayer.ClientPacketListener;


/**
 * Some minecraft network types can be registered here already, as they are the exact same class on all loaders.
 */
public class MinecraftNetworkTypes {

    static {
        SkriptRegistry.registerType(ClientPacketListener.class, "packet-listener", "[packet[-| ]]listener")
                .register();
    }
}
