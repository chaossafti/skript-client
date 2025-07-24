package de.safti.skriptclient.screens.components;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;

public class TabContainer extends FlowLayout {

    public TabContainer() {
        super(Sizing.fill(88), Sizing.fixed(20), Algorithm.HORIZONTAL);

        surface(Surface.DARK_PANEL);

    }
}
