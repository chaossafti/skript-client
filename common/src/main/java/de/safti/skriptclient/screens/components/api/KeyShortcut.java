package de.safti.skriptclient.screens.components.api;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public record KeyShortcut(int key, int modifiers, @Nullable Consumer<TextEditorAccess> listener) {

    public static KeyShortcut BACKSPACE = new KeyShortcut(GLFW.GLFW_KEY_BACKSPACE, 0, TextEditorAccess::popCharAtCursor);
    public static KeyShortcut CTR_BACKSPACE = new KeyShortcut(GLFW.GLFW_KEY_BACKSPACE, GLFW.GLFW_MOD_CONTROL, access -> {
        int distance = access.distanceUntil(character -> !Character.isAlphabetic(character) && !Character.isDigit(character));
        access.popAtCursor(distance);
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