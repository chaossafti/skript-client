package de.safti.skriptclient.commons.screens.components.api;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.network.chat.Component;

public class DynamicLabelComponent extends LabelComponent {
    protected final float fontScale;

    public DynamicLabelComponent(Component text, float fontScale) {
        super(text);
        sizing(Sizing.content(), Sizing.fixed(textRenderer.lineHeight));

        this.fontScale = fontScale;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return (int) (textRenderer.width(text)*fontScale);
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return (int) (textRenderer.width(text)*fontScale);
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrices = context.pose();
        int x = this.x;
        int y = this.y;

        matrices.pushPose();

        matrices.translate(x, y, 0);
        matrices.scale(fontScale, fontScale, 1f);

        context.drawString(this.textRenderer, text, 0, 0, this.color.get().argb(), this.shadow);

        matrices.popPose();
    }
}
