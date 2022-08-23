package com.die_waechter.celestemod.common.dash;

import com.mojang.math.Vector3f;

import net.minecraft.world.phys.Vec3;

public class dashDescriptor {
    // Counts the number of Dashes the player has,
    // The maximum number of Dashes he can have,
    // How long the current Dash has been active,
    // Whether the player is currently in a Dash,
    // What direction the player is dashing in,
    // As well as the number of ticks the player has not been on the ground for.

    public static float DASHSPEED = 0.7f; //The speed at which the player moves when dashing, in blocks per second.

    public int numberOfDashes;
    public int maxNumberOfDashes;
    public int activeDashTicks;
    public boolean isInDash;
    public Vector3f dashDirection;
    public int directionAsInt;
    public Vec3 direction;
    public int ticksNotOnGround;
    //TODO: inactiveDashTicks; For fall damage.

    public dashDescriptor() {
        this.numberOfDashes = 0; 
        this.maxNumberOfDashes = 0;
        this.activeDashTicks = 0;
        this.isInDash = false;
        this.dashDirection = Vector3f.XP; //This is arbitrary.
        this.direction = Vec3.ZERO;
        this.directionAsInt = 0;
        this.ticksNotOnGround = 0;
    }

    public void update() {


        if (this.activeDashTicks >= 10){
            this.activeDashTicks = 0;
            this.isInDash = false;
        }

        if (this.isInDash){
            this.activeDashTicks++;
        }

        
    }

    //Called when the player starts a Dash.
    public void dash(int dashDirection, float playerYaw, boolean isCreative) {
        if (this.numberOfDashes > 0 || isCreative){
            this.isInDash = true;
            this.activeDashTicks = 0;
            this.directionAsInt = dashDirection;
            this.dashDirection = dashDirections.getDirection(dashDirection);
            this.dashDirection = dashDirections.applyPlayerYawToDashDirection(this.dashDirection, playerYaw);
            this.dashDirection.normalize();
            this.direction = new Vec3(this.dashDirection.x(), this.dashDirection.y(), this.dashDirection.z());
            
            //Changes the speed of the dash.
            this.direction.scale(DASHSPEED);
            
            this.numberOfDashes--; 
            
            // celestemod.LOGGER.debug("Dashing...");
            // celestemod.LOGGER.debug("Now dashes: " + this.numberOfDashes);
        }
    }
}
