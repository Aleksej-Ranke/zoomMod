package de.alek.zoom;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class ZoomKeyBinding {

    // Die Instanz des KeyMappings, die registriert wird
    public static final KeyMapping ZOOM_KEY;

    static {
        // Definiert das KeyMapping: Name, Typ, Standard-Taste, Kategorie
        ZOOM_KEY = new KeyMapping(
                "key.zoom.toggle", // Lokalisierungsschlüssel
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C, // Standardtaste 'C'
                "key.categories.movement" // Kategorie
        );
    }

    // Methode zum Registrieren des KeyBindings bei Fabric
    public static void register() {
        KeyBindingHelper.registerKeyBinding(ZOOM_KEY);
    }

    // Einfache Methode, um den Status der Taste abzufragen
    public static boolean isZooming() {
        // Prüft, ob die Taste gedrückt ist
        return ZOOM_KEY.isDown();
    }
}