package com.die_waechter.celestemod.common.dash;

import net.minecraftforge.common.ForgeConfigSpec;

public class dashConfig {

    public static ForgeConfigSpec.DoubleValue DASHSPEED;
    public static ForgeConfigSpec.DoubleValue WAVESPEEDMULTIPLIER;
    public static ForgeConfigSpec.DoubleValue WAVEHEIGHT;
    

    public static void registerDashConfig(ForgeConfigSpec.Builder COMMON_BUILDER) {
        COMMON_BUILDER.comment("Dash Config").push("Dash");

        DASHSPEED = COMMON_BUILDER
            .comment("The speed at which the dash moves.")
            .defineInRange("Dash Speed", 0.7, 0.1, 10.0);

        WAVESPEEDMULTIPLIER = COMMON_BUILDER
            .comment("The speed increase the player gets on wavedash.")
            .defineInRange("Wavedash Speed Multiplier", 2.0, 0.1, 10.0);

        WAVEHEIGHT = COMMON_BUILDER
            .comment("The height the player gets on wavedash.")
            .defineInRange("Wavedash Height", 0.5, 0.1, 10.0);

        COMMON_BUILDER.pop();
    }

}
