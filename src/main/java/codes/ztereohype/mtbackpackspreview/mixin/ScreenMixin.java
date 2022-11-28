package codes.ztereohype.mtbackpackspreview.mixin;

import codes.ztereohype.mtbackpackspreview.tooltip.TooltipManager;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    //    @Shadow public abstract List<String> getTooltipFromItem(ItemStack itemStack);
    @Shadow
    public int width;

    private ClientTooltipComponent tooltipComponent = null;

    @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V")
    void getTooltipMetadata(ItemStack itemStack, int i, int j, CallbackInfo ci) {
        TooltipComponent potentialTooltip = TooltipManager.getCustomTooltip(itemStack);

        if (potentialTooltip == null) {
            tooltipComponent = null;
            return;
        }

        tooltipComponent = ClientTooltipComponent.create(potentialTooltip);
    }

    @Inject(at = @At("RETURN"), method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V")
    void injectImageTooltip(ItemStack itemStack, int i, int j, CallbackInfo ci) {
        if (tooltipComponent != null) {
            int x = i + 12;
            int tooltipWidth = this.tooltipComponent.getWidth();

            List<String> knownTooltip = itemStack.getTooltip(MinecraftClient.getInstance().player,
                    MinecraftClient.getInstance().options.advancedItemTooltips);

            for (String text : knownTooltip) {
                int width = MinecraftClient.getInstance().textRenderer.getStringWidth(text);
                if (width > tooltipWidth) {
                    tooltipWidth = width;
                }
            }

            if (x + tooltipWidth > this.width) {
                x -= 28 + tooltipWidth;
            }

            TooltipManager.renderTooltipComponent(tooltipComponent, x, j);
        }
    }

    @ModifyConstant(method = "renderTooltip(Ljava/util/List;II)V", constant = @Constant(intValue = 8, ordinal = 0))
    int injectTooltipHeight(int constant) {
//        return 287;
        return tooltipComponent != null ? constant + tooltipComponent.getHeight() : constant;
    }

    @ModifyConstant(method = "renderTooltip(Ljava/util/List;II)V", constant = @Constant(intValue = 0, ordinal = 0))
    int injectTooltipWidth(int constant) {
//        return 287;
        return tooltipComponent != null ? constant + tooltipComponent.getWidth() : constant;
    }

    @ModifyVariable(method = "renderTooltip(Ljava/util/List;II)V",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I"),
            ordinal = 4)
    int shiftDownardsRestOfTooltip(int value) {
        return value;
//        return tooltipComponent != null ? value + tooltipComponent.getHeight() : value;
    }
}
