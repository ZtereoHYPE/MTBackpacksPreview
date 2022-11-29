package codes.ztereohype.mtbackpackspreview.tooltip;

import codes.ztereohype.mtbackpackspreview.BackpackContent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Arrays;

public class TooltipManager {
    private static final String PREVIEW_TAG = "BackpackPreviewTag";
    private static final Gson gson = new Gson();

    public static TooltipComponent getCustomTooltip(ItemStack stack) {
        NbtCompound compound = stack.getNbt();
        if (compound == null)
            return null;

        if (!compound.contains(PREVIEW_TAG))
            return null;

        BackpackContent content = null;
        try {
            String unparsedContent = compound.getString(PREVIEW_TAG);
            content = gson.fromJson(unparsedContent, BackpackContent.class);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        ItemStack[] inventoryArray = new ItemStack[content.slotAmount];
        for (int i = 0; i < content.slotAmount; i++)
            inventoryArray[i] = null;

        for (BackpackContent.InventorySlot slot : content.populatedSlots)
            inventoryArray[slot.getIndex()] = slot.getItemStack();

        return new BackpackTooltip(Arrays.asList(inventoryArray));
    }

    public static void renderTooltipComponent(ClientTooltipComponent component, int i, int j) {
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        component.renderImage(i, j, renderer, 400);
    }
}
