package codes.ztereohype.mtbackpackspreview.mixin;

import codes.ztereohype.mtbackpackspreview.tooltip.BackpackTooltip;
import codes.ztereohype.mtbackpackspreview.tooltip.ClientBackpackTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientTooltipComponent.class)
interface ClientTooltipComponentMixin {
    @Inject(method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",
            at = @At(value = "HEAD"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private static void injectCustomTooltips(TooltipComponent visualTooltipComponent, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        if (visualTooltipComponent instanceof BackpackTooltip) {
            cir.setReturnValue(new ClientBackpackTooltip((BackpackTooltip) visualTooltipComponent));
            cir.cancel();
        }
    }
}
