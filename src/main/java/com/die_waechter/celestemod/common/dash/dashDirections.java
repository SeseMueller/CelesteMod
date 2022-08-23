package com.die_waechter.celestemod.common.dash;

import com.mojang.math.Matrix3f;
import com.mojang.math.Vector3f;

import net.minecraft.world.phys.Vec3;

public class dashDirections {

    public static float WAVESPEEDMULTIPLIER = 2.0f;
    public static float WAVEHEIGHT = 0.5f;
    
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
        direction = direction.scale(WAVESPEEDMULTIPLIER);

        // Add a small y component to the direction, to give the player a bit upwards velocity.
        direction = direction.add(0, WAVEHEIGHT, 0);
        
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
}
