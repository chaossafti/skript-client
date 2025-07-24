package de.safti.skriptclient.screens.components.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StorageTool extends ToolWidget {
    private static final ResourceLocation STORAGE_TOOL_IMG = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/chest_minecart.png");

    public StorageTool(ToolBar toolBar) {
        super(toolBar, STORAGE_TOOL_IMG, Component.literal("file explorer"));
    }
}
