package codes.ztereohype.mtbackpackspreview.tooltip;

import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class BackpackTooltip implements TooltipComponent {
    private final @Getter NonNullList<ItemStack> items;

    public BackpackTooltip(NonNullList<ItemStack> items) {
        this.items = items;
    }
}
