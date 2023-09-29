package de.fanta.module.impl.movement;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.RotationUtil;
import de.fanta.utils.TimeUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Speed extends Module {
	public Speed() {
		super("Speed", Keyboard.KEY_C, Type.Movement, new Color(50,255,10));
		this.settings.add(new Setting("Hover", new CheckBox(false)));
		this.settings.add(new Setting("DamageBoost", new CheckBox(false)));
		this.settings.add(new Setting("Timer", new CheckBox(false)));
		this.settings.add(new Setting("Minepex-Motion", new Slider(1, 60, 1, 60)));
		this.settings.add(new Setting("Boost", new Slider(0.4, 2, 1, 1)));

		this.settings.add(new Setting("Modes",
				new DropdownBox("Vanilla",
						new String[] { "Vanilla", "Intave", "NCP-Bhob", "OldAAC", "AAC", "Mineplex", "Watchdog",
								"HypixelLowHob", "AntiAC", "LibreCraft", "Mineplex2", "VerusGround", "AAC 3.3.13",
								"Verus", "TP", "BlocksMC", "VerusFloat" })));

	}

	public boolean turn;
	boolean verusdmg = false;

	public static double getSpeed() {
		return Math.sqrt(mc.getMinecraft().thePlayer.motionX * mc.getMinecraft().thePlayer.motionX
				+ mc.getMinecraft().thePlayer.motionZ * mc.getMinecraft().thePlayer.motionZ);
	}

	public void onEnable() {

		mineplexMotion = 0.2F;
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
		case "BlocksMC":

			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			// mc.timer.timerSpeed = 0.1F;
			break;
		case "Watchdog":

//			if (mc.thePlayer.onGround) {
//
//				final NetHandlerPlayClient netHandler = mc.getMinecraft().getNetHandler();
//				final EntityPlayerSP player = mc.getMinecraft().thePlayer;
//				final double x = player.posX;
//				final double y = player.posY;
//				final double z = player.posZ;
//				for (int i = 0; i < getMaxFallDist() / 0.05510000046342611 + 0.2; ++i) {
//					netHandler.addToSendQueue(
//							new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.060100000351667404, z, false));
//					netHandler.addToSendQueue(
//							new C03PacketPlayer.C04PacketPlayerPosition(x, y + 5.000000237487257E-4, z, false));
//					netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x,
//							y + 0.004999999888241291 + 6.01000003516674E-8, z, false));
//				}
//				netHandler.addToSendQueue(new C03PacketPlayer(true));
//				break;
//			}
		}
		super.onEnable();
	}

	public void onDisable() {
		mc.gameSettings.keyBindJump.pressed = false;
		// mc.gameSettings.keyBindJump.pressed = false;
		mineplexSpeed = 0;
		verusdmg = false;
		mc.timer.timerSpeed = 1F;
		super.onDisable();
	}

	private int stage = 1;
	TimeUtil time = new TimeUtil();
	double Mineplex;
	double Verus;
	TimeUtil time2 = new TimeUtil();
	public float mineplexMotion, mineplexSpeed;

	@Override
	public void onEvent(Event event) {
		Mineplex = ((Slider) this.getSetting("Minepex-Motion").getSetting()).curValue;
		Verus = ((Slider) this.getSetting("Boost").getSetting()).curValue;
		if (event instanceof EventTick) {
			switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {

			}

			double speed = 0;
			double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
			double xZ = -Math.sin(yaw) * speed;
			double zZ = Math.cos(yaw) * speed;

			switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
			case "Vanilla":
				if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
					if (!isBlockUnder()) {
						mc.thePlayer.motionY = 0F;
						this.setSpeed(3);
					
					}
				}
				if (mc.thePlayer.onGround) {
					mc.thePlayer.motionY = 0.25F;
				//	mc.timer.timerSpeed = 2F;
				} else {
				//	mc.timer.timerSpeed = 2F;
					if (isBlockUnder()) {
					this.setSpeed(2);
					}
				}
				break;
			case "Intave":
				if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
					if (!isBlockUnder()) {
						mc.thePlayer.motionY = 0F;
					}
				}
				if (mc.thePlayer.onGround) {
					mc.thePlayer.jump();
				} else {
					if (mc.thePlayer.fallDistance != 0 && mc.thePlayer.fallDistance > 0.2) {
						mc.timer.timerSpeed = 1.1F;
						//mc.thePlayer.motionY -= 0.0001;

						// mc.thePlayer.setPosition(mc.thePlayer.posX + -Math.sin(yaw) * speed,
						// mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(yaw) * speed);
					} else {
						// mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0001,
						// mc.thePlayer.posZ);
						// mc.thePlayer.speedInAir *= 1.0003F;
						mc.timer.timerSpeed = 1.00F;
					}
				}

				break;
			case "LibreCraft":
				if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
					if (!isBlockUnder()) {
						mc.thePlayer.motionY = 0F;
					}
				}
				if (mc.thePlayer.onGround) {
					// mc.thePlayer.jump();
					mc.thePlayer.motionY = 0.32F;
					mc.thePlayer.onGround = true;
					mc.thePlayer.motionX = -Math.sin(yaw) * 3.0;
					mc.thePlayer.motionZ = Math.cos(yaw) * 3.0;
				} else {
					if (mc.thePlayer.fallDistance != 0) {

						if (mc.thePlayer.moveForward > 0) {
							mc.thePlayer.motionX = -Math.sin(yaw) * 5.0;
							mc.thePlayer.motionZ = Math.cos(yaw) * 5.0;
						}
					}
				}

				break;

			case "AntiAC":
				if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
					if (!isBlockUnder()) {
						mc.thePlayer.motionY = 0F;
					}
				}
				if (event instanceof EventTick) {
//				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//						mc.thePlayer.posY += 0, mc.thePlayer.posZ, false));
					if (mc.thePlayer.onGround) {
						// mc.thePlayer.motionY = 0.42;
						mc.thePlayer.jump();
					} else {
						// setSpeed(0.3);
						final float Y = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.4, -0.7F);
						mc.thePlayer.motionY = Y;
					}
				}

				break;

			case "OldAAC":
				if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
					if (!isBlockUnder()) {
						mc.thePlayer.motionY = 0F;
					}
				}
				if (mc.thePlayer.onGround) {
					mc.thePlayer.jump();
				}
				break;
			case "AAC 3.3.13":
				boolean boost = (Math.abs(this.mc.thePlayer.rotationYawHead - this.mc.thePlayer.rotationYaw) < 90.0F);
			    if (mc.thePlayer.isMoving()&& this.mc.thePlayer.hurtTime < 5)
			      if (this.mc.thePlayer.onGround) {
			    	  if(!Client.INSTANCE.moduleManager.getModule("Scaffold").isState()) {
			        this.mc.thePlayer.motionY = 0.4D;
			    	  }else {
			    		  this.mc.thePlayer.motionY = 0.4D;
			    	  }
			       // mc.timer.timerSpeed = 1F;
			        float f = Speed.getDirection();
			        if(!Client.INSTANCE.moduleManager.getModule("Scaffold").isState()) {
			        this.mc.thePlayer.motionX -= (MathHelper.sin(f) * 0.2F);
			        this.mc.thePlayer.motionZ += (MathHelper.cos(f) * 0.2F);
			        }
			      } else {
			    	//  mc.timer.timerSpeed = 1F;
			        double currentSpeed = Math.sqrt(
			            this.mc.thePlayer.motionX * this.mc.thePlayer.motionX + this.mc.thePlayer.motionZ * this.mc.thePlayer.motionZ);
			        double speed1 = boost ? 1.0074D : 1.0074D;
			        double direction = Speed.getDirection();
			        this.mc.thePlayer.motionX = -Math.sin(direction) * speed1 * currentSpeed;
			        this.mc.thePlayer.motionZ = Math.cos(direction) * speed1 * currentSpeed;
			      }  
//				if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
//					if (!isBlockUnder()) {
//						mc.thePlayer.motionY = 0F;
//					}
//				}
//				setSpeed(0.2785);
//				if (mc.thePlayer.onGround) {
//					mc.thePlayer.motionY = 0.42F;
//				}

				break;
			case "BlocksMC":

				// mc.thePlayer.onGround = true;
				if (mc.thePlayer.hurtTime > 0) {
					verusdmg = true;
				}

				if (!verusdmg) {
					mc.timer.timerSpeed = 1F;
					setSpeed(-0.12);
					mc.gameSettings.keyBindForward.pressed = false;
					mc.gameSettings.keyBindBack.pressed = false;
					mc.gameSettings.keyBindLeft.pressed = false;
					mc.gameSettings.keyBindRight.pressed = false;
				} else {
					mc.gameSettings.keyBindForward.pressed = true;
				}
				// if (mc.thePlayer.fallDistance > 0F) {
				if (verusdmg) {
					if (mc.thePlayer.onGround) {
						// if (mc.thePlayer.onGround && mc.thePlayer.hurtTime > 0) {
						// mc.thePlayer.motionY = 0.92F;
						mc.thePlayer.motionY = 0.42F;
						mc.timer.timerSpeed = 0.1F;
					}else {
					
						mc.timer.timerSpeed = 0.8F;
					}
					mc.thePlayer.onGround = true;
					setSpeed(2);

//						double speed1 = 0.8D;
//						double xm = -Math.sin(yaw) * speed1;
//						double zm = Math.cos(yaw) * speed1;

					

				}
				break;
				
				
				
				
			case "Verus":
				if (((CheckBox) this.getSetting("Hover").getSetting()).state && !Client.INSTANCE.moduleManager.getModule("Scaffold").isState()) {
					if (!isBlockUnder() && mc.thePlayer.fallDistance > 1.7) {
						mc.thePlayer.motionY = 0F;
						mc.thePlayer.onGround = true;
					}
				}

				//setSpeed(0.392);
				
				if (((CheckBox) this.getSetting("DamageBoost").getSetting()).state && mc.thePlayer.hurtTime != 0 && mc.thePlayer.fallDistance< 3) {
					
					setSpeed(Verus);
				}else {
					setSpeed(0.292);
				}
				
			
				
				
				if (mc.thePlayer.onGround) {
					
					mc.thePlayer.jump();
					//mc.thePlayer.motionY = 0.44F;
				} else {
					mc.thePlayer.jumpMovementFactor = 0.10F;

					// mc.thePlayer.speedInAir = 14F;
					// mc.thePlayer.motionY = -0.5F;
				}
				break;
//			case "VerusFloat":
//				
//			
//			//	mc.gameSettings.keyBindJump.pressed = true;
//				if(mc.thePlayer.fallDistance > 0.3) {
//					mc.thePlayer.onGround = true;
//					
//				}
//				
			
				
//				if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
//					if (!isBlockUnder()) {
//						mc.thePlayer.motionY = 0F;
//					}
//				}
//				
//				
//				if(!mc.thePlayer.onGround) {
//					mc.thePlayer.motionY = -1F;
//				}
//				
//				if(time2.hasReached(50)) {
//					mc.thePlayer.motionY = 0F;
//					time2.reset();
//				}else {
//					mc.thePlayer.motionY = -1F;
//				}
//				
//				setSpeed(0.386);
//
//				if (mc.thePlayer.onGround) {
//					mc.thePlayer.motionY = 0.42F;
//					mc.thePlayer.jumpMovementFactor = 0.07F;
//				}
//				mc.thePlayer.onGround = true;
//				
				
				
//					if (!mc.thePlayer.onGround) {
//						if (time2.hasReached(10)) {
//							mc.thePlayer.motionY = 0F;
//							mc.thePlayer.onGround = true;
//							time2.reset();
//						
//						
//						}
					
			//	}

				// mc.thePlayer.speedInAir = 14F;
				// mc.thePlayer.motionY = -0.5F;

		//		break;

			case "AAC":
				if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
					if (!isBlockUnder()) {
						mc.thePlayer.motionY = 0F;
					}
				}
				if (mc.thePlayer.onGround) {
					mc.thePlayer.jump();
				} else {
					if (mc.thePlayer.fallDistance != 0 && mc.thePlayer.fallDistance > 0.2) {
						mc.timer.timerSpeed = 1.08F;
						mc.thePlayer.motionY -= 0.001;

						// mc.thePlayer.setPosition(mc.thePlayer.posX + -Math.sin(yaw) * speed,
						// mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(yaw) * speed);
					} else {
						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.001, mc.thePlayer.posZ);
						mc.timer.timerSpeed = 1.00F;
					}
				}
				break;

			}

		}
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
		case "VerusGround":
			if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
				if (!isBlockUnder()) {
					mc.thePlayer.motionY = 0F;
				}
			}
			// mc.thePlayer.onGround = true;

			if (event instanceof PlayerMoveEvent) {
				mc.timer.timerSpeed = 1;
				// setSpeed1(PlayerMoveEvent.INSTANCE, 0.2875);
				setSpeed1(PlayerMoveEvent.INSTANCE, 5.5875);

				if (mc.thePlayer.onGround) {
					if (!mc.thePlayer.isCollidedHorizontally) {
						PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY = -0.42);
					} else {
						PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY = 0.44);
					}
					// if (mc.gameSettings.keyBindJump.pressed) {

					// } else {
					// PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY = 0.42);
					// }
				}
			}
			break;
		
		case "NCP-Bhob":
			if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
				if (!isBlockUnder()) {
					mc.thePlayer.motionY = 0F;
				}
			}
			if (Client.INSTANCE.moduleManager.getModule("Longjump").isState()) {
				if (event instanceof EventTick) {

					if (mc.thePlayer.onGround) {

						final NetHandlerPlayClient netHandler = mc.getMinecraft().getNetHandler();
						final EntityPlayerSP player = mc.getMinecraft().thePlayer;
						final double x = player.posX;
						final double y = player.posY;
						final double z = player.posZ;
						for (int i = 0; i < getMaxFallDist() / 0.05510000046342611 + 0.2; ++i) {
							netHandler.addToSendQueue(
									new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.060100000351667404, z, false));
							netHandler.addToSendQueue(
									new C03PacketPlayer.C04PacketPlayerPosition(x, y + 5.000000237487257E-4, z, false));
							netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x,
									y + 0.004999999888241291 + 6.01000003516674E-8, z, false));
						}
						netHandler.addToSendQueue(new C03PacketPlayer(true));

					}
					boolean boost = (Math.abs(mc.thePlayer.rotationYawHead - mc.thePlayer.rotationYaw) < 90.0F);
					if ((mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindLeft.pressed
							|| mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindBack.pressed)
							&& mc.thePlayer.onGround) {
						mc.thePlayer.motionY = 0.52F;
						mc.timer.timerSpeed = (float) 0.1;
						mc.thePlayer.setSprinting(true);
					} else {
						mc.thePlayer.setSprinting(true);
						mc.timer.timerSpeed = (float) 1;
						if (mc.thePlayer.hurtTime != 0) {
							if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Longjump").getSetting("Boost")
									.getSetting()).state) {
								mc.timer.timerSpeed = (float) 4;
							} else {
								mc.timer.timerSpeed = (float) 1;
							}
							mc.thePlayer.motionY += 0.06F;
							setSpeed5(0.4);

						}
					}
					if (mc.thePlayer.onGround)
						Longjump.disable += 1;

					if (Longjump.disable > 1) {
						Client.INSTANCE.moduleManager.getModule("Longjump").setState(false);
					}
				}
			} else {
				if (mc.thePlayer.fallDistance < 2) {
					if (event instanceof EventTick) {
						mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
								mc.thePlayer.posY, mc.thePlayer.posZ, false));
						// mc.timer.timerSpeed = 1.1F;

				

						// setSpeedTest(0.3);

					}
					if (!mc.thePlayer.onGround) {
						mc.timer.timerSpeed = 1F;
					}
					if (mc.thePlayer.onGround) {
						mc.timer.timerSpeed = 1F;

						PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY = 0.42F);
					}

					if (event instanceof PlayerMoveEvent) {
					
						setSpeed(0.2895);
					}
				}
			}
			break;
		case "HypixelLowHob":
			if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
				if (!isBlockUnder()) {
					mc.thePlayer.motionY = 0F;
				}
			}
			if (mc.thePlayer.onGround) {
				// mc.gameSettings.keyBindJump.pressed = true;
			} else {
				// mc.gameSettings.keyBindJump.pressed = false;

				setSpeed1(PlayerMoveEvent.INSTANCE, 0.3);

				break;
			}
		case "Mineplex2":
			if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
				if (!isBlockUnder()) {
					mc.thePlayer.motionY = 0F;
				}
			}
			if (!mc.thePlayer.isInWeb) {
				if (mc.thePlayer.isCollidedHorizontally) {
					// setMotion(0);
					mineplexMotion = 0.02F;
				}
				// if (isMoving()) {
				if (mc.thePlayer.onGround) {
					mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
					mineplexMotion += 0.45F;
					mc.thePlayer.motionY = 0.34;
				} else {
					mineplexMotion -= mineplexMotion / 60;
					// setSpeed3(mineplexMotion);
				}
//                 } else {
//                     mineplexMotion = 0.02F;
//                 }
			}
			break;
		case "TP":
			
			
		
			
			if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
				if (!isBlockUnder()) {
					mc.thePlayer.motionY = 0F;
					setSpeed(getSpeed());
				}
			}
			//double speed = 0.08;
			 setSpeed(0.85);
			  double x = mc.thePlayer.posX;
              double y = mc.thePlayer.posY;
              double z = mc.thePlayer.posZ;

              //Timer.timerSpeed = 0.3F;
           
              
			if (mc.thePlayer.onGround) {
			//	mc.gameSettings.keyBindJump.pressed = true;
				mc.thePlayer.motionY = 0.50F;
			//}
				// mc.timer.timerSpeed = 0.2F;
//				final float SPEED = (float) MathHelper.getRandomDoubleInRange(new Random(), 4, 4.1);
//				setSpeed(0.4);
//				double x = mc.thePlayer.posX;
//				double y = mc.thePlayer.posY;
//				double z = mc.thePlayer.posZ;
//				final float YY = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.0001, 0.00001);
//				double Y = mc.thePlayer.motionY = YY;
//				if (mc.thePlayer.isMoving()) {
//					mc.thePlayer.setPosition(x, y + Y, z);
				//}
//					mc.thePlayer.setPosition(x, y + -Y, z);
			} else {
				// mc.timer.timerSpeed = 0.4F;

				// mc.thePlayer.motionY = -0.1F;
			}

			break;
		case "Watchdog":
			if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
				if (!isBlockUnder()) {
					mc.thePlayer.motionY = 0F;
				}
			}
			if (Client.INSTANCE.moduleManager.getModule("TP").isState()) {
				if (event instanceof EventReceivedPacket) {
					Packet p = EventReceivedPacket.INSTANCE.getPacket();
					if (p instanceof S08PacketPlayerPosLook) {
						ChatUtil.sendChatInfo("its FakeDamage");
					}
				}
				if (event instanceof EventTick) {
					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
							mc.thePlayer.posY, mc.thePlayer.posZ, false));
					if (mc.thePlayer.onGround) {

						final NetHandlerPlayClient netHandler = mc.getMinecraft().getNetHandler();
						final EntityPlayerSP player = mc.getMinecraft().thePlayer;
						final double x1 = player.posX;
						final double y1 = player.posY;
						final double z1 = player.posZ;
						for (int i = 0; i < getMaxFallDist() / 0.05510000046342611 + 0.2; ++i) {
							netHandler.addToSendQueue(
									new C03PacketPlayer.C04PacketPlayerPosition(x1, y1 + 0.060100000351667404, z1, false));
							netHandler.addToSendQueue(
									new C03PacketPlayer.C04PacketPlayerPosition(x1, y1 + 5.000000237487257E-4, z1, false));
							netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x1,
									y1 + 0.004999999888241291 + 6.01000003516674E-8, z1, false));
						}
						netHandler.addToSendQueue(new C03PacketPlayer(true));

					}

					// mc.timer.timerSpeed = 1.1F;

					// setSpeedTest(0.3);

					mc.thePlayer.jumpMovementFactor = 0.025F;
			
					boolean boost = (Math.abs(mc.thePlayer.rotationYawHead - mc.thePlayer.rotationYaw) < 90.0F);
					if ((mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindLeft.pressed
							|| mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindBack.pressed)
							&& mc.thePlayer.onGround) {
						mc.thePlayer.motionY = 0.42F;
						mc.timer.timerSpeed = (float) 0.1;
						mc.thePlayer.setSprinting(true);
					} else {
						mc.thePlayer.setSprinting(true);
						mc.timer.timerSpeed = (float) 1;
						if (mc.thePlayer.hurtTime != 0) {
							if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Longjump").getSetting("Boost")
									.getSetting()).state) {
								mc.timer.timerSpeed = (float) 4;
							} else {
								mc.timer.timerSpeed = (float) 1;
							}
							mc.thePlayer.motionY += 0.01F;

//							if (mc.thePlayer.fallDistance > 0) {
//								mc.thePlayer.onGround = true;
//								mc.gameSettings.keyBindJump.pressed = true;
//							}

							if (mc.thePlayer.fallDistance > 0.1) {
								mc.thePlayer.motionY = 0.42F;
							} else {
								// mc.thePlayer.motionY = 0F;
							}
							if (mc.thePlayer.fallDistance > 0) {
								mc.thePlayer.motionY = 0.12F;
							} else {
								// mc.thePlayer.motionY = 0F;
							}

							setSpeed5(0.45);

						}
					}
					if (mc.thePlayer.onGround)
						Longjump.disable += 1;

					if (Longjump.disable > 1) {
						Client.INSTANCE.moduleManager.getModule("Longjump").setState(false);
					}
				}
			} else {
			
				if (event instanceof EventTick) {
					mc.thePlayer.jumpMovementFactor = 0.027F;
					if(mc.thePlayer.ticksExisted % 3 == 0) {
					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
							mc.thePlayer.posY, mc.thePlayer.posZ, false));
					}
				}
			

				if (mc.thePlayer.onGround) {
					mc.gameSettings.keyBindJump.pressed = true;
					// PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY = 0.42F);
					// mc.thePlayer.motionY = 0.42F;
				}

//				doStrafeAtSpeed(PlayerMoveEvent.INSTANCE, Speed.getSpeed());
//						PlayerMoveEvent.INSTANCE.setX(0.2875);
//						PlayerMoveEvent.INSTANCE.setZ(0.2875);
				if (event instanceof PlayerMoveEvent) {
					
					setSpeed(getSpeed());
					if (((CheckBox) this.getSetting("Timer").getSetting()).state) {
					mc.timer.timerSpeed= 1.2F;
					}
					// setSpeed(0.2875);

				} else {
				
				}
			}
			// this.setSpeed1(0.2875, PlayerMoveEvent);
			// if (mc.thePlayer.fallDistance < 0) {

//						if ((mc.thePlayer.fallDistance < 0.9F)) {

			// setSpeed(0.2875);
//						}
			// } else {

			// }

			// }
			// }
			break;
		case "Mineplex":
			if (((CheckBox) this.getSetting("Hover").getSetting()).state) {
				if (!isBlockUnder()) {
					mc.thePlayer.motionY = 0F;
				}
			}
			if (event instanceof PlayerMoveEvent) {
				final float tmm = (float) MathHelper.getRandomDoubleInRange(new Random(), 93, 95);

				float Y = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.0, -0.0);
				float Y2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.015, 0.016);
				float slowdown1 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.00, 0.000);
				float Y3 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.45, 0.46);
				mc.thePlayer.setSprinting(true);

//				mc.gameSettings.keyBindForward.pressed = turn;
//				mc.gameSettings.keyBindBack.pressed = !turn;

//				if (!mc.gameSettings.keyBindForward.pressed) {
//					mineplexMotion = 0.02F;
//				}
				if ((this.mc.gameSettings.keyBindForward.pressed || this.mc.gameSettings.keyBindLeft.pressed
						|| this.mc.gameSettings.keyBindRight.pressed || this.mc.gameSettings.keyBindBack.pressed)) {
					double speed11 = 0;
					mc.timer.timerSpeed = 1f;
					stage++;
					if (mc.thePlayer.isCollidedHorizontally) {
						stage = 50;
						mineplexMotion = 0.02F;
					}
					if (mc.thePlayer.onGround
							&& (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
						mineplexMotion += 0.65F;
						PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY = 0.42F);
						stage = 0;
						speed11 = 0;
					}
					if (!mc.thePlayer.onGround) {
						if (mc.thePlayer.motionY > Y) {
							mc.thePlayer.motionY += Y2;
						} else {
							// mc.thePlayer.motionY += 0.01;
						}
						double slowdown = slowdown1;
						mineplexMotion -= mineplexMotion / Mineplex;
						speed11 = mineplexMotion - (stage * slowdown);
						if (speed11 < 0)
							speed11 = 0;
					}
					setSpeed1(PlayerMoveEvent.INSTANCE, speed11);
				}
				break;
			}
		}

	}

	public static void setSpeed1(PlayerMoveEvent moveEvent, double moveSpeed) {
		setSpeed1(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe,
				mc.thePlayer.movementInput.moveForward);
	}

	public static void setSpeed1(PlayerMoveEvent moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe,
			double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;
		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				yaw += ((forward > 0.0D) ? -45 : 45);
			} else if (strafe < 0.0D) {
				yaw += ((forward > 0.0D) ? 45 : -45);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1.0D;
			} else if (forward < 0.0D) {
				forward = -1.0D;
			}
		}
		if (strafe > 0.0D) {
			strafe = 1.0D;
		} else if (strafe < 0.0D) {
			strafe = -1.0D;
		}
		double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
		double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
		moveEvent.setX((forward * moveSpeed * mx + strafe * moveSpeed * mz));
		moveEvent.setZ((forward * moveSpeed * mz - strafe * moveSpeed * mx));
	}

	public static void setSpeed5(PlayerMoveEvent moveEvent, double moveSpeed) {
		setSpeed1(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe,
				mc.thePlayer.movementInput.moveForward);
	}

	public static void setSpeed5(PlayerMoveEvent moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe,
			double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;
		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				yaw += ((forward > 0.0D) ? -35 : 35);
			} else if (strafe < 0.0D) {
				yaw += ((forward > 0.0D) ? 35 : -35);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1.0D;
			} else if (forward < 0.0D) {
				forward = -1.0D;
			}
		}
		if (strafe > 0.0D) {
			strafe = 1.0D;
		} else if (strafe < 0.0D) {
			strafe = -1.0D;
		}
		double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
		double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
		moveEvent.setX((forward * moveSpeed * mx + strafe * moveSpeed * mz));
		moveEvent.setZ((forward * moveSpeed * mz - strafe * moveSpeed * mx));
	}

	public static void setSpeed6(PlayerMoveEvent moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe,
			double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;
		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				// yaw += ((forward > 0.0D) ? -45 : 45);
			} else if (strafe < 0.0D) {
				// yaw += ((forward > 0.0D) ? 45 : -45);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1.0D;
			} else if (forward < 0.0D) {
				forward = -1.0D;
			}
		}
		if (strafe > 0.0D) {
			strafe = 1.0D;
		} else if (strafe < 0.0D) {
			strafe = -1.0D;
		}
		double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
		double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
		moveEvent.setX((forward * moveSpeed * mx + strafe * moveSpeed * mz));
		moveEvent.setZ((forward * moveSpeed * mz - strafe * moveSpeed * mx));
	}

	public static void setSpeedTest(double speed) {
		EntityPlayerSP player = mc.getMinecraft().thePlayer;
		double yaw = (double) player.rotationYaw;
		boolean isMoving = player.moveForward != 0.0F || player.moveStrafing != 0.0F;
		boolean isMovingForward = player.moveForward > 0.0F;
		boolean isMovingBackward = player.moveForward < 0.0F;
		boolean isMovingRight = player.moveStrafing > 0.0F;
		boolean isMovingLeft = player.moveStrafing < 0.0F;
		boolean isMovingSideways = isMovingLeft || isMovingRight;
		boolean isMovingStraight = isMovingForward || isMovingBackward;
		if (isMoving) {
			if (isMovingForward && !isMovingSideways) {
				yaw += 0.0D;
			} else if (isMovingBackward && !isMovingSideways) {
				yaw += 180.0D;
			} else if (isMovingForward && isMovingLeft) {
				yaw += 45.0D;
			} else if (isMovingForward) {
				yaw -= 45.0D;
			} else if (!isMovingStraight && isMovingLeft) {
				yaw += 90.0D;
			} else if (!isMovingStraight && isMovingRight) {
				yaw -= 90.0D;
			} else if (isMovingBackward && isMovingLeft) {
				yaw += 135.0D;
			} else if (isMovingBackward) {
				yaw -= 135.0D;
			}
			yaw = Math.toRadians(yaw);
			player.motionX = -Math.sin(yaw) * speed;
			player.motionZ = Math.cos(yaw) * speed;
		}
	}

	public static void setSpeed(double speed) {
		EntityPlayerSP player = mc.getMinecraft().thePlayer;
		double yaw = (double) player.rotationYaw;
		boolean isMoving = player.moveForward != 0.0F || player.moveStrafing != 0.0F;
		boolean isMovingForward = player.moveForward > 0.0F;
		boolean isMovingBackward = player.moveForward < 0.0F;
		boolean isMovingRight = player.moveStrafing > 0.0F;
		boolean isMovingLeft = player.moveStrafing < 0.0F;
		boolean isMovingSideways = isMovingLeft || isMovingRight;
		boolean isMovingStraight = isMovingForward || isMovingBackward;
		if (isMoving) {
			if (isMovingForward && !isMovingSideways) {
				yaw += 0.0D;
			} else if (isMovingBackward && !isMovingSideways) {
				yaw += 180.0D;
			} else if (isMovingForward && isMovingLeft) {
				yaw += 45.0D;
			} else if (isMovingForward) {
				yaw -= 45.0D;
			} else if (!isMovingStraight && isMovingLeft) {
				yaw += 90.0D;
			} else if (!isMovingStraight && isMovingRight) {
				yaw -= 90.0D;
			} else if (isMovingBackward && isMovingLeft) {
				yaw += 135.0D;
			} else if (isMovingBackward) {
				yaw -= 135.0D;
			}

			yaw = Math.toRadians(yaw);
			player.motionX = -Math.sin(yaw) * speed;
			player.motionZ = Math.cos(yaw) * speed;
		}

	}

	public static void setSpeed5(double speed) {
		EntityPlayerSP player = mc.getMinecraft().thePlayer;
		double yaw = (double) player.rotationYaw;
		boolean isMoving = player.moveForward != 0.0F || player.moveStrafing != 0.0F;
		boolean isMovingForward = player.moveForward > 0.0F;
		boolean isMovingBackward = player.moveForward < 0.0F;
		boolean isMovingRight = player.moveStrafing > 0.0F;
		boolean isMovingLeft = player.moveStrafing < 0.0F;
		boolean isMovingSideways = isMovingLeft || isMovingRight;
		boolean isMovingStraight = isMovingForward || isMovingBackward;
		if (isMoving) {
			if (isMovingForward && !isMovingSideways) {
				yaw += 0.0D;
			} else if (isMovingBackward && !isMovingSideways) {
				// yaw += 180.0D;
			} else if (isMovingForward && isMovingLeft) {
				// yaw += 45.0D;
			} else if (isMovingForward) {
				yaw -= 45.0D;
			} else if (!isMovingStraight && isMovingLeft) {
				// yaw += 90.0D;
			} else if (!isMovingStraight && isMovingRight) {
				// yaw -= 90.0D;
			} else if (isMovingBackward && isMovingLeft) {
				// yaw += 135.0D;
			} else if (isMovingBackward) {
				// yaw -= 135.0D;
			}
			yaw = Math.toRadians(yaw);
			player.motionX = -Math.sin(yaw) * speed;
			player.motionZ = Math.cos(yaw) * speed;
		}
	}

	public static float getDirection() {

		float var1 = mc.thePlayer.rotationYaw;

		if (mc.thePlayer.moveForward < 0.0F) {
			var1 += 180.0F;
		}

		float forward = 1.0F;

		if (mc.thePlayer.moveForward < 0.0F) {
			forward = -0.5F;
		} else if (mc.thePlayer.moveForward > 0.0F) {
			forward = 0.5F;
		}

		if (mc.thePlayer.moveStrafing > 0.0F) {
			var1 -= 90.0F * forward;
		}

		if (mc.thePlayer.moveStrafing < 0.0F) {
			var1 += 90.0F * forward;
		}

		var1 *= 0.017453292F;
		return var1;
	}

	public static float getDirection2() {

		float var1 = mc.thePlayer.rotationYaw;

		if (mc.thePlayer.moveForward < 0.0F) {
			var1 += 180.0F;
		}

		float forward = 1.0F;

		if (mc.thePlayer.moveForward < 0.0F) {
			// forward = -0.5F;
		} else if (mc.thePlayer.moveForward > 0.0F) {
			// forward = 0.5F;
		}

		if (mc.thePlayer.moveStrafing > 0.0F) {
			// var1 -= 90.0F * forward;
		}

		if (mc.thePlayer.moveStrafing < 0.0F) {
			// var1 += 90.0F * forward;
		}

		var1 *= 0.017453292F;
		return var1;
	}

	public static float getMaxFallDist() {
		final PotionEffect potioneffect = mc.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump);
		final int f = (potioneffect != null) ? (potioneffect.getAmplifier() + 1) : 0;
		return (float) (mc.getMinecraft().thePlayer.getMaxFallHeight() + f);
	}

	public static void setStrafeSpeed(double strafeSpeed) {
		mc.thePlayer.motionX = -(Math.sin(getFaceDirection()) * strafeSpeed);
		mc.thePlayer.motionZ = Math.cos(getFaceDirection()) * strafeSpeed;
	}

	public static float getFaceDirection() {
		float var1 = mc.thePlayer.rotationYaw;
		if (mc.thePlayer.moveForward < 0.0F) {
			var1 += 180.0F;
		}

		float forward = 1.0F;
		if (mc.thePlayer.moveForward < 0.0F) {
			forward = -0.5F;
		} else if (mc.thePlayer.moveForward > 0.0F) {
			forward = 0.5F;
		}

		if (mc.thePlayer.moveStrafing > 0.0F) {
			var1 -= 90.0F * forward;
		}

		if (mc.thePlayer.moveStrafing < 0.0F) {
			var1 += 90.0F * forward;
		}

		var1 *= 0.017453292F;
		return var1;
	}

	public boolean isBlockUnder() {
		for (int i = (int) mc.thePlayer.posY; i >= 0; --i) {
			BlockPos position = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);

			if (!(mc.theWorld.getBlockState(position).getBlock() instanceof BlockAir)) {
				return true;
			}
		}

		return false;
	}

	public boolean doStrafeAtSpeed(PlayerMoveEvent event, double moveSpeed) {
		boolean strafe = canStrafe();
		if (Killaura.kTarget != null) {
			if (strafe) {
				Speed.setSpeed1(event, moveSpeed);

			}
		}

		return strafe;
	}

	public boolean canStrafe() {
		return Client.INSTANCE.moduleManager.getModule("Killaura").isState();
	}
}
