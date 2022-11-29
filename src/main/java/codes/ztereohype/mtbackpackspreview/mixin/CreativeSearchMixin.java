package codes.ztereohype.mtbackpackspreview.mixin;

import codes.ztereohype.mtbackpackspreview.tooltip.TooltipManager;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreativeModeInventoryScreen.class)
public abstract class CreativeSearchMixin extends ScreenMixin {

    @Shadow
    private static int selectedTab;

    @Inject(
            at = @At("HEAD"),
            method = "renderTooltip"
    )
    void getTooltipMetadata(PoseStack poseStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        TooltipComponent potentialTooltip = TooltipManager.getCustomTooltip(itemStack);
        if (potentialTooltip == null) {
            tooltipComponent = null;
            return;
        }
        tooltipComponent = ClientTooltipComponent.create(potentialTooltip);
    }

    @Inject(
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;II)V",
                    shift = At.Shift.BEFORE),
            method = "checkTabHovering"
    )
    public void method(PoseStack poseStack, CreativeModeTab creativeModeTab, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        this.tooltipComponent = null;
    }

    @Inject(at = @At("RETURN"), method = "renderTooltip")
    void injectImageTooltip(PoseStack poseStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        if (tooltipComponent != null) {
            Pair<Integer, Integer> snappedCoordinates = tooltipComponent.snapCoordinates(itemStack, i, j);
            int x = snappedCoordinates.getFirst();
            int y = snappedCoordinates.getSecond();
            TooltipManager.renderTooltipComponent(poseStack, tooltipComponent, x, y);
        }
    }

}
