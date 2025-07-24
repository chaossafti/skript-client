package de.safti.skriptclient.commons.standalone.types.common;

import de.safti.skriptclient.api.SkriptRegistry;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.UUID;


/**
 * Some minecraft network types can be registered here already, as they are the exact same class on all loaders.
 */
public class MinecraftNetworkTypes {

    static {

        /*
         * PACKET LISTENER
         */

        SkriptRegistry.registerType(ClientPacketListener.class, "packet-listener", "[packet[-| ]]listener")
                // .getId
                .property("uuid", UUID.class,
                        ClientPacketListener::getId)
                .register();


        /*
         * (TEXT) COMPONENT
         */
        SkriptRegistry.registerType(Component.class, "component", "[message|text][ |-]component")
                // .getStyle
                .property("style", Style.class,
                        Component::getStyle)

                // .getSiblings
                .propertyBuilder(Component.class, "siblings")
                    .getterCollection(Component::getSiblings)
                    .queueRegistration()

                // .copy
                .property("changeable copy", MutableComponent.class,
                        Component::copy)
                    .register();

        /*
         * MUTABLE (TEXT) COMPONENT
         */

        // TODO: Mutable component type registration
        SkriptRegistry.registerType(MutableComponent.class, "mutablecomponent", "mutable [message|text][ |-]component")
                .register();



    }
}
