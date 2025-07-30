package de.safti.skriptclient.commons.screens.components.main.file;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class LineColumn extends FlowLayout {
    private final FileMainArea fileMainArea;

    protected LineColumn(FileMainArea fileMainArea) {
        super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);
        this.fileMainArea = fileMainArea;
        this.gap(FileMainArea.LINE_GAP);

        // config
        surface((context, component) -> {
            context.fill(component.x() + component.width(), component.y(), component.x() + component.width() - 1, component.y() + component.height(), 0xFF777777);
        });

        this.updateLineLabels(); // Initial setup
    }

    public void onTextChange(int index, String str, StringBuilder text) {
        this.updateLineLabels();
    }

    private void updateLineLabels() {
        int targetLines = fileMainArea.getLineCount();
        int currentLines = this.children().size();

        // Add new labels if needed
        if (currentLines < targetLines) {
            for (int i = currentLines; i < targetLines; i++) {
                LabelComponent lineLabel = Components.label(Component.literal(String.valueOf(i + 1)));
                this.child(lineLabel);
            }
        }

        // Remove excess labels
        if (currentLines > targetLines) {
            // removeChild requires reference to actual component
            for (int i = currentLines - 1; i >= targetLines; i--) {
                this.removeChild(this.children().get(i));
            }
        }

        // Update existing label text (e.g., if lines were reordered)
        for (int i = 0; i < targetLines; i++) {
            io.wispforest.owo.ui.core.Component child = this.children().get(i);
            if (child instanceof LabelComponent label) {
                label.text(Component.literal(String.valueOf(i + 1)));
            }
        }
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        Insets margins = this.margins.get();
        return (int) (FileMainArea.FONT_SCALE * (fileMainArea.getLineCount() * (FileMainArea.LINE_HEIGHT + FileMainArea.LINE_GAP))) + margins.left() + margins.right();
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return children().stream()
                .map(component -> (LabelComponent) component)
                .mapToInt(BaseComponent::width)
                .max().orElse(0)
                + Minecraft.getInstance().font.width("a"); // add a buffer
    }
}