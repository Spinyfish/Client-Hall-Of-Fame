package crispy.features.hacks.impl.movement;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.misc.AntiVoid;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.util.player.PlayerUtil;
import crispy.util.player.SpeedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Timer;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;

@HackInfo(name = "Longjump", category = Category.MOVEMENT)
public class LongJump extends Hack {
    ModeValue mode = new ModeValue("LongJump Mode", "Matrix", "Matrix", "AAC 4.0");
    BooleanValue disableLag = new BooleanValue("Disable lag backs", true);
    NumberValue<Double> height = new NumberValue<Double>("Height", 0.1, 0.01, 0.9, () -> mode.getMode().equalsIgnoreCase("Matrix"));
    public LongJump() {

    }
    boolean moved;
    public int redesky;
    boolean jumped = false;


    @Override
    public void onEnable() {
        moved = false;
        jumped = false;
        if(Minecraft.theWorld != null) {
            mc.thePlayer.speedInAir = 0.02f;
            if(mode.getMode().equalsIgnoreCase("Matrix")) {
                mc.thePlayer.jump();
            }
        }


        super.onEnable();
    }

    @Override
    public void onDisable() {
        if(mode.getObject() != 0) {
            NotificationPublisher.queue("[WARNING] Anti flag", "Please do not toggle longjump until this goes away!", NotificationType.WARNING, 5000);
        }
        if (Minecraft.theWorld != null) {
            Timer.timerSpeed = 1;
            mc.thePlayer.speedInAir = 0.02f;

            redesky = 0;
            if(!SpeedUtils.isOnGround(0.05)) {
                Crispy.INSTANCE.getHackManager().getHack(AntiVoid.class).setFlagged(true);
            }
        }

        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            setDisplayName("Longjump \2477" + mode.getModes()[mode.getObject()]);
            if (mode.getObject() == 1) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }

                if(PlayerUtil.isMoving2()) {
                    Timer.timerSpeed = 1.3f;
                    mc.thePlayer.speedInAir = 0.11f;
                    mc.thePlayer.motionY += 0.037;
                    redesky++;
                    if(redesky > 20) {
                        toggle();
                    }

                } else {
                    Timer.timerSpeed = 1;
                    toggle();
                }

            }
            if (mode.getObject() == 0) {
                if(mc.thePlayer.onGround) {
                    moved = true;
                }
                if(!moved)
                    return;
                SpeedUtils.setMotion(SpeedUtils.getSpeed());
                event.sneak = true;
                if (SpeedUtils.isOnGround(0.05)) {
                    if (redesky > 30) {
                        toggle();

                        mc.thePlayer.setSprinting(false);
                    } else {
                        Timer.timerSpeed = (float) 0.7774;
                        mc.thePlayer.motionY = height.getObject();
                        mc.thePlayer.speedInAir = 0.15f;
                    }
                } else {
                    redesky++;
                    if (redesky < 30) {

                        Timer.timerSpeed = (float) 0.8774;
                        mc.thePlayer.motionY += 0.037;
                    }
                    if(redesky > 60) {
                        toggle();
                    }
                    if (redesky < 20) {
                        Timer.timerSpeed = (float) 1.0774;

                    }
                    if (redesky < 10 && redesky > 30) {
                        mc.thePlayer.speedInAir = 0.07f;
                        redesky++;
                        mc.thePlayer.motionY = 0.3;

                    }
                    if (redesky < 10 && redesky > 20) {
                        mc.thePlayer.speedInAir = 0.02f;
                        Timer.timerSpeed = (float) 0.573434;

                    }

                }


            }
        } else if(e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
            if(disableLag.getObject() && Minecraft.theWorld == null)
                setState(false);
            if(disableLag.getObject() && packet instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) packet;
                double x = s08PacketPlayerPosLook.getX();
                double y = s08PacketPlayerPosLook.getY();
                double z = s08PacketPlayerPosLook.getZ();
                double distancex = Math.abs(Math.abs(mc.thePlayer.posX) - Math.abs(x));
                double distancez = Math.abs(Math.abs(mc.thePlayer.posZ) - Math.abs(z));
                double distance = Math.sqrt(distancex *distancex + distancez * distancez);
                if(distance > 10) {
                    toggle();
                    e.setCancelled(false);
                } else {
                    e.setCancelled(true);
                    mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, s08PacketPlayerPosLook.getYaw(), s08PacketPlayerPosLook.getPitch(), true));
                }
            }

        }
    }
}
