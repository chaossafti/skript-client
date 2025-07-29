package de.safti.skriptclient.commons.screens.components.sidebar.storage;

import com.mojang.math.Axis;
import de.safti.skriptclient.commons.screens.components.api.DynamicLabelComponent;
import de.safti.skriptclient.utils.OwoUIUtils;
import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.Delta;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DirectoryComponent extends CollapsibleContainer {
    private static final ResourceLocation FOLDER_ICON = ResourceLocation.fromNamespaceAndPath("skriptclient", "extensions/folder.png");


    @org.jetbrains.annotations.NotNull
    private final PathNode node;

    // TODO: actual texture before of the title, not just an emoji
    protected DirectoryComponent(@NotNull PathNode pathNode, int depth) {
        super(Sizing.content(), Sizing.content(), Component.literal(pathNode.asFile().getName()), true);
        this.node = pathNode;

        if(!pathNode.isDirectory()) {
            throw new IllegalStateException("Provided a non-directory for DirectoryComponent!");
        }

        // configurations
        padding(Insets.of(0, 0, depth * 2, 0));

        FlowLayout titlePersonalLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        DynamicLabelComponent title = new DynamicLabelComponent(Component.literal(pathNode.asFile().getName()), 0.3f);
        titlePersonalLayout.child(title);

        // hover/unhover effect
        OwoUIUtils.hoverEffect(title, titlePersonalLayout);

        SpinnyBoiComponent spinnyBoiV2 = new SpinnyBoiComponent(0.3f);
        spinnyBoiV2.targetRotation = expanded ? 90 : 0;
        spinnyBoiV2.rotation = spinnyBoiV2.targetRotation;


        titleLayout.clearChildren();
        titleLayout.allowOverflow(true);
        titleLayout.child(spinnyBoiV2);

        titleLayout.child(titlePersonalLayout);
    }

    public @NotNull PathNode getNode() {
        return node;
    }

    protected static class SpinnyBoiComponent extends DynamicLabelComponent {

        protected float rotation = 90;
        protected float targetRotation = 90;

        public SpinnyBoiComponent(float fontScale) {
            super(net.minecraft.network.chat.Component.literal(">"), fontScale);
            this.margins(Insets.of(0, 0, 5, 10));
            this.cursorStyle(CursorStyle.HAND);
        }

        @Override
        public void update(float delta, int mouseX, int mouseY) {
            super.update(delta, mouseX, mouseY);
            this.rotation += Delta.compute(this.rotation, this.targetRotation, delta * .65);
        }

        @Override
        public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
            var matrices = context.pose();

            int x = this.x;
            int y = this.y;

            matrices.pushPose();

            double centerX = this.x + this.width / 2f - 1;
            double centerY = this.y + this.height / 2f - 1;


            matrices.translate(centerX, centerY, 0);
            matrices.scale(fontScale, fontScale, 1f);
            matrices.mulPose(Axis.ZP.rotationDegrees(this.rotation));
            matrices.translate(-centerX, -centerY, 0);


            context.drawString(this.textRenderer, text, x, y + height, this.color.get().argb(), this.shadow);
            matrices.popPose();

        }
    }

}
