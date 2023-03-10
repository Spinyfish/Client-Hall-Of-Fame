/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.November;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.event.impl.player.EventPostMotion;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.utils.player.MovementUtils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Delayed", "Hypixel", "NCP", "NCP 2");
    private boolean blocking;

    public NoSlow() {
        super("NoSlow", 0, Category.MOVEMENT);
        this.addSetting(this.mode);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        this.setSuffix(this.mode.getMode());
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        boolean notSprinting = November.INSTANCE.getModuleManager().getModule("Sprint").isEnabled() && MovementUtils.isMoving() && NoSlow.mc.thePlayer.isUsingItem();
        switch (this.mode.getMode()) {
            case "Hypixel": {
                if (notSprinting) {
                    NoSlow.mc.thePlayer.setSprinting(true);
                }
                if (!MovementUtils.isMoving() || !NoSlow.mc.thePlayer.isUsingItem()) break;
                this.sendPacketSilent(new C09PacketHeldItemChange(NoSlow.mc.thePlayer.inventory.currentItem));
                break;
            }
            case "Vanilla": {
                if (!notSprinting) break;
                NoSlow.mc.thePlayer.setSprinting(true);
                break;
            }
            case "Delayed": {
                if (notSprinting) {
                    NoSlow.mc.thePlayer.setSprinting(true);
                }
                if (!NoSlow.mc.thePlayer.isBlocking()) {
                    this.blocking = false;
                }
                if (NoSlow.mc.thePlayer.isBlocking() && NoSlow.mc.thePlayer.ticksExisted % 5 == 0 && this.blocking) {
                    NoSlow.mc.playerController.syncCurrentPlayItem();
                    this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    this.blocking = false;
                }
                if (!NoSlow.mc.thePlayer.isBlocking() || NoSlow.mc.thePlayer.ticksExisted % 5 != 1 || this.blocking) break;
                NoSlow.mc.playerController.syncCurrentPlayItem();
                this.sendPacket(new C08PacketPlayerBlockPlacement(NoSlow.mc.thePlayer.getCurrentEquippedItem()));
                this.blocking = true;
                break;
            }
            case "NCP": {
                if (notSprinting) {
                    NoSlow.mc.thePlayer.setSprinting(true);
                }
                if (!NoSlow.mc.thePlayer.isBlocking()) break;
                this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                break;
            }
            case "NCP 2": {
                if (notSprinting) {
                    NoSlow.mc.thePlayer.setSprinting(true);
                }
                if (!NoSlow.mc.thePlayer.isBlocking()) break;
                NoSlow.mc.playerController.syncCurrentPlayItem();
                this.sendPacket(new C08PacketPlayerBlockPlacement(NoSlow.mc.thePlayer.getCurrentEquippedItem()));
            }
        }
    }

    @Override
    @Subscribe
    public void onPostMotion(EventPostMotion event) {
        boolean notSprinting = November.INSTANCE.getModuleManager().getModule("Sprint").isEnabled() && MovementUtils.isMoving() && NoSlow.mc.thePlayer.isUsingItem();
        switch (this.mode.getMode()) {
            case "NCP": {
                if (notSprinting) {
                    NoSlow.mc.thePlayer.setSprinting(true);
                }
                if (!NoSlow.mc.thePlayer.isBlocking()) break;
                this.sendPacket(new C08PacketPlayerBlockPlacement(NoSlow.mc.thePlayer.getHeldItem()));
                break;
            }
            case "NCP 2": {
                if (notSprinting) {
                    NoSlow.mc.thePlayer.setSprinting(true);
                }
                if (!NoSlow.mc.thePlayer.isBlocking()) break;
                NoSlow.mc.playerController.syncCurrentPlayItem();
                this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        }
    }
}

