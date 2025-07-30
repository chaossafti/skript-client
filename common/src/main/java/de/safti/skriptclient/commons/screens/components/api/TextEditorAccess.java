package de.safti.skriptclient.commons.screens.components.api;

import java.util.function.Predicate;

public interface TextEditorAccess {

    int getSelectionStart();

    int getSelectionEnd();

    void setSelection(int start, int end);

    int getCaretPosition();

    void setCaretPosition(int pos);

    default void moveCaret(int offset) {
        setCaretPosition(Math.clamp(getCaretPosition() + offset, 0, getContent().length()));
    }


    StringBuilder getContent();

    default void append(String str) {
        getContent().append(str);
    }

    default void appendAtCaret(String str) {
        int cursorPos = getCaretPosition();

        getContent().insert(cursorPos, str);
        setCaretPosition(cursorPos + str.length());
    }

    void setAll(String str);

    default void popCharAtCursor() {
        int pos = getCaretPosition() - 1;
        if (pos < getContent().length() && pos >= 0) {
            getContent().deleteCharAt(pos);
            setCaretPosition(pos);
        }
    }

    default void popAtCursor(int amount) {
        int cursorPos = getCaretPosition();
        StringBuilder text = getContent();

        if (amount <= 0 || cursorPos < amount || cursorPos > text.length()) return;

        text.delete(cursorPos - amount, cursorPos);
        setCaretPosition(cursorPos - amount);
    }


    /**
     * Calculates the distance from the current caret position to the first character (searching **leftward**)
     * that matches the given predicate.
     *
     * @param checker the predicate to test each character
     * @return the number of characters to move left until a match is found. If no match, returns caret position (i.e. jump to start)
     */
    default int distanceUntil(Predicate<Character> checker) {
        StringBuilder content = getContent();
        int cursorPos = getCaretPosition();

        for (int i = cursorPos - 1; i >= 0; i--) {
            if (checker.test(content.charAt(i))) {
                return cursorPos - i - 1;
            }
        }

        // Nothing matched: return distance to beginning
        return cursorPos;
    }

    /**
     * Calculates the distance from the current caret position to the next character (searching **rightward**)
     * that matches the given predicate.
     *
     * @param checker the predicate to test each character
     * @return the number of characters to move right until a match is found. If no match, returns distance to end
     */
    default int distanceUntilForward(Predicate<Character> checker) {
        StringBuilder content = getContent();
        int cursorPos = getCaretPosition();

        for (int i = cursorPos; i < content.length(); i++) {
            if (checker.test(content.charAt(i))) {
                return i - cursorPos;
            }
        }

        // Nothing matched: jump to end
        return content.length() - cursorPos;
    }


    long lastBlink();

    void setLastBlink(long time);


}
