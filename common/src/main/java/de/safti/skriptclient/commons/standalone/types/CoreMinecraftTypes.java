package de.safti.skriptclient.commons.standalone.types;

import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class CoreMinecraftTypes {

    static {

        /*
         * MINECRAFT CLIENT
         */
        SkriptRegistry.registerType(Minecraft.class, "client", "[minecraft(-| )]client")
                // .screen, .setScreen
                .property("screen", Screen.class, SecurityLevel.INTERFACE,
                        minecraft -> minecraft.screen,
                        Minecraft::setScreen
                        )

                //
                .property("packet listener", ClientPacketListener.class,
                        PropertyGetter.createSingle(Minecraft::getConnection))

                .register();


    }

}
