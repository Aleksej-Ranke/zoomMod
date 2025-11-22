package de.alek.zoom.mixin;

import de.alek.zoom.ZoomManager;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shedaniel.autoconfig.AutoConfig;
import de.alek.zoom.ZoomConfig;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyVariable(method = "getFov", at = @At("RETURN"), ordinal = 0)
    private float zoom$modifyFov(float originalFov) {
        double zoomFactor = ZoomManager.getZoomFactor();
        if (zoomFactor == 1.0) return originalFov;
        return (float) (originalFov * zoomFactor);
    }

    // Some mappings or other mods may cause the local to be double-typed. Provide an overload
    // so we don't hit ClassCastExceptions in those environments.
    @ModifyVariable(method = "getFov", at = @At("RETURN"), ordinal = 0)
    private double zoom$modifyFovDouble(double originalFov) {
        double zoomFactor = ZoomManager.getZoomFactor();
        if (zoomFactor == 1.0) return originalFov;
        return originalFov * zoomFactor;
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void zoom$hideHand(Camera camera, float f, Matrix4f matrix4f, CallbackInfo ci) {
         if (ZoomManager.isZooming()) {
             ZoomConfig config = AutoConfig.getConfigHolder(ZoomConfig.class).getConfig();
             if (config.hideHand) {
                 ci.cancel();
             }
         }
    }
}