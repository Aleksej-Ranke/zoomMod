package de.alek.zoom.client;

import de.alek.zoom.ZoomConfig;
import de.alek.zoom.ZoomKeyBinding;
import de.alek.zoom.ZoomManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ZoomClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ZoomConfig.class, GsonConfigSerializer::new);
        ZoomKeyBinding.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> ZoomManager.updateZoom());
    }
}
