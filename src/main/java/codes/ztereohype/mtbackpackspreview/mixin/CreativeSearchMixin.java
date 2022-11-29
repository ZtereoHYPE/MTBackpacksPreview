package codes.ztereohype.mtbackpackspreview.mixin;

import codes.ztereohype.mtbackpackspreview.tooltip.TooltipManager;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.ClientTooltipComponent;
import codes.ztereohype.mtbackpackspreview.tooltip.interfaces.TooltipComponent;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreativeInventoryScreen.class)
public abstract class CreativeSearchMixin extends ScreenMixin {

    @Shadow
    private static int selectedTab;

    @Inject(
            at = @At("HEAD"),
            method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V"
    )
    void getTooltipMetadata(ItemStack itemStack, int i, int j, CallbackInfo ci) {
        TooltipComponent potentialTooltip = TooltipManager.getCustomTooltip(itemStack);
        if (potentialTooltip == null) {
            tooltipComponent = null;
            return;
        }
        tooltipComponent = ClientTooltipComponent.create(potentialTooltip);
    }

    @Inject(
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTooltip(Ljava/lang/String;II)V",
                    shift = At.Shift.BEFORE),
            method = "renderTabTooltipIfHovered"
    )
    public void method(ItemGroup group, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
        this.tooltipComponent = null;
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

}
