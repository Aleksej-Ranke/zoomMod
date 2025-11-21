package de.alek.zoom.mixin;

import de.alek.zoom.ZoomManager;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void zoom$onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        if (ZoomManager.isZooming()) {
             ZoomManager.onMouseScroll(yOffset);
             ci.cancel();
        }
    }

    @Inject(method = "turnPlayer", at = @At("HEAD"))
    private void zoom$modifyAccumulated(CallbackInfo ci) {
        if (ZoomManager.isZooming()) {
            double scale = ZoomManager.getMouseSensitivityScale();
            this.accumulatedDX *= scale;
            this.accumulatedDY *= scale;
        }
    }
}
