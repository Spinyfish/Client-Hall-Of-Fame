package felix.module.impl.combat;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.value.impl.NumberValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSoup;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AutoSoup extends Module {
    
    private NumberValue<Float> health = new NumberValue<>("Health", 7.0f, 2.0f, 9.5f);
    
    public AutoSoup() {
        super("AutoSoup", 0, ModuleCategory.COMBAT);
        addValues(health);
    }
    
    @Override
    public void onDisable() {
    	super.onDisable();
    }
    
    @Override
    public void onEnable() {
    	super.onEnable();
    }
    
    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
    	final EntityPlayerSP player = mc.thePlayer;
    	player.rotationPitch += (float)1.0E-4;
    	if (player.getHealth() != player.getMaxHealth() && player.getHealth() < health.getValue() * 2 && doesNextSlotHaveSoup() && player.hurtTime >= 9) {
    		player.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(player.posX, player.posY, player.posZ, player.rotationYawHead, 90.0f, player.onGround));
    		player.sendQueue.addToSendQueue(new C09PacketHeldItemChange(getSlotWithSoup()));
    		mc.playerController.sendUseItem(player, mc.theWorld, player.inventory.getStackInSlot(getSlotWithSoup()));
    		new BlockPos(0, 0, 0);
    		player.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
    		player.sendQueue.addToSendQueue(new C09PacketHeldItemChange(player.inventory.currentItem));
    		mc.playerController.onStoppedUsingItem(player);
        }
    }
    
    public boolean doesNextSlotHaveSoup() {
    	final EntityPlayerSP player = mc.thePlayer;
        for (int i = 0; i < 9; ++i) {
            if (player.inventory.getStackInSlot(i) != null && player.inventory.getStackInSlot(i).getItem() instanceof ItemSoup) {
                return true;
            }
        }
        return false;
    }
    
    public int getSlotWithPot() {
    	final EntityPlayerSP player = mc.thePlayer;
        for (int i = 0; i < 9; ++i) {
            if (player.inventory.getStackInSlot(i) != null && player.inventory.getStackInSlot(i).getItem() instanceof ItemPotion) {
                return i;
            }
        }
        return 0;
    }
    
    public int getSlotWithSoup() {
    	final EntityPlayerSP player = mc.thePlayer;
        for (int i = 0; i < 9; ++i) {
            if (player.inventory.getStackInSlot(i) != null && player.inventory.getStackInSlot(i).getItem() instanceof ItemSoup) {
                return i;
            }
        }
        return 0;
    }
}
