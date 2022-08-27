package com.die_waechter.celestemod.common.dash;

import com.die_waechter.celestemod.celestemod;
import com.mojang.math.Matrix3f;
import com.mojang.math.Vector3f;

import net.minecraft.world.phys.Vec3;

public class dashDirections {

    public static int UP = 1;
    public static int DOWN = 2;
    public static int LEFT = 3;
    public static int RIGHT = 6;
    public static int FORWARD = 9;
    public static int BACK = 18;

    public static Vector3f getDirection(int direction) {
        Vector3f outVector = new Vector3f(0, 0, 0);

        if (direction == 0){
            direction = 9; //Forward is the default direction.
        }


        if (direction % 3 == 1){
            outVector.add(new Vector3f(0, 1, 0));
        }
        if (direction % 3 == 2){
            outVector.add(new Vector3f(0, -1, 0));
        }
        if ((direction % 9)/3 == 1){
            outVector.add(new Vector3f(1, 0, 0));
        }
        if ((direction % 9)/3 == 2){
            outVector.add(new Vector3f(-1, 0, 0));
        }
        if ((direction % 27)/9 == 1){
            outVector.add(new Vector3f(0, 0, 1));
        }
        if ((direction % 27)/9 == 2){
            outVector.add(new Vector3f(0, 0, -1));
        }

        outVector.normalize();

        //Because the checks for groundedness for players aren't very good when the player is moving in a straight line perfectly, 
        //like he is through a dash, a small downward vector is added to the dash direction.
        
        outVector.add(new Vector3f(0, -1e-2f, 0));
        return outVector;
    }


    public static Vector3f applyPlayerYawToDashDirection(Vector3f dashDirection, float playerYaw) {

        playerYaw = playerYaw % 360;
        playerYaw = playerYaw / 180f * (float) Math.PI;
        playerYaw *=-1;

        Matrix3f xzRotationmatrix = new Matrix3f();
        xzRotationmatrix.set(0, 0, (float)Math.cos(playerYaw));
        xzRotationmatrix.set(0, 1, 0);
        xzRotationmatrix.set(0, 2, (float)Math.sin(playerYaw));
        
        xzRotationmatrix.set(1, 0, 0);
        xzRotationmatrix.set(1, 1, 1);
        xzRotationmatrix.set(1, 2, 0);
        
        xzRotationmatrix.set(2, 0, -(float)Math.sin(playerYaw));
        xzRotationmatrix.set(2, 1, 0);
        xzRotationmatrix.set(2, 2, (float)Math.cos(playerYaw));
        
        dashDirection.transform(xzRotationmatrix);
        return dashDirection;
    }

    public static Vec3 getHyperDashDirection(Vec3 direction) {
        // Remove all y components from the direction.
        direction = direction.subtract(0, direction.y(), 0);
        direction = direction.normalize();
        direction = direction.scale(dashConfig.WAVESPEEDMULTIPLIER.get());

        // Add a small y component to the direction, to give the player a bit upwards velocity.
        direction = direction.add(0, dashConfig.WAVEHEIGHT.get(), 0);
        
        return direction;
        
    }

    public static Vec3 getSuperDashDirection(Vec3 direction) {

        direction = direction.scale(dashConfig.SUPERSPEEDMULTIPLIER.get());

        // Add a small y component to the direction, to give the player a bit upwards velocity.
        // direction = direction.add(0, dashConfig.SUPERHEIGHT.get(), 0);
        direction = direction.add(0.0d, 10.0d, 0.0d);
        // For anyone who came here wondering why their player is not getting the height boost from their code or whatever.
        // I looked into the code and the only senseable place this behaviour could originate from is the player's JUMP FUNCTION.
        // It sets the player's y velocity, since it expects it to be 0 or negligible.
        // This means that any xz movement will be applied as expected, but the y velocity will be gotten from the jump function.
        // This only happens WHEN THE PLAYER IS JUMPING ON THE SAME TICK YOU CALL setDeltaMovement.
        // My solution is to wait a tick before calling setDeltaMovement, since the jump function shouldn't be called again.
        // TODO: Actually fix this.
        // TODO: Actually fix this.
        // TODO: Actually fix this.

        celestemod.LOGGER.debug("Super dash direction: "+ direction);
        
        return direction;
        
    }

    public static boolean isValidHyperDirection(int dashDirection){
        // There are 8 valid down-diagonal directions for the player to hyper OR wave dash.
        int[] validDirections = {5,8,11,14,17,20,23,26}; // All directions that have a down component without being down only.
        for (int i = 0; i < validDirections.length; i++){
            if (dashDirection == validDirections[i]){
                return true;
            }
        }
        return false;
    }


    public static boolean isValidSuperDirection(int dashDirectionAsInt) {
        // There are 8 valid direction, that are in the plane. (no up/down)
        // Since Up is represented as 1 and Down as 2, all directions that are % 3 == 0 are valid.
        return (dashDirectionAsInt % 3 == 0) && (dashDirectionAsInt != 0) && (dashDirectionAsInt < 27);
    }


    
}
