package igbt.astolfy.module.player;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.EventType;
import igbt.astolfy.events.listeners.EventMotion;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.events.listeners.EventRender2D;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.module.visuals.Hud;
import igbt.astolfy.utils.TimerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class Scaffold extends ModuleBase {

	public Scaffold() {
		super("Scaffold", Keyboard.KEY_B, Category.PLAYER);
	}
    private double startY;
    public BlockData blockData;
    public TimerUtils towerTimer = new TimerUtils();
    private List<Block> badBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence);
	public BlockPos lastBlock;
	public TimerUtils timer = new TimerUtils();
    public static boolean isPlaceTick = false;
    
	public void onDisable() {
		mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
		mc.timer.timerSpeed = 1;
		this.isPlaceTick = false;
		towerTimer.reset();
	}
	
	public void onEnable() {
		int slot = this.getSlot();
		//startY = mc.thePlayer.posY;
		//this.isPlaceTick = keeprots.getValue() ? blockData != null && slot != -1 : blockData != null && slot != -1 && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() == Blocks.air;
		//if (slot == -1) {
		//	this.moveBlocksToHotbar();
		//	return;
		//}
		//if(mode.getCurrentValue().equalsIgnoreCase("Sprint"))
			//mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,C0BPacketEntityAction.Action.STOP_SPRINTING));
	}
	public void onPacketEvent(EventPacket e){
		//if(mode.getCurrentValue().equalsIgnoreCase("Sprint")) {
			//if (e.getPacket() instanceof C0BPacketEntityAction && ((C0BPacketEntityAction) e.getPacket()).getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
				mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0BPacketEntityAction(mc.thePlayer,C0BPacketEntityAction.Action.STOP_SPRINTING));
			//	e.setCancelled(true);
			//}
		//}
	}
	public void onEvent(Event evenmt) {
			if (evenmt instanceof EventMotion) {
				EventMotion event = (EventMotion) evenmt;
				blockData = getBlockData();
				if (this.blockData != null) {
					event.setPitch(hyprots(this.blockData.getPosition())[1]);
					event.setYaw(hyprots(this.blockData.getPosition())[0]);
				}
				mc.thePlayer.setSprinting(false);
				int slot = this.getSlot();
				if (slot != -1 && this.blockData != null && event.getEventType() == EventType.POST) {
					final int currentSlot = mc.thePlayer.inventory.currentItem;
					mc.thePlayer.inventory.currentItem = slot;
					if (this.getPlaceBlock(this.blockData.getPosition(), this.blockData.getFacing())) {
						mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentSlot));
					}
					mc.thePlayer.inventory.currentItem = currentSlot;
				}
			} else if (evenmt instanceof EventRender2D) {
				ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
				int blocks = this.getBlockCount();
				FontRenderer font = mc.fontRendererObj;
				String text = blocks + "";
				//font.drawString(text + " Blocks Left", (sr.getScaledWidth() / 2) + 1.1F, (sr.getScaledHeight() / 2) + font.FONT_HEIGHT + 1.1F, 0xFF000000);
				Gui.drawRect((sr.getScaledWidth() / 2) - 30, (sr.getScaledHeight() / 2 - 10), (sr.getScaledWidth() / 2) - 10, (sr.getScaledHeight() / 2) + 20, 0x80000000);

				if (this.getSlot() != -1) {
					mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.inventory.mainInventory[this.getSlot()], (int) ((sr.getScaledWidth() / 2) - 28), (int) ((sr.getScaledHeight() / 2) + font.FONT_HEIGHT - 17.1F));
				}
				font.drawString(text + "", (sr.getScaledWidth() / 2) - 19.5F - (mc.fontRendererObj.getStringWidth(text) / 2), (sr.getScaledHeight() / 2) + font.FONT_HEIGHT + 0.1F, Hud.getColor(0));
			}
		}
			
	private float[] hyprots(BlockPos e) {
		double x = e.getX() - mc.thePlayer.posX;
		double y = (e.getY()  - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() * 1.9));
		if(e.getY() - 1 > mc.thePlayer.posY)
			y = (e.getY()  - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() * 1.1));
		double z = e.getZ() - mc.thePlayer.posZ;
		double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
		
		float yaw = (float)Math.toDegrees(-Math.atan(x / z));
		float pitch = (float)-Math.toDegrees(Math.atan(y / dist));
		
		if(x < 0 && z < 0)
			yaw = 90 + (float)Math.toDegrees(Math.atan(z / x));
		else if (x > 0 && z < 0)
			yaw = -90 + (float)Math.toDegrees(Math.atan(z / x));
		
		if (pitch > 90)
			pitch = 90;
		if (pitch < -90)
			pitch = -90;
		if (yaw > 180)
			yaw = 180;
		if (yaw < -180)
			yaw = -180;
		
		return new float[] { yaw, pitch };
	}
    private boolean getPlaceBlock(BlockPos pos, final EnumFacing facing) {
        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        Vec3i data = this.blockData.getFacing().getDirectionVec();
        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, new Vec3(this.blockData.getPosition()).addVector(0.5, 0.5, 0.5).add(new Vec3(data.getX() * 0.5, data.getY() * 0.5, data.getZ() * 0.5)))) {
        	//if(this.swing.getValue()) {
        		mc.thePlayer.swingItem();
        	//} else {
        	//	mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
        	//}
            return true;
        }
        return false;
    }
	
	private BlockData getBlockData() {
        final EnumFacing[] invert = { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST };
        double yValue = 0;
        BlockPos playerpos = new BlockPos(mc.thePlayer.getPositionVector()).offset(EnumFacing.DOWN).add(0, yValue, 0);
        
        List<EnumFacing> facingVals = Arrays.asList(EnumFacing.values());
        for (int i = 0; i < facingVals.size(); ++i) { 
            if (mc.theWorld.getBlockState(playerpos.offset(facingVals.get(i))).getBlock().getMaterial() != Material.air) {
                return new BlockData(playerpos.offset(facingVals.get(i)), invert[facingVals.get(i).ordinal()]);
            }
        }
        final BlockPos[] addons = {
        		new BlockPos(-1, 0, 0), 
        		new BlockPos(1, 0, 0), 
        		new BlockPos(0, 0, -1), 
        		new BlockPos(0, 0, 1)};
        for (int length2 = addons.length, j = 0; j < length2; ++j) {
            final BlockPos offsetPos = playerpos.add(addons[j].getX(), 0, addons[j].getZ());
            if (mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
                for (int k = 0; k < EnumFacing.values().length; ++k) {
                    if (mc.theWorld.getBlockState(offsetPos.offset(EnumFacing.values()[k])).getBlock().getMaterial() != Material.air) {
						//if(diagonal.getValue())
                        	return new BlockData(offsetPos.offset(EnumFacing.values()[k]), invert[EnumFacing.values()[k].ordinal()]);
                    }
                }
            }
        }
        return null;
    }
	
    private int getSlot() {
        for (int k = 0; k < 9; ++k) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[k];
            if (itemStack != null && this.isValid(itemStack) && itemStack.stackSize >= 1) {
                return k;
            }	
        }
        return -1;
    }
   
	public static void shiftClick(int k) {
		Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, k, 0, 1, Minecraft.getMinecraft().thePlayer);
	}
    private boolean isValid(ItemStack itemStack) {
    	if (itemStack.getItem() instanceof ItemBlock) {
        	boolean isBad = false;
        	
        	ItemBlock block = (ItemBlock) itemStack.getItem();
        	for (int i = 0; i < this.badBlocks.size(); i++) {
        		if (block.getBlock().equals(this.badBlocks.get(i))) {
        			isBad = true;
        		}
        	}
        	
        	return !isBad;
    	}
    	return false;
    }
    
    private int getBlockCount() {
    	int count = 0;
        for (int k = 0; k < mc.thePlayer.inventory.mainInventory.length; ++k) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[k];
            if (itemStack != null && this.isValid(itemStack) && itemStack.stackSize >= 1) {
                count += itemStack.stackSize;
            }	
        }
        return count;
    }
	
    private class BlockData {
        private BlockPos blockPos;
        private EnumFacing enumFacing;
        
        private BlockData(final BlockPos blockPos, final EnumFacing enumFacing) {
            this.blockPos = blockPos;
            this.enumFacing = enumFacing;
        }
        
        private EnumFacing getFacing() {
            return this.enumFacing;
        }
        
        private BlockPos getPosition() {
            return this.blockPos;
        }
    }
}
