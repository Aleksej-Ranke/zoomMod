package de.alek.zoom;

import de.alek.zoom.client.ZoomClient;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ZoomManager {

    private static double currentZoomFactor = 1.0;
    private static double prevZoomFactor = 1.0; // For interpolation
    private static double targetZoomFactor = 1.0;
    private static double lastZoomFactor = 1.0; // Store user's adjusted zoom level

    private static boolean wasZooming = false;
    private static boolean originallySmoothCamera = false; // Store original state

    public static void updateZoom() {
        ZoomConfig config = AutoConfig.getConfigHolder(ZoomConfig.class).getConfig();
        Minecraft client = Minecraft.getInstance();

        boolean isZooming = ZoomKeyBinding.isZooming();

        // Initialize lastZoomFactor if it hasn't been set (e.g. first run)
        if (lastZoomFactor == 1.0) {
             lastZoomFactor = config.baseZoomFactor;
        }

        if (isZooming) {
            if (!wasZooming) {
                // Just started zooming
                if (config.resetZoomOnRelease) {
                    lastZoomFactor = config.baseZoomFactor;
                }
                targetZoomFactor = lastZoomFactor;
                if (config.cinematicCamera) {
                    originallySmoothCamera = client.options.smoothCamera;
                    client.options.smoothCamera = true;
                }
            }
            // Ensure target is within bounds (in case config changed)
            targetZoomFactor = Mth.clamp(targetZoomFactor, config.minZoomFactor, config.maxZoomFactor);

        } else {
            if (wasZooming) {
                 // Just stopped zooming
                 if (config.cinematicCamera) {
                     client.options.smoothCamera = originallySmoothCamera;
                 }
            }
            targetZoomFactor = 1.0;
        }

        // Store the state for next tick
        wasZooming = isZooming;
        prevZoomFactor = currentZoomFactor; // Store previous value for interpolation

        // Smooth transition
        // We interpolate currentZoomFactor towards targetZoomFactor
        // Using a simple lerp logic suitable for per-tick update
        // "smoothDuration" in config is treated as a divisor for speed or similar.
        // 1.0 = Instant. Higher = Slower.

        double speed = 1.0 / Math.max(1.0, config.smoothDuration);

        // Basic lerp: current = current + (target - current) * speed
        currentZoomFactor = currentZoomFactor + (targetZoomFactor - currentZoomFactor) * speed;

        // Snap if very close
        if (Math.abs(targetZoomFactor - currentZoomFactor) < 0.0001) {
            currentZoomFactor = targetZoomFactor;
        }
    }

    public static double getZoomFactor() {
        return currentZoomFactor;
    }

    public static double getZoomFactor(float partialTicks) {
        return Mth.lerp(partialTicks, prevZoomFactor, currentZoomFactor);
    }

    public static boolean isZooming() {
        return ZoomKeyBinding.isZooming() || currentZoomFactor != 1.0;
        // Return true even if key is released but animation is still zooming out
        // This is useful for hand hiding etc.
    }

    public static void onMouseScroll(double amount) {
        if (ZoomKeyBinding.isZooming()) {
            ZoomConfig config = AutoConfig.getConfigHolder(ZoomConfig.class).getConfig();

            // Negative amount means scrolling down (zooming out usually), Positive is up (zooming in)
            // However, in factor logic: Smaller Factor = More Zoom (FOV is multiplied by factor).
            // So:
            // Scroll UP (+): Should ZOOM IN -> Decrease Factor
            // Scroll DOWN (-): Should ZOOM OUT -> Increase Factor

            double change = -amount * config.zoomScrollStep;
            lastZoomFactor = Mth.clamp(lastZoomFactor + change, config.minZoomFactor, config.maxZoomFactor);
            targetZoomFactor = lastZoomFactor;
        }
    }

    // Helper to get the exact scale for sensitivity
    public static double getMouseSensitivityScale() {
         ZoomConfig config = AutoConfig.getConfigHolder(ZoomConfig.class).getConfig();
         if (isZooming()) {
             // Interpolate sensitivity based on current zoom vs 1.0?
             // Or just jump?
             // Requirement says: "Wenn gezoomt wird, muss die Maus-Empfindlichkeit dynamisch reduziert werden"
             // A common way is to scale it by the zoom factor itself, or use the configured fixed factor.
             // The requirement asks for a "Mouse Sensitivity Factor" in config.

             // Let's mix them. If factor is 0.25, it means 1/4 speed.
             // But maybe we should scale it by how zoomed in we are.
             // If we are at 1.0 (not zoomed), scale should be 1.0.
             // If we are at baseZoomFactor, scale should be config.mouseSensitivityFactor.

             // Simple approach first:
             if (currentZoomFactor == 1.0) return 1.0;

             // If we use the configured factor directly, it applies fully as soon as we start zooming?
             // That might feel jerky during transition.
             // Let's lerp the sensitivity scale based on currentZoomFactor.

             // Map currentZoomFactor (1.0 to base) to (1.0 to sensitivityFactor)
             // Actually currentZoomFactor goes from 1.0 down to 0.1.

             // A good natural feel is scale = currentZoomFactor. (Zoom 2x -> Sens 0.5x)
             // If user wants extra control, we can multiply by the config factor.

             // Requirement: "Maus-Sensitivitäts-Faktor: Wie stark wird die Maus verlangsamt?"
             // Let's use the config value as the target multiplier at MAX zoom (or base zoom).
             // For now, let's just use the config value when fully zoomed, and interpolate.

             // Actually, many mods just multiply by currentZoomFactor * sensitivityModifier.

             double progress = (1.0 - currentZoomFactor) / (1.0 - config.minZoomFactor);
             // progress 0 at 1.0 zoom, 1 at minZoom.

             // Let's just return currentZoomFactor * config.mouseSensitivityFactor * someNormalization?
             // Simplest implementation that feels "Pro":
             // scale = currentZoomFactor * (something to tune).

             // Let's stick to the requested config:
             // "Maus-Sensitivitäts-Faktor"

             // I will assume the user wants the sensitivity to be multiplied by this factor when "zoomed".
             // To make it smooth, I will interpolate between 1.0 and that factor based on current zoom transition.

             // However, since zoom factor varies with scroll, it's better to link it to the zoom level.
             // Lower FOV = Lower Sensitivity.
             // Factor = currentZoomFactor.

             // Let's use: return currentZoomFactor * config.mouseSensitivityFactor * (1.0/config.baseZoomFactor);
             // This normalizes so that at baseZoom, we get exactly the config sensitivity.

             // Wait, simpler:
             // Return currentZoomFactor; This is what Vanilla Spyglass does (I think).
             // But the user wants a setting.

             // Let's implement: return currentZoomFactor * config.mouseSensitivityFactor;
             // If sensitivity factor is 1.0, it behaves like standard FOV scaling.
             // If user sets it to 0.5, it becomes even slower.

             // Let's verify the logic.
             // If I zoom to 0.1 FOV. Sensitivity should be very low.
             // If I return 0.1 * 0.5 = 0.05. extremely slow. Good.

             return currentZoomFactor * config.mouseSensitivityFactor;
         }
         return 1.0;
    }
}
