package com.siamant.datadumper;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Created by Evgeniy on 08.04.2017.
 */
public class KeyBindings {

    // Declare two KeyBindings, ping and pong
    public static KeyBinding ping;

    public static void init() {
        ping = new KeyBinding("key.ping", Keyboard.KEY_NUMPAD1, "key.categories.MercenaryMod");
        ClientRegistry.registerKeyBinding(ping);
    }

}