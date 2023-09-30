package felix.module.impl.player;

import felix.util.other.ItemUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.events.render.EventGuiContainer;
import felix.module.Module;
import felix.util.other.TimeHelper;
import felix.value.impl.BooleanValue;
import felix.value.impl.NumberValue;

public class ChestStealer extends Module {

	public BooleanValue titleCheck = new BooleanValue("Title Check", true);
    public BooleanValue autoClose = new BooleanValue("Auto Close", true);
    public BooleanValue aura = new BooleanValue("Aura", false);
    public BooleanValue filter = new BooleanValue("Filter", true);
    public BooleanValue silent = new BooleanValue("Silent", false);
    
    public NumberValue<Integer> delay = new NumberValue<>("Delay", 4, 1, 10, 1);
    public NumberValue<Double> range = new NumberValue("Aura Range", 5.0, 1.0, 6.0);
    
    public TimeHelper timer = new TimeHelper();
    
    public TimeHelper auraTimer = new TimeHelper();
    
    public final Set openedChests = new HashSet();

    public ChestStealer() {
        super("ChestStealer", 0, ModuleCategory.PLAYER);
        addValues(autoClose, titleCheck, filter, aura, silent, range, delay);
    }

    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
        final int delay = this.delay.getValue() * 50;
        final EntityPlayerSP player = mc.thePlayer;
        int index;
        if (mc.currentScreen instanceof GuiChest && event.getType().equals(EventMotionUpdate.Type.PRE)) {
            final GuiChest chest = (GuiChest) mc.currentScreen;
            boolean titleCheck = this.titleCheck.isEnabled() ? (chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Chest")) || chest.getLowerChestInventory().getDisplayName().getUnformattedText().equalsIgnoreCase("LOW") : true;
            if (titleCheck) {
            	if (autoClose.isEnabled()) {
	                if (isChestEmpty(chest) || isInventoryFull()) {
	                	player.closeScreen();
	                    return;
	                }
            	}
                for (index = 0; index < chest.getLowerChestInventory().getSizeInventory(); ++index) {
                    final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
                    if (stack != null && timer.reach(delay - ThreadLocalRandom.current().nextInt(0, 250))) {
                    	boolean trash = filter.isEnabled() ? !ItemUtils.isTrash(stack) : true;
                    	if (trash) {
	                        mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, player);
	                        timer.reset();
	                        break;
                    	}
                    }
                }
            }
        }
        
        if (!event.isPre() && mc.currentScreen == null && aura.isEnabled()) {
        	List loadedTileEntityList = mc.theWorld.loadedTileEntityList;
            index = 0;
            
            for(int loadedTileEntityListSize = loadedTileEntityList.size(); index < loadedTileEntityListSize; ++index) {
            	TileEntity tile = (TileEntity)loadedTileEntityList.get(index);
            	BlockPos pos = tile.getPos();
            	if (tile instanceof TileEntityChest && getDistToPos(pos) < range.getValue() && !openedChests.contains(tile) && auraTimer.reach(500L) && mc.playerController.onPlayerRightClick(player, mc.theWorld, player.getHeldItem(), pos, EnumFacing.DOWN, getVec3(tile.getPos()))) {
            		mc.getNetHandler().addToSendQueueNoEvent(new C0APacketAnimation());
            		set(openedChests, tile);
            		auraTimer.reset();
            		return;
            	}
            }
        }
    }
    
    @Handler
    public void onGuiContainer(final EventGuiContainer event) {

    }
    
    public void set(Set set, TileEntity chest) {
    	if (set.size() > 128) {
    		set.clear();
    	}
    	set.add(chest);
    }

	public Vec3 getVec3(BlockPos pos) {
    	return new Vec3((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
    }
    
    public double getDistToPos(BlockPos pos) {
    	return mc.thePlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
    }

	public boolean isChestEmpty(final GuiChest chest) {
        for (int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); ++index) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
            if (stack != null)
            	
                if (!ItemUtils.isTrash(stack) )
                    return false;
        }
        return true;
    }

    public boolean isInventoryFull() {
        for (int index = 9; index <= 44; ++index) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null) {
                return false;
            }
        }
        return true;
    }
    
    public void onEnable() {
    	super.onEnable();
    	auraTimer.reset();
    	timer.reset();
    }
    
    public void onDisable() {
    	super.onDisable();
    }
}
