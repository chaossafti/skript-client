package de.safti.skriptclient.fabric.client;

import de.safti.skriptclient.screens.ScriptManagementScreen;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class SkriptclientFabricClient implements ClientModInitializer {

    private static KeyMapping OPEN_SCREEN_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.skriptclient.opengui",
            GLFW.GLFW_KEY_O,
            "key.categories.creative"
    ));

    @Override
    public void onInitializeClient() {
        ClientRawInputEvent.KEY_PRESSED.register((client, keyCode, scanCode, action, modifiers) -> {
            if(keyCode == GLFW.GLFW_KEY_O && client.screen == null) {
                client.setScreen(new ScriptManagementScreen());
            }

            return EventResult.interruptFalse();
        });
    }
}
