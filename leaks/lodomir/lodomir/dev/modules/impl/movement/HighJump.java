/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.notification.Notification;
import lodomir.dev.ui.notification.NotificationManager;
import lodomir.dev.ui.notification.NotificationType;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.MovementUtils;
import net.minecraft.block.BlockSlime;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class HighJump
extends Module {
    public NumberSetting speed = new NumberSetting("Speed", 0.1, 10.0, 1.0, 0.1);
    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "Fireball");
    public TimeUtils timer = new TimeUtils();

    public HighJump() {
        super("HighJump", 0, Category.MOVEMENT);
        this.addSetting(this.mode);
        this.addSetting(this.speed);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        this.setSuffix(this.mode.getMode());
        switch (this.mode.getMode()) {
            case "Vanilla": {
                if (!HighJump.mc.thePlayer.onGround) break;
                HighJump.mc.thePlayer.jump();
                HighJump.mc.thePlayer.motionY = this.speed.getValueFloat();
                break;
            }
            case "Verus": {
                BlockPos pos = new BlockPos(HighJump.mc.thePlayer.posX, HighJump.mc.thePlayer.posY - 1.0, HighJump.mc.thePlayer.posZ);
                if (!(HighJump.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockSlime)) break;
                if (HighJump.mc.thePlayer.onGround && HighJump.mc.thePlayer.motionY > 0.0) {
                    HighJump.mc.thePlayer.jump();
                }
                if (HighJump.mc.thePlayer.ticksExisted % 7 != 0 || !HighJump.mc.thePlayer.onGround) break;
                HighJump.mc.thePlayer.jump();
                HighJump.mc.thePlayer.motionY = this.speed.getValueFloat();
                break;
            }
            case "Fireball": {
                if (HighJump.mc.thePlayer.onGround && HighJump.mc.thePlayer.hurtTime > 0) {
                    HighJump.mc.thePlayer.jump();
                }
                double movement = MovementUtils.getBaseMoveSpeed() - 0.01;
                if (!HighJump.mc.thePlayer.onGround && MovementUtils.isMoving() && HighJump.mc.thePlayer.ticksExisted % 7 == 0) {
                    MovementUtils.setMotion((float)(movement + Math.random() / 2000.0));
                }
                if (!HighJump.mc.thePlayer.onGround || HighJump.mc.thePlayer.hurtTime <= 0) break;
                HighJump.mc.thePlayer.jump();
                HighJump.mc.thePlayer.motionY = this.speed.getValueFloat();
            }
        }
        super.onUpdate(event);
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        switch (this.mode.getMode()) {
            case "Fireball": {
                if (MovementUtils.getOnRealGround(HighJump.mc.thePlayer, 1.0)) {
                    HighJump.mc.thePlayer.rotationPitch = 90.0f;
                    HighJump.mc.gameSettings.keyBindUseItem.pressed = true;
                    break;
                }
                this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                HighJump.mc.gameSettings.keyBindUseItem.pressed = false;
                this.timer.reset();
            }
        }
    }

    @Override
    public void onEnable() {
        if (this.mode.isMode("Verus")) {
            NotificationManager.show(new Notification(NotificationType.INFO, "Certified Verus Moment", "Stand on slimeblock", 1));
        }
        super.onEnable();
    }
}

