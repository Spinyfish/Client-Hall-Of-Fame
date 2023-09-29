package haptic.util.world;

import haptic.module.impl.world.Breaker;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BedInfo {
		
		public BedInfo(BlockPos pos, EnumFacing face) {
			this.pos = pos;
			this.face = face;
		}
		
		public BlockPos pos;
		public EnumFacing face;
		
		public static Minecraft mc;
		
		public BedInfo findBed() {
			
			try {
				
				if (Breaker.lastBed != null && mc.thePlayer.getDistance(Breaker.lastBed.pos.getX(), Breaker.lastBed.pos.getY(), Breaker.lastBed.pos.getZ()) <= 4) {
					if (mc.theWorld.getBlockState(Breaker.lastBed.pos).getBlock() instanceof BlockBed) {
						return Breaker.lastBed;
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
			
			Breaker.lastBed = new BedInfo(bed, bedFace);
			return Breaker.lastBed;
			
			
		}
		
	}