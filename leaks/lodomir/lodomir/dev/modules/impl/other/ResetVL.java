/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.player.EventMove;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.utils.math.TimeUtils;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;

public class ResetVL
extends Module {
    public TimeUtils timer = new TimeUtils();

    public ResetVL() {
        super("ResetVL", 0, Category.OTHER);
    }

    @Override
    @Subscribe
    public void onMove(EventMove event) {
        if (ResetVL.mc.thePlayer.onGround) {
            ResetVL.mc.thePlayer.motionY = 0.42;
            event.setY(0.42);
        }
    }

    @Override
    public void onEnable() {
        this.timer.reset();
        super.onEnable();
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        if (this.timer.hasReached(1000L) && ResetVL.mc.thePlayer.onGround) {
            this.sendPacketSilent(new C0BPacketEntityAction(ResetVL.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            this.setEnabled(false);
        }
        if (!ResetVL.mc.thePlayer.onGround) {
            ResetVL.mc.thePlayer.motionZ = 0.0;
            ResetVL.mc.thePlayer.motionX = 0.0;
            if (this.timer.getCurrentMS() % 50L == 0L) {
                this.sendPacketSilent(new C0BPacketEntityAction(ResetVL.mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.sendPacketSilent(new C0BPacketEntityAction(ResetVL.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
        } else if (ResetVL.mc.thePlayer.getHeldItem() != null && ResetVL.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && this.timer.getCurrentMS() % 250L == 0L) {
            this.sendPacketSilent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, ResetVL.mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
        }
    }
}

