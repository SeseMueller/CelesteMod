package com.die_waechter.celestemod.common.packets;

import java.util.function.Supplier;

import com.die_waechter.celestemod.celestemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class LoginDashPacket {
    
    //The packet a player gets on login, telling him how many dashes he has.
    public final int numDashes;

    public LoginDashPacket(int numDashes) {
        this.numDashes = numDashes;
    }

    public static LoginDashPacket decode(FriendlyByteBuf buf) {
        int numDashes = buf.readInt();
        return new LoginDashPacket(numDashes);
    }

    public static void encode(LoginDashPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.numDashes);
    }

    public static void handle(LoginDashPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //This is executed on client, and is therefor unsafe.
            celestemod.LOGGER.debug("Got LoginDashPacket...");
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LoginDashClientHandler.handlePacket(packet, ctx));
        });
        ctx.get().setPacketHandled(true);
    }
}
