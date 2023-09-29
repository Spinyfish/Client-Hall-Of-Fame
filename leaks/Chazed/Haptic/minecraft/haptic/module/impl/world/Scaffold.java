package haptic.module.impl.world;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.event.impl.EventPostMotionUpdate;
import haptic.event.impl.EventRender;
import haptic.event.impl.EventSendPacket;
import haptic.event.impl.EventUpdate;
import haptic.event.impl.MovementEvent;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.setting.impl.ModeSetting;
import haptic.util.network.PacketUtils;
import haptic.util.world.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Scaffold extends Module {
	
	public BlockPos pos, lastPos;
	public EnumFacing facing, lastFacing;
	
	public int oldSlot, slotSpoofed;
	private boolean changedSlot;
	
	private boolean diagonal = true;
	
	private double blockYaw;
	private double currentDelay;
	
	private float yaw, pitch;
	private float finalYaw, finalPitch;
	
	private int ticks, towerTicks;
	
	public ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal");
	public ModeSetting picking = new ModeSetting("Picking Mode", "Switch", "Switch", "Spoof", "LiteSpoof", "None");
	private ModeSetting tower = new ModeSetting("Tower", "None", "None", "NCP");
	
	public Scaffold() {
		super("Scaffold", Category.WORLD, ListeningType.ALWAYS, Keyboard.KEY_H);
		this.addSettings(mode, tower, picking);
	}
	
	public void onEnable() {
		oldSlot = mc.thePlayer.inventory.currentItem;
		ticks = 0;
		towerTicks = 0;
		
		lastPos = pos = null;
		lastFacing = facing = null;
	}
	
	public void onDisable() {
		switchToOriginalSlot();
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventRender) {
			this.setDisplayName(this.getName() + ChatFormatting.GRAY + " " + mode.getMode());
		}
		
		if(!this.isEnabled()) {
			return;
		}
		
		if(event instanceof EventUpdate) {
			pickBlock();
		} else if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			if(e.getPacket() instanceof C09PacketHeldItemChange) {
				e.setCancelled(picking.is("Spoof"));
			}
		} else if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			Tower(e);
		}
		
		switch(mode.getMode()) {
		case "Normal":
			Normal(event);
			break;
		}
	}
	
	private void Tower(MovementEvent e) {
		switch(tower.getMode()) {
		case "NCP":
			if(mc.thePlayer.onGround && mc.gameSettings.keyBindJump.isKeyDown()) {
				e.setMotionY(0.41999998688698);
				mc.thePlayer.motionY = 0.42;
				towerTicks = 0;
			} else if(towerTicks == 2) {
				mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
				e.setMotionY(mc.thePlayer.motionY = 0);
				mc.thePlayer.onGround = true;
			}
			towerTicks++;
			break;
		}
	}
	
	private void Normal(Event event) {
		if(event instanceof EventUpdate) {
			if(isAirUnder()) {
				setBlockFacing(getBlockPosUnder());
				
				if(pos != null && facing != null) {
					lastPos = pos;
					lastFacing = facing;
					
					float rotations[] = getNormalRotations();
					finalYaw = rotations[0];
					finalPitch = rotations[1];
					
					placeBlock(0);
				}
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			e.setYaw(mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = finalYaw);
			e.setPitch(mc.thePlayer.rotationPitchHead = finalPitch);
		}
	}
	
	private float[] getNormalRotations() {
		if(pos != null && facing != null) {
			float gottenYaw = BlockUtil.getDirectionToBlock(pos.getX(), pos.getY(), pos.getZ(), facing)[0];
			float gottenPitch = BlockUtil.getDirectionToBlock(pos.getX(), pos.getY(), pos.getZ(), facing)[1];
			
			double yawDiff = Math.abs(yaw - gottenYaw);
			
			if(yawDiff > 20 && yawDiff < 340) {
				yaw = gottenYaw;
			}
			
			double pitchDiff = Math.abs(pitch - gottenYaw);
			
			if(pitchDiff > 2) {
				pitch = gottenPitch;
			}
		}
		
		return new float[] {yaw, pitch};
	}
	
	private float[] getSmoothRotations() {
		if(lastPos != null && lastFacing != null) {
			float gottenYaw = BlockUtil.getDirectionToBlock(lastPos.getX(), lastPos.getY(), lastPos.getZ(), lastFacing)[0];
			float gottenPitch = BlockUtil.getDirectionToBlock(lastPos.getX(), lastPos.getY(), lastPos.getZ(), lastFacing)[1];
			
			double yawDiff = Math.abs(yaw - gottenYaw);
			
			if(yawDiff > 20 && yawDiff < 340) {
				if(yawDiff < 180) {
					if(yaw > gottenYaw) {
						yaw -= 20;
					} else {
						yaw += 20;
					}
				} else {
					if(yaw > gottenYaw) {
						yaw += 20;
					} else {
						yaw -= 20;
					}
				}
			} else {
				//yaw = gottenYaw;
			}
			
			double pitchDiff = Math.abs(pitch - gottenPitch);
			
			if(pitchDiff > 2) {
				if(pitch > gottenPitch) {
					pitch -= 2;
				} else {
					pitch += 2;
				}
			}
		}
		return new float[] {yaw, pitch};
	}
	
	private boolean placeBlock(double negativeExpand) {
		if(!world.negativeExpand(negativeExpand)) {
			return false;
		}
		
		if(picking.is("Spoof")) {
			if(slotSpoofedIsBlock()) {
				return sendPlacing();
			}
		} else if(picking.is("LiteSpoof")) {
			if(slotSpoofedIsBlock()) {
				liteSpoofPickBlock();
				boolean placed = sendPlacing();
				liteSpoofSwitchBack();
				return placed;
			}
		} else {
			if(currentSlotIsBlock()) {
				return sendPlacing();
			}
		}
		return false;
	}
	
	private boolean sendPlacing() {
		boolean placed = mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, picking.is("None") || picking.is("Switch") ? mc.thePlayer.getHeldItem() : mc.thePlayer.inventory.getStackInSlot(slotSpoofed), pos, facing, world.getVec3(pos, facing));
		if(placed) {
			PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
		}
		return placed;
	}
	
	private BlockPos getBlockPosUnder() {
		return new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
	}
	
	private boolean isAirUnder() {
		Block block = mc.theWorld.getBlockState(getBlockPosUnder()).getBlock();
		return block instanceof BlockAir || block instanceof BlockTallGrass || block instanceof BlockLiquid;
	}
	
	private boolean currentSlotIsBlock() {
		return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
	}
	
	private boolean slotSpoofedIsBlock() {
		return mc.thePlayer.inventory.getStackInSlot(slotSpoofed) != null && mc.thePlayer.inventory.getStackInSlot(slotSpoofed).getItem() instanceof ItemBlock;
	}
	
	private void pickBlock() {
		for(int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                continue;
            if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !BlockUtil.blockBlacklist.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
            	if(i != slotSpoofed || mc.thePlayer.inventory.currentItem != i) {
            		changedSlot = true;
            	}
        		slotSpoofed = i;
				break;
            }
        }
		
		if(picking.is("Switch")) {
			mc.thePlayer.inventory.currentItem = slotSpoofed;
		}
		
		if(changedSlot) {
			if(picking.is("Spoof")) {
				PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(slotSpoofed));
			}
			
			changedSlot = false;
		}
	}
	
	private void switchToOriginalSlot() {
		if(picking.is("Switch")) {
			mc.thePlayer.inventory.currentItem = oldSlot;
		} else if(picking.is("Spoof")) {
			if(mc.thePlayer.inventory.currentItem != slotSpoofed) {
				PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
			}
		}
	}
	
	private void liteSpoofPickBlock() {
		for(int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                continue;
            if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !BlockUtil.blockBlacklist.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
            	PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(slotSpoofed = i));
				break;
            }
        }
	}
	
	private void liteSpoofSwitchBack() {
		if(mc.thePlayer.inventory.currentItem != slotSpoofed) {
			PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
		}
	}
	
	private void setBlockFacing(BlockPos pos) {
		if(diagonal) {
			if (mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
				this.pos = pos.add(0, -1, 0);
				facing = EnumFacing.UP;
				if(blockYaw != 0) {
					currentDelay = 0;
				}
				blockYaw = 0;
			} else if (mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
				this.pos = pos.add(-1, 0, 0);
				facing = EnumFacing.EAST;
				if(blockYaw != 90) {
					currentDelay = 0;
				}
				blockYaw = 90;
			} else if (mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
				this.pos = pos.add(1, 0, 0);
				facing = EnumFacing.WEST;
				if(blockYaw != -90) {
					currentDelay = 0;
				}
				blockYaw = -90;
			} else if (mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
				this.pos = pos.add(0, 0, -1);
				facing = EnumFacing.SOUTH;
				if(blockYaw != 180) {
					currentDelay = 0;
				}
				blockYaw = 180;
			} else if (mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
				this.pos = pos.add(0, 0, 1);
				facing = EnumFacing.NORTH;
				if(blockYaw != 0) {
					currentDelay = 0;
				}
				blockYaw = 0;
			}
			else if (mc.theWorld.getBlockState(pos.add(-1, 0, -1)).getBlock() != Blocks.air) {
				facing = EnumFacing.EAST;
				this.pos = pos.add(-1, 0, -1);
				if(blockYaw != 135) {
					currentDelay = 0;
				}
				blockYaw = 135;
			} else if (mc.theWorld.getBlockState(pos.add(1, 0, 1)).getBlock() != Blocks.air) {
				facing = EnumFacing.WEST;
				this.pos = pos.add(1, 0, 1);
				if(blockYaw != -45) {
					currentDelay = 0;
				}
				blockYaw = -45;
			} else if (mc.theWorld.getBlockState(pos.add(1, 0, -1)).getBlock() != Blocks.air) {
				facing = EnumFacing.SOUTH;
				this.pos = pos.add(1, 0, -1);
				if(blockYaw != 135) {
					currentDelay = 0;
				}
				blockYaw = -135;
			} else if (mc.theWorld.getBlockState(pos.add(-1, 0, 1)).getBlock() != Blocks.air) {
				facing = EnumFacing.NORTH;
				this.pos = pos.add(-1, 0, 1);
				if(blockYaw != 45) {
					currentDelay = 0;
				}
				blockYaw = 45;
			}
			else if (mc.theWorld.getBlockState(pos.add(0, -1, 1)).getBlock() != Blocks.air) {
				this.pos = pos.add(0, -1, 1);
				facing = EnumFacing.UP;
				blockYaw = mc.thePlayer.rotationYaw;
			}
			else if (mc.theWorld.getBlockState(pos.add(0, -1, -1)).getBlock() != Blocks.air) {
				this.pos = pos.add(0, -1, -1);
				facing = EnumFacing.UP;
				blockYaw = mc.thePlayer.rotationYaw;
			}
			else if (mc.theWorld.getBlockState(pos.add(1, -1, 0)).getBlock() != Blocks.air) {
				this.pos = pos.add(1, -1, 0);
				facing = EnumFacing.UP;
				blockYaw = mc.thePlayer.rotationYaw;
			}
			else if (mc.theWorld.getBlockState(pos.add(-1, -1, 0)).getBlock() != Blocks.air) {
				this.pos = pos.add(-1, -1, 0);
				facing = EnumFacing.UP;
				blockYaw = mc.thePlayer.rotationYaw;
			}
			else {
				pos = null;
				facing = null;
			}
		} else {
			if (mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
				this.pos = pos.add(0, -1, 0);
				facing = EnumFacing.UP;
			} else if (mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
				this.pos = pos.add(-1, 0, 0);
				facing = EnumFacing.EAST;
			} else if (mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
				this.pos = pos.add(1, 0, 0);
				facing = EnumFacing.WEST;
			} else if (mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
				this.pos = pos.add(0, 0, -1);
				facing = EnumFacing.SOUTH;
			} else if (mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
				this.pos = pos.add(0, 0, 1);
				facing = EnumFacing.NORTH;
			} else {
				pos = null;
				facing = null;
			}
		}
	}

}