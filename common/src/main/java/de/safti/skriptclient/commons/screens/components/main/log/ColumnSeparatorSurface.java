package de.safti.skriptclient.commons.screens.components.main.log;

import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Surface;

import java.util.List;

public class ColumnSeparatorSurface implements Surface {

    private final List<Integer> xSeparators;
    private final int lineGap;
    private final int color;

    public ColumnSeparatorSurface(List<Integer> xSeparators, int lineGap, int color) {
        this.xSeparators = xSeparators;
        this.lineGap = lineGap;
        this.color = color;
    }

    @Override
    public void draw(OwoUIDrawContext context, ParentComponent component) {
        int x = component.x();
        int y = component.y();
        int height = component.height() + lineGap;

        for (int offset : xSeparators) {
            int lineX = x + offset;
            context.fill(lineX, y, lineX + 1, y + height, color);
        }
    }

}