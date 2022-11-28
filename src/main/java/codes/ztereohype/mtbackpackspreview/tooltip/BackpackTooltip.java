package codes.ztereohype.mtbackpackspreview.tooltip;

import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import lombok.Getter;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BackpackTooltip implements TooltipComponent {
    private final @Getter List<ItemStack> items;

    public BackpackTooltip(List<ItemStack> items) {
        this.items = items;
    }
}
