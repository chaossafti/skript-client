package de.safti.skriptclient.screens.components.api;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.util.HashMap;
import java.util.Map;

public class EditableTextView extends BaseComponent implements TextEditorAccess {
    private final StringBuilder text = new StringBuilder();
    private final Font font = Minecraft.getInstance().font;
    private final Map<Long, KeyShortcut> registeredShortcuts = new HashMap<>();

    private int cursorPos = 0;
    private int selectionStart, selectionEnd;
    private boolean cursorVisible = true;
    private long lastBlink = System.currentTimeMillis();

    public EditableTextView(Sizing horizontalSizing, Sizing verticalSizing) {
        this.sizing(horizontalSizing, verticalSizing);

        // listeners
        this.charTyped().subscribe((chr, modifiers) -> {
            appendAtCursor(chr + "");
            return true;
        });

        this.keyPress().subscribe((keyCode, scanCode, modifiers) -> {
            long merged = ((long) keyCode << 32) | (modifiers & 0xFFFFFFFFL);
            KeyShortcut keyShortcut = registeredShortcuts.get(merged);

            if(keyShortcut == null) return false;
            assert keyShortcut.listener() != null;
            keyShortcut.listener().accept(this);
            return true;
        });

        // register default shortcuts
        registerShortcut(KeyShortcut.BACKSPACE);
        registerShortcut(KeyShortcut.CTR_BACKSPACE);

    }

    protected void registerShortcut(KeyShortcut shortcut) {
        registeredShortcuts.put(shortcut.toLong(), shortcut);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        // create a background square
        int bgColor = 0xFF1E1F22; // opaque RGB(30,31,34)
        context.fill(this.x(), this.y(), this.x() + this.width(), this.y() + this.height(), bgColor);


        // fixme: line wrapping not quite working
        // line wrapping
        int lineHeight = font.lineHeight;
        int maxWidth = this.width();
        int x = this.x();
        int y = this.y();

        int cursorX = x, cursorY = y;
        int widthAccum = 0;

        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            int charWidth = font.width(ch);

            if (widthAccum + charWidth > maxWidth) {
                widthAccum = 0;
                y += lineHeight;
            }

            if (i == cursorPos) {
                cursorX = x + widthAccum;
                cursorY = y;
            }

            context.drawString(font, ch, x + widthAccum, y, 0xFFFFFF);
            widthAccum += charWidth;
        }

        if (cursorPos == text.length()) {
            if (widthAccum + 1 > maxWidth) {
                cursorX = x;
                cursorY = y + lineHeight;
            } else {
                cursorX = x + widthAccum;
                cursorY = y;
            }
        }

        // Cursor blinking
        long now = System.currentTimeMillis();
        if (now - lastBlink > 500) {
            cursorVisible = !cursorVisible;
            lastBlink = now;
        }

        if (cursorVisible) {
            cursorX = this.x() + font.width(text.substring(0, Math.min(cursorPos, text.length())));
            cursorY = this.y();
            context.fill(cursorX, cursorY, cursorX + 1, cursorY + font.lineHeight, 0xFFFFFFFF);
        }
    }


    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.width;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        int maxWidth = this.width();
        int widthAccum = 0;
        int lines = 1;

        for (int i = 0; i < text.length(); i++) {
            int charWidth = font.width(String.valueOf(text.charAt(i)));
            if (widthAccum + charWidth > maxWidth) {
                lines++;
                widthAccum = 0;
            }
            widthAccum += charWidth;
        }

        return lines * font.lineHeight;
    }

    public String getText() {
        return text.toString();
    }

    public void setText(String newText) {
        text.setLength(0);
        text.append(newText);
    }

    // TODO: selection

    @Override
    public int getSelectionStart() {
        return selectionStart;
    }

    @Override
    public int getSelectionEnd() {
        return selectionEnd;
    }

    @Override
    public void setSelection(int start, int end) {
        selectionStart = start;
        selectionEnd = end;
    }

    @Override
    public int getCursorPosition() {
        return cursorPos;
    }

    @Override
    public void setCursorPosition(int pos) {
        cursorPos = pos;
    }

    @Override
    public StringBuilder getContent() {
        return text;
    }

    @Override
    public void setAll(String str) {
        text.setLength(0);
        text.append(str);
    }
}