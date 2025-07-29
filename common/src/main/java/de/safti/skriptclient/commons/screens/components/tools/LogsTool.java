package de.safti.skriptclient.commons.screens.components.tools;

import de.safti.skriptclient.commons.screens.components.sidebar.logs.LogsSidebar;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class LogsTool extends ToolWidget {
    private static final ResourceLocation LOGS_TOOL = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/oak_log.png");
    private final LogsSidebar bar;

    public LogsTool(ToolBar toolBar) {
        super(toolBar, LOGS_TOOL, Component.literal("Logs"));
        bar = new LogsSidebar(getScreen());

        // events
        mouseDown().subscribe((mouseX, mouseY, button) -> {
           if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
           getScreen().setSideBar(bar);

            return true;
        });

    }
}
