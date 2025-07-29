package de.safti.skriptclient.utils;

import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Surface;

public class OwoUIUtils {

    private static final Surface HOVER_SURFACE = (context, component) -> {
        context.drawGradientRect(
                component.x(), component.y(), component.width(), component.height(),
                0x33FFFFFF, 0x33FFFFFF, 0x33FFFFFF, 0x33FFFFFF);
    };

    public static void hoverEffect(BaseParentComponent surfaceComponent) {
        hoverEffect(surfaceComponent, surfaceComponent);
    }

    public static void hoverEffect(Component hoverComponent, BaseParentComponent surfaceComponent) {
        hoverComponent.mouseEnter().subscribe(() -> {
            surfaceComponent.surface(HOVER_SURFACE);
        });

        hoverComponent.mouseLeave().subscribe(() -> surfaceComponent.surface(Surface.BLANK));
    }

}
