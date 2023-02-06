package dev.elysium.client.mods.impl.combat;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.events.EventPacket;
import dev.elysium.client.events.EventRenderHUD;
import dev.elysium.client.utils.Timer;
import dev.elysium.client.utils.player.RotationUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Killaura extends Mod {
    public float yaw, pitch;
    public float syaw, spitch;
    public boolean hasRotated;
    public boolean unblock;
    public EntityLivingBase target;
    public ModeSetting mode = new ModeSetting("Mode",this,"Single","Switch","Multi");
    public NumberSetting switchdelay = new NumberSetting("Switch Delay",50,5000,500,10, this);
    public NumberSetting maxtargets = new NumberSetting("Max Targets",1,8,2,1,this);
    public NumberSetting range = new NumberSetting("Attack Range",0,8,3,0.01,this);
    public NumberSetting aps = new NumberSetting("APS",1,20,8,0.1, this);
    public BooleanSetting swing = new BooleanSetting("Swing",true, this);
    public ModeSetting rotation = new ModeSetting("Rotation",this,"Instant","Random","Smooth","Conservative","Tick Delay","None");
    public NumberSetting tickdelay = new NumberSetting("Tick Delay",2,20,2,1,this);
    public NumberSetting smoothness = new NumberSetting("Smoothness",1,20,5,0.5, this);
    public BooleanSetting lockview = new BooleanSetting("Lockview",false, this);
    public BooleanSetting raytrace = new BooleanSetting("Raytrace",true, this);
    public ModeSetting blockmode = new ModeSetting("Block Mode",this,"Interact","Packet","Legit","Vanilla","None");
    public NumberSetting autoblockrange = new NumberSetting("Block Range",0,15,5,0.1, this);
    public BooleanSetting keepsprint = new BooleanSetting("Keepsprint",true, this);
    public BooleanSetting teams = new BooleanSetting("Teams",true, this);
    public Timer timer = new Timer();
    public EntityLivingBase prevtarget;

    public Killaura() {
        super("Killaura","Kills for you", Category.COMBAT);
        smoothness.setPredicate(mod -> rotation.is("Smooth") || rotation.is("Conservative"));
        lockview.setPredicate(mod -> !rotation.is("None"));
        lockview.setPredicate(mod -> !rotation.is("None"));
        autoblockrange.setPredicate(mod -> !blockmode.is("None"));
        maxtargets.setPredicate(mod -> !mode.is("Single"));
    }

    public void onEnable() {
        syaw = yaw = mc.thePlayer.rotationYaw;
        spitch = pitch = mc.thePlayer.rotationPitch;
        target = null;
    }

    public void onDisable() {
        syaw = yaw = mc.thePlayer.rotationYaw;
        spitch = pitch = mc.thePlayer.rotationPitch;
        target = null;
        if(unblock) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            unblock = false;
        }
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {

        if(e.isPost())
            return;

        if(unblock) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            unblock = false;
        }

        this.target = null;

        List<EntityLivingBase> targets = Elysium.getInstance().getTargets();
        targets = targets.stream().filter(entity -> !entity.getName().contains("CIT-") && (!teams.isEnabled() || !entity.isOnSameTeam(mc.thePlayer)) && !entity.getName().contains("CIT") && !entity.getName().contains("SHOP") && !entity.getName().contains("UPGRADES")).collect(Collectors.toList());

        targets.sort(Comparator.comparingDouble(entity -> (RotationUtil.getYawChange(mc.thePlayer.rotationYaw, ((EntityLivingBase)entity).posX, ((EntityLivingBase)entity).posX))));

        int index = 1;
        hasRotated = false;

        boolean resetValues = false;

        double targetTime = Math.min(maxtargets.getValue(), targets.stream().filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= range.getValue()).collect(Collectors.toList()).size());

        int switchTime = (int) Math.ceil((System.currentTimeMillis() % (switchdelay.getValue() * targetTime)) / (switchdelay.getValue()));

        for(EntityLivingBase target : targets) {

            float dist = mc.thePlayer.getDistanceToEntity(target);

            if(!mode.is("Switch") && (dist <= autoblockrange.getValue() || dist <= range.getValue()))
                Elysium.getInstance().addTarget(target);

            if(maxtargets.getValue() < index && mode.is("Multi") || mode.is("Single") && index > 1) continue;
            if(mode.is("Switch") && dist <= range.getValue()) {
                if(index != switchTime) {
                    index++;
                    continue;
                } else
                    Elysium.getInstance().addTarget(target);
            }

            this.prevtarget = this.target = target;

            if(dist <= autoblockrange.getValue()) {
                doAutoBlock(target);
                resetValues = true;
                if(!hasRotated) {
                    hasRotated = true;
                    LookAt(target, e);
                }
            }

            if(dist <= range.getValue()) {
                index++;
                Attack(target, e);
                resetValues = true;
            }
        }

        if(mode.is("Multi") && timer.hasTimeElapsed((long) (1000 / aps.getValue()), true)) {
            timer.reset();
        }

        if(!resetValues) {
            syaw = yaw = mc.thePlayer.rotationYaw;
            spitch = pitch = mc.thePlayer.rotationPitch;
        }
    }

    public void Attack(EntityLivingBase e, EventMotion event) {
        if(!hasRotated) {
            hasRotated = true;
            LookAt(e, event);
        }

        if(raytrace.isEnabled() && !isLookingAtTarget(e)) return;

        double timervalue = 1000;

        double divisor = aps.getValue() + 1.5 - Math.random()*12;
        if(divisor < 1) divisor = 1;

        if(Math.random() > 0.5){
            timervalue = (1000-(Math.random()*500)) / divisor;
        } else {
            timervalue = (1000+(Math.random()*500)) / divisor;
        }

        timervalue = (timervalue + aps.getValue()) / 2;

        if(timer.hasTimeElapsed((long)(timervalue), false))
        {
            if(blockmode.is("Packet") && mc.thePlayer.isBlocking())
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            if(blockmode.is("Legit"))
                mc.gameSettings.keyBindUseItem.pressed = false;
            if(!swing.isEnabled())
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            else
                mc.thePlayer.swingItem();

            if(keepsprint.isEnabled())
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
            else
                mc.playerController.attackEntity(mc.thePlayer, target);

            if(blockmode.is("Packet") && mc.thePlayer.isBlocking())
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            if(mode.getMode() != "Multi") timer.reset();
        }
    }

    public void LookAt(EntityLivingBase e, EventMotion event) {
        hasRotated = true;
        if(rotation.is("None")) return;
        float yaw = getRotations(e)[0];
        float pitch = getRotations(e)[1];

        event.setYaw(yaw);
        event.setPitch(pitch);

        if(lockview.isEnabled()) {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }

        mc.thePlayer.renderYawOffset = yaw;
        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.rotationPitchHead = pitch;
    }

    public boolean isLookingAtTarget(EntityLivingBase target) {
        float rotationneeded = 15;
        return -rotationneeded <= (getRotations(target)[2] - getRotations(target)[0]) && rotationneeded >= (getRotations(target)[2] - getRotations(target)[0]) || -rotationneeded - 360 <= (getRotations(target)[2] - getRotations(target)[0]) && rotationneeded - 360 >= (getRotations(target)[2] - getRotations(target)[0]);
    }

    public void doAutoBlock(EntityLivingBase target) {
        if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            unblock = true;
            switch(blockmode.getMode()) {
                case "Vanilla":
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                    break;
                case "Legit":
                    mc.gameSettings.keyBindUseItem.pressed = true;
                    break;
                case "Interact":
                    mc.gameSettings.keyBindUseItem.pressed = true;

                    if(!mc.thePlayer.isUsingItem()) {
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                        timer.reset();
                    }
                    break;
                case "Packet":
                    mc.gameSettings.keyBindUseItem.pressed = true;

                    if(!mc.thePlayer.isUsingItem()) {
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                        timer.reset();
                    }
                    break;
            }
        }
    }

    public float[] getRotations(EntityLivingBase e) {
        double deltaX = e.posX + (e.posX - e.lastTickPosX) - this.mc.thePlayer.posX;
        double deltaY = e.posY - 3.5 + (e.posY - e.lastTickPosY) + (double)e.getEyeHeight()*1.3185 - this.mc.thePlayer.posY + (double)this.mc.thePlayer.getEyeHeight()-0.25592;
        double deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - this.mc.thePlayer.posZ;
        double distance = Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0));
        float yawdiff = RotationUtil.getYawChange(syaw, e.posX + (e.posX - e.lastTickPosX), e.posZ + (e.posZ - e.lastTickPosZ));
        float pitchdiff = RotationUtil.getPitchChange(spitch, e, e.posY);

        yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
        pitch = (float) ((float)(-Math.toDegrees(Math.atan(deltaY / distance)))-2+((target.getDistanceToEntity(mc.thePlayer)*0.2))) + 1;
        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = (float)(90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = (float)(-90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }

        yaw = syaw + RotationUtil.getYawChange(syaw, yaw);

        float dyaw = yaw;
        float dpitch = pitch;

        double smoothnessValue = (smoothness.getValue());

        switch (rotation.getMode()) {
            case "Tick Delay":
                if(mc.thePlayer.ticksExisted % tickdelay.getValue() == 0) {
                    yaw = syaw;
                    pitch = spitch;
                }
                break;
            case "Random":
                yaw += Math.random() - 0.5;
                pitch += Math.random() - 0.5;
                break;
            case "Smooth":

                if(Math.abs(yawdiff) > 1) {
                    yaw = (float) (syaw + yawdiff / smoothnessValue);
                    pitch += Math.random() - 0.5;
                } else
                    yaw = syaw;

                if(Math.abs(pitchdiff) > 1) {
                    pitch = (float) (spitch + pitchdiff / smoothnessValue);
                    yaw += Math.random() - 0.5;
                } else
                    pitch = spitch;
                break;
            case "Conservative":
                if(Math.abs(pitchdiff) > 5) {
                    pitch = (float) (spitch + pitchdiff / smoothnessValue);
                    yaw += Math.random() - 0.5;
                }

                if(-15 <= (dyaw - syaw) && 15 >= (dyaw - syaw) || -15 - 360 <= (dyaw - syaw) && 15 - 360 >= (dyaw - syaw)) {
                    yaw = syaw;
                    pitch = spitch;
                    break;
                }

                if(Math.abs(yawdiff) > 1) {
                    yaw = (float) (syaw + yawdiff / smoothnessValue);
                    pitch += Math.random() - 0.5;
                }
                break;
        }

        float gcd = this.mc.gameSettings.mouseSensitivity * 0.8F + 0.2F;
        gcd = gcd * gcd * gcd * 8.0F;

        pitch -= pitch % gcd;
        yaw -= yaw % gcd;

        if(pitch > 90) pitch = 90;
        else if(pitch < -90) pitch = -90;
        syaw = yaw;
        spitch = pitch;
        return new float[]{yaw, pitch, dyaw, dpitch};
    }
}
