package de.safti.skriptclient.commons.screens.components.api;

import com.mojang.blaze3d.vertex.PoseStack;
import de.safti.skriptclient.commons.screens.components.main.file.FileMainArea;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.gui.Font;

public interface TextDrawer {
    int TAB_SIZE = 4;

    TextDrawer LINE_WRAPPING = (context, textRenderer, deltaX, x, y, maxWidth, height, access) -> {
        int lineHeight = FileMainArea.LINE_HEIGHT;
        int lineSpacing = FileMainArea.LINE_GAP;
        float scale = FileMainArea.FONT_SCALE;
        StringBuilder text = access.getContent();

        int caretIndex = access.getCaretPosition();

        PoseStack matrices = context.getMatrixStack();
        matrices.pushPose();

        int pivotY = (int) (y + Math.ceil(textRenderer.lineHeight*scale));
        matrices.translate(x, pivotY, 0);
        matrices.scale(scale, scale, 1.0f);
        matrices.translate(-x, -pivotY, 0);

        int caretX = x, caretY = y; // will store the pixel location of the caret
        int widthAccum = deltaX;

        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            int charWidth = textRenderer.width(ch);

            // Line wrap
            if (widthAccum + charWidth > maxWidth) {
                widthAccum = deltaX;
                y += lineHeight + lineSpacing;
            }

            // Check caret position BEFORE continuing, so it points at the newline
            if (i == caretIndex) {
                caretX = x + widthAccum;
                caretY = y;
            }

            if (ch.equals("\r")) {
                // Don't render carriage returns
                continue;
            }

            // Handle newline
            if (ch.equals("\n")) {
                widthAccum = deltaX;
                y += lineHeight + lineSpacing;
                continue;
            }

            // Handle tab
            if (ch.equals("\t")) {
                int tabWidth = textRenderer.width(" ") * TAB_SIZE;
                widthAccum += tabWidth;
                continue;
            }

            // Draw char
            context.drawString(textRenderer, ch, x + widthAccum, y, 0xFFFFFF);
            widthAccum += charWidth;
        }

        // In case the caret is at the end of the string
        if (caretIndex == text.length()) {
            caretX = x + widthAccum;
            caretY = y;
        }

        // Blinking caret
        long now = System.currentTimeMillis();
        long elapsed = now - access.lastBlink();

        if (elapsed % 1000 < 500) {
            context.fill(caretX, caretY, caretX + 1, caretY + lineHeight, 0xFFFFFFFF);
        }
    };

    TextDrawer IN_LINE = (context, textRenderer, deltaX, x, y, maxWidth, height, access) -> {
        int lineHeight = FileMainArea.LINE_HEIGHT;
        int lineSpacing = FileMainArea.LINE_GAP;
        float scale = FileMainArea.FONT_SCALE;
        StringBuilder text = access.getContent();

        int caretIndex = access.getCaretPosition();

        PoseStack matrices = context.getMatrixStack();
        matrices.pushPose();

        int pivotY = (int) (y + Math.ceil(textRenderer.lineHeight * scale));
        matrices.translate(x, pivotY, 0);
        matrices.scale(scale, scale, 1.0f);
        matrices.translate(-x, -pivotY, 0);

        int caretX = x, caretY = y;
        int widthAccum = deltaX;

        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            int charWidth = textRenderer.width(ch);

            if (i == caretIndex) {
                caretX = x + widthAccum;
                caretY = y;
            }

            if (ch.equals("\r")) {
                continue; // skip carriage returns
            }

            if (ch.equals("\n")) {
                widthAccum = deltaX;
                y += lineHeight + lineSpacing;
                continue;
            }

            if (ch.equals("\t")) {
                int tabWidth = textRenderer.width(" ") * TAB_SIZE;
                widthAccum += tabWidth;
                continue;
            }

            context.drawString(textRenderer, ch, x + widthAccum, y, 0xFFFFFF);
            widthAccum += charWidth;
        }

        if (caretIndex == text.length()) {
            caretX = x + widthAccum;
            caretY = y;
        }

        long now = System.currentTimeMillis();
        long elapsed = now - access.lastBlink();

        if (elapsed % 1000 < 500) {
            context.fill(caretX, caretY, caretX + 1, caretY + lineHeight, 0xFFFFFFFF);
        }

        matrices.popPose();
    };


    void drawTextAndCursor(OwoUIDrawContext context, Font font, int deltaX, int x, int y, int width, int height, TextEditorAccess access);


}
