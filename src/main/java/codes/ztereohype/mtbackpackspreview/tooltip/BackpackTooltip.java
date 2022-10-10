package codes.ztereohype.mtbackpackspreview.tooltip;

import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class BackpackTooltip implements TooltipComponent {
    private final @Getter NonNullList<ItemStack> items;

    public BackpackTooltip(NonNullList<ItemStack> items) {
        this.items = items;
    }
}
