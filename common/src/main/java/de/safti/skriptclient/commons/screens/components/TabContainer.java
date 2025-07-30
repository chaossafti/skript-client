package de.safti.skriptclient.commons.screens.components;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.commons.screens.components.tabs.TabWidget;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;

public class TabContainer extends FlowLayout {

    @Nullable
    private TabWidget openTab;
    private ScriptManagementScreen screen;

    public TabContainer() {
        super(Sizing.fill(), Sizing.fixed(20), Algorithm.HORIZONTAL);
        surface(Surface.DARK_PANEL);
    }

    public void updateScreen(ScriptManagementScreen screen) {
        this.screen = screen;
    }

    public void addTabWidget(TabWidget tab) {
        tab.setOwner(this);

        child(tab);
        open(tab);
    }

    public void open(@NotNull TabWidget tab) {
        if(!children().contains(tab)) throw new IllegalArgumentException("Tab is not added to the tab container!");

        if(openTab != null) openTab.deselect();
        openTab = tab;
        tab.open(screen);
    }


    public void notifyDestruction(TabWidget tab) {
        removeChild(tab);
        if(tab == openTab) {
            openTab = !children().isEmpty() ? (TabWidget) children().getFirst() : null;
        }
        screen.setMainArea(null);
    }


    public Optional<TabWidget> getTab(Path path) {
        return children().stream()
                .map(component -> (TabWidget) component)
                .filter(tabWidget -> tabWidget.getPath().toAbsolutePath().equals(path.toAbsolutePath()))
                .findFirst();
    }
}
