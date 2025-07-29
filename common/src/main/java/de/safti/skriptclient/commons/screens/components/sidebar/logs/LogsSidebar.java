package de.safti.skriptclient.commons.screens.components.sidebar.logs;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.logging.StoringLogRecipient;
import de.safti.skriptclient.commons.screens.components.sidebar.Sidebar;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class LogsSidebar extends Sidebar {
    @Nullable
    private ScrollContainer<FlowLayout> scrollContainer;

    public LogsSidebar(ScriptManagementScreen screen) {
        super(Sizing.content(10), Sizing.expand());
        var logs = StoringLogRecipient.INSTANCE.getLogs();

        // configuration
        surface(Surface.DARK_PANEL);
        padding(Insets.of(2, 0, 2, 0));
        gap(2);

        // children/title
        LabelComponent titleComponent = Components.label(Component.literal("Logs"));
        titleComponent.margins(Insets.bottom(4));
        child(titleComponent);

        // logs
        List<ScriptLogEntryComponent> logComponents = logs
                .stream()
                .map(entry -> new ScriptLogEntryComponent(screen, entry))
                .toList();


        // create child layout
        FlowLayout scrollContainerChild = Containers.verticalFlow(Sizing.content(), Sizing.expand());
        scrollContainerChild.children(logComponents);
        scrollContainerChild.gap(2);


        // create and place scrollContainer
        scrollContainer = Containers.verticalScroll(Sizing.content(), Sizing.expand(), scrollContainerChild);
        child(scrollContainer);

    }
}
