package de.safti.skriptclient.screens.components.tools;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ToolWidget extends FlowLayout {
    private final ToolBar toolBar;
    Surface HOVER_SURFACE = (context, component) -> {
        context.drawGradientRect(
                component.x(), component.y(), component.width(), component.height(),
                0xC0000000, 0xC0000000, 0xC0000000, 0xC0000000
        );
    };


    public ToolWidget(ToolBar toolBar, ResourceLocation icon, Component tooltip) {
        super(Sizing.fill(100), Sizing.fixed(32), io.wispforest.owo.ui.container.FlowLayout.Algorithm.HORIZONTAL);
        this.toolBar = toolBar;

        this.padding(Insets.of(4))
                .surface(Surface.BLANK)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .tooltip(tooltip);

        TextureComponent texture = Components.texture(icon, 0, 0, 16, 16, 16, 16);
        this.child(texture);
    }
}