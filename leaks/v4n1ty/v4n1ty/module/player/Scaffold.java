package v4n1ty.module.player;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MouseFilter;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import v4n1ty.V4n1ty;
import v4n1ty.events.Event;
import v4n1ty.events.impl.EventMotion;
import v4n1ty.module.Category;
import v4n1ty.module.Module;
import v4n1ty.utils.misc.TimerUtils;
import v4n1ty.utils.player.ScaffoldUtils;
import v4n1ty.utils.player.RotUtils;

public class Scaffold extends Module {

    private ScaffoldUtils.BlockCache blockCache, lastBlockCache;
    private float rotations[];
    private TimerUtils timer = new TimerUtils();

    public Scaffold() {
        super("Scaffold", Keyboard.KEY_F2, Category.MOVEMENT);
    }

    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            if (e.isPre()) {
                EventMotion event = (EventMotion) e;
                mc.thePlayer.setSprinting(false);
                if (lastBlockCache != null) {
                    rotations = RotUtils.getFacingRotations2(lastBlockCache.getPosition().getX(), lastBlockCache.getPosition().getY(), lastBlockCache.getPosition().getZ());
                    mc.thePlayer.renderYawOffset = rotations[0];
                    mc.thePlayer.rotationYawHead = rotations[0];
                    ((EventMotion) e).setYaw(rotations[0]);
                    ((EventMotion) e).setPitch(81);
                    mc.thePlayer.rotationPitchHead = 81;
                } else {
                    ((EventMotion) e).setPitch(81);
                    ((EventMotion) e).setYaw(mc.thePlayer.rotationYaw + 180);
                    mc.thePlayer.rotationPitchHead = 81;
                    mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw + 180;
                    mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + 180;
                }

                if (mc.thePlayer.isPotionActive(Potion.moveSpeed.id)) {
                    mc.thePlayer.motionX *= 0.66;
                    mc.thePlayer.motionZ *= 0.66;
                }

                mc.thePlayer.setSprinting(false);

                blockCache = ScaffoldUtils.grab();
                if (blockCache != null) {
                    lastBlockCache = ScaffoldUtils.grab();
                } else {
                    return;
                }

                // Setting Item Slot (Pre)
                int slot = ScaffoldUtils.grabBlockSlot();
                if (slot == -1) return;

                // Setting Slot
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));

                if (blockCache == null) return;
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(slot), lastBlockCache.position, lastBlockCache.facing, ScaffoldUtils.getHypixelVec3(lastBlockCache));
                mc.thePlayer.swingItem();
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                blockCache = null;
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        super.onDisable();
    }

    @Override
    public void onEnable() {
        lastBlockCache = null;
        super.onEnable();
    }
}
