package com.alan.clients.module.impl.player.antivoid;

import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.component.impl.player.FallDistanceComponent;
import com.alan.clients.module.impl.movement.LongJump;
import com.alan.clients.module.impl.other.Test;
import com.alan.clients.module.impl.player.AntiVoid;
import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.NumberValue;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.Vec3;

public class BlinkAntiVoid extends Mode<AntiVoid> {

    private final NumberValue distance = new NumberValue("Distance", this, 5, 0, 10, 1);
    private final BooleanValue toggleScaffold = new BooleanValue("Toggle Scaffold", this, false);
    private Vec3 position, motion;
    private boolean wasVoid, setBack;
    private int overVoidTicks;
    private Scaffold scaffold;
    private LongJump longJump;
    private Test test;

    public BlinkAntiVoid(String name, AntiVoid parent) {
        super(name, parent);
    }

    @Override
    public void onDisable() {
        BlinkComponent.blinking = false;
    }

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        if (mc.thePlayer.ticksExisted <= 50) return;

        if (scaffold == null) {
            scaffold = getModule(Scaffold.class);
        }

        if (longJump == null) {
            longJump = getModule(LongJump.class);
        }

        if (test == null) {
            test = getModule(Test.class);
        }

        if (scaffold.isEnabled() || longJump.isEnabled() || test.isEnabled()) {
            return;
        }

        boolean overVoid = !mc.thePlayer.onGround && !PlayerUtil.isBlockUnder(30, true);

        if (overVoid) {
            overVoidTicks++;
        } else if (mc.thePlayer.onGround) {
            overVoidTicks = 0;
        }

        if (overVoid && position != null && motion != null && overVoidTicks < 30 + distance.getValue().doubleValue() * 20) {
            if (!setBack) {
                wasVoid = true;

                BlinkComponent.blinking = true;
                BlinkComponent.setExempt(C0FPacketConfirmTransaction.class, C00PacketKeepAlive.class, C01PacketChatMessage.class);

                if (FallDistanceComponent.distance > distance.getValue().doubleValue() || setBack) {
                    PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(position.xCoord, position.yCoord - 0.1 - Math.random(), position.zCoord, false));
                    if (this.toggleScaffold.getValue()) {
                        getModule(Scaffold.class).setEnabled(true);
                    }

                    BlinkComponent.packets.clear();

                    FallDistanceComponent.distance = 0;

                    setBack = true;
                }
            } else {
                BlinkComponent.blinking = false;
            }
        } else {

            setBack = false;

            if (wasVoid) {
                BlinkComponent.blinking = false;
                wasVoid = false;
            }

            motion = new Vec3(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
            position = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        }
    };
}