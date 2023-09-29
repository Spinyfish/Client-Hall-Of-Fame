package dev.elysium.client.mods.impl.movement;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.*;
import dev.elysium.client.utils.player.MovementUtil;
import net.minecraft.potion.Potion;

public class Speed extends Mod {
    public ModeSetting mode = new ModeSetting("Mode",this,"Watchdog","Verus","Vanilla","Strafe","Legit");
    public ModeSetting wdmode = new ModeSetting("Watchdog Mode",this,"Normal","Fast","Low-Hop");
    public NumberSetting timer = new NumberSetting("Timer",0.1,8,1.3,0.05,this);
    public NumberSetting speed = new NumberSetting("Speed",0,10,1,0.01,this);

    public Speed() {
        super("Speed","Modifies player movement", Category.MOVEMENT);
        wdmode.setPredicate(mod -> mode.is("Watchdog"));
        speed.setPredicate(mod -> mode.is("Vanilla"));
    }

    public void onEnable() {
        mc.timer.timerSpeed = (float) timer.getValue();
        super.onEnable();
    }

    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
        mc.thePlayer.speedInAir = 0.02F;
    }

    @EventTarget
    public void onSprint(EventSprint e) {
        if(mode.is("Verus") && mc.thePlayer.isMoving())
            e.setSprinting(true);
    }

    @EventTarget
    public void onEventJump(EventJump e) {
        TargetStrafe targetStrafe = (TargetStrafe) Elysium.getInstance().getModManager().getModByName("TargetStrafe");
        if(!mc.thePlayer.isMoving() || targetStrafe.strafing)
            return;
        e.setYaw(MovementUtil.getDirection() / 0.017453292F);
        e.setSprinting(true);
    }

    @EventTarget
    public void onEventMoveFlying(EventMoveFlying e) {
        if(!mc.thePlayer.onGround)
            e.setFriction(0.025999999F);
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        if(e.isPre())return;
        switch(mode.getMode()) {
            case "Legit":
                if(mc.thePlayer.onGround && mc.thePlayer.isMoving() && !mc.gameSettings.keyBindJump.pressed) {
                    mc.thePlayer.jump();
                }
                break;
            case "Strafe":
                MovementUtil.strafe();
                if(mc.thePlayer.onGround && mc.thePlayer.isMoving() && !mc.gameSettings.keyBindJump.pressed)
                    mc.thePlayer.jump();
                break;
            case "Verus":
                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;

                float speedoffset = (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.12F) : 0);

                if(mc.thePlayer.onGround && mc.thePlayer.isMoving() &&!mc.gameSettings.keyBindJump.pressed && !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -0.01, 0).expand(0, 0, 0)).isEmpty())
                        mc.thePlayer.jump();
                else
                    MovementUtil.strafe(0.377F + speedoffset);
                break;
            case "Watchdog":
                if(mc.thePlayer.isMoving()) mc.thePlayer.setSprinting(true);
                switch(wdmode.getMode()) {
                    case "Normal":
                        MovementUtil.strafe();
                        if(mc.thePlayer.onGround && mc.thePlayer.isMoving() && !mc.gameSettings.keyBindJump.pressed)
                            mc.thePlayer.jump();
                        break;
                    case "Fast":
                        boolean hasSpeed = mc.thePlayer.isPotionActive(Potion.moveSpeed);
                        double speedAmplifier = 0;
                        if(hasSpeed)
                            speedAmplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();

                        mc.thePlayer.motionX *= 0.00075 + 1 + speedAmplifier * 0.02F;
                        mc.thePlayer.motionZ *= 0.00075 + 1 + speedAmplifier * 0.02F;

                        MovementUtil.strafe();
                        if(mc.thePlayer.onGround && mc.thePlayer.isMoving() && !mc.gameSettings.keyBindJump.pressed) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionX *= 0.00075 + 1 + speedAmplifier * 0.2F;
                            mc.thePlayer.motionZ *= 0.00075 + 1 + speedAmplifier * 0.2F;
                            mc.thePlayer.motionY = 0.4;
                        }
                        break;
                    case "Low-Hop":
                        hasSpeed = mc.thePlayer.isPotionActive(Potion.moveSpeed);
                        speedAmplifier = 0;
                        if(hasSpeed)
                            speedAmplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();

                        mc.thePlayer.motionX *= 0.00075 + 1 + speedAmplifier * 0.01F;
                        mc.thePlayer.motionZ *= 0.00075 + 1 + speedAmplifier * 0.01F;

                        MovementUtil.strafe();
                        if(mc.thePlayer.onGround && mc.thePlayer.isMoving() && !mc.gameSettings.keyBindJump.pressed) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionX *= 0.00075 + 1 + speedAmplifier * 0.2F;
                            mc.thePlayer.motionZ *= 0.00075 + 1 + speedAmplifier * 0.2F;
                            mc.thePlayer.motionY = 0.41;
                        }
                        break;
                }
                break;
        }
    }
}
