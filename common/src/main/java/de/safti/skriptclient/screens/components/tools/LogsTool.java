package de.safti.skriptclient.screens.components.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LogsTool extends ToolWidget {
    private static final ResourceLocation LOGS_TOOL = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/oak_log.png");

    public LogsTool(ToolBar toolBar) {
        super(toolBar, LOGS_TOOL, Component.literal("Logs"));

    }
}
