package de.safti.skriptclient.commons.screens.components.tabs;

import de.safti.skriptclient.utils.PathUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TabWidget extends FlowLayout {
    private static final Map<String, ResourceLocation> FILE_EXTENSION_RESOURCES = new HashMap<>();
    private final Path path;


    public TabWidget(Path path) {
        this(iconOfFileExtension(PathUtils.getExtension(path)), path.toAbsolutePath().toFile().getName(), path);
    }


    public TabWidget(ResourceLocation icon, String tabName, Path path) {
        super(Sizing.fill(20), Sizing.content(), Algorithm.HORIZONTAL);
        this.path = path;

        TextureComponent texture = Components.texture(icon, 0, 0, 16, 16, 16, 16);
        this.child(texture);
        this.child(Components.label(Component.literal(tabName)));
    }


    @Override
    public void drawFocusHighlight(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.drawFocusHighlight(context, mouseX, mouseY, partialTicks, delta);
    }

    public static ResourceLocation iconOfFileExtension(String ext) {
        return FILE_EXTENSION_RESOURCES.computeIfAbsent(ext, s -> ResourceLocation.fromNamespaceAndPath("skriptclient", "extensions/" + ext + ".png"));
    }

    public Path getPath() {
        return path;
    }
}
