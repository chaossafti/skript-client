package de.safti.skriptclient.commons.screens.components.main.log;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class LogTableHead extends FlowLayout {
    private static final ResourceLocation HEADER = ResourceLocation.fromNamespaceAndPath("skriptclient", "log/header.png");

    private final LogTableSizeContext sizeContext;


    protected LogTableHead(LogTableSizeContext sizeContext, Font textRenderer) {
        super(Sizing.expand(), Sizing.content(), Algorithm.HORIZONTAL);
        this.sizeContext = sizeContext;

        // config
        gap(sizeContext.horizontalGap());
        margins(Insets.top(4));
        int halfGap = sizeContext.horizontalGap() / 2;
        int fullGap = sizeContext.horizontalGap();
        java.util.List<Integer> xSeparators = List.of(
                12 + halfGap,                                              // after icon
                12 + fullGap + sizeContext.lineStringWidth() + halfGap,       // after line
                12 + fullGap + sizeContext.lineStringWidth() + fullGap + sizeContext.messageWidth() + halfGap // after message
        );

        this.surface(new ColumnSeparatorSurface(xSeparators, sizeContext.verticalGap(), 0xFFFFFFFF)); // white lines

        // children

        // Texture
        TextureComponent texture = Components.texture(HEADER, 0, 0, 12, 12, 12, 12);
        child(texture);

        // "Line"
        LabelComponent lineLabel = (LabelComponent) Components.label(Component.literal("Line"))
                .horizontalSizing(Sizing.fixed(sizeContext.lineStringWidth()));
        child(lineLabel);

        // "Message"
        LabelComponent messageLabel = (LabelComponent) Components.label(Component.literal("Message"))
                .horizontalSizing(Sizing.fixed(sizeContext.messageWidth()));
        child(messageLabel);

        // "Script"
        LabelComponent scriptLabel = (LabelComponent) Components.label(Component.literal("Script"))
                .horizontalSizing(Sizing.fixed(sizeContext.scriptWidth()));
        child(scriptLabel);
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return sizeContext.totalWidth();
    }
}
