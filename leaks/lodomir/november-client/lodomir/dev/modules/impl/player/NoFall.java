/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.ModeSetting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class NoFall
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Spoof", "Spoof", "Packet", "Edit", "Verus", "Vulcan");
    private float fallDist = 0.0f;

    public NoFall() {
        super("NoFall", 0, Category.PLAYER);
        this.addSetting(this.mode);
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        this.setSuffix(this.mode.getMode());
        switch (this.mode.getMode()) {
            case "Packet": {
                if (!(NoFall.mc.thePlayer.fallDistance > 2.5f)) break;
                this.sendPacketSilent(new C03PacketPlayer(true));
                break;
            }
            case "Verus": {
                double ground = (double)Math.round(event.getY() / 0.015625) * 0.015625;
                if (!((double)NoFall.mc.thePlayer.fallDistance - NoFall.mc.thePlayer.motionY > 4.0) || NoFall.mc.thePlayer.ticksExisted % 4 != 0) break;
                NoFall.mc.thePlayer.setPosition(NoFall.mc.thePlayer.posX, ground, NoFall.mc.thePlayer.posZ);
                event.setY(ground);
                break;
            }
            case "Spoof": {
                if (!(NoFall.mc.thePlayer.fallDistance > 2.5f)) break;
                event.setOnGround(true);
                NoFall.mc.thePlayer.fallDistance = 0.0f;
                break;
            }
            case "Vulcan": {
                double mathGround = (double)Math.round(event.getY() / 0.015625) * 0.015625;
                if (NoFall.mc.thePlayer.fallDistance > 4.0f && NoFall.mc.thePlayer.ticksExisted % 6 == 0) {
                    NoFall.mc.thePlayer.setPosition(NoFall.mc.thePlayer.posX, mathGround, NoFall.mc.thePlayer.posZ);
                    event.setY(mathGround);
                    mathGround = (double)Math.round(event.getY() / 0.015625) * 0.015625;
                    if (!(Math.abs(mathGround - event.getY()) < 0.01)) break;
                    if (NoFall.mc.thePlayer.motionY < -0.4) {
                        NoFall.mc.thePlayer.motionY = -0.4;
                    }
                    NoFall.mc.timer.timerSpeed = 0.4f;
                    break;
                }
                if (NoFall.mc.timer.timerSpeed != 0.4f) break;
                NoFall.mc.timer.timerSpeed = 1.0f;
                this.sendPacket(new C03PacketPlayer(true));
                break;
            }
            case "Edit": {
                double d;
                if (this.fallDist > NoFall.mc.thePlayer.fallDistance) {
                    this.fallDist = 0.0f;
                }
                if (!(NoFall.mc.thePlayer.motionY < 0.0) || !((double)NoFall.mc.thePlayer.fallDistance > 2.124) || this.checkVoid(NoFall.mc.thePlayer) || !this.isBlockUnder() || NoFall.mc.thePlayer.isSpectator() || NoFall.mc.thePlayer.capabilities.allowFlying) break;
                double fallingDist = NoFall.mc.thePlayer.fallDistance - this.fallDist;
                double motionY = NoFall.mc.thePlayer.motionY;
                double realDist = fallingDist + -((motionY - 0.08) * (double)0.98f);
                if (!(d >= 3.0)) break;
                event.setOnGround(true);
            }
        }
    }

    @Override
    public void onDisable() {
        NoFall.mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (this.mode.isMode("Verus") && event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer)event.getPacket();
            packet.onGround = true;
        }
        super.onGetPacket(event);
    }

    private boolean checkVoid(EntityLivingBase entity) {
        for (int b = -1; b <= 0; b = (int)((byte)(b + 1))) {
            for (int b2 = -1; b2 <= 0; b2 = (int)((byte)(b2 + 1))) {
                if (!this.isVoid(b, b2, entity)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isVoid(int X, int Z, EntityLivingBase entity) {
        if (NoFall.mc.thePlayer.posY < 0.0) {
            return true;
        }
        for (int off = 0; off < (int)entity.posY + 2; off += 2) {
            AxisAlignedBB bb = entity.getEntityBoundingBox().offset(X, -off, Z);
            if (NoFall.mc.theWorld.getCollidingBoundingBoxes(entity, bb).isEmpty()) continue;
            return false;
        }
        return true;
    }

    private boolean isBlockUnder() {
        int offset = 0;
        while ((double)offset < NoFall.mc.thePlayer.posY + (double)NoFall.mc.thePlayer.getEyeHeight()) {
            AxisAlignedBB boundingBox = NoFall.mc.thePlayer.getEntityBoundingBox().offset(0.0, -offset, 0.0);
            if (NoFall.mc.theWorld.getCollidingBoundingBoxes(NoFall.mc.thePlayer, boundingBox).isEmpty()) {
                return true;
            }
            offset += 2;
        }
        return false;
    }
}

