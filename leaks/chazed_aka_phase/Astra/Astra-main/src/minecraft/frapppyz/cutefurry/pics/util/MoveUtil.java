package frapppyz.cutefurry.pics.util;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.impl.Motion;
import frapppyz.cutefurry.pics.event.impl.Move;
import frapppyz.cutefurry.pics.modules.impl.combat.Killaura;
import net.minecraft.client.Minecraft;

public class MoveUtil {

    public static double getSpeed() {
        return Math.sqrt(Minecraft.getMinecraft().thePlayer.motionX * Minecraft.getMinecraft().thePlayer.motionX + Minecraft.getMinecraft().thePlayer.motionZ * Minecraft.getMinecraft().thePlayer.motionZ);
    }

    public static double getDirection() {

        boolean movingForward = Minecraft.getMinecraft().thePlayer.moveForward > 0.0F;
        boolean movingBackward = Minecraft.getMinecraft().thePlayer.moveForward < 0.0F;
        boolean movingRight = Minecraft.getMinecraft().thePlayer.moveStrafing > 0.0F;
        boolean movingLeft = Minecraft.getMinecraft().thePlayer.moveStrafing < 0.0F;

        boolean isMovingSideways = movingLeft || movingRight;
        boolean isMovingStraight = movingForward || movingBackward;

        double direction = Minecraft.getMinecraft().thePlayer.rotationYaw;

        if(movingForward && !isMovingSideways) {

        } else if(movingBackward && !isMovingSideways) {
            direction += 180;
        } else if(movingForward && movingLeft) {
            direction += 45;
        } else if(movingForward) {
            direction -= 45;
        } else if(!isMovingStraight && movingLeft) {
            direction += 90;
        } else if(!isMovingStraight && movingRight) {
            direction -= 90;
        } else if(movingBackward && movingRight) {
            direction -= 135;
        } else if(movingBackward) {
            direction += 135;
        }

        return Math.toRadians(direction);
    }

    public static double getDirectionEvent(Motion event) {

        boolean movingForward = Minecraft.getMinecraft().thePlayer.moveForward > 0.0F;
        boolean movingBackward = Minecraft.getMinecraft().thePlayer.moveForward < 0.0F;
        boolean movingRight = Minecraft.getMinecraft().thePlayer.moveStrafing > 0.0F;
        boolean movingLeft = Minecraft.getMinecraft().thePlayer.moveStrafing < 0.0F;

        boolean isMovingSideways = movingLeft || movingRight;
        boolean isMovingStraight = movingForward || movingBackward;
        double direction = Killaura.entity != null ? RotUtil.getRots(Killaura.entity)[0] : event.getYaw();

        if(movingForward && !isMovingSideways) {

        } else if(movingBackward && !isMovingSideways) {
            direction += 180;
        } else if(movingForward && movingLeft) {
            direction += 45;
        } else if(movingForward) {
            direction -= 45;
        } else if(!isMovingStraight && movingLeft) {
            direction += 90;
        } else if(!isMovingStraight && movingRight) {
            direction -= 90;
        } else if(movingBackward && movingRight) {
            direction -= 135;
        } else if(movingBackward) {
            direction += 135;
        }

        return Math.toRadians(direction);
    }

    public static double getDirectionEvent() {

        boolean movingForward = Minecraft.getMinecraft().thePlayer.moveForward > 0.0F;
        boolean movingBackward = Minecraft.getMinecraft().thePlayer.moveForward < 0.0F;
        boolean movingRight = Minecraft.getMinecraft().thePlayer.moveStrafing > 0.0F;
        boolean movingLeft = Minecraft.getMinecraft().thePlayer.moveStrafing < 0.0F;

        boolean isMovingSideways = movingLeft || movingRight;
        boolean isMovingStraight = movingForward || movingBackward;
        double direction = Killaura.entity != null ? RotUtil.getRots(Killaura.entity)[0] : RotUtil.yaw;

        if(movingForward && !isMovingSideways) {

        } else if(movingBackward && !isMovingSideways) {
            direction += 180;
        } else if(movingForward && movingLeft) {
            direction += 45;
        } else if(movingForward) {
            direction -= 45;
        } else if(!isMovingStraight && movingLeft) {
            direction += 90;
        } else if(!isMovingStraight && movingRight) {
            direction -= 90;
        } else if(movingBackward && movingRight) {
            direction -= 135;
        } else if(movingBackward) {
            direction += 135;
        }

        return Math.toRadians(direction);
    }

    public static void setSpeed(Move event, double speed) {
        if(Minecraft.getMinecraft().thePlayer.moveStrafing != 0 || Minecraft.getMinecraft().thePlayer.moveForward != 0) {
            if(Wrapper.getModManager().getModByName("TargetStrafe").isToggled()){
                event.setX(-Math.sin(getDirectionEvent()) * speed);
                event.setZ(Math.cos(getDirectionEvent()) * speed);
            }else{
                event.setX(-Math.sin(getDirection()) * speed);
                event.setZ(Math.cos(getDirection()) * speed);
            }

        } else {
            event.setX(0);
            event.setZ(0);
        }
    }

    public static void setSpeed(double speed) {
        if(Minecraft.getMinecraft().thePlayer.moveStrafing != 0 || Minecraft.getMinecraft().thePlayer.moveForward != 0) {
            Minecraft.getMinecraft().thePlayer.motionX = -Math.sin(getDirection()) * speed;
            Minecraft.getMinecraft().thePlayer.motionZ = Math.cos(getDirection()) * speed;
        } else {
            Minecraft.getMinecraft().thePlayer.motionX = Minecraft.getMinecraft().thePlayer.motionZ = 0;

        }
    }
}
