package codes.ztereohype.mtbackpackspreview.mixin;

import codes.ztereohype.mtbackpackspreview.BackpackContent;
import codes.ztereohype.mtbackpackspreview.tooltip.BackpackTooltip;
import com.google.gson.Gson;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
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
	private final Gson gson = new Gson();

	@Inject(at = @At("RETURN"), method = "getTooltipImage", cancellable = true)
	private void injectCustomTooltip(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
		CompoundTag nbtData = stack.getTagElement("BackpackPreviewTag");

		if (nbtData != null) {
			String jsonData = nbtData.getAsString();

			BackpackContent content = gson.fromJson(jsonData, BackpackContent.class);

			NonNullList<ItemStack> inventoryList = NonNullList.withSize(content.slotAmount, ItemStack.EMPTY);

			for (BackpackContent.InventorySlot slot : content.populatedSlots) {
				inventoryList.set(slot.getIndex(), slot.getItemStack());
			}

			cir.setReturnValue(Optional.of(new BackpackTooltip(inventoryList)));
			cir.cancel();
		}
	}
}
