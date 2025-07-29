package de.safti.skriptclient.commons.screens.components.sidebar.logs;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.commons.screens.components.main.log.LogMainArea;
import de.safti.skriptclient.logging.StoringLogRecipient.ScriptLoadLogEntry;
import de.safti.skriptclient.utils.OwoUIUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class ScriptLogEntryComponent extends FlowLayout {
    private final ScriptManagementScreen screen;
    private final ScriptLoadLogEntry logEntry;
    private final LabelComponent label;

    @Nullable
    private LogMainArea logArea;

    protected ScriptLogEntryComponent(ScriptManagementScreen screen, ScriptLoadLogEntry logEntry) {
        super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);
        this.screen = screen;
        this.logEntry = logEntry;

        // collect loaded scripts
        String[] loadedScripts = logEntry.loadResults()
                .stream()
                .map(loadResult -> loadResult.getScript().getPath().toFile().getName())
                .toArray(String[]::new);

        // create name
        String name;
        if(loadedScripts.length > 1) {
            name = "%d scripts".formatted(loadedScripts.length);
        } else {
            name = loadedScripts[0];
        }

        // config
        margins(Insets.left(4));

        // children
        label = Components.label(Component.literal(name));
        child(label);
        OwoUIUtils.hoverEffect(label, this);


        // tooltip
        Component tooltipComponent = Component
                .literal(String.join(", ", loadedScripts))
                .append("\nErrors: " + logEntry.getTotalErrors())
                .append("\nWarnings: " + logEntry.getTotalWarnings());

        tooltip(tooltipComponent);

        // events
        // TODO: right click to select. Offer log options (delete...)
        mouseDown().subscribe((mouseX, mouseY, button) -> {
            if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            if(logArea == null) logArea = new LogMainArea(screen, this);
            screen.setMainArea(logArea);

            return true;
        });
    }

    public LabelComponent getLabel() {
        return label;
    }

    public ScriptLoadLogEntry getLogEntry() {
        return logEntry;
    }
}
