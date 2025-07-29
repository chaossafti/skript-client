package de.safti.skriptclient.commons.screens.components.sidebar;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Sidebar extends FlowLayout {

    public Sidebar() {
        super(Sizing.fill(30), Sizing.fill(), Algorithm.VERTICAL);
    }

    public Sidebar(Sizing horizontal, Sizing vertical) {
        super(horizontal, vertical, Algorithm.VERTICAL);
    }

    public void close() {

    }

}
