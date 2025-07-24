package de.safti.skriptclient.screens.components.tools;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;

public class ToolBar extends FlowLayout {
    public ToolBar() {
        super(Sizing.fixed(50), Sizing.fill(100), Algorithm.VERTICAL);

        // initial values
        this.gap(4);
        this.surface(Surface.DARK_PANEL);

        // add children
        this.child(new StorageTool(this));
        this.child(new LogsTool(this));

    }

}
