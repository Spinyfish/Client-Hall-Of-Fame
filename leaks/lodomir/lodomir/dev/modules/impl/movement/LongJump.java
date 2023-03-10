/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.player.EventMove;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.event.impl.player.EventStrafe;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.MovementUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class LongJump
extends Module {
    public boolean jumped;
    public boolean flag;
    public int i;
    public int ticks;
    public static float pitch;
    public int slot;
    private TimeUtils timer = new TimeUtils();
    public int currentSlot;
    ItemStack itemStack = null;
    public NumberSetting speed = new NumberSetting("Speed", 0.2, 10.0, 1.0, 0.1);
    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "Minemora", "Bow");
    public NumberSetting v = new NumberSetting("Vertical", 0.1, 10.0, 1.0, 0.1);
    public NumberSetting h = new NumberSetting("Horizontal", 0.1, 10.0, 1.0, 0.1);
    public BooleanSetting stop = new BooleanSetting("", true);

    public LongJump() {
        super("LongJump", 0, Category.MOVEMENT);
        this.addSetting(this.speed);
        this.addSetting(this.v);
        this.addSetting(this.h);
        this.addSetting(this.mode);
    }

    @Override
    @Subscribe
    public void onStrafe(EventStrafe event) {
        if (!this.jumped && this.mode.isMode("Bow")) {
            event.setCancelled(true);
        } else if (!this.jumped && this.mode.isMode("Verus") && LongJump.mc.thePlayer.hurtTime < 0) {
            event.setCancelled(true);
        }
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (this.mode.isMode("Bow")) {
            this.v.setVisible(true);
            this.h.setVisible(true);
        } else {
            this.v.setVisible(false);
            this.h.setVisible(false);
        }
        if (!this.mode.isMode("Bow")) {
            this.speed.setVisible(true);
        } else {
            this.speed.setVisible(false);
        }
    }

    @Subscribe
    public void onUpdate(lodomir.dev.event.impl.game.EventUpdate event) {
        this.setSuffix(this.mode.getMode());
        switch (this.mode.getMode()) {
            case "Vanilla": 
            case "Verus": {
                if (LongJump.mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    LongJump.mc.thePlayer.jump();
                }
                MovementUtils.strafe(this.speed.getValueFloat());
            }
        }
        super.onUpdate(event);
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (this.mode.isMode("Verus") && event.getPacket() instanceof C03PacketPlayer && event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition && event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            event.setCancelled(true);
        }
    }

    @Override
    @Subscribe
    public void onMove(EventMove event) {
        this.mode.getMode().getClass();
        super.onMove(event);
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        switch (this.mode.getMode()) {
            case "Bow": {
                pitch = -90.0f;
                event.setPitch(pitch);
                if (LongJump.mc.thePlayer.ticksExisted >= 9) {
                    LongJump.mc.timer.timerSpeed = 1.0f;
                }
                if (LongJump.mc.thePlayer.ticksExisted < 1) {
                    LongJump.mc.thePlayer.ticksExisted = 1;
                }
                if (this.jumped && MovementUtils.getOnRealGround(LongJump.mc.thePlayer, 1.5)) {
                    this.setEnabled(false);
                }
                if (LongJump.mc.thePlayer.hurtTime <= 0 || !LongJump.mc.thePlayer.onGround || LongJump.mc.thePlayer.getCurrentEquippedItem() == null || !(LongJump.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)) break;
                event.setPitch(0.0f);
                LongJump.mc.thePlayer.jump();
                this.jumped = true;
                LongJump.mc.thePlayer.motionY = this.h.getValueFloat();
                MovementUtils.setMotion(this.v.getValueFloat());
                break;
            }
            case "Minemora": {
                if (LongJump.mc.thePlayer.onGround) {
                    LongJump.mc.thePlayer.motionY = 0.42;
                    LongJump.mc.thePlayer.jump();
                } else if (LongJump.mc.thePlayer.motionY <= -0.08) {
                    LongJump.mc.thePlayer.motionY = 0.0;
                }
                MovementUtils.setMotion(Math.max((double)this.speed.getValueFloat(), MovementUtils.getBaseMoveSpeed()));
            }
        }
        super.onPreMotion(event);
    }

    @Override
    public void onEnable() {
        this.jumped = false;
        switch (this.mode.getMode()) {
            case "Verus": {
                this.sendPacketSilent(new C08PacketPlayerBlockPlacement(new BlockPos(LongJump.mc.thePlayer.posX, LongJump.mc.thePlayer.posY - 1.5, LongJump.mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(LongJump.mc.theWorld, new BlockPos(-1, -1, -1))), 0.0f, 0.94f, 0.0f));
                this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(LongJump.mc.thePlayer.posX, LongJump.mc.thePlayer.posY + 3.05, LongJump.mc.thePlayer.posZ, false));
                this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(LongJump.mc.thePlayer.posX, LongJump.mc.thePlayer.posY, LongJump.mc.thePlayer.posZ, false));
                this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(LongJump.mc.thePlayer.posX, LongJump.mc.thePlayer.posY + (double)0.42f, LongJump.mc.thePlayer.posZ, true));
                break;
            }
            case "Bow": {
                if (!this.timer.hasReached(1000L)) break;
                this.sendPacketSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                this.timer.reset();
            }
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.jumped = false;
        LongJump.mc.gameSettings.keyBindUseItem.pressed = false;
        super.onDisable();
    }
}

