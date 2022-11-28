package codes.ztereohype.mtbackpackspreview.mixin;

import codes.ztereohype.mtbackpackspreview.tooltip.TooltipManager;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow public abstract List<String> getTooltipFromItem(ItemStack itemStack);
    @Shadow public int width;

    @Shadow public int height;
    private ClientTooltipComponent tooltipComponent = null;

    @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/world/item/ItemStack;II)V")
    void getTooltipMetadata(ItemStack itemStack, int i, int j, CallbackInfo ci) {
        Optional<TooltipComponent> potentialTooltip = TooltipManager.getCustomTooltip(itemStack);

        if (!potentialTooltip.isPresent()) {
            tooltipComponent = null;
            return;
        }

        tooltipComponent = ClientTooltipComponent.create(potentialTooltip.get());
    }

    @Inject(at = @At("RETURN"), method = "renderTooltip(Lnet/minecraft/world/item/ItemStack;II)V")
    void injectImageTooltip(ItemStack itemStack, int i, int j, CallbackInfo ci) {
        if (tooltipComponent != null) {
            int x = i + 12;
            int y = j;

            int tooltipWidth = this.tooltipComponent.getWidth();
            for (String text : this.getTooltipFromItem(itemStack)) {
                int width = Minecraft.getInstance().font.width(text);
                if (width > tooltipWidth) {
                    tooltipWidth = width;
                }
            }

            if (x + tooltipWidth > this.width) {
                x -= 28 + tooltipWidth;
            }

            int o = 8 + this.tooltipComponent.getHeight();
            List<String> list = getTooltipFromItem(itemStack);
            if (list.size() > 1) {
                o += 2 + (list.size() - 1) * 10;
            }

            if (y + o - 6 > this.height) {
                y = this.height - o + 6;
            }

            TooltipManager.renderTooltipComponent(tooltipComponent, x, y);
        }
    }

    @ModifyConstant(method = "renderTooltip(Ljava/util/List;II)V", constant = @Constant(intValue = 8, ordinal = 0))
    int injectTooltipHeight(int constant) {
        return tooltipComponent != null ? constant + tooltipComponent.getHeight() : constant;
    }

    @ModifyConstant(method = "renderTooltip(Ljava/util/List;II)V", constant = @Constant(intValue = 0, ordinal = 0))
    int injectTooltipWidth(int constant) {
         return tooltipComponent != null ? constant + tooltipComponent.getWidth() : constant;
    }

    @ModifyVariable(method = "renderTooltip(Ljava/util/List;II)V",
                    at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)I"),
                    ordinal = 4)
    int shiftDownardsRestOfTooltip(int value) {
        return tooltipComponent != null ? value + tooltipComponent.getHeight() : value;
    }
}
