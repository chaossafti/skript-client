package de.safti.skriptclient.commons.screens.components.sidebar.storage;

import de.safti.skriptclient.commons.screens.components.api.DynamicLabelComponent;
import de.safti.skriptclient.commons.screens.components.tabs.TabWidget;
import de.safti.skriptclient.utils.OwoUIUtils;
import de.safti.skriptclient.utils.PathUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FileComponent extends FlowLayout {


    private final PathNode path;


    protected FileComponent(PathNode node, int depth) {
        super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);
        this.path = node;
        id("File");

        padding(Insets.of(0, 0, depth, 0));

        // texture
        String ext = PathUtils.getExtension(node.path());
        ResourceLocation location = TabWidget.iconOfFileExtension(ext);
        TextureComponent textureComponent = Components.texture(location, 0, 0, 8, 8, 8, 8);

        // label
        LabelComponent label = new DynamicLabelComponent(Component.literal(node.fullFileName()), 0.3f);
        allowOverflow(true);

        OwoUIUtils.hoverEffect(label, this);

        child(textureComponent);
        child(label);
    }

    public PathNode getNode() {
        return path;
    }
}
