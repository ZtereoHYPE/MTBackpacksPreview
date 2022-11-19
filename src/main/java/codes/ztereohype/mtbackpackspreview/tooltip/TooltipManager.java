package codes.ztereohype.mtbackpackspreview.tooltip;

import codes.ztereohype.mtbackpackspreview.BackpackContent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class TooltipManager {
    private static final Gson gson = new Gson();
    public static Optional<TooltipComponent> getCustomTooltip(ItemStack stack) {
        CompoundTag nbtData = stack.getTag();

        if (nbtData == null || !nbtData.contains("BackpackPreviewTag")) return Optional.empty();

        BackpackContent content = gson.fromJson(nbtData.get("BackpackPreviewTag").getAsString(), BackpackContent.class);

        NonNullList<ItemStack> inventoryList = NonNullList.withSize(content.slotAmount, ItemStack.EMPTY);
        for (BackpackContent.InventorySlot slot : content.populatedSlots) {
            inventoryList.set(slot.getIndex(), slot.getItemStack());
        }

        return Optional.of(new BackpackTooltip(inventoryList));
    }

    public static void renderTooltipComponent(PoseStack poseStack, TooltipComponent component, int i, int j) {
        ClientTooltipComponent tooptip = new ClientBackpackTooltip((BackpackTooltip) component);

        tooptip.renderImage();
    }
}
