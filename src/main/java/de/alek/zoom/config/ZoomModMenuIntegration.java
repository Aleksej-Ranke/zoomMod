package de.alek.zoom.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.alek.zoom.ZoomConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class ZoomModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(ZoomConfig.class, parent).get();
    }
}
