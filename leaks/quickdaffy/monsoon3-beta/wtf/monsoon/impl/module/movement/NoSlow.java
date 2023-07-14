/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPostMotion;
import wtf.monsoon.impl.event.EventPreMotion;

public class NoSlow
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.VANILLA).describedBy("How to prevent slow down");
    private boolean sneakState = false;
    private boolean sprintState = false;
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (this.mode.getValue()) {
            case NCP: {
                if (!this.mc.thePlayer.isUsingItem() || !this.player.isMoving()) break;
                if (this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    break;
                }
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
                break;
            }
            case WATCHDOG: {
                if (!this.player.isMoving() || !this.mc.thePlayer.isUsingItem()) break;
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
            }
        }
    };
    @EventLink
    public final Listener<EventPostMotion> eventPostMotionListener = e -> {
        switch (this.mode.getValue()) {
            case NCP: {
                if (!this.mc.thePlayer.isUsingItem() || !(this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) break;
                PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
                break;
            }
        }
    };
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            return;
        }
        switch (this.mode.getValue()) {
            case WATCHDOG: {
                break;
            }
        }
    };

    public NoSlow() {
        super("No Slow", "Doesn't slow you down", Category.MOVEMENT);
        this.setMetadata(() -> StringUtil.formatEnum(this.mode.getValue()));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.sneakState && !this.mc.thePlayer.isSneaking()) {
            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            this.sneakState = false;
        }
        if (this.sprintState && this.mc.thePlayer.isSprinting()) {
            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            this.sprintState = false;
        }
    }

    static enum Mode {
        VANILLA("Vanilla"),
        WATCHDOG("Watchdog"),
        NCP("NCP");

        String modeName;

        private Mode(String modeName) {
            this.modeName = modeName;
        }

        public String toString() {
            return this.modeName;
        }
    }
}

