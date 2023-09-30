package de.verschwiegener.atero.module.modules.combat;



import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.darkmagician6.eventapi.events.callables.EventPostMotionUpdate;
import com.darkmagician6.eventapi.events.callables.EventPreMotionUpdate;
import com.darkmagician6.eventapi.events.callables.EventReceivedPacket;
import de.verschwiegener.atero.module.modules.movement.Speed;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.util.Util;
import net.minecraft.client.Minecraft;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Velocity extends Module {
    TimeUtils timeUtils;
    public static Setting setting;
    public Velocity() {
        super("Velocity", "Velocity", Keyboard.KEY_NONE, Category.Combat);
    }

    public void onEnable() {
        super.onEnable();
        setting = Management.instance.settingsmgr.getSettingByName(getName());
    }

    public void onDisable() {
        super.onDisable();
    }
    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    @EventTarget
    public void onEvent(EventReceivedPacket ppe) {

        //System.out.println("Event");

        if (this.isEnabled()) {
            try {
                Packet p = ppe.getPacket();
                String modes = setting.getItemByName("VelocityMode").getCurrent();
                setExtraTag(modes);
                switch (modes) {
                    case "AAC3.3.12":
                   //     if (mc.thePlayer.isCollidedHorizontally) {

                                if (!Management.instance.modulemgr.getModuleByName("HighJump").isEnabled()) {
                                    final float SPEED = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.1, 0.2);

                                }
                                     if (Minecraft.thePlayer.hurtTime != 0) {
                                         if(!mc.thePlayer.onGround){
                                             setSpeed(setting.getItemByName("AACStrength").getCurrentValue());
                                         }else {
                                             setSpeed(0);
                                         }
                                    if (setting.getItemByName("Jump").isState()) {
                                        if (Minecraft.thePlayer.onGround) {
                                            Minecraft.thePlayer.motionY = 0.42F;
                                        }
                                  //  }
                                }
                            }
                    //    }
                        break;
                    case "AAC3.3.13":
                        //     if (mc.thePlayer.isCollidedHorizontally) {
                        if (Minecraft.thePlayer.hurtTime != 0) {
                            if(!mc.thePlayer.onGround){
                                setSpeed(0.3);
                            }else {
                                setSpeed(0.1);
                            }
                                if (Minecraft.thePlayer.onGround) {
                                    Minecraft.thePlayer.motionY = 0.42F;
                            }
                        }
                        //    }
                        break;
                    case "AAC3.3.14":
                        if (Minecraft.thePlayer.hurtTime != 0) {
                         setSpeed(0);
                        }
                        break;
                    case "Intave":
                        if (mc.thePlayer.isCollidedHorizontally) {
                      if (Minecraft.thePlayer.hurtTime != 0) {
                              // setSpeed(0);
                               if(mc.thePlayer.onGround) {
                                    //mc.thePlayer.motionY = 0.42;
                               }
                            }

                       }
                        break;
                    case "NCP":
                        if (p instanceof S12PacketEntityVelocity) {
                            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) p;
                            if (packet.getEntityID() == Minecraft.thePlayer.getEntityId())
                                ppe.setCancelled(true);
                        }
                        if (p instanceof net.minecraft.network.play.server.S27PacketExplosion)
                            ppe.setCancelled(true);
                        break;


                    case "Cubecraft":
                        if (Minecraft.thePlayer.hurtTime != 0) {
                            if(mc.thePlayer.onGround){
                              mc.thePlayer.motionY = 0.42F;
                                mc.timer.timerSpeed = 1F;
                                	Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = true;
                            } else {
                            boolean boost2 = (Math.abs(mc.thePlayer.rotationYawHead - mc.thePlayer.rotationYaw) < 90.0F);
                                mc.timer.timerSpeed = 1F;
                                double currentSpeed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
                                double speed = boost2 ? 1 : 1D;
                                double direction = Speed.getDirection();
                                mc.thePlayer.motionX = -Math.sin(direction) * speed * currentSpeed;
                                mc.thePlayer.motionZ = Math.cos(direction) * speed * currentSpeed;

                            }
                        }
                        break;
                    case "Mineman":
                        if (Minecraft.thePlayer.hurtTime != 0) {
                            setSpeed(0.1);
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.motionY= 0.42F;
                                mc.thePlayer.motionX *= -1F;
                                mc.thePlayer.motionZ *= -1F;
                            }
                        }
                        break;

                }

            }catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void setup() {
        final ArrayList<SettingsItem> items = new ArrayList<>();
        ArrayList<String> modes = new ArrayList<>();
        modes.add("NCP");
        modes.add("Intave");
        modes.add("Mineman");
        modes.add("Cubecraft");
        modes.add("AAC3.3.12");
        modes.add("AAC3.3.13");
        modes.add("AAC3.3.14");
        items.add(new SettingsItem("VelocityMode", modes, "AAC", "Jump", "AAC3.3.12"));
        items.add(new SettingsItem("WallStrength", 0, 0.4F, 0.4F, ""));
        items.add(new SettingsItem("AACStrength", 0, 0.7F, 0.4F, ""));
        items.add(new SettingsItem("Jump", false, ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }
    public static void setSpeed(double speed) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        double yaw = (double) player.rotationYaw;
        boolean isMoving = player.moveForward != 0.0F || player.moveStrafing != 0.0F;
        boolean isMovingForward = player.moveForward > 0.0F;
        boolean isMovingBackward = player.moveForward < 0.0F;
        boolean isMovingRight = player.moveStrafing > 0.0F;
        boolean isMovingLeft = player.moveStrafing < 0.0F;
        boolean isMovingSideways = isMovingLeft || isMovingRight;
        boolean isMovingStraight = isMovingForward || isMovingBackward;
        if (isMoving) {
            if (isMovingForward && !isMovingSideways) {
                yaw += 0.0D;
            } else if (isMovingBackward && !isMovingSideways) {
                yaw += 180.0D;
            } else if (isMovingForward && isMovingLeft) {
                yaw += 45.0D;
            } else if (isMovingForward) {
                yaw -= 45.0D;
            } else if (!isMovingStraight && isMovingLeft) {
                yaw += 90.0D;
            } else if (!isMovingStraight && isMovingRight) {
                yaw -= 90.0D;
            } else if (isMovingBackward && isMovingLeft) {
                yaw += 135.0D;
            } else if (isMovingBackward) {
                yaw -= 135.0D;
            }

            yaw = Math.toRadians(yaw);
            player.motionX = -Math.sin(yaw) * speed;
            player.motionZ = Math.cos(yaw) * speed;
        }

    }



    @EventTarget
    public void onPost(EventPreMotionUpdate pre) {
        String modes = setting.getItemByName("VelocityMode").getCurrent();
        switch (modes) {
            case "Intave":
        if (Minecraft.thePlayer.hurtTime != 0) {
            final float speed = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.04, 0.08);
            if(!mc.thePlayer.isInLava()) {
                setSpeed(speed);

            if(mc.thePlayer.onGround && mc.thePlayer.ticksExisted % 15 == 0) {
                mc.thePlayer.motionY = 0.42F;
            }
                break;
            }
        }
        }
    }
}
