package de.alek.zoom;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "zoom")
public class ZoomConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public double baseZoomFactor = 0.23; // Default zoom (smaller is closer)

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public double minZoomFactor = 0.05;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public double maxZoomFactor = 0.50;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public double zoomScrollStep = 0.01;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public double smoothDuration = 10.0; // Smoothness factor (higher is slower)

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("control")
    public double mouseSensitivityFactor = 0.25; // Sensitivity multiplier when zoomed

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("visual")
    public boolean cinematicCamera = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("visual")
    public boolean hideHand = true;

    // Optional: Reset zoom on release
    @ConfigEntry.Category("control")
    public boolean resetZoomOnRelease = true;
}
