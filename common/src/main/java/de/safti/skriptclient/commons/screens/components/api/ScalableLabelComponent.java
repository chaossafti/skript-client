package de.safti.skriptclient.commons.screens.components.api;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ScalableLabelComponent extends LabelComponent {

    protected final float scale;

    public ScalableLabelComponent(Component text, float scale) {
        super(text);
        this.scale = scale;
    }

    protected void changeMatrices(PoseStack matrices) {

    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return (int) (Minecraft.getInstance().font.width(text) * scale + margins.get().right() + margins.get().left());
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        PoseStack matrices = context.getMatrixStack();

        matrices.pushPose();

        // scale the pose correctly
        int pivotX = this.x;
        int pivotY = (int) (this.y + Math.ceil(textRenderer.lineHeight*scale));
        matrices.translate(pivotX, pivotY, 0);
        changeMatrices(matrices);
        matrices.scale(scale, scale, 1.0f);
        matrices.translate(-pivotX, -pivotY, 0);


        int x = this.x;
        int y = this.y;

        if (this.horizontalSizing.get().isContent()) {
            x += this.horizontalSizing.get().value;
        }
        if (this.verticalSizing.get().isContent()) {
            y += this.verticalSizing.get().value;
        }

        switch (this.verticalTextAlignment) {
            case CENTER -> y += (this.height - (this.textHeight())) / 2;
            case BOTTOM -> y += this.height - (this.textHeight());
        }

        final int lambdaX = x;
        final int lambdaY = y;

        context.drawManaged(() -> {
            for (int i = 0; i < this.wrappedText.size(); i++) {
                var renderText = this.wrappedText.get(i);
                int renderX = lambdaX;

                switch (this.horizontalTextAlignment) {
                    case CENTER -> renderX += (this.width - this.textRenderer.width(renderText)) / 2;
                    case RIGHT -> renderX += this.width - this.textRenderer.width(renderText);
                }

                int renderY = lambdaY + i * (this.lineHeight() + this.lineSpacing());
                renderY += this.lineHeight() - this.textRenderer.lineHeight;

                context.drawString(this.textRenderer, renderText, renderX, renderY, this.color.get().argb(), this.shadow);
            }
        });

        matrices.popPose();
        
    }
}
