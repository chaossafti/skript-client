package de.safti.skriptclient.commons.screens.components.tabs;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.commons.screens.components.TabContainer;
import de.safti.skriptclient.commons.screens.components.api.EditableTextView;
import de.safti.skriptclient.commons.screens.components.api.ScalableLabelComponent;
import de.safti.skriptclient.commons.screens.components.main.file.FileMainArea;
import de.safti.skriptclient.utils.PathUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.util.FocusHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TabWidget extends FlowLayout {
    private static final Map<String, ResourceLocation> FILE_EXTENSION_RESOURCES = new HashMap<>();

    private final Path path;
    private boolean isSelected;
    private FileMainArea mainArea;
    private TabContainer tabContainer;


    public TabWidget(Path path) {
        this(iconOfFileExtension(PathUtils.getExtension(path)), path.toAbsolutePath().toFile().getName(), path);
    }


    public TabWidget(ResourceLocation icon, String tabName, Path path) {
        super(Sizing.content(1), Sizing.content(), Algorithm.HORIZONTAL);
        this.path = path;


        // events
        mouseDown().subscribe((mouseX, mouseY, button) -> {
           if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

           tabContainer.open(this);

            return true;
        });


        // config
        padding(Insets.of(4, 0, 2, 6));

        // children
        TextureComponent texture = Components.texture(icon, 0, 0, 12, 12, 12, 12);
        this.child(texture);
        ScalableLabelComponent label = new ScalableLabelComponent(Component.literal(tabName), 0.6f);
        this.child(label);

        ScalableLabelComponent closeLabel = new ScalableLabelComponent(Component.literal("X"), 0.4f);
        closeLabel.mouseDown().subscribe((mouseX, mouseY, button) -> {
            if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            tabContainer.notifyDestruction(this);
            return true;
        });
        closeLabel.tooltip(Component.literal("Close"));
        closeLabel.margins(Insets.left(2));
        this.child(closeLabel);
    }

    public void setOwner(TabContainer container) {
        this.tabContainer = container;
    }

    public void open(ScriptManagementScreen screen) {
        if(isSelected) return;

        isSelected = true;
        if(mainArea == null) {
            mainArea = new FileMainArea(screen, path);
        }

        screen.setMainArea(mainArea);
        if(mainArea.focusHandler() != null) {
            mainArea.focusHandler().focus(mainArea.children().getFirst(), FocusSource.MOUSE_CLICK);
        }
    }

    public void deselect() {
        isSelected = false;
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(context, mouseX, mouseY, partialTicks, delta);

        if (isSelected) {
            int lineY = this.y + this.height - 1; // 1 pixel above the bottom edge
            context.fill(this.x, lineY, this.x + this.width, lineY + 2, 0xFF375FAD); // solid blue line (ARGB)
        }

    }

    public static ResourceLocation iconOfFileExtension(String ext) {
        return FILE_EXTENSION_RESOURCES.computeIfAbsent(ext, s -> ResourceLocation.fromNamespaceAndPath("skriptclient", "extensions/" + ext + ".png"));
    }

    public Path getPath() {
        return path;
    }
}
