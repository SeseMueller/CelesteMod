package com.die_waechter.celestemod.client;

import java.util.UUID;

import com.die_waechter.celestemod.celestemod;
import com.die_waechter.celestemod.common.dashEssenceItem;
import com.die_waechter.celestemod.common.doubleDashEssenceItem;
import com.die_waechter.celestemod.common.dash.dashDirections;
import com.die_waechter.celestemod.common.packets.CelestePacketHandler;
import com.die_waechter.celestemod.common.packets.WaveDashPacket;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class onDash {
    
    @Mod.EventBusSubscriber(modid = celestemod.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
    public static class ClientTickHandler {

        public static Boolean WASPRESSEDLASTTICK = false;
        public static Boolean PRESSESUSELASTTICK = false;
        public static Boolean PRESSEDJUMPLASTTICK = false;

        @SubscribeEvent
        public static void clientTick(ClientTickEvent event){

            Minecraft instance = Minecraft.getInstance();
            if (instance == null) {
                return; 
            }
            if (instance.player == null) {
                return;//This needs to be here, because the client can be ticked before the player is valid. Appearently.
            }
            UUID playerUUID = instance.player.getUUID();
            Options options = instance.options;

            //Check for Dash.
            if (keyHandler.dashKeyMapping != null && keyHandler.dashKeyMapping.isDown()) { //Why can this even BE null? I don't know.
                if (!WASPRESSEDLASTTICK){
                    WASPRESSEDLASTTICK = true;
                    celestemod.ClientDH.dash(event, getDashDirection());   
                }
            }
            else if (keyHandler.dashKeyMapping != null){
                WASPRESSEDLASTTICK = false;
            }

            //Check for Wave Dash: if the player is grounded, in a dash, and presses the jump key, then try to wave dash.
            if ((options.keyJump.isDown() && !PRESSEDJUMPLASTTICK) && // Jump was pressed this tick
             (celestemod.ClientDH.dashDescriptors.get(playerUUID).isInDash && instance.player.isOnGround()) && // Player is in a dash and is on ground
             (dashDirections.isValidHyperDirection(celestemod.ClientDH.dashDescriptors.get(playerUUID).directionAsInt)) && // Player is dashing in a valid direction
             (celestemod.ClientDH.dashDescriptors.get(playerUUID).activeDashTicks >= 3)) { // Player has been in a dash for at least 3 ticks
                // celestemod.LOGGER.debug("Wave Dash:"+ celestemod.ClientDH.dashDescriptors.get(playerUUID).directionAsInt);

                //Ends the dash and gives the player a certain velocity.
                celestemod.ClientDH.dashDescriptors.get(playerUUID).isInDash = false;

                //Potential reverse Wavedash: when the current dash direction is not 0 and different from the direction the player dashed in, use the new held direction.

                int heldDirection = getDashDirection();

                Vec3 direction = dashDirections.getHyperDashDirection(celestemod.ClientDH.dashDescriptors.get(playerUUID).direction);
                // celestemod.LOGGER.debug("heldDirection:"+heldDirection);
                if (heldDirection > 3 ){ //The Player is holding a direction that isn't only up, down or nothing.
                    Vector3f tempDirection = dashDirections.getDirection(heldDirection);
                    tempDirection = dashDirections.applyPlayerYawToDashDirection(tempDirection, instance.player.yRotO);

                    direction = new Vec3(tempDirection.x(), tempDirection.y(), tempDirection.z());
                    direction = dashDirections.getHyperDashDirection(direction);
                }


                direction = direction.add(instance.player.getDeltaMovement());
                instance.player.setDeltaMovement(direction);
                //FIXME: this seems wrong.

                CelestePacketHandler.INSTANCE.sendToServer(new WaveDashPacket(celestemod.ClientDH.dashDescriptors.get(playerUUID).directionAsInt));
                //TODO: The Player always gets their dash back, is that too much?
            }
            
            //Check for dash Essence use.
            if (options.keyUse.isDown()){
                ItemStack heldItem = instance.player.getItemInHand(InteractionHand.MAIN_HAND);
                if (heldItem.getItem() instanceof dashEssenceItem && !PRESSESUSELASTTICK){
                    //Player is holding dash essence and is pressing the use key.
                    //The use key action is consumed and the dash essence is used.

                    InteractionResult result = tryConsumeDashEssence(instance.player, heldItem);
                    if (result == InteractionResult.CONSUME){
                        //Dash essence was used.
                        options.keyDown.consumeClick();
                        options.keyDown.consumeClick();
                    }
                }

                //Check for double dash essence use.
                if (heldItem.getItem() instanceof doubleDashEssenceItem && !PRESSESUSELASTTICK){
                    //Player is holding dash essence and is pressing the use key.
                    //The use key action is consumed and the dash essence is used.
                    InteractionResult result = tryConsumeDoubleDashEssence(instance.player, heldItem);
                    if (result == InteractionResult.CONSUME){
                        //Dash essence was used.
                        options.keyDown.consumeClick();
                        options.keyDown.consumeClick();
                    }
                }
            }

            PRESSESUSELASTTICK = options.keyUse.isDown(); //Update on end so that the value is correct for the next tick.
            PRESSEDJUMPLASTTICK = options.keyJump.isDown();
        }

        private static int getDashDirection(){
            
            int direction = 0;
            
            //Directions: Up, Down, Left, Right, Forward, Backward

            Options options = Minecraft.getInstance().options; //TODO: ressource leak? I don't know.

            if(options.keyJump.isDown()){
                direction += 1;
            } else if (options.keyShift.isDown()){
                direction += 2;
            }

            if(options.keyLeft.isDown()){
                direction += 3;
            } else if (options.keyRight.isDown()){
                direction += 6;
            }

            if(options.keyUp.isDown()){
                direction += 9;
            } else if (options.keyDown.isDown()){
                direction += 18;
            }

            return direction;
        }

        private static InteractionResult tryConsumeDashEssence(Player player, ItemStack stack){

            // If the player already has a dash count of 1, write in the chat that he already has a dash.
            // Then pass the interaction result to the server.
            if (celestemod.ClientDH.getMaxDashes(player.getUUID()) == 1) {
                player.sendMessage(new TextComponent("You feel that this item cannot improve your power further."), player.getUUID());
                return InteractionResult.CONSUME;
            }

            if (celestemod.ClientDH.getMaxDashes(player.getUUID()) == 2) {
                player.sendMessage(new TextComponent("You feel that your power is already at a maximum."), player.getUUID());
                return InteractionResult.CONSUME;
            }

            if (celestemod.ClientDH.getMaxDashes(player.getUUID()) == 0) {
                celestemod.ClientDH.setMaxDashes(player.getUUID(), 1);
                stack.setCount(0);
                player.sendMessage(new TextComponent("You feel that your power has increased."), player.getUUID());
                return InteractionResult.CONSUME;
            }

            return InteractionResult.PASS;

        }

        private static InteractionResult tryConsumeDoubleDashEssence(Player player, ItemStack stack){

            // If the player already has a dash count of 1, increase the dash count by 1.
            // Else, tell him that he is already at full power or that he lacks the power to increase it further.
            if (celestemod.ClientDH.getMaxDashes(player.getUUID()) == 2) {
                player.sendMessage(new TextComponent("You feel that your power is already at a maximum."), player.getUUID());
                return InteractionResult.CONSUME;
            }

            if (celestemod.ClientDH.getMaxDashes(player.getUUID()) == 0) {
                player.sendMessage(new TextComponent("You feel that your power is too weak to be increased by this item."), player.getUUID());
                return InteractionResult.CONSUME;
            }

            if (celestemod.ClientDH.getMaxDashes(player.getUUID()) == 1) {
                celestemod.ClientDH.setMaxDashes(player.getUUID(), 2);
                stack.setCount(0);
                player.sendMessage(new TextComponent("You feel that your power has increased to a maximum."), player.getUUID());
                return InteractionResult.CONSUME;
            }

            return InteractionResult.PASS;
        }

}

    @Mod.EventBusSubscriber(modid = celestemod.MODID, bus = Bus.MOD, value = Dist.CLIENT)
    public static class clientSetupHandler {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){
            // Debug:
            celestemod.LOGGER.debug("Client Setup Event received, setting up KeyHandler..."); //This should only ever execute from the client.
            
            keyHandler.init();
        }
    }

    

}
