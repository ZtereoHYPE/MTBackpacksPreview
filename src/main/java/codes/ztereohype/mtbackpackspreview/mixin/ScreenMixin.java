package codes.ztereohype.mtbackpackspreview.mixin;

import codes.ztereohype.mtbackpackspreview.tooltip.TooltipManager;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow
    public abstract List<Component> getTooltipFromItem(ItemStack itemStack);

    protected ClientTooltipComponent tooltipComponent = null;

    @Inject(at = @At("HEAD"), method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V")
    void getTooltipMetadata(PoseStack poseStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        TooltipComponent potentialTooltip = TooltipManager.getCustomTooltip(itemStack);

        if (potentialTooltip == null) {
            tooltipComponent = null;
            return;
        }

        tooltipComponent = ClientTooltipComponent.create(potentialTooltip);
    }

    @Inject(at = @At("RETURN"), method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V")
    void injectImageTooltip(PoseStack poseStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        if (tooltipComponent != null) {
            Pair<Integer, Integer> snappedCoordinates = tooltipComponent.snapCoordinates(itemStack, i, j);

            int x = snappedCoordinates.getFirst();
            int y = snappedCoordinates.getSecond();
            TooltipManager.renderTooltipComponent(poseStack, tooltipComponent, x, y);
        }
    }

    @ModifyConstant(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V", constant = @Constant(intValue = 8, ordinal = 0))
    int injectTooltipHeight(int constant) {
        return tooltipComponent != null ? constant + tooltipComponent.getHeight() : constant;
    }

    @ModifyConstant(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V", constant = @Constant(intValue = 0, ordinal = 0))
    int injectTooltipWidth(int constant) {
        return tooltipComponent != null ? constant + tooltipComponent.getWidth() : constant;
    }

    private int forIndex = 0;

    @ModifyArg(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;")
    )
    public int captureLocals(int original) {
        forIndex = original;
        return original;
    }

    @ModifyVariable(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)I"),
            ordinal = 4)
    int shiftDownardsRestOfTooltip(int original) {
        if (this.tooltipComponent == null)
            return original;
        if (forIndex == 1)
            return original + this.tooltipComponent.getHeight();
        return original;
    }
}
