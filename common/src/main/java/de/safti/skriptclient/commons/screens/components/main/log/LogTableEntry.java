package de.safti.skriptclient.commons.screens.components.main.log;

import io.github.syst3ms.skriptparser.log.LogEntry;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.List;


// SYMBOL line      error message          script
public class LogTableEntry extends FlowLayout {
    private static final ResourceLocation DEBUG = ResourceLocation.fromNamespaceAndPath("skriptclient", "log/debug.png");
    private static final ResourceLocation INFORMATION = ResourceLocation.fromNamespaceAndPath("skriptclient", "log/information.png");
    private static final ResourceLocation WARNING = ResourceLocation.fromNamespaceAndPath("skriptclient", "log/warning.png");
    private static final ResourceLocation ERROR = ResourceLocation.fromNamespaceAndPath("skriptclient", "log/error.png");

    private final LogEntry logEntry;
    private final LogTableSizeContext sizeContext;
    private int color = 0xFFFFFFFF;

    protected LogTableEntry(LogEntry logEntry, LogTableSizeContext sizeContext) {
        super(Sizing.expand(), Sizing.content(), Algorithm.HORIZONTAL);
        this.logEntry = logEntry;
        this.sizeContext = sizeContext;

        // config
        gap(sizeContext.horizontalGap());
        int halfGap = sizeContext.horizontalGap() / 2;
        int fullGap = sizeContext.horizontalGap();
        List<Integer> xSeparators = List.of(
                12 + halfGap,                                              // after icon
                12 + fullGap + sizeContext.lineStringWidth() + halfGap,       // after line
                12 + fullGap + sizeContext.lineStringWidth() + fullGap + sizeContext.messageWidth() + halfGap // after message
        );

        this.surface(new ColumnSeparatorSurface(xSeparators, sizeContext.verticalGap(), color)); // white lines

        /* children */
        TextureComponent texture = Components.texture(getResourceLocation(), 0, 0, 12, 12, 12, 12);
        texture.tooltip(Component.literal(logEntry.getType().name().toLowerCase()));

        texture.mouseDown().subscribe((mouseX, mouseY, button) -> {
            if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;



            return true;
        });

        child(texture);

        // "Line"
        LabelComponent lineLabel = (LabelComponent) Components.label(Component.literal(String.valueOf(logEntry.getLine())))
                .horizontalSizing(Sizing.fixed(sizeContext.lineStringWidth()));
        child(lineLabel);

        // "Message"
        LabelComponent messageLabel = (LabelComponent) Components.label(Component.literal(logEntry.getMessage()))
                .horizontalSizing(Sizing.fixed(sizeContext.messageWidth()));
        child(messageLabel);

        // "Script"
        LabelComponent scriptLabel = (LabelComponent) Components.label(Component.literal(logEntry.getScript().getName()))
                .horizontalSizing(Sizing.fixed(sizeContext.scriptWidth()));
        child(scriptLabel);


    }


    protected ResourceLocation getResourceLocation() {
        return switch (logEntry.getType()) {
            case DEBUG -> DEBUG;
            case INFO -> INFORMATION;
            case WARNING -> WARNING;
            case ERROR -> ERROR;
        };
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return sizeContext.totalWidth();
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }
}
