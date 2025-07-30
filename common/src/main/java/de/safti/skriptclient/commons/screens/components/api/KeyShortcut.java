package de.safti.skriptclient.commons.screens.components.api;

import de.safti.skriptclient.commons.screens.components.api.keys.DownArrowKey;
import de.safti.skriptclient.commons.screens.components.api.keys.UpArrowKey;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public record KeyShortcut(int key, int modifiers, @Nullable Consumer<TextEditorAccess> listener) {

    public static final KeyShortcut BACKSPACE = new KeyShortcut(GLFW.GLFW_KEY_BACKSPACE, 0, TextEditorAccess::popCharAtCursor);
    public static final KeyShortcut CTRL_BACKSPACE = new KeyShortcut(GLFW.GLFW_KEY_BACKSPACE, GLFW.GLFW_MOD_CONTROL, access -> {
        StringBuilder content = access.getContent();
        int caret = access.getCaretPosition();

        // Step 1: skip non-word characters first (whitespace, punctuation)
        int pos = caret - 1;
        while (pos >= 0 && !Character.isLetterOrDigit(content.charAt(pos))) {
            pos--;
        }

        // Step 2: skip word characters
        while (pos >= 0 && Character.isLetterOrDigit(content.charAt(pos))) {
            pos--;
        }

        int distance = caret - (pos + 1);
        access.popAtCursor(distance);
    });
    public static final KeyShortcut ENTER = new KeyShortcut(GLFW.GLFW_KEY_ENTER, 0, access -> access.appendAtCaret("\n"));
    public static final KeyShortcut TAB = new KeyShortcut(GLFW.GLFW_KEY_TAB, 0, access -> access.appendAtCaret("\t"));

    public static final KeyShortcut RIGHT_ARROW = new KeyShortcut(GLFW.GLFW_KEY_RIGHT, 0, access -> {
        access.moveCaret(1);
        access.setLastBlink(System.currentTimeMillis() - 250);
    });
    public static final KeyShortcut LEFT_ARROW = new KeyShortcut(GLFW.GLFW_KEY_LEFT, 0, access -> {
        access.moveCaret(-1);
        access.setLastBlink(System.currentTimeMillis() - 250);
    });

    public static final KeyShortcut UP_ARROW = new KeyShortcut(GLFW.GLFW_KEY_UP, 0, new UpArrowKey());
    public static final KeyShortcut DOWN_ARROW = new KeyShortcut(GLFW.GLFW_KEY_DOWN, 0, new DownArrowKey());

    public static final KeyShortcut CTRL_LEFT = new KeyShortcut(GLFW.GLFW_KEY_LEFT, GLFW.GLFW_MOD_CONTROL, access -> {
        StringBuilder content = access.getContent();
        int caret = access.getCaretPosition();

        if (caret <= 0) return;

        int pos = caret - 1;

        if (content.charAt(pos) == '\t') {
            access.setCaretPosition(pos);
            return;
        }

        while (pos >= 0 && !Character.isLetterOrDigit(content.charAt(pos))) {
            if (content.charAt(pos) == '\t') {
                access.setCaretPosition(pos);
                return;
            }
            pos--;
        }

        // Step 2: Skip the word characters
        while (pos >= 0 && Character.isLetterOrDigit(content.charAt(pos))) {
            pos--;
        }

        access.setCaretPosition(pos + 1);
    });

    public static final KeyShortcut CTRL_RIGHT = new KeyShortcut(GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_MOD_CONTROL, access -> {
        StringBuilder content = access.getContent();
        int caret = access.getCaretPosition();
        int length = content.length();
        int pos = caret;

        // Step 1: Skip any non-word characters (punctuation, whitespace, etc.)
        while (pos < length && !Character.isLetterOrDigit(content.charAt(pos))) {
            pos++;
        }

        // Step 2: Skip over the word
        while (pos < length && Character.isLetterOrDigit(content.charAt(pos))) {
            pos++;
        }

        access.setCaretPosition(pos);
    });


    public boolean matches(int keyCode, int actualModifiers) {
        return this.key == keyCode && this.modifiers == (actualModifiers & (GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT | GLFW.GLFW_MOD_SHIFT));
    }

    public static Builder builder() {
        return new Builder();
    }

    public long toLong() {
        return ((long) key << 32) | (modifiers & 0xFFFFFFFFL);
    }

    public static int getKey(long value) {
        return (int) (value >>> 32);
    }

    public static int getModifiers(long value) {
        return (int) value;
    }

     public static class Builder {
        private boolean ctrl, alt, shift;
        private int baseKey;
        private Consumer<TextEditorAccess> listener;

        public Builder ctrl(boolean ctrl) {
            this.ctrl = ctrl;
            return this;
        }

        public Builder alt(boolean alt) {
            this.alt = alt;
            return this;
        }

        public Builder shift(boolean shift) {
            this.shift = shift;
            return this;
        }

        public Builder key(int key) {
            this.baseKey = key;
            return this;
        }

        public Builder key(char chr) {
            if(chr >= 'a' && chr <= 'z') {
                this.baseKey = GLFW.GLFW_KEY_A + (chr - 'a');
            } else if(chr >= 'A' && chr <= 'Z') {
                this.baseKey = GLFW.GLFW_KEY_A + (chr - 'A');
                this.shift = true;
            } else {
                throw new IllegalArgumentException("Unsupported character: " + chr);
            }
            return this;
        }

        public Builder listener(Consumer<TextEditorAccess> listener) {
            this.listener = listener;
            return this;
        }

        public KeyShortcut build() {
            int mods = 0;
            if(ctrl) mods |= GLFW.GLFW_MOD_CONTROL;
            if(alt) mods |= GLFW.GLFW_MOD_ALT;
            if(shift) mods |= GLFW.GLFW_MOD_SHIFT;
            return new KeyShortcut(baseKey, mods, listener);
        }
    }
}