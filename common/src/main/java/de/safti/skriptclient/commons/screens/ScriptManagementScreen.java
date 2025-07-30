package de.safti.skriptclient.commons.screens;

import de.safti.skriptclient.commons.screens.components.TabContainer;
import de.safti.skriptclient.commons.screens.components.api.SimpleFlowLayout;
import de.safti.skriptclient.commons.screens.components.main.AbstractMainArea;
import de.safti.skriptclient.commons.screens.components.sidebar.Sidebar;
import de.safti.skriptclient.commons.screens.components.tabs.TabWidget;
import de.safti.skriptclient.commons.screens.components.tools.ToolBar;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptManagementScreen extends BaseOwoScreen<FlowLayout> {
    private static final Logger log = LoggerFactory.getLogger(ScriptManagementScreen.class);
    @Nullable
    private Sidebar activeSidebar;
    private TabContainer tabContainer;
    private FlowLayout mainScreenLayout;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, (sizing, sizing2) -> new SimpleFlowLayout(sizing, sizing2, FlowLayout.Algorithm.HORIZONTAL));
    }

    @Override
    protected void build(FlowLayout root) {
        // Toolbar
        root.child(new ToolBar(this));

        root.padding(Insets.of(4, 4, 4, 4));


        // container for the editor and tab area
        FlowLayout mainWindowContainer = Containers.verticalFlow(Sizing.expand(), Sizing.expand());
        mainWindowContainer.gap(1);

        // add tab container
        tabContainer = new TabContainer();
        mainWindowContainer.child(tabContainer);

        // add script editor
        mainScreenLayout = Containers.verticalFlow(Sizing.expand(), Sizing.expand());
        mainScreenLayout.surface(Surface.DARK_PANEL);

        // the layout housing the script editor
        FlowLayout editorLayout = Containers.horizontalFlow(Sizing.expand(), Sizing.expand());

        // configure
        editorLayout.surface(Surface.DARK_PANEL);

        // add children
        editorLayout.child(mainScreenLayout);


        mainWindowContainer.child(editorLayout);
        root.child(mainWindowContainer);
    }

    public TabContainer getTabContainer() {
        return tabContainer;
    }

    public void addTab(@NotNull TabWidget tabWidget) {
        tabContainer.updateScreen(this);
        tabContainer.addTabWidget(tabWidget);
    }

    public void setSideBar(@Nullable Sidebar sideBar) {
        FlowLayout root = uiAdapter.rootComponent;
        if(activeSidebar != null) root.removeChild(activeSidebar);

        activeSidebar = sideBar;
        if(sideBar != null) {
            root.child(1, activeSidebar);
        }
    }

    public void setMainArea(AbstractMainArea mainArea) {
        mainScreenLayout.clearChildren();
        if(mainArea != null) {
            mainScreenLayout.child(mainArea);
        }
    }


    public Sidebar getSideBar() {
        return activeSidebar;
    }
}
