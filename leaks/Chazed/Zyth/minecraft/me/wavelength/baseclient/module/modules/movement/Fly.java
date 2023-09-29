package me.wavelength.baseclient.module.modules.movement;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.modules.exploit.Disabler;
import me.wavelength.baseclient.utils.MovementUtils;
import me.wavelength.baseclient.utils.Utils;
import net.minecraft.block.BlockSlime;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;

public class Fly extends Module {

	public Fly() {
		super("Fly", "Reach the outer skies!", 0, Category.MOVEMENT, AntiCheat.VULCAN, AntiCheat.VANILLA, AntiCheat.MORGAN, AntiCheat.VERUS, AntiCheat.SPARTAN, AntiCheat.MINEMORA, AntiCheat.HYPIXEL, AntiCheat.NCP, AntiCheat.GHOSTLY, AntiCheat.BLOCKDROP);
	}

	private boolean isFlying;
	private boolean allowFlying;

	@Override
	public void setup() {
		moduleSettings.addDefault("speed", 1.0D);
		moduleSettings.addDefault("verusdamage", false);
	}

	double posY;
	int verusdamage;
	int damagedelay;
	private int state;
	int ticks;
	int packets;
	boolean hasClipped;
	private int ncp;
	double oldX;
	double oldY;
	double oldZ;
	int funni;
	
	double y1;
	
	double x1;
	
	double z1;

	@Override
	public void onEnable() {

		if(this.antiCheat == AntiCheat.HYPIXEL) {
			mc.timer.timerSpeed = 1;
			oldX = mc.thePlayer.posX;
			oldY = mc.thePlayer.posY;
			oldZ = mc.thePlayer.posZ;
		}
		
		packets = 0;
		if(this.antiCheat == AntiCheat.MORGAN) {
			oldX = mc.thePlayer.posX;
			oldY = mc.thePlayer.posY;
			oldZ = mc.thePlayer.posZ;
			mc.thePlayer.sendQueue.addToSendQueue(new C0FPacketConfirmTransaction());
			funni = 0;
		}
		
		ticks = 0;
		if(this.antiCheat == AntiCheat.SPARTAN) {
			Utils.verusdamage();
		}
		
		if(this.antiCheat == AntiCheat.VULCAN) {
			mc.thePlayer.jump();
		}
		
			
	        
		ncp = mc.thePlayer.inventory.currentItem;
		if(this.antiCheat == AntiCheat.GHOSTLY) {
		        Utils.verusdamage();
		}

		if(this.antiCheat == AntiCheat.NCP) {
			hasClipped = false;
			ticks = 0;
		}

		if(this.antiCheat == AntiCheat.MINEMORA) {
			mc.thePlayer.jump();
		}
		

		





		mc.thePlayer.motionX *= 0.1;
		mc.thePlayer.motionZ *= 0.1;

		damagedelay = 0;
		verusdamage = 0;
	}

	@Override
	public void onDisable() {
		mc.thePlayer.capabilities.allowFlying = false;
		mc.thePlayer.capabilities.isFlying = false;
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionZ = 0;
		mc.thePlayer.motionY = 0;
		mc.timer.timerSpeed = 1f;
		if(this.antiCheat == AntiCheat.MINEMORA) {
	        for(int i = 0; i <= 10000000; i++) {
	        	mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
	        }
		}

		
	}
	
	@Override
	public void onPacketSent(PacketSentEvent event) {
		if(this.antiCheat == AntiCheat.MINEMORA) {
			if(event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C04PacketPlayerPosition || event.getPacket() instanceof C05PacketPlayerLook) {
				mc.thePlayer.sendQueue.addToSendQueue(new C0FPacketConfirmTransaction());
				
				Utils.message = "blink";
				Utils.print();
				
				event.setCancelled(true);


			}
			
			if(event.getPacket() instanceof C00PacketKeepAlive || event.getPacket() instanceof C0FPacketConfirmTransaction) {
				try {
					Thread.sleep(10L);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		
		if(this.moduleSettings.getBoolean("verusdamage") == true) {
			if(verusdamage <= 3) {
				if(event.getPacket() instanceof C03PacketPlayer) {
					event.setCancelled(true);
				}
			}
		}


		
	}
	
	public void onPacketReceived(PacketReceivedEvent event) {

	}
	
	

	@Override
	public void onUpdate(UpdateEvent event) {


		
		if(Disabler.hazel == true) { if(mc.thePlayer.ticksExisted % 2 == 0) { mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0)); } }
		

		if(this.moduleSettings.getBoolean("verusdamage") == true) {
			
			
			if(damagedelay == 4) {
				Utils.verusdamage();
			}
			
			
			mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
		}
		
		
		if(this.antiCheat == AntiCheat.MORGAN) {

			
			if(funni == 1) {
				mc.thePlayer.motionX *= 1.4;
				mc.thePlayer.motionZ *= 1.4;
			}else {
				mc.thePlayer.motionY = 0;
			}


			funni++;
			
		}

		
		if(this.antiCheat == AntiCheat.BLOCKDROP) {


			

			
			mc.thePlayer.motionY = -0.01;
           
		}
		
		if(this.antiCheat == AntiCheat.HYPIXEL) {
			ticks++;

			mc.thePlayer.isCollided = true;
			mc.thePlayer.isCollidedVertically = true;


				MovementUtils.setMotion(0.255);




			mc.thePlayer.motionY = 0;
			
		}
		
		if(this.antiCheat == AntiCheat.SPARTAN) {
			ticks++;
				mc.thePlayer.motionY = 0;


			
			if(ticks <= 2) {
				MovementUtils.setMotion(0);
			}
			
			
			if(ticks <= 5 && ticks >= 5) {
				MovementUtils.setMotion(0.9);
				mc.timer.timerSpeed = 1.8f;
			}else {
				if(mc.timer.timerSpeed > 0.35)
					mc.timer.timerSpeed = mc.timer.timerSpeed - 0.01;
			}
			
			if(ticks >= 6) {
				MovementUtils.setMotion(0.8);

			}
 

			

			


		}
		

		
		if(this.antiCheat == AntiCheat.VULCAN) {
			double speed = moduleSettings.getDouble("speed");
			verusdamage++;
			damagedelay++;
			
			if(damagedelay <= 100) {
				mc.timer.timerSpeed = 5;
				MovementUtils.setMotion(0);
				
				if(mc.thePlayer.onGround) {
					mc.thePlayer.jump();
					mc.thePlayer.motionY = 0.05;
				}

			}else {
				mc.thePlayer.motionY = 0;
				MovementUtils.setMotion(speed);
				mc.timer.timerSpeed = 1;
			}
			

			
		}

		if(this.antiCheat == AntiCheat.MINEMORA) {
			packets++;
			mc.thePlayer.motionY = 0;
			MovementUtils.setMotion(1);
			
			y1 = mc.thePlayer.posY;
			x1 = mc.thePlayer.posX;
			z1 = mc.thePlayer.posZ;
			
		}

		if(this.antiCheat == AntiCheat.VANILLA) {
			verusdamage++;
			if(this.moduleSettings.getBoolean("verusdamage") == true) {
				
				if(verusdamage == 4) {
					Utils.verusdamage();
					mc.thePlayer.motionY = 0.25;

				}
				
				if(verusdamage == 5) {
					mc.thePlayer.motionY = 0.25;
				}
				

				if(verusdamage <= 6) {
					mc.thePlayer.motionX = 0;
					mc.thePlayer.motionZ = 0;
					MovementUtils.setMotion(0);
				}else {
					mc.timer.timerSpeed = 1;
					
					if(this.moduleSettings.getBoolean("verusdamage") == true) {
						if(verusdamage >= 29) {
							MovementUtils.setMotion(0.33);
							mc.timer.timerSpeed = 1;
						}else {
							MovementUtils.setMotion(this.moduleSettings.getDouble("speed"));
							mc.timer.timerSpeed = 1;
						}	
					}else {
						MovementUtils.setMotion(this.moduleSettings.getDouble("speed"));
					}


					
					
					if(mc.gameSettings.keyBindJump.isKeyDown()) {
						mc.thePlayer.motionY = this.moduleSettings.getDouble("speed")/2;
					}else {
						if(mc.gameSettings.keyBindSneak.isKeyDown()) {
							mc.thePlayer.motionY = -this.moduleSettings.getDouble("speed")/2;
						}
						else {
					mc.thePlayer.motionY = 0;
					
						}
					}
				}
			}else {
				mc.timer.timerSpeed = 1;
				MovementUtils.setMotion(this.moduleSettings.getDouble("speed"));
				if(mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.thePlayer.motionY = this.moduleSettings.getDouble("speed")/2;
				}else {
					if(mc.gameSettings.keyBindSneak.isKeyDown()) {
						mc.thePlayer.motionY = -this.moduleSettings.getDouble("speed")/2;
					}
					else {
				mc.thePlayer.motionY = 0;
				
					}
				}
			}
			

		}
		
		//ghostly fly started banning bec I edited sum
		if(this.antiCheat == AntiCheat.GHOSTLY) {
			verusdamage++;
			MovementUtils.setMotion(this.moduleSettings.getDouble("speed")*5);
			
			if(verusdamage >= 10) {
				mc.thePlayer.motionY = -0.1;
		        verusdamage = 0;
			}
			
			
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY = 0.42;
			}else {
			mc.thePlayer.motionY = 0;
			}
		}

		if(this.antiCheat == AntiCheat.VERUS) {
			

			mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));


			if(timer.delay(2)){
				mc.timer.timerSpeed = 1f;
				MovementUtils.setMotion(0.375);
				timer.reset();
			}else {
				MovementUtils.setMotion(0.375);

				mc.timer.timerSpeed = 1f;
			}
			
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY = 0.42;
			}else {
				if(mc.gameSettings.keyBindSneak.isKeyDown()) {
					mc.thePlayer.motionY = -0.42;
				}
				else {
			mc.thePlayer.motionY = 0;
			
				}
			}
		}

		if(this.antiCheat == AntiCheat.NCP) {
			if(this.isToggled()) {
				ticks++;
				if (!hasClipped && getSlimeSlot()!= -1) {
					mc.timer.timerSpeed = 1F;
					mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
					MovementUtils.setMotion(0);
					if (ticks == 1) mc.thePlayer.jump();
					
					if(ticks == 5) {
						mc.thePlayer.inventory.currentItem = getSlimeSlot();
					}

					if (ticks == 6) {
						mc.thePlayer.isSwingInProgress = false;
						BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ);
						mc.thePlayer.inventory.currentItem = getSlimeSlot();
						mc.thePlayer.inventory.currentItem = ncp;
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, 90, mc.thePlayer.onGround));
						mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(pos, EnumFacing.UP.getIndex(), mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem), 0, 1, 0));
						mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, 90, mc.thePlayer.onGround));
					}

					if (ticks == 7) {
						hasClipped = true;
					}
				} else {
					
					if(ticks >= 7) {
						MovementUtils.setMotion(0.153);
						 //debugging timer to set good motion speed
						//Utils.message = " " + mc.timer.timerSpeed;
						//Utils.print();
						mc.thePlayer.motionY = 0;
						Utils.verusc08();
						mc.thePlayer.onGround = true;

					}
					if(ticks >= 10) {
						
						if(ticks == 12) {
							mc.timer.timerSpeed = 2;
						}


							if(mc.timer.timerSpeed >= 1.0275) {
								mc.timer.timerSpeed = mc.timer.timerSpeed - 0.025;
							}

						
						
						if(ticks >= 9 && ticks <= 88) {
							mc.thePlayer.onGround = true;

						}
						
						if(ticks >= 50) {
							mc.timer.timerSpeed = 1;
						}
						
						if(ticks >= 88) {
							toggle();
						}

					}else {
						mc.thePlayer.onGround = true;
						//setting timer to 1 in case of errors.
						mc.timer.timerSpeed = 1F;
					}
					

					mc.thePlayer.motionY = 0;

				}
			}
	}
}

	private int getSlimeSlot() {
		for (int i = 36; i < 45; ++i) {
			ItemStack item;
			if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
					|| !isSlime(item = mc.thePlayer.inventoryContainer.getSlot(i).getStack()))
				continue;
			return i - 36;
		}
		return -1;
	}
	public boolean isSlime(ItemStack item) {
		return ((ItemBlock) item.getItem()).getBlock() instanceof BlockSlime;
	}

	public boolean isEmpty(ItemStack stack) {
		return stack == null;
	}

	private int getSlot() {
		return 0;
	}

}