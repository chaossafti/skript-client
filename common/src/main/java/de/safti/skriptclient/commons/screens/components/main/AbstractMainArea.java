package de.safti.skriptclient.commons.screens.components.main;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.commons.screens.components.tabs.TabWidget;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;

public class AbstractMainArea extends FlowLayout {
    protected final ScriptManagementScreen screen;

    protected AbstractMainArea(ScriptManagementScreen screen, Sizing horizontalSizing, Sizing verticalSizing, Algorithm algorithm) {
        super(horizontalSizing, verticalSizing, algorithm);
        this.screen = screen;
    }

    public void onTabChange(TabWidget widget) {

    }

}
