package codes.ztereohype.mtbackpackspreview.mixin;

import codes.ztereohype.mtbackpackspreview.tooltip.TooltipManager;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    protected ClientTooltipComponent tooltipComponent = null;

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
            Pair<Integer, Integer> snappedCoordinates = tooltipComponent.snapCoordinates(itemStack, i, j);

            int x = snappedCoordinates.getLeft();
            int y = snappedCoordinates.getRight();
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


    private int forIndex = 0;

    @ModifyArg(method = "renderTooltip(Ljava/util/List;II)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;")
    )
    public int captureLocals(int original) {
        forIndex = original;
        return original;
    }

    @ModifyVariable(method = "renderTooltip(Ljava/util/List;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I",
                    shift = At.Shift.BEFORE),
            ordinal = 4
    )
    int shiftTooltipText(int original) {
        if (this.tooltipComponent == null)
            return original;
        if (forIndex == 1)
            return original + this.tooltipComponent.getHeight();
        return original;
    }
}
