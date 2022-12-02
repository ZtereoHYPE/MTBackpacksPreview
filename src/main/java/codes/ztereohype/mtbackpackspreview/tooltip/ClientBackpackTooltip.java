package codes.ztereohype.mtbackpackspreview.tooltip;

import codes.ztereohype.mtbackpackspreview.MTBackpacksPreview;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;

public class ClientBackpackTooltip implements ClientTooltipComponent {
    private static final Identifier CHEST_LOCATION = new Identifier("textures/gui/container/generic_54.png");
    public static final Identifier BUNDLE_LOCATION = new Identifier(MTBackpacksPreview.MODID + ":textures/bundle.png");
    private static final int SLOT_SIZE = 18;
    private final List<ItemStack> items;
    private final int unlockedSize;

    public ClientBackpackTooltip(BackpackTooltip backpackTooltip) {
        this.items = backpackTooltip.getItems();
        this.unlockedSize = backpackTooltip.getItems().size();

        Identifier
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

        itemRenderer.zOffset = blitOffset;

        renderRows(mouseX, mouseY, j);

        for (int l = 0; l < j; ++l) {
            for (int m = 0; m < i; ++m) {
                int n = mouseX + m * SLOT_SIZE + 1;
                int o = mouseY + l * SLOT_SIZE + 1;
                this.renderSlot(n, o, slot++);
            }
        }
    }

    private void renderRows(int x, int y, int rows) {
        blit(x, y, 6, 16, SLOT_SIZE * 9 + 2, SLOT_SIZE * rows + 1, 256, 256, CHEST_LOCATION);
    }

    private void renderSlot(int x, int y, int itemIndex) {
        if (itemIndex >= this.unlockedSize) {
            this.blit(x, y, 0, 40, SLOT_SIZE, SLOT_SIZE, 128, 128, BUNDLE_LOCATION);
        } else {
            ItemStack itemStack = this.items.get(itemIndex);
            if (itemStack == null) return;

            try {
                ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

                DiffuseLighting.enable();
                GlStateManager.enableDepthTest();

                renderer.method_10249(MinecraftClient.getInstance().player, itemStack, x + 1, y + 1);
                renderer.renderGuiItemOverlay(textRenderer, itemStack, x + 1, y + 1, itemStack.getCount() > 1 ? itemStack.getCount() + "" : "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void blit(int xScreen, int yScreen, int blitX, int blitY, int blitW, int blitH, float spriteSizeX, float spriteSizeY, Identifier location) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        MinecraftClient.getInstance().getTextureManager().bindTexture(location);
        DrawableHelper.drawTexture(xScreen, yScreen, blitX, blitY, blitW, blitH, spriteSizeX, spriteSizeY);
    }

    private int gridSizeX() {
        return 9;
    }

    private int gridSizeY() {
        return (int) Math.ceil((double) unlockedSize / 9);
    }


    public Pair<Integer, Integer> snapCoordinates(ItemStack itemStack, int i, int j) {
        int x = i + 12;
        int y = j;

        int height = MinecraftClient.getInstance().currentScreen.height;
        int width = MinecraftClient.getInstance().currentScreen.width;

        int tooltipWidth = this.getWidth();
        int tooltipHeight = this.getHeight();

        List<String> knownTooltip = itemStack.getTooltip(MinecraftClient.getInstance().player,
                MinecraftClient.getInstance().options.advancedItemTooltips);

        if (MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen) {
            CreativeInventoryScreen screen = (CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen;
            if (screen.getSelectedTab() == ItemGroup.SEARCH.getIndex())
                y += 10;
        }

        for (String text : knownTooltip) {
            int textWidth = MinecraftClient.getInstance().textRenderer.getStringWidth(text);
            if (textWidth > tooltipWidth)
                tooltipWidth = textWidth;
        }
        if (x + tooltipWidth > width)
            x -= 28 + tooltipWidth;


        int componentSize = knownTooltip.size();
        if (componentSize > 1) {
            tooltipHeight += 10 + (componentSize - 1) * 10;
        }

        if (y + tooltipHeight - 6 > height) {
            y = height - tooltipHeight + 6;
        }

        return new Pair<>(x, y);
    }
}
