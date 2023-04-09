package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.BlockPos;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.inventory.InventoryUtils;

import java.util.Objects;

public class AutoTool extends Module {

    public AutoTool(ModuleData moduleData) {
        super(moduleData);
    }

    public Entity getItems(double range) {
        Entity temporaryEntity = null;
        double distance = range;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && (entity instanceof EntityItem)) {
                double currentDistance = mc.thePlayer.getDistanceToEntity(entity);
                if (currentDistance <= distance) {
                    distance = currentDistance;
                    temporaryEntity = entity;
                }
            }
        }

        return temporaryEntity;
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packetUseEntity = event.getPacket();
            if (Objects.equals(packetUseEntity.getAction(), C02PacketUseEntity.Action.ATTACK) && !mc.thePlayer.isEating())
                bestSword();
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (!event.isPre()) {
            boolean checks = !mc.thePlayer.isEating();
            if (checks && mc.playerController.isHittingBlock && !Objects.isNull(mc.objectMouseOver.func_178782_a()))
                bestTool(mc.objectMouseOver.func_178782_a().getX(), mc.objectMouseOver.func_178782_a().getY(), mc.objectMouseOver.func_178782_a().getZ());
        }
    }

    public void bestSword() {
        int bestSlot = 0;
        int index = -1;
        float bestSwordDamage = -1.0F;

        for (int i = 0; i < 36; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemSword) {
                float damageLevel = InventoryUtils.getDamageLevel(itemStack);
                if (bestSwordDamage < damageLevel) {
                    bestSwordDamage = damageLevel;
                    index = i;
                }
            }
        }

        if (index >= 0) {
            mc.thePlayer.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }
    }

    public void bestTool(int x, int y, int z) {
        int blockId = Block.getIdFromBlock(mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock());
        int bestSlot = 0;
        float strengthAgainstBlock = -1F;
        for (int currentIndex = 36; currentIndex < 45; currentIndex++) {
            ItemStack curSlot = mc.thePlayer.inventoryContainer.getSlot(currentIndex).getStack();
            if (curSlot == null)
                continue;
            if ((curSlot.getItem() instanceof ItemTool || curSlot.getItem() instanceof ItemSword || curSlot.getItem() instanceof ItemShears) && curSlot.getStrVsBlock(Block.getBlockById(blockId)) > strengthAgainstBlock) {
                bestSlot = currentIndex - 36;
                strengthAgainstBlock = curSlot.getStrVsBlock(Block.getBlockById(blockId));
            }
        }

        if (strengthAgainstBlock >= 0) {
            mc.thePlayer.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }
    }

}
