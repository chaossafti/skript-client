package de.safti.skriptclient.commons.screens.components.api;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;

import java.awt.*;

/**
 * Removes the Tab focusing feature from FlowLayout
 */
public class SimpleFlowLayout extends FlowLayout {


    public SimpleFlowLayout(Sizing horizontalSizing, Sizing verticalSizing, Algorithm algorithm) {
        super(horizontalSizing, verticalSizing, algorithm);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (this.focusHandler == null) return false;

        if(focusHandler.focused() != null) {
            return this.focusHandler.focused().onKeyPress(keyCode, scanCode, modifiers);
        }

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }
}
