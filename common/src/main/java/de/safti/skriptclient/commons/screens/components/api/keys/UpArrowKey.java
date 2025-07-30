package de.safti.skriptclient.commons.screens.components.api.keys;

import de.safti.skriptclient.commons.screens.components.api.TextEditorAccess;

import java.util.function.Consumer;

public class UpArrowKey implements Consumer<TextEditorAccess> {

    @Override
    public void accept(TextEditorAccess access) {
        int caretPos = access.getCaretPosition();
        StringBuilder text = access.getContent();

        // Find current caret line start and previous line start
        int lineStart = text.lastIndexOf("\n", caretPos - 1);
        if (lineStart == -1) {
            // Already on the first line, can't move up
            return;
        }
        int prevLineEnd = lineStart - 1;
        int prevLineStart = text.lastIndexOf("\n", prevLineEnd);

        int col = caretPos - lineStart - 1; // caret column (zero-based) in current line

        // Calculate target position in previous line
        int prevLineLength = lineStart - prevLineStart - 1;
        int targetPos = prevLineStart + 1 + Math.min(col, prevLineLength);

        access.setCaretPosition(targetPos);
    }

}
