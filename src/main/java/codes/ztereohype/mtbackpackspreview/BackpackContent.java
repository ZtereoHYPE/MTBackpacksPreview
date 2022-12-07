package codes.ztereohype.mtbackpackspreview;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

@NoArgsConstructor
public class BackpackContent {
    public List<InventorySlot> populatedSlots;
    public int slotAmount;

    public static class InventorySlot {
        private @Getter int index;

        private String itemName;
        private int amount;
        private int durability;
        private boolean enchanted;
        private int customModelData;

        public ItemStack getItemStack() {
            ItemStack items = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.of(itemName.toLowerCase(), ':')), amount);

            items.setDamageValue(durability);

            if (enchanted) {
                Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(ResourceLocation.of("minecraft:protection", ':'));
                items.enchant(enchantment, 1);
            }

            if (customModelData != 0) {
                items.addTagElement("CustomModelData", IntTag.valueOf(customModelData));
            }

            return items;
        }
    }
}
