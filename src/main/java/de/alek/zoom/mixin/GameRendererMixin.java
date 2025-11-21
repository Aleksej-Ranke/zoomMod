package de.alek.zoom.mixin;

import de.alek.zoom.ZoomManager;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Injeziert Code, kurz bevor die Methode getFov zurückkehrt (RETURN)
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void zoom$applyZoom(net.minecraft.client.Camera camera, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir) {
        double zoomFactor = ZoomManager.getZoomFactor();
        if (zoomFactor != 1.0) {
            float originalFov = cir.getReturnValue();
            cir.setReturnValue((float) (originalFov * zoomFactor));
        }
    }

}