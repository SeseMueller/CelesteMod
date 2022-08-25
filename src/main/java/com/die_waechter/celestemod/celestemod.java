package com.die_waechter.celestemod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

import com.die_waechter.celestemod.client.ClientDashHandler;
import com.die_waechter.celestemod.common.Config;
import com.die_waechter.celestemod.common.registerItems;
import com.die_waechter.celestemod.common.tickhandler;
import com.die_waechter.celestemod.common.dash.dashHandler;
import com.die_waechter.celestemod.common.packets.CelestePacketHandler;
import com.die_waechter.celestemod.server.ServerDashHandler;

@Mod(celestemod.MODID)
public class celestemod {

    public static final String MODID = "celestemod";
    public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MODID);

    public static final dashHandler ServerDH = new ServerDashHandler();
    public static dashHandler ClientDH; //This is only used on the client.

    public celestemod() {
        LOGGER.info("Celestemod starting up.");
        registerItems.registerAllItems();

        MinecraftForge.EVENT_BUS.register(tickhandler.class);

        CelestePacketHandler.registerPackets();

        Config.register();

        //Loads the client side dashHandler, if the client is running.
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> celestemod::loadClientDH);

        LOGGER.info("Celestemod loaded");
    }

    private static void loadClientDH(){
        ClientDH = new ClientDashHandler();
    }

}
