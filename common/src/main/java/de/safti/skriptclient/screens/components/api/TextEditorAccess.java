package de.safti.skriptclient.screens.components.api;

import java.util.function.Predicate;

public interface TextEditorAccess {

    int getSelectionStart();

    int getSelectionEnd();

    void setSelection(int start, int end);

    int getCursorPosition();

    void setCursorPosition(int pos);


    StringBuilder getContent();

    default void append(String str) {
        getContent().append(str);
    }

    default void appendAtCursor(String str) {
        int cursorPos = getCursorPosition();

        getContent().insert(cursorPos, str);
        setCursorPosition(cursorPos + str.length());
    }

    void setAll(String str);

    default void popCharAtCursor() {
        int pos = getCursorPosition() - 1;
        if (pos < getContent().length() && pos >= 0) {
            getContent().deleteCharAt(pos);
            setCursorPosition(pos);
        }
    }

    default void popAtCursor(int amount) {
        int cursorPos = getCursorPosition();
        StringBuilder text = getContent();

        if (cursorPos < 0 || cursorPos > text.length()) {
            throw new IllegalArgumentException("Invalid cursor position");
        }

        // Start from cursor position and expand outward
        int left = cursorPos;
        int right = cursorPos;

        // Keep track of positions to remove
        boolean[] positionsToRemove = new boolean[text.length()];

        // Expand left until we hit a non-alphanumeric character
        while (left > 0 && Character.isLetterOrDigit(text.charAt(left - 1))) {
            positionsToRemove[left - 1] = true;
            left--;
        }

        // Add current position if alphanumeric
        if (cursorPos < text.length() && Character.isLetterOrDigit(text.charAt(cursorPos))) {
            positionsToRemove[cursorPos] = true;
        }

        // Expand right until we hit a non-alphanumeric character
        while (right < text.length() - 1 && Character.isLetterOrDigit(text.charAt(right + 1))) {
            positionsToRemove[right + 1] = true;
            right++;
        }

        // Remove characters at marked positions
        int offset = 0;
        for (int i = 0; i < text.length(); i++) {
            if (!positionsToRemove[i]) {
                text.setCharAt(i - offset, text.charAt(i));
            } else {
                offset++;
            }
        }

        // Trim the StringBuilder to remove trailing characters
        text.delete(text.length() - offset, text.length());
        setCursorPosition(Math.min(Math.max(0, cursorPos - offset), text.length()));
    }


    default int distanceUntil(Predicate<Character> checker) {
        StringBuilder content = getContent() ;
        int cursorPos = getCursorPosition() - 1;

        for (int i = cursorPos; i >= 0; i--) {
            if (checker.test(content.charAt(i))) {
                return i;
            }
        }

        for (int pos = cursorPos; pos >= 0; pos--) {
            if(checker.test(content.charAt(pos))) {
                return cursorPos - pos;
            }
        }


        return cursorPos;
    }


}
