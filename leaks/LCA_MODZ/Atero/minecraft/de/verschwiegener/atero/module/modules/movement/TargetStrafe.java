package de.verschwiegener.atero.module.modules.movement;


import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventPreMotionUpdate;
import com.darkmagician6.eventapi.events.callables.PlayerMoveEvent;
import de.verschwiegener.atero.module.modules.combat.Killaura;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public class TargetStrafe extends Module {
    TimeUtils timeUtils;
    public static boolean space;
    private int direction = -1;
    private double Range = 0;
    public TargetStrafe() {
        super("TargetStrafe", "TargetStrafe", Keyboard.KEY_NONE, Category.Movement);
    }

    public void onEnable() {

        super.onEnable();
    }

    public void onDisable() {

        super.onDisable();
    }




    public boolean doStrafeAtSpeed(PlayerMoveEvent event, double moveSpeed) {
        boolean strafe = canStrafe();
        if (Killaura.instance.hasTarget()) {
            if (strafe) {
                float[] rotations = getRotations(Killaura.instance.getTarget());
                final float Strafe = (float) MathHelper.getRandomDoubleInRange(new Random(), 1.5, 2);
                Range = Management.instance.settingsmgr.getSettingByName("TargetStrafe").getItemByName("Range").getCurrentValue();
                if (Minecraft.thePlayer.getDistanceToEntity(Killaura.instance.getTarget()) <= Range)
                    setSpeed(event, moveSpeed, rotations[0], direction, 0);
                else setSpeed(event, moveSpeed, rotations[0], direction, 1);
            }
        }

        return strafe;
    }


    @EventTarget
    private void onUpdate(PlayerMoveEvent event) {

        doStrafeAtSpeed(event, getSpeed(event));
    }


    public void  onUpdate() {
        setExtraTag("Motion");
        space = (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode()) && Killaura.instance.hasTarget());

     if(Minecraft.getMinecraft().gameSettings.keyBindRight.pressed){
            direction = -1;
        } else {
         if(Minecraft.getMinecraft().gameSettings.keyBindLeft.pressed){
            direction = 1;
        }
    }

    }






    public boolean canStrafe() {
        return Objects.requireNonNull(Management.instance.modulemgr.getModuleByName("Killaura")).isEnabled() && Killaura.instance.hasTarget();
    }
    public static double getSpeed(PlayerMoveEvent em) {
        double x = em.getX();
        double z = em.getZ();

        return Math.sqrt(x * x + z * z);
    }
    public static void setSpeed(PlayerMoveEvent moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        moveEvent.x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        moveEvent.z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }
    public static float[] getRotations(Entity entity) {
        double diffY;
        if (Minecraft.getMinecraft().isGamePaused())
            return new float[] { 0.0F, -90.0F };
        if (entity == null) {
            return null;
        }


        double diffX = entity.posX - Minecraft.thePlayer.posX;
        double diffZ = entity.posZ - Minecraft.thePlayer.posZ;




        if (entity instanceof EntityLivingBase) {

            EntityLivingBase elb = (EntityLivingBase)entity;

            diffY = elb.posY + elb.getEyeHeight() - 0.4D -
                    Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight();
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D -
                    Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight();
        }


        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[] { yaw, pitch };
    }
    public void setup() {
        final ArrayList<SettingsItem> items = new ArrayList<>();
        ArrayList<String> modes = new ArrayList<>();
        items.add(new SettingsItem("Range", 0.2F, 6, 3, ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }

}



