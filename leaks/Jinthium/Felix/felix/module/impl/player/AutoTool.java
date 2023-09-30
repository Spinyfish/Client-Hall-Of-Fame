package felix.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.BlockPos;

import java.util.Iterator;
import java.util.Objects;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;

public class AutoTool extends Module {

    public AutoTool() {
        super("AutoTool", 0, ModuleCategory.PLAYER);
        setHidden(true);
    }

    public Entity getItems(double range) {
        Entity tempEntity = null;
        double dist = range;
        for (Iterator iterator = mc.theWorld.loadedEntityList.iterator(); iterator.hasNext(); ) {
            Object i = iterator.next();
            Entity entity = (Entity) i;
            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && (entity instanceof EntityItem)) {
                double curDist = mc.thePlayer.getDistanceToEntity(entity);
                if (curDist <= dist) {
                    dist = curDist;
                    tempEntity = entity;
                }
            }
        }

        return tempEntity;
    }

    @Handler
    public void onAttack(final EventPacketSend event) {
        if ((event.getPacket() instanceof C02PacketUseEntity) && ((C02PacketUseEntity) event.getPacket()).getAction().equals(net.minecraft.network.play.client.C02PacketUseEntity.Action.ATTACK)) {
            boolean checks = !mc.thePlayer.isEating();
            if (checks)
                bestSword(((C02PacketUseEntity) event.getPacket()).getEntityFromWorld(mc.theWorld));
        }
    }

    @Handler
    public void onClickBlock(final EventMotionUpdate event) {
        boolean checks = !mc.thePlayer.isEating();
        if (checks && mc.playerController.isHittingBlock && !Objects.isNull(mc.objectMouseOver.getBlockPos()))
            bestTool(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY(), mc.objectMouseOver.getBlockPos().getZ());
    }

    public void bestSword(Entity targetEntity) {
        int bestSlot = 0;
        float f = -1F;
        for (int i1 = 36; i1 < 45; i1++)
            if (mc.thePlayer.inventoryContainer.inventorySlots.toArray()[i1] != null && targetEntity != null) {
                ItemStack curSlot = mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if (curSlot != null && (curSlot.getItem() instanceof ItemSword)) {
                    ItemSword sword = (ItemSword) curSlot.getItem();
                    if (sword.getDamageVsEntity() > f) {
                        bestSlot = i1 - 36;
                        f = sword.getDamageVsEntity();
                    }
                }
            }

        if (f > -1F) {
            mc.thePlayer.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }
    }

    public void bestTool(int x, int y, int z) {
        int blockId = Block.getIdFromBlock(mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock());
        int bestSlot = 0;
        float f = -1F;
        for (int i1 = 36; i1 < 45; i1++)
            try {
                ItemStack curSlot = mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if (((curSlot.getItem() instanceof ItemTool) || (curSlot.getItem() instanceof ItemSword) || (curSlot.getItem() instanceof ItemShears)) && curSlot.getStrVsBlock(Block.getBlockById(blockId)) > f) {
                    bestSlot = i1 - 36;
                    f = curSlot.getStrVsBlock(Block.getBlockById(blockId));
                }
            } catch (Exception exception) {
            }

        if (f != -1F) {
            mc.thePlayer.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }
    }
}
