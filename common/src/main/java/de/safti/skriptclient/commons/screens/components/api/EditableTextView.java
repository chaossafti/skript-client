package de.safti.skriptclient.commons.screens.components.api;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.util.HashMap;
import java.util.Map;

public class EditableTextView extends BaseComponent implements TextEditorAccess {
    private static final Font FONT = Minecraft.getInstance().font;
    private static final int START_X = 2;
    private static final int CHAR_COUNT_BUFFER = 3;

    private final StringBuilder text = new StringBuilder();
    private final Map<Long, KeyShortcut> registeredShortcuts = new HashMap<>();

    private int cursorPos = 0;
    private int selectionStart, selectionEnd;
    private long lastBlink;

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
        registerShortcut(KeyShortcut.ENTER);
        registerShortcut(KeyShortcut.TAB);
        registerShortcut(KeyShortcut.BACKSPACE);
        registerShortcut(KeyShortcut.CTRL_BACKSPACE);
        registerShortcut(KeyShortcut.LEFT_ARROW);
        registerShortcut(KeyShortcut.RIGHT_ARROW);
        registerShortcut(KeyShortcut.DOWN_ARROW);
        registerShortcut(KeyShortcut.UP_ARROW);
        registerShortcut(KeyShortcut.CTRL_RIGHT);
        registerShortcut(KeyShortcut.CTRL_LEFT);

    }

    protected void registerShortcut(KeyShortcut shortcut) {
        registeredShortcuts.put(shortcut.toLong(), shortcut);
    }


    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        // create a background square
        //int bgColor = 0xFF1E1F22; // opaque RGB(30,31,34)
        //context.fill(this.x(), this.y(), this.x() + this.width(), this.y() + this.height(), bgColor);


        int width = this.width - FONT.width("a")*CHAR_COUNT_BUFFER;
        TextDrawer.LINE_WRAPPING.drawTextAndCursor(context, FONT, START_X, x, y, width, height, this);


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
            int charWidth = FONT.width(String.valueOf(text.charAt(i)));
            if (widthAccum + charWidth > maxWidth) {
                lines++;
                widthAccum = 0;
            }
            widthAccum += charWidth;
        }

        return lines * FONT.lineHeight;
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
    public int getCaretPosition() {
        return cursorPos;
    }

    @Override
    public void setCaretPosition(int pos) {
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

    @Override
    public long lastBlink() {
        return lastBlink;
    }

    @Override
    public void setLastBlink(long time) {
        this.lastBlink = time;
    }
}