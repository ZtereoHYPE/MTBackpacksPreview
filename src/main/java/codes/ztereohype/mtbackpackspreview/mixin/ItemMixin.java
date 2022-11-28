package codes.ztereohype.mtbackpackspreview.mixin;

import codes.ztereohype.mtbackpackspreview.tooltip.TooltipManager;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
abstract class ItemMixin {
	@Inject(at = @At("RETURN"), method = "getTooltipImage", cancellable = true)
	private void injectCustomTooltip(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
		var returned = TooltipManager.getCustomTooltip(stack);
		if (returned.isPresent()) {
			cir.setReturnValue(returned);
			cir.cancel();
		}
	}
}
