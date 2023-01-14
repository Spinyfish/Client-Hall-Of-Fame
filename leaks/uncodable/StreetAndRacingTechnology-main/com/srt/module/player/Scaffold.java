package com.srt.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import com.srt.SRT;
import com.srt.events.Event;
import com.srt.events.listeners.EventMotion;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.BooleanSetting;
import com.srt.settings.settings.ModeSetting;

import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;

public final class Scaffold extends ModuleBase {

	final BlockPos[] possiblePositions = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
	final EnumFacing[] possibleFacings = new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH};

	public static ModeSetting rotation;
	public static ModeSetting mode;
	public BooleanSetting sprint = new BooleanSetting("Sprint",false);
	public BooleanSetting sameY = new BooleanSetting("SameY",false);
	public BooleanSetting keepY = new BooleanSetting("Smooth Camera",false);
	public BooleanSetting autoJump = new BooleanSetting("AutoJump",false);
	public BooleanSetting keepRotations = new BooleanSetting("KeepRotations",false);
	float prevYaw = 0, prevPitch = 0;
	int oldItemSlot;
	ItemStack oldItemStack;
	Set<Block> disallowedBlocks = new HashSet<>();
	double yPos = 0;
	public Scaffold() {
		super("Scaffold", Keyboard.KEY_B, Category.PLAYER);
		ArrayList<String> rotations = new ArrayList<>();
		ArrayList<String> modes = new ArrayList<>();
		rotations.add("Forward");
		rotations.add("Backwards");
		rotations.add("Hypixel");
		modes.add("Normal");
		modes.add("Hypixel");
		this.rotation = new ModeSetting("Rotation", rotations);
		this.mode = new ModeSetting("Mode", modes);
		addSettings(mode, rotation, sprint, sameY, autoJump, keepRotations, keepY);
		disallowedBlocks.addAll(Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence));
	}

	@Override
	public void onEvent(Event e) {
		this.setSuffix(mode.getCurrentValue());
		if (e instanceof EventMotion) {
			mc.thePlayer.cameraYaw = 0.0f;
			if(keepY.getValue()) {
				mc.thePlayer.posY = yPos;
			}
			 if(getValidBlockSlotFromHotbar() != -1) {
				 mc.thePlayer.inventory.currentItem = getValidBlockSlotFromHotbar();
			 }
			 if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
				mc.gameSettings.keyBindSprint.pressed = sprint.getValue();
				mc.thePlayer.setSprinting(sprint.getValue());
			 }
			 BlockPos underneath = new BlockPos(1,1,1);
			 if(sameY.getValue()) {
				 underneath = new BlockPos(Math.floor(mc.thePlayer.posX),Math.floor(yPos),Math.floor(mc.thePlayer.posZ)).add(0, -1, 0);
			 }else {
				 underneath = new BlockPos(Math.floor(mc.thePlayer.posX),Math.floor(mc.thePlayer.posY),Math.floor(mc.thePlayer.posZ)).add(0, -1, 0);
			 }
			 BlockData currentBlock = getBlockData(underneath);
			 if (e.isPre()) {
				 if(mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed && autoJump.getValue() && !SRT.moduleManager.getModuleByName("Speed").isToggled()) {
					 mc.thePlayer.jump();
				 }
				 if(currentBlock != null) {
					 Vec3 vector = getVector(underneath, currentBlock.getFacing());
					 ((EventMotion) e).setYaw(getAngles(vector)[0]);
					 ((EventMotion) e).setPitch(getAngles(vector)[1]);
					 if(rotation.getCurrentValue() == "Backwards") {
						 ((EventMotion) e).setYaw(-getAngles(vector)[0]);
					 }
					if(mode.getCurrentValue() == "Hypixel" && sprint.getValue()) {
						mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,Action.STOP_SPRINTING));
					}
					if(rotation.getCurrentValue() == "Hypixel") {
						((EventMotion) e).setYaw(getAngles(vector)[0]);
						((EventMotion) e).setPitch(87);
					}
					 System.out.println(getAngles(vector)[0] + " 2");
					 mc.thePlayer.rotationYawHead = ((EventMotion) e).getYaw();
					 mc.thePlayer.renderYawOffset = ((EventMotion) e).getYaw();
	
					 prevYaw = ((EventMotion) e).getYaw();
					 prevPitch = ((EventMotion) e).getPitch();
					 if((mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) || mc.gameSettings.keyBindJump.pressed) {
						 placeBlock(currentBlock);
					 }
				 }
				 if(rotation.getCurrentValue() == "Hypixel") {
						((EventMotion) e).setYaw(prevYaw);
						mc.thePlayer.rotationYawHead = prevYaw;
						((EventMotion) e).setPitch(prevPitch);
					}
			}
			 if(e.isPre()) {
				 if(prevYaw != 0 && prevPitch != 0 && keepRotations.getValue()) {
					 ((EventMotion) e).setYaw(prevYaw);
					 ((EventMotion) e).setPitch(prevYaw);
					 mc.thePlayer.rotationYawHead = ((EventMotion) e).getYaw();
					 mc.thePlayer.renderYawOffset = ((EventMotion) e).getYaw();
				 }
				 if(mode.getCurrentValue().contains("Hypixel") && !sprint.getValue() && !mc.thePlayer.onGround){
					 mc.thePlayer.motionX *= 1.0334;
					 mc.thePlayer.motionZ *= 1.0334;
				 }
			 }
			if(mode.getCurrentValue() == "Hypixel" && sprint.getValue() && (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0 && (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))) {
				mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(1,1,1), 255, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
			}
		}
	}

	/*
	Didn't make this, please recode these if you need.
	 */
	float[] getAngles(Vec3 vector) {
		double posX = vector.xCoord, posY = vector.yCoord, posZ = vector.zCoord;
		final EntityPlayerSP player = mc.thePlayer;
		final double diffX = posX - player.posX, diffY = posY - (player.posY + player.getEyeHeight()), diffZ = posZ - player.posZ, dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ); // @on
		final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F, pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		return new float[]{player.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - player.rotationYaw), player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - player.rotationPitch)};
	}

	void placeBlock(final BlockData block){
		if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
			final BlockPos pos = block.getPosition();
			final EnumFacing face = block.getFacing();
			oldItemSlot = mc.thePlayer.inventory.currentItem;
			//mc.thePlayer.inventory.currentItem = getValidBlockSlotFromHotbar();
			if(mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, face, getVector(pos,face))){
				mc.thePlayer.swingItem();
			}
			//mc.thePlayer.inventory.currentItem = oldItemSlot;
		}
	}

	boolean isPosSolid(BlockPos pos) {
		return (!(mc.theWorld.getBlockState(pos) instanceof BlockLiquid)) && ((mc.theWorld.getBlockState(pos)).getBlock().getMaterial() != Material.air);
	}

	private Vec3 getVector(BlockPos pos, EnumFacing face) {
		Vec3i direction = face.getDirectionVec();
		double x = direction.getX() * .5, y = direction.getY() * .5, z = direction.getZ() * .5;
		return new Vec3(pos).addVector(.5, .5, .5).addVector(x, y, z);
	}

	/*
	I took it from my old client, I don't know who made this but credit to them / replace this please
	 */
	private BlockData getBlockData(BlockPos pos) {
		if (isPosSolid(pos.add(0, -1, 0))) {
			return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos.add(-1, 0, 0))) {
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos.add(1, 0, 0))) {
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos.add(0, 0, 1))) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos.add(0, 0, -1))) {
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos1 = pos.add(-1, 0, 0);

		if (isPosSolid(pos1.add(0, -1, 0))) {
			return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos1.add(-1, 0, 0))) {
			return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos1.add(1, 0, 0))) {
			return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos1.add(0, 0, 1))) {
			return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos1.add(0, 0, -1))) {
			return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos2 = pos.add(1, 0, 0);

		if (isPosSolid(pos2.add(0, -1, 0))) {
			return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos2.add(-1, 0, 0))) {
			return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos2.add(1, 0, 0))) {
			return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos2.add(0, 0, 1))) {
			return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos2.add(0, 0, -1))) {
			return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos3 = pos.add(0, 0, 1);

		if (isPosSolid(pos3.add(0, -1, 0))) {
			return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos3.add(-1, 0, 0))) {
			return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos3.add(1, 0, 0))) {
			return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos3.add(0, 0, 1))) {
			return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos3.add(0, 0, -1))) {
			return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos4 = pos.add(0, 0, -1);

		if (isPosSolid(pos4.add(0, -1, 0))) {
			return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos4.add(-1, 0, 0))) {
			return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos4.add(1, 0, 0))) {
			return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos4.add(0, 0, 1))) {
			return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos4.add(0, 0, -1))) {
			return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos19 = pos.add(-2, 0, 0);

		if (isPosSolid(pos1.add(0, -1, 0))) {
			return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos1.add(-1, 0, 0))) {
			return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos1.add(1, 0, 0))) {
			return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos1.add(0, 0, 1))) {
			return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos1.add(0, 0, -1))) {
			return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos29 = pos.add(2, 0, 0);

		if (isPosSolid(pos2.add(0, -1, 0))) {
			return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos2.add(-1, 0, 0))) {
			return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos2.add(1, 0, 0))) {
			return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos2.add(0, 0, 1))) {
			return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos2.add(0, 0, -1))) {
			return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos39 = pos.add(0, 0, 2);

		if (isPosSolid(pos3.add(0, -1, 0))) {
			return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos3.add(-1, 0, 0))) {
			return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos3.add(1, 0, 0))) {
			return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos3.add(0, 0, 1))) {
			return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos3.add(0, 0, -1))) {
			return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos49 = pos.add(0, 0, -2);

		if (isPosSolid(pos4.add(0, -1, 0))) {
			return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos4.add(-1, 0, 0))) {
			return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos4.add(1, 0, 0))) {
			return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos4.add(0, 0, 1))) {
			return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos4.add(0, 0, -1))) {
			return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos5 = pos.add(0, -1, 0);

		if (isPosSolid(pos5.add(0, -1, 0))) {
			return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos5.add(-1, 0, 0))) {
			return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos5.add(1, 0, 0))) {
			return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos5.add(0, 0, 1))) {
			return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos5.add(0, 0, -1))) {
			return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos6 = pos5.add(1, 0, 0);

		if (isPosSolid(pos6.add(0, -1, 0))) {
			return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos6.add(-1, 0, 0))) {
			return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos6.add(1, 0, 0))) {
			return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos6.add(0, 0, 1))) {
			return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos6.add(0, 0, -1))) {
			return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos7 = pos5.add(-1, 0, 0);

		if (isPosSolid(pos7.add(0, -1, 0))) {
			return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos7.add(-1, 0, 0))) {
			return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos7.add(1, 0, 0))) {
			return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos7.add(0, 0, 1))) {
			return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos7.add(0, 0, -1))) {
			return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos8 = pos5.add(0, 0, 1);

		if (isPosSolid(pos8.add(0, -1, 0))) {
			return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos8.add(-1, 0, 0))) {
			return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos8.add(1, 0, 0))) {
			return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos8.add(0, 0, 1))) {
			return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos8.add(0, 0, -1))) {
			return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos9 = pos5.add(0, 0, -1);

		if (isPosSolid(pos9.add(0, -1, 0))) {
			return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos9.add(-1, 0, 0))) {
			return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos9.add(1, 0, 0))) {
			return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos9.add(0, 0, 1))) {
			return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos9.add(0, 0, -1))) {
			return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
		}
		return null;
	}


	@Override
	public void onEnable() {
		oldItemSlot = mc.thePlayer.inventory.currentItem;
		yPos = mc.thePlayer.posY;
		prevYaw = 0;
		prevPitch = 0;
	}

	@Override
	public void onDisable() {
		mc.thePlayer.inventory.currentItem = 0;
	}

	int getValidBlockSlotFromHotbar(){
		 for(int i = 36; i < 45; ++i) {
			 ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
		     if (stack != null && stack.getItem() instanceof ItemBlock && !disallowedBlocks.contains(((ItemBlock)(stack.getItem())).getBlock())) {
		    	 return i - 36;
		     }
		 }
		 return -1;
	}

	int getFinalBlockCount() {
		int burnlgbtqandfurries = 0;
		for (int i = 9; i < 45; ++i) {
			final ItemStack iteratedStack = mc.thePlayer.inventory.getStackInSlot(i);
			final Item iteratedItem = iteratedStack.getItem();
			if (iteratedStack != null && iteratedItem instanceof ItemBlock && doesItemQualify(iteratedItem)) {
				burnlgbtqandfurries += iteratedStack.stackSize;
			}
		}
		return burnlgbtqandfurries;
	}


	boolean isAir(BlockPos pos) {
		return mc.theWorld.isAirBlock(pos);
	}

	boolean doesItemQualify(Item item) {
		if (!(item instanceof ItemBlock)) {
			return false;
		} else {
			final ItemBlock itemBlock = (ItemBlock) item;
			final Block block = itemBlock.getBlock();

			return !disallowedBlocks.contains(block);
		}
	}


	private static final class BlockData{
		BlockPos position;
		EnumFacing facing;

		public BlockData(BlockPos position, EnumFacing facing) {
			this.position = position;
			this.facing = facing;
		}

		public BlockPos getPosition() {
			return position;
		}

		public void setPosition(BlockPos position) {
			this.position = position;
		}

		public EnumFacing getFacing() {
			return facing;
		}

		public void setFacing(EnumFacing facing) {
			this.facing = facing;
		}
	}
}

