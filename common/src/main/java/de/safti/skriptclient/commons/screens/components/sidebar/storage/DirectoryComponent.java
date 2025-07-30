package de.safti.skriptclient.commons.screens.components.sidebar.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.safti.skriptclient.commons.screens.components.api.ScalableLabelComponent;
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
    private final SpinnyBoiComponent spinnyBoiV2;

    protected DirectoryComponent(@NotNull PathNode pathNode, int depth) {
        super(Sizing.content(), Sizing.content(), Component.literal(pathNode.asFile().getName()), true);
        this.node = pathNode;

        if(!pathNode.isDirectory()) {
            throw new IllegalStateException("Provided a non-directory for DirectoryComponent!");
        }

        // configurations
        padding(Insets.of(0, 0, depth * 2, 0));

        FlowLayout titlePersonalLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        ScalableLabelComponent title = new ScalableLabelComponent(Component.literal(pathNode.asFile().getName()), 0.5f);
        titlePersonalLayout.child(title);

        // hover/unhover effect
        OwoUIUtils.hoverEffect(title, titlePersonalLayout);

        spinnyBoiV2 = new SpinnyBoiComponent(0.5f);
        spinnyBoiV2.targetRotation = expanded ? 90 : 0;
        spinnyBoiV2.rotation = spinnyBoiV2.targetRotation;

        this.contentLayout.allowOverflow(true);


        titleLayout.clearChildren();
        titleLayout.allowOverflow(true);
        titleLayout.child(spinnyBoiV2);

        titleLayout.child(titlePersonalLayout);
    }

    @Override
    public void toggleExpansion() {
        if (expanded) {
            this.spinnyBoiV2.targetRotation = 0;
        } else {
            this.spinnyBoiV2.targetRotation = 90;
        }
        super.toggleExpansion();
    }

    public @NotNull PathNode getNode() {
        return node;
    }

    protected static class SpinnyBoiComponent extends ScalableLabelComponent {

        protected float rotation = 90;
        protected float targetRotation = 90;

        public SpinnyBoiComponent(float fontScale) {
            super(net.minecraft.network.chat.Component.literal(">"), fontScale);
            this.margins(Insets.of(0, 2, 1, 3));
            this.cursorStyle(CursorStyle.HAND);
        }

        @Override
        public void update(float delta, int mouseX, int mouseY) {
            super.update(delta, mouseX, mouseY);
            this.rotation += Delta.compute(this.rotation, this.targetRotation, delta * .65);
        }

        @Override
        protected void changeMatrices(PoseStack matrices) {
            matrices.mulPose(Axis.ZP.rotationDegrees(this.rotation));
        }
    }

}
