package com.die_waechter.celestemod.common;

import com.die_waechter.celestemod.common.dash.dashConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    

    public static void register(){
        registerCommonConfigs();
    }

    public static void registerCommonConfigs(){
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        dashConfig.registerDashConfig(COMMON_BUILDER);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
    }
}
