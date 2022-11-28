package codes.ztereohype.mtbackpackspreview.tooltip;

import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.time.Instant;
import java.util.List;

public class ClientBackpackTooltip implements ClientTooltipComponent {
    private static final Identifier CHEST_LOCATION = new Identifier("textures/gui/container/generic_54.png");
    public static final Identifier BUNDLE_LOCATION = new Identifier("modid:textures/bundle.png");
    private static final int SLOT_SIZE = 18;
    private final List<ItemStack> items;
    private final int unlockedSize;

    public ClientBackpackTooltip(BackpackTooltip backpackTooltip) {
        this.items = backpackTooltip.getItems();
        this.unlockedSize = backpackTooltip.getItems().size();
    }

    @Override
    public int getHeight() {
        return gridSizeY() * SLOT_SIZE + 2 + 4;
    }

    @Override
    public int getWidth() {
        return gridSizeX() * SLOT_SIZE + 2;
    }

    @Override
    public void renderImage(int mouseX, int mouseY, ItemRenderer itemRenderer, int blitOffset) {
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        int slot = 0;

        renderRows(mouseX, mouseY, j);

        for (int l = 0; l < j; ++l) {
            for (int m = 0; m < i; ++m) {
                int n = mouseX + m * SLOT_SIZE + 1;
                int o = mouseY + l * SLOT_SIZE + 1;
                this.renderSlot(n, o, slot++, itemRenderer, blitOffset);
            }
        }
    }

    private void renderRows(int x, int y, int rows) {
        blit(x, y, 6, 16, SLOT_SIZE * 9 + 2, SLOT_SIZE * rows + 1, 256, 256, CHEST_LOCATION);
    }

    private void renderSlot(int x, int y, int itemIndex, ItemRenderer itemRenderer, int blitOffset) {
        if (itemIndex >= this.unlockedSize) {
            this.blit(x, y, 0, 40, SLOT_SIZE, SLOT_SIZE, 128, 128, BUNDLE_LOCATION);
        } else {
            ItemStack itemStack = this.items.get(itemIndex);
            if (itemStack == null) return;

            try {
                ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

                renderer.renderInGuiWithOverrides(itemStack, x + 1, y + 1);
                renderer.renderGuiItemOverlay(textRenderer, itemStack, x + 1, y + 1, itemStack.count + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void blit(int xScreen, int yScreen, int blitX, int blitY, int blitW, int blitH, float spriteSizeX, float spriteSizeY, Identifier location) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableRescaleNormal();
        DiffuseLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();

        MinecraftClient.getInstance().getTextureManager().bindTexture(location);
        DrawableHelper.drawTexture(xScreen, yScreen, blitX, blitY, blitW, blitH, spriteSizeX, spriteSizeY);
    }

    private int gridSizeX() {
        return 9;
    }

    private int gridSizeY() {
        return (int) Math.ceil((double) unlockedSize / 9);
    }

}
