package de.safti.skriptclient.commons.screens.components.api;

import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;

public class ALScrollContainer<C extends Component> extends ScrollContainer<C> {

    protected ALScrollContainer(ScrollDirection direction, Sizing horizontalSizing, Sizing verticalSizing, C child) {
        super(direction, horizontalSizing, verticalSizing, child);
    }

    @Override
    public void scrollBy(double offset, boolean instant, boolean showScrollbar) {
        super.scrollBy(offset, instant, showScrollbar);
    }
}
