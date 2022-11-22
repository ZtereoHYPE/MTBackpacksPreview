package codes.ztereohype.mtbackpackspreview.tooltip;

import codes.ztereohype.mtbackpackspreview.BackpackContent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class TooltipManager {
    private static final Gson gson = new Gson();
    public static Optional<TooltipComponent> getCustomTooltip(ItemStack stack) {
        CompoundTag nbtData = stack.getTag();

        if (nbtData == null || !nbtData.contains("BackpackPreviewTag")) return Optional.empty();

        BackpackContent content;
        try {
            content = gson.fromJson(nbtData.get("BackpackPreviewTag").getAsString(), BackpackContent.class);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        NonNullList<ItemStack> inventoryList = NonNullList.withSize(content.slotAmount, ItemStack.EMPTY);
        for (BackpackContent.InventorySlot slot : content.populatedSlots) {
            inventoryList.set(slot.getIndex(), slot.getItemStack());
        }

        return Optional.of(new BackpackTooltip(inventoryList));
    }

    public static void renderTooltipComponent(PoseStack poseStack, ClientTooltipComponent component, int i, int j) {
        component.renderImage(Minecraft.getInstance().font, i, j, poseStack, Minecraft.getInstance().getItemRenderer(), 400);
    }
}
