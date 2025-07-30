package de.safti.skriptclient.commons.screens.components.sidebar.storage;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.commons.screens.components.api.ScalableLabelComponent;
import de.safti.skriptclient.commons.screens.components.tabs.TabWidget;
import de.safti.skriptclient.utils.OwoUIUtils;
import de.safti.skriptclient.utils.PathUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.net.StandardProtocolFamily;
import java.util.Optional;

public class FileComponent extends FlowLayout {
    private final PathNode path;


    protected FileComponent(ScriptManagementScreen screen, PathNode node, int depth) {
        super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);
        this.path = node;
        id("File");

        padding(Insets.of(0, 0, depth, 0));

        // events
        mouseDown().subscribe((mouseX, mouseY, button) -> {
            if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            Optional<TabWidget> tabOpt = screen.getTabContainer().getTab(node.path());
            tabOpt.ifPresent(tab -> screen.getTabContainer().open(tab));

            if(tabOpt.isEmpty()) {
                TabWidget tab = new TabWidget(node.path());
                screen.addTab(tab);
            }


            return true;
        });


        // label
        LabelComponent label = new ScalableLabelComponent(Component.literal(node.fullFileName()), 0.5f);
        allowOverflow(true);

        // config
        OwoUIUtils.hoverEffect(label, this);

        // children
        String ext = PathUtils.getExtension(node.path());
        ResourceLocation location = TabWidget.iconOfFileExtension(ext);
        TextureComponent textureComponent = Components.texture(location, 0, 0, 8, 8, 8, 8);
        child(textureComponent);
        child(label);
    }

    public PathNode getNode() {
        return path;
    }
}
