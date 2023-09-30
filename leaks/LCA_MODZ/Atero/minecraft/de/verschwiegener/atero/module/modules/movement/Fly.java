package de.verschwiegener.atero.module.modules.movement;


import net.minecraft.client.Minecraft;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import de.verschwiegener.atero.util.Util;
import god.buddy.aot.BCompiler;

public class Fly extends Module {
    TimeUtils timeUtils;
    public Fly() {
        super("Fly", "Fly", Keyboard.KEY_NONE, Category.Movement);
    }

    public void onEnable() {
        if(mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.42F;
        }
super.onEnable();
    }

    public void onDisable() {
        mc.timer.timerSpeed =1F;
	Util.setSpeed(0);
        Minecraft.thePlayer.capabilities.isFlying = false;
        super.onDisable();
    }
    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            setExtraTag("AntiVanillaKick");
            double value = 0.1;
            mc.timer.timerSpeed = 1F;
            if(mc.thePlayer.ticksExisted % 15 == 0){
                mc.thePlayer.motionY = 0.05;
            }else {
                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    mc.thePlayer.motionY = 0.1;
                } else {
                    mc.thePlayer.motionY = -0.05;
                }
            }
         //   mc.thePlayer.motionY = 0F;
            Util.setSpeed(2);
            if(mc.gameSettings.keyBindJump.pressed){
                mc.thePlayer.motionY = 1F;
            }
            if(mc.gameSettings.keyBindSneak.pressed){
                mc.thePlayer.motionY = -1F;
            }
        }
    }
}
