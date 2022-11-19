package codes.ztereohype.mtbackpackspreview.tooltip.interfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;


public interface ClientTooltipComponent {
    int getHeight();

    int getWidth(Font font);

    void renderImage(Font font, int mouseX, int mouseY, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset, TextureManager textureManager);
}

