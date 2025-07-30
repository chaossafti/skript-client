package de.safti.skriptclient.commons.screens.components.api;

import de.safti.skriptclient.commons.screens.components.main.file.FileMainArea;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.util.HashMap;
import java.util.Map;

public class EditableTextView extends BaseComponent implements TextEditorAccess {
    private static final Font FONT = Minecraft.getInstance().font;
    private static final int START_X = 2;
    private static final int CHAR_COUNT_BUFFER = 3;

    private final Map<Long, KeyShortcut> registeredShortcuts = new HashMap<>();
    private final StringBuilder text = new StringBuilder();

    private int cursorPos = 0;
    private int selectionStart, selectionEnd;
    private long lastBlink;

    protected EventStream<Type> eventType = Type.newStream();



    /**
     * Creates an EditableTextView wrapped in 2 ScrollContainers to allow scrolling vertically and horizontally
     */
    public static BiScrollContainer<EditableTextView> scrollable(Sizing horizontalSizing, Sizing verticalSizing) {
        return new BiScrollContainer<>(verticalSizing, new EditableTextView(Sizing.content(), Sizing.content()));

    }

    public EditableTextView(Sizing horizontalSizing, Sizing verticalSizing) {
        this.sizing(horizontalSizing, verticalSizing);

        // listeners
        this.charTyped().subscribe((chr, modifiers) -> {
            appendAtCaret(chr + "");
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
        TextDrawer.IN_LINE.drawTextAndCursor(context, FONT, START_X, x, y, width, height, this);
    }

    public EventSource<Type> type() {
        return eventType.source();
    }


    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        int maxWidth = 0;
        int lineStart = 0;

        for (int i = 0; i <= text.length(); i++) {
            if (i == text.length() || text.charAt(i) == '\n') {
                String line = text.substring(lineStart, i);
                int width = Minecraft.getInstance().font.width(line);
                if (width > maxWidth) {
                    maxWidth = width;
                }
                lineStart = i + 1;
            }
        }

        return maxWidth;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        long lineCount = text.chars().filter(value -> value == '\n').count() + 1;
        return (int) ((int) lineCount * (FileMainArea.LINE_GAP + FileMainArea.LINE_HEIGHT) * FileMainArea.FONT_SCALE);
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

    @Override
    public void appendAtCaret(String str) {
        TextEditorAccess.super.appendAtCaret(str);
        eventType.sink().onType(getCaretPosition(), str);
    }

    public interface Type {

        void onType(int index, String str);

        static EventStream<Type> newStream() {
            return new EventStream<>(subscribers -> (index, str) -> {
                for (var subscriber : subscribers) {
                    subscriber.onType(index, str);
                }
            });
        }

    }

}