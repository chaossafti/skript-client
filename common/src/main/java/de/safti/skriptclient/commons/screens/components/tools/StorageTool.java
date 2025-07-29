package de.safti.skriptclient.commons.screens.components.tools;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.commons.screens.components.sidebar.storage.StorageSidebar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class StorageTool extends ToolWidget {
    private static final ResourceLocation STORAGE_TOOL_IMG = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/chest_minecart.png");
    private final StorageSidebar storageSidebar = new StorageSidebar(SkriptClient.CORE.getScriptsFolder());

    public StorageTool(ToolBar toolBar) {
        super(toolBar, STORAGE_TOOL_IMG, Component.literal("file explorer"));

        mouseDown().subscribe((mouseX, mouseY, button) -> {
           if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
           if(getScreen().getSideBar() == storageSidebar) {
               getScreen().setSideBar(null);
               return true;
           }

            LocalPlayer player = Minecraft.getInstance().player;
           if(player != null) {
               player.sendSystemMessage(Component.literal("I'm not competent enough to make a working script editor. Please use an external editor!"));
           }

            return true;
        });

    }
}
