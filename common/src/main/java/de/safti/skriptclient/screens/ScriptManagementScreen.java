package de.safti.skriptclient.screens;

import de.safti.skriptclient.screens.components.api.EditableTextView;
import de.safti.skriptclient.screens.components.TabContainer;
import de.safti.skriptclient.screens.components.tools.ToolBar;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import org.jetbrains.annotations.NotNull;

public class ScriptManagementScreen extends BaseOwoScreen<FlowLayout> {
    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::horizontalFlow);
    }

    @Override
    protected void build(FlowLayout root) {
        // Toolbar
        root.child(new ToolBar());

        root.padding(Insets.of(4, 4, 4, 4));


        // container for the editor and tab area
        FlowLayout mainWindowContainer = Containers.verticalFlow(Sizing.fill(), Sizing.fill());


        // add tab container
        TabContainer tabContainer = new TabContainer();
        mainWindowContainer.child(tabContainer);

        // add script editor
        EditableTextView editableTextView = new EditableTextView(Sizing.fill(), Sizing.fill());

        // the layout housing the script editor
        FlowLayout editorLayout = Containers.horizontalFlow(Sizing.fill(), Sizing.fill());

        // configure
        editorLayout.surface(Surface.DARK_PANEL);

        // add children
        editorLayout.child(editableTextView);


        mainWindowContainer.child(editorLayout);
        root.child(mainWindowContainer);
    }
}
