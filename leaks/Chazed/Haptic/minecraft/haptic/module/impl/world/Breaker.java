package haptic.module.impl.world;

import org.lwjgl.input.Keyboard;

import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.module.Category;
import haptic.module.Module;
import haptic.setting.impl.BooleanSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.network.PacketUtils;
import haptic.util.rotations.RotationsUtils;
import haptic.util.world.BedInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Breaker extends Module {
	
	private BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
	private NumberSetting range = new NumberSetting("Range", 4, 1, 10, 0.25, this);
	
	public static BedInfo lastBed = null;
	
	public Breaker() {
		super("Breaker", Category.WORLD, Keyboard.KEY_NONE);
		this.addSettings(rotate, range);
	}

	public void onEvent(Event event) {
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			int n = (int) range.getValue();
			for (int i = -n; i < n; ++i) {
				for (int j = n; j > -n; --j) {
					for (int k = -n; k < n; ++k) {
						int n2 = (int)mc.thePlayer.posX + i;
						int n3 = (int)mc.thePlayer.posY + j;
						int n4 = (int)mc.thePlayer.posZ + k;
						BlockPos blockPos = new BlockPos(n2, n3, n4);
						Block block = mc.theWorld.getBlockState(blockPos).getBlock();
						float[] rotations = RotationsUtils.getRotationFromPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());

						if(block instanceof BlockBed) {
							/*
							if(rotate.isEnabled()) {
								e.setYaw(rotations[0]);
								e.setPitch(rotations[1]);
							}
							mc.thePlayer.swingItem();
							//mc.playerController.onPlayerDamageBlock(blockPos, EnumFacing.DOWN);
							PacketUtil.sendPacket(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
							PacketUtil.sendPacket(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
							*/
							BedInfo info = findBed();
							
							BlockPos bed = info.pos;
							EnumFacing bedFace = info.face;
							
							if (bed == null || bedFace == null) {
								return;
							}
							
							float[] rots = RotationsUtils.getRotationFromPosition(bed.getX(), bed.getZ(), bed.getY());
							e.setYaw(rots[0]);
							e.setPitch(rots[1]);
							
							mc.playerController.onPlayerDamageBlock(bed, bedFace);
							mc.thePlayer.swingItem();
							//mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bed, bedFace));
							//mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, bed, bedFace));
						}

						if(block instanceof BlockCake) {
							BlockPos blockPos1 = new BlockPos(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ());
							Block block1 = this.mc.theWorld.getBlockState(blockPos1).getBlock();
							BlockPos blockPos2 = new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ());
							Block block2 = this.mc.theWorld.getBlockState(blockPos2).getBlock();
							BlockPos blockPos3 = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() - 1);
							Block block3 = this.mc.theWorld.getBlockState(blockPos3).getBlock();
							BlockPos blockPos4 = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1);
							Block block4 = this.mc.theWorld.getBlockState(blockPos4).getBlock();
							BlockPos blockPos5 = new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
							Block block5 = this.mc.theWorld.getBlockState(blockPos5).getBlock();
							if(block1 instanceof BlockAir || block2 instanceof BlockAir || block3 instanceof BlockAir || block4 instanceof BlockAir || block5 instanceof BlockAir) {
								if(rotate.isEnabled()) {
									e.setYaw(rotations[0]);
									e.setPitch(rotations[1]);
								}
								PacketUtils.sendPacket(new C0APacketAnimation());
								PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(blockPos, 1, mc.thePlayer.getHeldItem(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
							} else {
								PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos5, EnumFacing.DOWN));
								PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos5, EnumFacing.DOWN));
								PacketUtils.sendPacket(new C0APacketAnimation());
							}
						}
					}
				}
			}
		}
	}
	
	public BedInfo findBed() {
		
		try {
			
			if (lastBed != null && mc.thePlayer.getDistance(lastBed.pos.getX(), lastBed.pos.getY(), lastBed.pos.getZ()) <= range.getValue()) {
				if (mc.theWorld.getBlockState(lastBed.pos).getBlock() instanceof BlockBed) {
					return lastBed;
				}
			}
			
		} catch (Exception e) {
			
		}
		
		BlockPos bed = null;
		EnumFacing bedFace = null;
		
		for (EnumFacing face1 : EnumFacing.VALUES) {
			
			BlockPos playerPos = mc.thePlayer.getPosition();
			if (mc.theWorld.getBlockState(playerPos.offset(face1)).getBlock() instanceof BlockBed) {
				
				bed = playerPos.offset(face1);
				bedFace = face1.getOpposite();
				break;
				
			}
			
			for (EnumFacing face2 : EnumFacing.VALUES) {
				
				BlockPos pos2 = playerPos.offset(face2);
				
				if (mc.theWorld.getBlockState(pos2).getBlock() instanceof BlockBed) {
					
					bed = pos2;
					bedFace = face2.getOpposite();
					break;
					
				}
				
				for (EnumFacing face3 : EnumFacing.VALUES) {
					
					BlockPos pos3 = pos2.offset(face3);
					
					if (mc.theWorld.getBlockState(pos3).getBlock() instanceof BlockBed) {
						
						bed = pos3;
						bedFace = face3.getOpposite();
						break;
						
					}
					
					for (EnumFacing face4 : EnumFacing.VALUES) {
						
						BlockPos pos4 = pos3.offset(face4);
						
						if (mc.theWorld.getBlockState(pos4).getBlock() instanceof BlockBed) {
							
							bed = pos4;
							bedFace = face4.getOpposite();
							break;
							
						}
						
						for (EnumFacing face5 : EnumFacing.VALUES) {
							
							BlockPos pos5 = pos4.offset(face5);
							
							if (mc.theWorld.getBlockState(pos5).getBlock() instanceof BlockBed) {
								
								bed = pos5;
								bedFace = face5.getOpposite();
								break;
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		lastBed = new BedInfo(bed, bedFace);
		return lastBed;
		
		
	}

}
