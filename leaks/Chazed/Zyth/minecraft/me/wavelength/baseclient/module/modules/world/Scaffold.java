package me.wavelength.baseclient.module.modules.world;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.MovementUtils;
import me.wavelength.baseclient.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;



public class Scaffold extends Module {

	public Scaffold() {
		super("Scaffold", "This is a test module...", 0, Category.WORLD, AntiCheat.VANILLA);
	}
	
	
	
	
	private int block;
	private int boost;
	

	@Override
	public void setup() {
        moduleSettings.addDefault("Tower", true);
        moduleSettings.addDefault("Swing", false);
        moduleSettings.addDefault("Rots", true);
        moduleSettings.addDefault("Sprint", true);
        moduleSettings.addDefault("HypixelSprint", true);
        moduleSettings.addDefault("delay", 0D);
	}

	@Override
	public void onEnable() {
		block = mc.thePlayer.inventory.currentItem;
		boost = 0;
		mc.timer.timerSpeed = 1;
		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw - 180, 75, true));
	}

	@Override
	public void onDisable() {
		mc.thePlayer.inventory.currentItem = block;
		mc.timer.timerSpeed = 1;
		mc.thePlayer.isSwingInProgress = false;
		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.cameraYaw, mc.thePlayer.cameraPitch, true));
	}
	
    public void onPacketSent(PacketSentEvent event) {
    	
    	
    	
		if(MovementUtils.isMoving()) {
			
        	if(event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
        		C05PacketPlayerLook C05 = (C05PacketPlayerLook)event.getPacket();
        		if(C05.getYaw() != mc.thePlayer.rotationYaw - 180) {
        			event.setCancelled(true);
        			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw - 180, 75, C05.isOnGround()));
        			 //Utils.message = "Fail2.";
        			//Utils.print();
        		}
        	}

        	if(event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
        		C06PacketPlayerPosLook C06 = (C06PacketPlayerPosLook)event.getPacket();
        		if(C06.getYaw() != mc.thePlayer.rotationYaw - 180) {
        			event.setCancelled(true);
        			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(C06.getPositionX(), C06.getPositionY(), C06.getPositionZ(), mc.thePlayer.rotationYaw - 180, 75,  mc.thePlayer.onGround));
        			 //Utils.message = "Fail.";
        			//Utils.print();
        		}
        	}	
		}else {
        	if(event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
        		C05PacketPlayerLook C05 = (C05PacketPlayerLook)event.getPacket();
        		if(C05.getYaw() != mc.thePlayer.rotationYaw - 180) {
        			event.setCancelled(true);
        			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw - 180, 90, C05.isOnGround()));
        			 //Utils.message = "Fail2.";
        			//Utils.print();
        		}
        	}

        	if(event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
        		C06PacketPlayerPosLook C06 = (C06PacketPlayerPosLook)event.getPacket();
        		if(C06.getYaw() != mc.thePlayer.rotationYaw - 180) {
        			event.setCancelled(true);
        			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(C06.getPositionX(), C06.getPositionY(), C06.getPositionZ(), mc.thePlayer.rotationYaw - 180, 90,  mc.thePlayer.onGround));
        			 //Utils.message = "Fail.";
        			//Utils.print();
        		}
        	}	
		}
		
		
		
    }

	@Override
	public void onUpdate(UpdateEvent event) {
		boost++;
		
		mc.thePlayer.inventory.currentItem = 2;

        if(this.moduleSettings.getBoolean("HypixelSprint") == true) {
        	if(boost >= 15) {
        		if(mc.thePlayer.onGround) {
            		mc.thePlayer.motionX *= 1.19f;
        			mc.thePlayer.motionZ *= 1.19f;
        		}
        	}else {
        		mc.timer.timerSpeed = 1;
        	}

        	
        }else {

        }
        
        

         if(this.moduleSettings.getBoolean("Swing") == false) {
 			mc.thePlayer.isSwingInProgress = false;
         }

         if(this.moduleSettings.getBoolean("Sprint") == false) {
 			mc.thePlayer.setSprinting(false);
 			mc.gameSettings.keyBindSprint.setPressed(false);
         }else {
  			mc.thePlayer.setSprinting(true);
         }


					if(MovementUtils.isMoving()) {

							

					}else {

						//tower

						if(mc.gameSettings.keyBindJump.isKeyDown()) {
							
							if(this.moduleSettings.getBoolean("Tower") == true) {
								mc.timer.timerSpeed = 1;
									mc.thePlayer.motionY = 0.42f;
									mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.cameraYaw -120, 90, true));
					               
					                
							}
			               
			                
			            }
					}
					
					
					
					
					Entity p = mc.thePlayer;
					BlockPos bp = new BlockPos(p.posX, p.getEntityBoundingBox().minY, p.posZ);
					
					if(valid(bp.add(0, -2, 0)))
						
						place(bp.add(0, -1, 0), EnumFacing.UP);
					
					else if (valid(bp.add(-1, -1, 0)))
						
						place(bp.add(0, -1, 0), EnumFacing.EAST);
					
					else if (valid(bp.add(1, -1, 0)))
						
						place(bp.add(0, -1, 0), EnumFacing.WEST);
					
					else if (valid(bp.add(0, -1, -1)))
						
						place(bp.add(0, -1, 0), EnumFacing.SOUTH);
					
					else if (valid(bp.add(0, -1, 1)))
						
						place(bp.add(0, -1, 0), EnumFacing.NORTH);
					
					else if(valid(bp.add(1, -1, 1))) {
						
						if(valid(bp.add(0, -1, 1)))
							
							place(bp.add(0, -1, 1), EnumFacing.NORTH);
						place(bp.add(1, -1, 1), EnumFacing.EAST);
					}else if (valid(bp.add(-1, -1, 1))) {
						if(valid(bp.add(-1, -1, 0)))
							place(bp.add(0, -1, 1), EnumFacing.WEST);
						place(bp.add(-1, -1, 1),EnumFacing.SOUTH);
					}else if (valid(bp.add(-1, -1, -1))) {
						if(valid(bp.add(0, -1, -1)))
							place(bp.add(0, -1, 1), EnumFacing.SOUTH);
						place(bp.add(-1, -1, 1), EnumFacing.WEST);
					}else if (valid(bp.add(1, -1 , -1))) {
						if(valid(bp.add(1, -1, 0)))
							place(bp.add(1, -1, 0), EnumFacing.EAST);
						place(bp.add(1, -1, -1), EnumFacing.NORTH);
					}
				}



			
			void place(BlockPos p, EnumFacing f) {
				if(f == EnumFacing.UP)
					p = p.add(0, -1 ,0);
				else if(f == EnumFacing.NORTH)
					p = p.add(0, 0, 1);
				else if(f == EnumFacing.EAST)
					p = p.add(-1, 0, 0);
				else if(f == EnumFacing.SOUTH)
					p = p.add(0, 0, -1);
				else if(f == EnumFacing.WEST)
					p = p.add(1, 0, 0);
				
				EntityPlayerSP _p = mc.thePlayer;
				
				if(_p.getHeldItem() != null && _p.getHeldItem().getItem() instanceof ItemBlock) {


					
					if(timer.delay(this.moduleSettings.getDouble("delay")*50)) {
						_p.swingItem();
						mc.playerController.onPlayerRightClick(_p, mc.theWorld, _p.getHeldItem(), p, f, new Vec3(1, 2, 1));
						//Utils.message = "Block Place at " + Math.round(mc.thePlayer.posX) + ", " + Math.round(mc.thePlayer.posY) + ", " + Math.round(mc.thePlayer.posZ);
						//Utils.print();
						}



				//double x = p.getX() + 0.25 - _p.posX;
				//double z = p.getZ() + 0.25 - _p.posZ;
				//double y = p.getY() + 0.25 - _p.posY;
				//double distance = MathHelper.sqrt(x * x + z * z);
				//float yaw = (float) (Math.atan2(z, x) * 180 / Math.PI - 90);
				//float pitch = (float) - (Math.atan2(y, distance) * 180 /Math.PI);
				
				}
			}
			
			boolean valid(BlockPos p) {
				boolean b = mc.theWorld.isAirBlock(p);
				boolean n;
				if(b == true) {
					n = false;
				}else {
					n = true;
				}
				return n;
			}

	@Override
	public void onPacketReceived(PacketReceivedEvent event) {

	}

}