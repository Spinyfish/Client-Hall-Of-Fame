package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.component.impl.player.ItemDamageComponent;
import com.alan.clients.component.impl.player.PingSpoofComponent;
import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PostStrafeEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WatchdogFlight extends Mode<Flight> {
    private final List<Packet<?>> packets = new ArrayList<>();
    private double speed;
    public Vec3 position = new Vec3(0, 0, 0);
    private boolean aBoolean;
    private boolean aBoolean2;
    private int anInt;
    private int anInt2;
    private int anInt3;
    private int ticks;
    private double start;
    private double startx;
    private double startz;


    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
    };

    @EventLink(value = Priorities.LOW)
    public final Listener<PacketReceiveEvent> receive = event -> {
        Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

            if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
                event.setCancelled(true);
                packets.add(packet);
            }
        }
    };

    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
        FontManager.getProductSansMedium(18).drawString("Distance Flown:" + Math.round(mc.thePlayer.getDistance(startx, mc.thePlayer.posY, startz)), 445,
                285, new Color(255, 255, 255, 255).getRGB());

    };

    @EventLink
    public final Listener<PreUpdateEvent> preUpdate = event -> {
        ticks++;

        if (anInt3 < 2 && !ItemDamageComponent.active) {
            ItemDamageComponent.damage(true);
            MoveUtil.stop();
        }


        if (mc.thePlayer.ticksSinceVelocity == 1) {
            anInt3++;
        }
        PingSpoofComponent.setSpoofing(19000, true, false, false, false, false);
    };


    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {


        ChatUtil.display(mc.thePlayer.ticksSincePlayerVelocity);
        if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
            mc.thePlayer.jump();
        }

        if (start >= mc.thePlayer.posY) {
            if (!aBoolean) {
                if (!packets.isEmpty()) {
                    PacketUtil.receiveNoEvent(packets.get(0));
                    MoveUtil.strafe();
                    anInt = 0;
                    aBoolean2 = false;
                    packets.remove(0);
                    if (mc.thePlayer.ticksSincePlayerVelocity > 20 && mc.thePlayer.ticksSincePlayerVelocity < 24) {
                        speed = (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.35 - mc.thePlayer.ticksSincePlayerVelocity / 100f : 1.3 - mc.thePlayer.ticksSincePlayerVelocity / 100f);
                    } else if (mc.thePlayer.ticksSincePlayerVelocity < 20) {
                        speed = (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.35 - mc.thePlayer.ticksSincePlayerVelocity / 100f : 1.3 - mc.thePlayer.ticksSincePlayerVelocity / 100f);
                    }

                }
            }

            aBoolean = true;
        } else {
            aBoolean = false;
        }

        anInt++;
        anInt2++;

        if (!aBoolean2 && anInt2 > 20) {
            if (anInt <= 10 && anInt > 1) {
                mc.thePlayer.motionY += 0.02;
            }
        }
    };

    @EventLink()
    public final Listener<PostStrafeEvent> onStrafe = event -> {
        if (anInt <= 20 && mc.thePlayer.ticksSincePlayerVelocity > 0 && mc.thePlayer.ticksSincePlayerVelocity < 20) {
            MoveUtil.strafe(speed);
            speed = Math.max(0, speed - ((speed / 229.9) + Math.random() / 100f));
        } else if (mc.thePlayer.ticksSincePlayerVelocity > 20) {
            if (mc.thePlayer.ticksSincePlayerVelocity == 22) {
                mc.thePlayer.motionX *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.4 : 1.35;
                mc.thePlayer.motionZ *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.4 : 1.35;
            }
            if (mc.thePlayer.ticksSincePlayerVelocity == 24) {
                mc.thePlayer.motionX *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.038 : 1.03;
                mc.thePlayer.motionZ *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.038 : 1.03;
            }
            if (mc.thePlayer.ticksSincePlayerVelocity == 27) {
                mc.thePlayer.motionX *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 2.9 : 2.75;
                mc.thePlayer.motionZ *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 2.9 : 2.75;
            }
        }
    };

    public WatchdogFlight(String name, Flight parent) {
        super(name, parent);
    }

    @Override
    public void onDisable() {
//        NotificationComponent.post("Flight", "Please wait until this disappears to fly again.", 10000);
        mc.timer.timerSpeed = 1.0f;
        while (!packets.isEmpty()) {
            PacketUtil.receiveNoEvent(packets.get(0));
            MoveUtil.strafe();
            packets.remove(0);
        }
    }

    @Override
    public void onEnable() {
        startx = mc.thePlayer.posX;
        startz = mc.thePlayer.posZ;
        ticks = 0;
        anInt3 = 0;
        speed = 0;
        start = mc.thePlayer.posY;
    }
}