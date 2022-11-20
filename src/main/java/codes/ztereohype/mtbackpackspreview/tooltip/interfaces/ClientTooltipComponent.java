package codes.ztereohype.mtbackpackspreview.tooltip.interfaces;

import codes.ztereohype.mtbackpackspreview.tooltip.BackpackTooltip;
import codes.ztereohype.mtbackpackspreview.tooltip.ClientBackpackTooltip;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;


public interface ClientTooltipComponent {
    static ClientTooltipComponent create(TooltipComponent tooltipComponent) {
        if (tooltipComponent instanceof BackpackTooltip) {
            return new ClientBackpackTooltip((BackpackTooltip) tooltipComponent);
        } else {
            throw new IllegalArgumentException("Unknown TooltipComponent");
        }
    }

    int getHeight();

    int getWidth();

    void renderImage(Font font, int mouseX, int mouseY, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset);
}

