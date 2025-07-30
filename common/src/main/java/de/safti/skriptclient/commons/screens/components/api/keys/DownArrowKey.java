package de.safti.skriptclient.commons.screens.components.api.keys;

import de.safti.skriptclient.commons.screens.components.api.TextEditorAccess;

import java.util.function.Consumer;

public class DownArrowKey implements Consumer<TextEditorAccess> {
    @Override
    public void accept(TextEditorAccess access) {
        int caretPos = access.getCaretPosition();
        StringBuilder text = access.getContent();
        int textLength = text.length();

        // Find current line start and end
        int lineStart = text.lastIndexOf("\n", caretPos - 1);
        int lineEnd = text.indexOf("\n", caretPos);
        if (lineEnd == -1) lineEnd = textLength;  // end of text

        // Find next line start and end
        if (lineEnd == textLength) {
            // Already on the last line, can't move down
            return;
        }
        int nextLineStart = lineEnd + 1;
        int nextLineEnd = text.indexOf("\n", nextLineStart);
        if (nextLineEnd == -1) nextLineEnd = textLength;

        // Calculate caret column (zero-based) on current line
        int col = caretPos - lineStart - 1;

        // Calculate new caret position on next line, clamped to next line length
        int nextLineLength = nextLineEnd - nextLineStart;
        int targetPos = nextLineStart + Math.min(col, nextLineLength);

        access.setCaretPosition(targetPos);
    }
}
