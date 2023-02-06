package dev.elysium.client.mods.impl.combat;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.utils.player.RotationUtil;
import net.minecraft.entity.EntityLivingBase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AimAssist extends Mod {
    public BooleanSetting whileClick = new BooleanSetting("Click to aim", true, this);
    public BooleanSetting hitbox = new BooleanSetting("Only while not looking", false, this);
    public NumberSetting range = new NumberSetting("Range", 0, 20, 6, 0.25, this);
    public ModeSetting lock = new ModeSetting("Lock",this,"Head","Head","Torso","Legs");
    public ModeSetting mode = new ModeSetting("Rotate",this,"Both","Yaw Only","Pitch Only");
    public NumberSetting jitter = new NumberSetting("Jitter Size", 0,100,25, 1,this);
    public ModeSetting curve = new ModeSetting("Curve",this,"Linear","Logarithmic","Exponential");
    public NumberSetting speed = new NumberSetting("Turn Speed", 0, 100, 50,2,this);
    public NumberSetting fov = new NumberSetting("FOV", 0, 360, 360, 1, this);
    public BooleanSetting teams = new BooleanSetting("Teams", true,this);

    public float yaw, pitch;
    public float otherYaw, otherPitch;

    public EntityLivingBase target = null;

    public AimAssist() {
        super("Aim Assist","Helps you aim", Category.COMBAT);
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        if(e.isPost()) return;
        List<EntityLivingBase> targets = Elysium.getInstance().getTargets();

        if(!targets.contains(target))
            target = null;
        else
            Elysium.getInstance().addTarget(target);

        targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < range.getValue() && !entity.getName().contains("CIT-") && (!teams.isEnabled() || !entity.isOnSameTeam(mc.thePlayer)) && !entity.getName().contains("CIT")  && entity != mc.thePlayer && !entity.getName().contains("SHOP") && !entity.getName().contains("UPGRADES") && !entity.isInvisible() && !entity.isDead && entity.getHealth() > 0).collect(Collectors.toList());

        targets.sort(Comparator.comparingDouble(entity -> (RotationUtil.getYawChange(mc.thePlayer.rotationYaw, ((EntityLivingBase)entity).posX, ((EntityLivingBase)entity).posX))));



        if(mc.gameSettings.keyBindAttack.isKeyDown() || !whileClick.isEnabled()) {
            if (!targets.isEmpty() && !mc.thePlayer.isDead) {
                while(true) {
                    if (!hitbox.isEnabled() || !(mc.objectMouseOver.entityHit instanceof EntityLivingBase)) {
                        EntityLivingBase en = targets.get(0);
                        target = en;
                        Elysium.getInstance().addTarget(en);
                        double oh = en.posY;

                        switch(lock.getMode()) {
                            case "Torso":
                                en.posY -= 0.3;
                                break;
                            case "Legs":
                                en.posY -= 0.7;
                                break;
                        }

                        double predTicks = 5;
                        double predSize = 0.5;
                        double posZ = en.posZ + (en.posZ - en.lastTickPosZ)*predTicks*predSize;
                        double posX = en.posX + (en.posX - en.lastTickPosX)*predTicks*predSize;
                        double desiredYawGrowth = RotationUtil.getYawChange(mc.thePlayer.rotationYaw, posX, posZ);

                        if(Math.abs(desiredYawGrowth) > fov.getValue()) break;

                        pitch = mc.thePlayer.rotationPitch;
                        yaw = mc.thePlayer.rotationYaw;

                        boolean shouldChangeYaw = mode.getMode() != "Pitch Only";
                        boolean shouldChangePitch = mode.getMode() != "Yaw Only";

                        if(shouldChangeYaw) {
                            int direction = desiredYawGrowth > 0 ? 1 : -1;
                            double yawgrowth = desiredYawGrowth/(100+Math.random()*40)*speed.getValue();
                            switch(curve.getMode()) {
                                case "Logarithmic":
                                    yaw = (float) (yaw + yawgrowth);
                                    break;
                                case "Linear":
                                    if(Math.abs(yawgrowth) > speed.getValue()/500) {
                                        yaw += Math.min(Math.abs(yawgrowth > 0 ? speed.getValue() / 10 : -speed.getValue() / 10), Math.abs(desiredYawGrowth))*direction;
                                    }
                                    break;
                                case "Exponential":
                                    yaw = (float) (yaw + ((desiredYawGrowth-yawgrowth)/speed.getMax())*speed.getValue());
                                    break;
                            }
                        }

                        if(shouldChangePitch) {

                            double desiredGrowth = RotationUtil.getPitchChange(mc.thePlayer.rotationPitch, en, en.posY);
                            double pitchGrowth = (desiredGrowth/(100+Math.random()*40))*speed.getValue();
                            int direction = desiredGrowth > 0 ? 1 : -1;
                            switch(curve.getMode()) {
                                case "Logarithmic":
                                    pitch = (float) (pitch + ((RotationUtil.getPitchChange(mc.thePlayer.rotationPitch, en, en.posY)/(100+Math.random()*40))*speed.getValue()));
                                    break;
                                case "Linear":
                                    if(Math.abs(pitchGrowth) > speed.getValue()/500) {
                                        pitch += Math.min(Math.abs(pitchGrowth > 0 ? speed.getValue() / 10 : -speed.getValue() / 10), Math.abs(desiredGrowth))*direction;
                                    }
                                    break;
                                case "Exponential":
                                    pitch = (float) (pitch + ((desiredGrowth-pitchGrowth)/speed.getMax())*speed.getValue());
                                    break;
                            }

                            if(pitch > 90) pitch = 90;
                            if(pitch < -90) pitch = -90;

                        }

                        float yawDiff = Math.abs(yaw - mc.thePlayer.rotationYaw);
                        float pitchDiff = Math.abs(pitch - mc.thePlayer.rotationPitch);

                        if(yawDiff > 1 || pitchDiff > 1) {
                            float yawJitter = (float) (((Math.random()-0.565)/(100+(Math.random()-0.5)*40))*jitter.getValue());
                            float pitchJitter = (float) (jitter.getValue()*0.01 + (((Math.random()+0.15)/100/(100+Math.random()*40))*jitter.getValue()));
                            yawJitter = Math.random() > 0.1 ? -yawJitter : yawJitter;
                            pitchJitter = Math.random() > 0.5 ? -pitchJitter : pitchJitter;
                            yaw += yawJitter;
                            pitch += pitchJitter;
                        }

                        if(pitch > 90) pitch = 90;
                        if(pitch < -90) pitch = -90;

                        float gcd = this.mc.gameSettings.mouseSensitivity * 0.8F + 0.2F;
                        gcd = gcd * gcd * gcd * 8.0F;

                        pitch -= pitch % gcd;
                        yaw -= yaw % gcd;

                        mc.thePlayer.rotationPitch += pitch - mc.thePlayer.rotationPitch;
                        mc.thePlayer.rotationYaw += yaw - mc.thePlayer.rotationYaw;
                        otherPitch = pitch;
                        otherYaw = yaw;

                        en.posY = oh;
                    }
                    break;
                }
            }
        }
        otherPitch = pitch = mc.thePlayer.rotationPitch;
        otherYaw = yaw = mc.thePlayer.rotationYaw;
    }
}
