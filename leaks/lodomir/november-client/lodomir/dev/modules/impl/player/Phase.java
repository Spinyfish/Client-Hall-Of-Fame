/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.movement.Fly;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.utils.player.MovementUtils;
import lodomir.dev.utils.player.PlayerUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Phase
extends Module {
    private BlockPos currentPos;
    private EnumFacing currentFacing;
    private ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Vanilla", "Clip", "Vulcan");
    private ModeSetting direction = new ModeSetting("Direction", "Down", "Down", "Up");
    private BooleanSetting sneakOnly = new BooleanSetting("While Sneaking", false);

    public Phase() {
        super("Phase", 0, Category.PLAYER);
        this.addSetting(this.mode);
        this.addSetting(this.direction);
        this.addSetting(this.sneakOnly);
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (!this.mode.isMode("Clip")) {
            this.direction.setVisible(false);
            this.sneakOnly.setVisible(true);
            if (!this.mode.isMode("Normal")) {
                this.sneakOnly.setVisible(false);
            }
        } else {
            this.direction.setVisible(true);
            this.sneakOnly.setVisible(false);
        }
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        this.currentPos = null;
        this.currentFacing = null;
        double rotation = Math.toRadians(Phase.mc.thePlayer.rotationYaw);
        switch (this.mode.getMode()) {
            case "Normal": {
                double x = Math.sin(rotation) * 0.005;
                double z = Math.cos(rotation) * 0.005;
                if (this.sneakOnly.isEnabled() && !Phase.mc.gameSettings.keyBindSneak.isKeyDown()) break;
                if (Phase.mc.thePlayer.isCollidedHorizontally) {
                    this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(Phase.mc.thePlayer.posX - x, Phase.mc.thePlayer.posY, Phase.mc.thePlayer.posZ + z, true));
                    Phase.mc.thePlayer.setPosition(Phase.mc.thePlayer.posX - x, Phase.mc.thePlayer.posY, Phase.mc.thePlayer.posZ + z);
                }
                if (!PlayerUtils.isInsideBlock()) break;
                this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(Phase.mc.thePlayer.posX + 1.5 * Math.cos(Math.toRadians(Phase.mc.thePlayer.rotationYaw + 90.0f)), Phase.mc.thePlayer.posY, Phase.mc.thePlayer.posZ + 1.5 * Math.sin(Math.toRadians(Phase.mc.thePlayer.rotationYaw + 90.0f)), true));
                MovementUtils.strafe((float)(MovementUtils.getBaseMoveSpeed() / 2.0));
                break;
            }
            case "Vulcan": {
                this.sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(Phase.mc.thePlayer.posX, Phase.mc.thePlayer.posY + 1.0, Phase.mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(Phase.mc.theWorld, new BlockPos(-1, -1, -1))), 0.0f, 1.0f, 0.0f));
                if (!PlayerUtils.isInsideBlock()) break;
                double d = Phase.mc.gameSettings.keyBindJump.isKeyDown() ? (double)Fly.speed.getValueFloat() : (Phase.mc.thePlayer.motionY = Phase.mc.gameSettings.keyBindSneak.isKeyDown() ? (double)(-Fly.speed.getValueFloat()) : 0.0);
                if (MovementUtils.isMoving()) {
                    MovementUtils.strafe(Fly.speed.getValueFloat());
                    break;
                }
                MovementUtils.stop();
                break;
            }
            case "Vanilla": {
                if (!Phase.mc.thePlayer.isCollidedHorizontally) break;
                double d = Phase.mc.gameSettings.keyBindJump.isKeyDown() ? (double)Fly.speed.getValueFloat() : (Phase.mc.thePlayer.motionY = Phase.mc.gameSettings.keyBindSneak.isKeyDown() ? (double)(-Fly.speed.getValueFloat()) : 0.0);
                if (MovementUtils.isMoving()) {
                    MovementUtils.strafe(Fly.speed.getValueFloat());
                    break;
                }
                MovementUtils.stop();
            }
        }
        super.onPreMotion(event);
    }

    @Override
    public void onEnable() {
        switch (this.mode.getMode()) {
            case "Vanilla": 
            case "Vulcan": {
                this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(Phase.mc.thePlayer.posX + Double.POSITIVE_INFINITY, Phase.mc.thePlayer.posY + Double.POSITIVE_INFINITY, Phase.mc.thePlayer.posZ + Double.POSITIVE_INFINITY, true));
                Phase.mc.thePlayer.setPositionAndUpdate(Phase.mc.thePlayer.posX, Phase.mc.thePlayer.posY - 2.0, Phase.mc.thePlayer.posZ);
                break;
            }
            case "Clip": {
                Phase.mc.thePlayer.setPositionAndUpdate(Phase.mc.thePlayer.posX, Phase.mc.thePlayer.posY + (double)(this.direction.isMode("Up") ? 3 : -3), Phase.mc.thePlayer.posZ);
                if (Phase.mc.thePlayer.ticksExisted % 2 != 0) break;
                this.setEnabled(false);
            }
        }
        super.onEnable();
    }
}

