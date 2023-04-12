package markgg.modules.movement;

import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.settings.Setting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import markgg.util.MathUtil;
import markgg.util.MoveUtil;
import markgg.util.RandomUtil;
import markgg.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

public class Scaffold extends Module {

	static int slotIndex;
	int lastItem;
	float yaw;
	float pitch = 90.0F;

	public BooleanSetting sprint = new BooleanSetting("Sprint", this, false);
	public BooleanSetting switchToBlock = new BooleanSetting("Switch to Block", this, true);

	public Timer timer = new Timer();

	public Scaffold() {
		super("Scaffold", "Places blocks for you", 0, Category.MOVEMENT);
		addSettings(switchToBlock,sprint);
	}

	private boolean canPlaceBlock(BlockPos pos) {
		Block block = mc.theWorld.getBlockState(pos).getBlock();
		return (!mc.theWorld.isAirBlock(pos) && !(block instanceof BlockLiquid));
	}

	public void onEvent(Event e) {
		if (e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			if (e.isPost()) {
				if(sprint.isEnabled()) {
					mc.gameSettings.keyBindSprint.pressed = true;
					mc.thePlayer.setSprinting(true);
				} else {
					mc.gameSettings.keyBindSprint.pressed = false;
					mc.thePlayer.setSprinting(false);
				}

				float yaw = mc.thePlayer.rotationYaw;
				yaw = Math.abs(yaw);

				if(yaw > 360 || -360 > yaw) {
					mc.thePlayer.rotationYaw = 0;
				}


				String direction = "SOUTH";


				if(yaw > 45 && yaw < 135) {
					direction = "WEST";
				}

				if(yaw > 135 && yaw < 225) {
					direction = "NORTH";
				}

				if(yaw > 225 && yaw < 315) {
					direction = "EAST";
				}

				if(yaw > 315 && yaw < 360) {
					direction = "SOUTH";
				}

				if(yaw > 360 && yaw < 45) {
					direction = "SOUTH";
				}

				event.setYaw(mc.thePlayer.rotationYaw + 157);
				mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + 157;
				mc.thePlayer.renderYawOffset = (float) MathUtil.randomNumber(89, 91);
				float a75 = 90;
				event.setPitch((float) MathUtil.randomNumber(89, 91));


				BlockPos playerBlock = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
				BlockData data = null;

				double yDif = 1.0D;
				for (double posY = mc.thePlayer.posY - 1.0D; posY > 0.0D; posY--) {
					BlockData newData = getBlockData(new BlockPos(mc.thePlayer.posX, posY, mc.thePlayer.posZ));
					if (newData != null) {
						yDif = mc.thePlayer.posY - posY;
						if (yDif <= 3.0D) {

							data = newData;

							break;
						} 
					} 
				} 
				if (data == null) {
					return;
				}
				if (data.pos == new BlockPos(0, -1, 0)) {
					mc.thePlayer.motionX = 0.0D;
					mc.thePlayer.motionZ = 0.0D;
				} 
				
				List<Block> allBlocks = new ArrayList<>(Block.blockRegistry.getKeys().size());
				for (Object obj : Block.blockRegistry.getKeys()) {
				    ResourceLocation resLoc = (ResourceLocation) obj;
				    Block block = (Block) Block.blockRegistry.getObject(resLoc);
				    allBlocks.add(block);
				}
				Map<Block, Integer> blockSlotMap = getSlotIndicesWithMostItemBlocks(mc.thePlayer);
				for (Block block : blockSlotMap.keySet()) {
				    slotIndex = blockSlotMap.get(block);
				    if (slotIndex != -1) {
				    	mc.thePlayer.inventory.currentItem = getSlotIndexWithMostItemBlocks(mc.thePlayer);
				    }
				}

				if(switchToBlock.isEnabled()) {
					if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
						mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), data.pos, data.face, new Vec3(data.pos.getX(), data.pos.getY(), data.pos.getZ()));
						if(e.isPre()) {
							mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0APacketAnimation());
						}
					}
					mc.thePlayer.inventory.currentItem = getSlotIndexWithMostItemBlocks(mc.thePlayer);
				}else {
					if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
						mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), data.pos, data.face, new Vec3(data.pos.getX(), data.pos.getY(), data.pos.getZ()));
						if(e.isPre()) {
							mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0APacketAnimation());
						}
					}
					mc.thePlayer.inventory.currentItem = lastItem;
				}
				
			} 
			if (e.isPre()) {
				BlockPos playerBlock1 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
				BlockData data1 = null;
				double yDif1 = 1.0D;
				for (double posY = mc.thePlayer.posY - 1; posY > 0; posY--) {
					BlockData newData = getBlockData(new BlockPos(mc.thePlayer.posX, posY, mc.thePlayer.posZ));
					if (newData != null) {
						yDif1 = mc.thePlayer.posY - posY;
						if (yDif1 <= 3.0D) {
							data1 = newData;
							break;
						} 
					} 
				} 
				if (data1 == null) {
					return;
				}
				if (data1.pos == new BlockPos(0, -1, 0)) {
					mc.thePlayer.motionX = 0.0D;
					mc.thePlayer.motionZ = 0.0D;
				} 
				if (data1.face == EnumFacing.UP) {
					yaw = 90.0F;
				} else if (data1.face == EnumFacing.NORTH) {
					yaw = 360.0F;
				} else if (data1.face == EnumFacing.EAST) {
					yaw = 90.0F;
				} else if (data1.face == EnumFacing.SOUTH) {
					yaw = 180.0F;
				} else if (data1.face == EnumFacing.WEST) {
					yaw = 270.0F;
				} else {
					yaw = 90.0F;
				} 


			}
		}
	}

	private boolean isPosSolid(BlockPos pos) {
		Block block = mc.theWorld.getBlockState(pos).getBlock();
		if ((block.getMaterial().isSolid() || !block.isTranslucent() || block.isSolidFullCube() || block instanceof net.minecraft.block.BlockLadder || block instanceof net.minecraft.block.BlockCarpet || block instanceof net.minecraft.block.BlockSnow || block instanceof net.minecraft.block.BlockSkull) && !block.getMaterial().isLiquid() && !(block instanceof net.minecraft.block.BlockContainer)) {
			return true;
		}
		return false;
	}

	private BlockData getBlockData(BlockPos pos) {
		if (mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air)
			return new BlockData(pos.add(0, -1, 0), EnumFacing.UP); 
		if (mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air)
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST); 
		if (mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air)
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST); 
		if (mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air)
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH); 
		if (mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		}
		return null;
	}

	public class BlockData
	{
		public final BlockPos pos;
		public final EnumFacing face;

		BlockData(BlockPos pos, EnumFacing face) {
			this.pos = pos;
			this.face = face;
		}
	}
	
	public static int getSlotIndexWithMostItemBlocks(EntityPlayer player) {
	    List<ItemStack> stacks = Arrays.asList(player.inventory.mainInventory);
	    int maxIndex = -1;
	    int maxCount = 0;
	    for (int i = 0; i < stacks.size(); i++) {
	        ItemStack stack = stacks.get(i);
	        if (stack != null && stack.getItem() instanceof ItemBlock) {
	            int count = stack.stackSize;
	            if (count > maxCount) {
	                maxIndex = i;
	                maxCount = count;
	            }
	        }
	    }
	    if (maxIndex != -1) {
	        player.inventory.currentItem = maxIndex;
	    }
	    return maxIndex;
	}
	
	public static Map<Block, Integer> getSlotIndicesWithMostItemBlocks(EntityPlayer player) {
	    Map<Block, Integer> blockSlotMap = new HashMap<>();
	    List<Block> allBlocks = new ArrayList<>(Block.blockRegistry.getKeys().size());
	    Object[] keysArray = Block.blockRegistry.getKeys().toArray();
	    for (int i = 0; i < keysArray.length; i++) {
	        Object obj = keysArray[i];
	        if (obj instanceof ResourceLocation) {
	            ResourceLocation resLoc = (ResourceLocation)obj;
	            Block block = (Block) Block.blockRegistry.getObject(resLoc);
	            allBlocks.add(block);
	        }
	    }
	    for (Block block : allBlocks) {
	        int maxCount = 0;
	        slotIndex = -1;
	        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
	            ItemStack stack = player.inventory.getStackInSlot(i);
	            if (stack != null && stack.getItem() instanceof ItemBlock) {
	                ItemBlock itemBlock = (ItemBlock) stack.getItem();
	                if (itemBlock.getBlock() == block && stack.stackSize > maxCount) {
	                    maxCount = stack.stackSize;
	                    slotIndex = i;
	                }
	            }
	        }
	        blockSlotMap.put(block, slotIndex);
	    }
	    return blockSlotMap;
	}
	
	
	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		lastItem = mc.thePlayer.inventory.currentItem;
	}

	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.timer.timerSpeed = 1;
		mc.thePlayer.inventory.currentItem = lastItem;
	}
}
