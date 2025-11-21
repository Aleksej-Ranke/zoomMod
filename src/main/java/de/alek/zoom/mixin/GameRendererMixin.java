package de.alek.zoom.mixin;

import de.alek.zoom.ZoomManager;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.shedaniel.autoconfig.AutoConfig;
import de.alek.zoom.ZoomConfig;
import com.mojang.blaze3d.vertex.PoseStack;

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

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void zoom$hideHand(PoseStack poseStack, net.minecraft.client.Camera camera, float partialTick, CallbackInfo ci) {
         if (ZoomManager.isZooming()) {
             ZoomConfig config = AutoConfig.getConfigHolder(ZoomConfig.class).getConfig();
             if (config.hideHand) {
                 ci.cancel();
             }
         }
    }
}