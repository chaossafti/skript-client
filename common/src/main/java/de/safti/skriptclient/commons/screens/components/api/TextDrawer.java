package de.safti.skriptclient.commons.screens.components.api;

import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.gui.Font;

public interface TextDrawer {
    int TAB_SIZE = 4;

    TextDrawer LINE_WRAPPING = (context, font, deltaX, x, y, maxWidth, height, text) -> {
        int lineHeight = font.lineHeight;

        int cursorX = x, cursorY = y;
        int widthAccum = deltaX;

        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            int charWidth = font.width(ch);

            if (widthAccum + charWidth > maxWidth) {
                widthAccum = deltaX;
                y += lineHeight;

                cursorX = x;
                cursorY = y;
            }

            if(ch.equals("\n") || ch.equals("\r")) {
                widthAccum = deltaX;
                y += lineHeight;

                cursorX = x;
                cursorY = y;
                continue;
            }

            if(ch.equals("\t")) {
                widthAccum += font.width(" ")*TAB_SIZE;

                cursorX += font.width(" ")*TAB_SIZE;
                continue;
            }


            context.drawString(font, ch, x + widthAccum, y, 0xFFFFFF);
            widthAccum += charWidth;
            cursorX += charWidth;
        }


        // Cursor blinking
        long now = System.currentTimeMillis();
        if (now % 1000 < 500) {
            context.fill(cursorX + deltaX, cursorY, cursorX + deltaX + 1, cursorY + font.lineHeight, 0xFFFFFFFF);
        }

    };


    void drawTextAndCursor(OwoUIDrawContext context, Font font, int deltaX, int x, int y, int width, int height, String text);


}
