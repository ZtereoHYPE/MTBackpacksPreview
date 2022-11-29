package codes.ztereohype.mtbackpackspreview.tooltip.interfaces;

import codes.ztereohype.mtbackpackspreview.tooltip.BackpackTooltip;
import codes.ztereohype.mtbackpackspreview.tooltip.ClientBackpackTooltip;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

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

    void renderImage(int mouseX, int mouseY, ItemRenderer itemRenderer, int blitOffset);

    Pair<Integer, Integer> snapCoordinates(ItemStack itemStack, int i, int j);
}

