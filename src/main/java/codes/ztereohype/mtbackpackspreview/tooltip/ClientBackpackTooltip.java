package codes.ztereohype.mtbackpackspreview.tooltip;

import codes.ztereohype.mtbackpackspreview.MTBackpacksPreview;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ClientBackpackTooltip implements ClientTooltipComponent {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MTBackpacksPreview.MODID, "textures/gui/bundle.png");
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 18;
    private final NonNullList<ItemStack> items;
    private final int unlockedSize;

    public ClientBackpackTooltip(BackpackTooltip backpackTooltip) {
        this.items = backpackTooltip.getItems();
        this.unlockedSize = backpackTooltip.getItems().size();
    }

    @Override
    public int getHeight() {
        return gridSizeY() * SLOT_SIZE_Y + 2 + 4;
    }

    @Override
    public int getWidth() {
        return gridSizeX() * SLOT_SIZE_X + 2;
    }

    @Override
    public void renderImage(Font font, int mouseX, int mouseY, ItemRenderer itemRenderer, int blitOffset) {
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        int slot = 0;

        for (int l = 0; l < j; ++l) {
            for (int m = 0; m < i; ++m) {
                int n = mouseX + m * SLOT_SIZE_X + 1;
                int o = mouseY + l * SLOT_SIZE_Y + 1;
                this.renderSlot(n, o, slot++, font, itemRenderer, blitOffset);
            }
        }

        this.drawBorder(mouseX, mouseY, i, j, blitOffset);
    }

    private void renderSlot(int x, int y, int itemIndex, Font font, ItemRenderer itemRenderer, int blitOffset) {
        if (itemIndex >= this.unlockedSize) {
            this.blit(x, y, blitOffset, Texture.BLOCKED_SLOT);
        } else {
            ItemStack itemStack = this.items.get(itemIndex);
            this.blit(x, y, blitOffset, ClientBackpackTooltip.Texture.SLOT);
            itemRenderer.blitOffset = blitOffset;
            itemRenderer.renderAndDecorateItem(Minecraft.getInstance().player, itemStack, x + 1, y + 1);
            itemRenderer.renderGuiItemDecorations(font, itemStack, x + 1, y + 1);
        }
    }

    private void drawBorder(int x, int y, int slotWidth, int slotHeight, int blitOffset) {
        this.blit(x, y, blitOffset, ClientBackpackTooltip.Texture.BORDER_CORNER_TOP);
        this.blit(x + slotWidth * SLOT_SIZE_X + 1, y, blitOffset, ClientBackpackTooltip.Texture.BORDER_CORNER_TOP);

        for (int i = 0; i < slotWidth; ++i) {
            this.blit(x + 1 + i * SLOT_SIZE_X, y, blitOffset, ClientBackpackTooltip.Texture.BORDER_HORIZONTAL_TOP);
            this.blit(x + 1 + i * SLOT_SIZE_X, y + slotHeight * SLOT_SIZE_Y + 1, blitOffset, ClientBackpackTooltip.Texture.BORDER_HORIZONTAL_BOTTOM);
        }

        for (int i = 0; i < slotHeight; ++i) {
            this.blit(x, y + i * SLOT_SIZE_Y + 1, blitOffset, ClientBackpackTooltip.Texture.BORDER_VERTICAL);
            this.blit(x + slotWidth * SLOT_SIZE_X + 1, y + i * SLOT_SIZE_Y + 1, blitOffset, ClientBackpackTooltip.Texture.BORDER_VERTICAL);
        }

        this.blit(x, y + slotHeight * SLOT_SIZE_Y + 1, blitOffset, Texture.BORDER_CORNER_BOTTOM);
        this.blit(x + slotWidth * SLOT_SIZE_X + 1, y + slotHeight * SLOT_SIZE_Y + 1, blitOffset, ClientBackpackTooltip.Texture.BORDER_CORNER_BOTTOM);
    }

    /**
     * This method will cycle through a different configuration of the
     * {@link GlStateManager} every 5 seconds in order to attempt to
     * find the correct configuration for a certain task.
     */
    private void fenaggleGLStateManager(){
        Class<?> cls = GlStateManager.class;
        Random r = new Random();
        r.setSeed(Instant.now().getEpochSecond());

        for (Method cr : cls.getMethods()) {
            if(r.nextBoolean())
                continue;
            if (cr.getParameterTypes().length == 0) {
                try {
                    System.out.print(cr.getName()+" ");
                    cr.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println();
    }

    private void blit(int i, int j, int k, ClientBackpackTooltip.Texture texture) {
        Lighting.turnOnGui();

        Minecraft.getInstance().getTextureManager().bind(TEXTURE_LOCATION);
        GuiComponent.blit(i, j, k, (float) texture.x, (float) texture.y, texture.w, texture.h, 128, 128);
    }

    private int gridSizeX() {
        return 9;
    }

    private int gridSizeY() {
        return (int) Math.ceil((double) unlockedSize / 9);
    }

    @Environment(EnvType.CLIENT)
    enum Texture {
        SLOT(0, 0, SLOT_SIZE_X, SLOT_SIZE_Y),
        BLOCKED_SLOT(0, 40, SLOT_SIZE_X, SLOT_SIZE_Y),
        BORDER_VERTICAL(0, SLOT_SIZE_X, 1, SLOT_SIZE_Y),
        BORDER_HORIZONTAL_TOP(0, SLOT_SIZE_Y, SLOT_SIZE_X, 1),
        BORDER_HORIZONTAL_BOTTOM(0, 60, SLOT_SIZE_X, 1),
        BORDER_CORNER_TOP(0, SLOT_SIZE_Y, 1, 1),
        BORDER_CORNER_BOTTOM(0, 60, 1, 1);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        Texture(int j, int k, int l, int m) {
            this.x = j;
            this.y = k;
            this.w = l;
            this.h = m;
        }
    }
}
