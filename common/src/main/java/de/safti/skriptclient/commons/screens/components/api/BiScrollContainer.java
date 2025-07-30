package de.safti.skriptclient.commons.screens.components.api;

import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.screens.Screen;

public class BiScrollContainer<C extends Component> extends ScrollContainer<ALScrollContainer<C>> {


    public BiScrollContainer(Sizing verticalSizing, C child) {
        super(ScrollDirection.VERTICAL, Sizing.content(), verticalSizing, new ALScrollContainer<>(ScrollDirection.HORIZONTAL, Sizing.expand(), Sizing.content(), child));

        scrollbar(Scrollbar.vanillaFlat());
        horizontalContainer().scrollbar(Scrollbar.vanillaFlat());
        child.mouseScroll().subscribe(this::onMouseScroll);

    }

    public ALScrollContainer<C> horizontalContainer() {
        return child();
    }

    public ScrollContainer<ALScrollContainer<C>> verticalContainer() {
        return this;
    }

    public C content() {
        return horizontalContainer().child();
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        if(Screen.hasShiftDown()) {
            if (this.scrollStep < 1) {
                horizontalContainer().scrollBy(-amount * 15, false, true);
            } else {
                horizontalContainer().scrollBy(-amount * this.scrollStep, true, true);
            }

        }
        else {
            if (this.scrollStep < 1) {
                this.scrollBy(-amount * 15, false, true);
            } else {
                this.scrollBy(-amount * this.scrollStep, true, true);
            }
        }
        return true;
    }

}
