package v4n1ty.module.player;

import de.Hero.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import v4n1ty.V4n1ty;
import v4n1ty.events.Event;
import v4n1ty.events.impl.EventMotion;
import v4n1ty.module.Category;
import v4n1ty.module.Module;
import v4n1ty.module.combat.KillAura;
import v4n1ty.utils.player.AimUtil;
import v4n1ty.utils.player.Rotation;

import java.util.ArrayList;

public class Breaker extends Module {

    public Breaker() {
        super("Breaker", Keyboard.KEY_I, Category.PLAYER);
    }

    @Override
    public void setup() {
        V4n1ty.settingsManager.rSetting(new Setting("Destroy Reach", this, 4, 0, 6, true));
    }

    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            if (!e.isPre()) {
                EventMotion event = (EventMotion) e;
                for (int radius = (int) V4n1ty.settingsManager.getSettingByName("Destroy Reach").getValDouble(), x = -radius; x < radius; ++x) {
                    for (int y = radius; y > -radius; --y) {
                        for (int z = -radius; z < radius; ++z) {
                            final int xPos = (int) mc.thePlayer.posX + x;
                            final int yPos = (int) mc.thePlayer.posY + y;
                            final int zPos = (int) mc.thePlayer.posZ + z;
                            final BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                            final Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                            if ((block.getBlockState().getBlock() == Block.getBlockById(92) || block.getBlockState().getBlock() == Blocks.bed)) {
                                if (mc.thePlayer.swingProgress == 0f) {
                                    Rotation rot = AimUtil.attemptFacePosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                    ((EventMotion) e).setYaw(rot.getRotationYaw());
                                    ((EventMotion) e).setPitch(rot.getRotationPitch());
                                    mc.thePlayer.swingItem();
                                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}