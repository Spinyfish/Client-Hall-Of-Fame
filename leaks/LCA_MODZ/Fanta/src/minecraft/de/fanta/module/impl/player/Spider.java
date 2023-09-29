package de.fanta.module.impl.player;

import java.awt.Color;

import de.fanta.events.Event;
import de.fanta.events.listeners.EventPacket;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.TimeUtil;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Spider extends Module {
    public Spider() {
        super("Spider", 0, Type.Player, Color.red);

        this.settings.add(new Setting("SpiderMode", new DropdownBox("Intave", new String[] { "Intave", "AntiAC" })));

    }

    TimeUtil time = new TimeUtil();



    public void onEnable() {

        super.onEnable();
    }

    @Override
    public void onEvent(Event event) {

        if (event instanceof EventTick) {
            double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
            switch (((DropdownBox) this.getSetting("SpiderMode").getSetting()).curOption) {
            case "Intave":
                if (mc.thePlayer.isCollidedHorizontally) {
                  //  mc.gameSettings.keyBindJump.pressed = true;
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    } else {
                        if (mc.thePlayer.motionY < 0.03) {
                                mc.thePlayer.motionY += 0.08;
                        }
                    }
                    if (mc.thePlayer.motionY > 0.03) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.001, mc.thePlayer.posZ);
                        mc.thePlayer.onGround = true;
                        if (mc.thePlayer.moveForward > 0) {
                            mc.thePlayer.motionX = -Math.sin(yaw) * 0.04;
                            mc.thePlayer.motionZ = Math.cos(yaw) * 0.04;
                        }
                    }
                }
                break;
            case "AntiAC":

                if(mc.thePlayer.onGround) {
                    if (event instanceof EventTick) {
//                    mc.gameSettings.keyBindAttack.pressed = true;
//                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//                            mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
//                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//                            mc.thePlayer.posY, mc.thePlayer.posZ, false));
//                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//                            mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    }
                }


                mc.timer.timerSpeed = 1F;
                mc.thePlayer.stepHeight = 0.1f;

                if (!mc.thePlayer.isCollidedHorizontally) {
                    mc.timer.timerSpeed = 1F;
                }else {
                    mc.timer.timerSpeed = 1F;
                }
                if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                } else if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.onGround
                        && mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.thePlayer.onGround = true;                
                        mc.thePlayer.motionY = 0.42F;

                }
                break;

            }
        }
    }
}