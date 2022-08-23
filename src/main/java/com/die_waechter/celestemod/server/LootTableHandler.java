package com.die_waechter.celestemod.server;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

import com.die_waechter.celestemod.celestemod;

@Mod.EventBusSubscriber(modid = celestemod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootTableHandler {
    

    @SubscribeEvent
    public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        celestemod.LOGGER.info("Registering LootModifierSerializers...");
        event.getRegistry().register(new DashEssenceInStronghold.Serializer().setRegistryName(new ResourceLocation(celestemod.MODID, "dash_essence_in_stronghold")));

    }
}
