package de.safti.skriptclient.commons.standalone.types.client;

import de.safti.skriptclient.api.SkriptRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClientRenderingTypes {

    static {

        SkriptRegistry.registerType(Screen.class, "screen", "screen")
                .property("title", Component.class,
                        Screen::getTitle)
                .property("width", Integer.class,
                        screen -> screen.width)
                .property("height", Integer.class,
                        screen -> screen.height)
                .register();





    }

}
