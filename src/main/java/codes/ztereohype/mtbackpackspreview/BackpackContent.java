package codes.ztereohype.mtbackpackspreview;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.List;

@NoArgsConstructor
public class BackpackContent {
    public List<InventorySlot> populatedSlots;
    public int slotAmount;

    public static class InventorySlot {
        private @Getter int index;

        private int id;
        private String itemName;
        private int amount;
        private int durability;
        private boolean enchanted;
        private int customModelData;

        public ItemStack getItemStack() {
            Identifier identifier = new Identifier("minecraft", itemName.toLowerCase());
            boolean validItem = Item.REGISTRY.containsKey(identifier);

            ItemStack items = null;
            if (validItem)
                items = new ItemStack(Item.REGISTRY.get(identifier));
            else
                items = new ItemStack(Item.byRawId(id));

            items.count = this.amount;
            items.setDamage(durability);

            if (enchanted) {
                items.addEnchantment(Enchantment.PROTECTION, 1);
            }

            if (customModelData != 0) {
                NbtCompound compound = items.getNbt();
                if (compound == null)
                    compound = new NbtCompound();
                compound.putInt("CustomModelData", customModelData);
            }

            return items;
        }
    }
}
