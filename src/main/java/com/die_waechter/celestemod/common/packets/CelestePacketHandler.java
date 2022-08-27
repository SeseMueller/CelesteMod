package com.die_waechter.celestemod.common.packets;

import com.die_waechter.celestemod.celestemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CelestePacketHandler {
    // Handles packets between the client and the server

    private static final String PROTOCOL_VERSION = "1";
    // public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
    //   new ResourceLocation(celestemod.MODID, "main"),
    //     () -> PROTOCOL_VERSION,
    //     PROTOCOL_VERSION::equals,
    //     PROTOCOL_VERSION::equals
    //   );

    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
      .named(new ResourceLocation(celestemod.MODID, "main"))
      .clientAcceptedVersions(PROTOCOL_VERSION::equals)
      .serverAcceptedVersions(PROTOCOL_VERSION::equals)
      .networkProtocolVersion(() -> PROTOCOL_VERSION)
      .simpleChannel();

    public static void registerPackets() {
        int id = 0;
        INSTANCE.registerMessage(id++, DashNumberPacket.class, DashNumberPacket::encode, DashNumberPacket::decode, DashNumberPacket::handle);
        INSTANCE.registerMessage(id++, DashPacket.class, DashPacket::encode, DashPacket::decode, DashPacket::handle);
        INSTANCE.registerMessage(id++, LoginDashPacket.class, LoginDashPacket::encode, LoginDashPacket::decode, LoginDashPacket::handle);
        INSTANCE.registerMessage(id++, WaveDashPacket.class, WaveDashPacket::encode, WaveDashPacket::decode, WaveDashPacket::handle);
        INSTANCE.registerMessage(id++, CommandSetPacket.class, CommandSetPacket::encode, CommandSetPacket::decode, CommandSetPacket::handle);
        INSTANCE.registerMessage(id++, SuperDashPacket.class, SuperDashPacket::encode, SuperDashPacket::decode, SuperDashPacket::handle);
    }
}
