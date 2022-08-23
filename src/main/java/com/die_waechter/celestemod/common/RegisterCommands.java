package com.die_waechter.celestemod.common;

import com.die_waechter.celestemod.celestemod;
import com.die_waechter.celestemod.common.dash.dHHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = celestemod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterCommands {
    

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        CelesteCommand.register(event.getDispatcher());
    }

    public class CelesteCommand {
        public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
            LiteralArgumentBuilder<CommandSourceStack> dash =  Commands.literal("celeste")
                .then(Commands.literal("dash")
            
                    .then(Commands.literal("get").executes((CSS) -> {return getDash(CSS);}))
                    .then(Commands.literal("set")
                        .requires((CSS) -> {return CSS.hasPermission(2);}) //Only players with permission level 2 can use this command.
                        .then(Commands.argument("numDashes", IntegerArgumentType.integer(0, 2))
                        .executes((CSS) -> {return setDash(CSS);})))


            );
            dispatcher.register(dash);
        }
            
        
        

        private static int getDash(CommandContext<CommandSourceStack> command){
            if (command.getSource().getEntity() instanceof Player) {
                Player player = (Player) command.getSource().getEntity();
                int numDashes = celestemod.ServerDH.getDashes(player.getUUID());
                player.sendMessage(new TextComponent("You have " + numDashes + " dashes."), player.getUUID());
                return Command.SINGLE_SUCCESS;
            } else {
                command.getSource().sendFailure(new TextComponent("You must be a player to use this command."));
                return Command.SINGLE_SUCCESS;
            }
        }

        private static int setDash(CommandContext<CommandSourceStack> command){
            // celestemod.LOGGER.debug("setDash");
            if (command.getSource().getEntity() instanceof Player) {
                Player player = (Player) command.getSource().getEntity();
                int numDashes = command.getArgument("numDashes", Integer.class);
                dHHelper.setMaxDashes(player.getUUID(), numDashes);
                player.sendMessage(new TextComponent("You now have " + numDashes + " dashes."), player.getUUID());
                return Command.SINGLE_SUCCESS;
            } else {
                command.getSource().sendFailure(new TextComponent("You must be a player to use this command."));
                return Command.SINGLE_SUCCESS;
            }
        }

    }
}
