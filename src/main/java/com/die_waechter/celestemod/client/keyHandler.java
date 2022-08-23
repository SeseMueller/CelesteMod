package com.die_waechter.celestemod.client;

import com.die_waechter.celestemod.celestemod;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class keyHandler {
    public static KeyMapping dashKeyMapping;

    private keyHandler() {
    }
    
    public static void init(){
        dashKeyMapping = registerKey("dash", KeyMapping.CATEGORY_GAMEPLAY, InputConstants.KEY_O);
    }

    
    private static KeyMapping registerKey(String name, String category, int keyCode){
        final var key = new KeyMapping("key."+celestemod.MODID+"."+name, KeyConflictContext.IN_GAME,InputConstants.Type.KEYSYM, keyCode, category);
        ClientRegistry.registerKeyBinding(key);
        return key;
    }


}


