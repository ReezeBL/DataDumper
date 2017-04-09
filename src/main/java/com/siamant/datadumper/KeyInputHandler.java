package com.siamant.datadumper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;


/**
 * Created by Evgeniy on 08.04.2017.
 */
public class KeyInputHandler {
    @SubscribeEvent
    public void OnKeyInput(InputEvent.KeyInputEvent event){
        if(KeyBindings.ping.isPressed())
            DataDumperBase.makeDump();
    }
}
