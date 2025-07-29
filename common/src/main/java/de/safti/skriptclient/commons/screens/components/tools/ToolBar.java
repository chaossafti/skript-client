package de.safti.skriptclient.commons.screens.components.tools;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;

public class ToolBar extends FlowLayout {
    private final ScriptManagementScreen screen;

    public ToolBar(ScriptManagementScreen screen) {
        super(Sizing.fixed(50), Sizing.fill(100), Algorithm.VERTICAL);
        this.screen = screen;

        // initial values
        this.gap(4);
        this.surface(Surface.DARK_PANEL);

        // add children
        this.child(new StorageTool(this));
        this.child(new LogsTool(this));

    }

    public ScriptManagementScreen getScreen() {
        return screen;
    }
}
