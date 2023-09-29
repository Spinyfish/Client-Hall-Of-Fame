package de.fanta.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.awt.Color;
import java.util.Random;

import de.fanta.Client;
import de.fanta.command.impl.Spec;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventNoClip;
import de.fanta.events.listeners.EventPacket;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.PlayerUtil;
import de.fanta.utils.TimeUtil;

public class Flight extends Module {
	TimeUtil time = new TimeUtil();
	double speed = 0.0D;
	boolean cubedmg = false;
	boolean verusdmg = false;
	boolean high = false;

	private long startTime;
	public boolean turn;
	public static boolean Start = false;

	public Flight() {
		super("Flight", 0, Type.Movement, Color.blue);
		this.settings.add(new Setting("Bobbing", new CheckBox(false)));
		this.settings.add(new Setting("MineplexFast", new CheckBox(false)));
		this.settings.add(new Setting("Modes",
				new DropdownBox("Vanilla",
						new String[] { "Motion", "Vanilla", "Hypixel", "Redesky", "Cubecraft", "MCCentral",
								"VanillaBypass", "LibreCraft", "MushBW", "Mush", "Verus", "Rededark", "Rededark2",
								"Karhu", "OldNCP", "LuckyNetwork", "BlocksMC", "VerusFloat", "WatchDug", "VerusColide",
								"Funcraft", "VerusJump" })));
		this.settings.add(new Setting("MotionSpeed", new Slider(1.2, 9, 0.1, 3)));
		this.settings.add(new Setting("VerusSpeed", new Slider(0.1, 3, 0.1, 3)));
		this.settings.add(new Setting("LuckySpeed", new Slider(0.1, 9, 0.1, 3)));
		this.settings.add(new Setting("RededarkSpeed", new Slider(1.2, 8, 0.1, 3)));
		this.settings.add(new Setting("KaroSpeed", new Slider(1, 8, 0.1, 3)));
		this.settings.add(new Setting("BlocksMCSpeed", new Slider(3, 9, 0.1, 3)));
		this.settings.add(new Setting("BlocksMCTimer", new Slider(1, 10, 0.1, 1)));

	}

	public static double motion;
	public static double MineplexSpeed;
	public static double RededarkSpeed;
	public static double KaroSpeed;
	public static double BlocksMC;
	public static double BlocksMCTimer;

	@Override
	public void onDisable() {
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {

		case "VerusFloat":
			if (mc.thePlayer.onGround) {
				mc.thePlayer.motionY = 0.42F;
			}
			// mc.timer.timerSpeed = 0.1F;
			break;
		}
		mc.gameSettings.keyBindSprint.pressed = true;
		mc.thePlayer.setSprinting(true);
		PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY = 0);
		boolean high = false;
		cubedmg = false;
		verusdmg = false;
		Start = false;
		mc.timer.timerSpeed = 1F;
		// setSpeed(0.2875);
		mc.thePlayer.capabilities.allowFlying = false;
		mc.thePlayer.capabilities.isFlying = false;
		mc.thePlayer.onGround = false;

		super.onDisable();
		setSpeed(0);
		PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY = 0);
	}

	public void onEnable() {
//		if(mc.thePlayer.onGround) {
//			mc.thePlayer.motionY = 0.42F;
//		}
		setSpeed(0);
		mc.thePlayer.onGround = false;
		startTime = System.currentTimeMillis();
		Start = false;
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
		case "Cubecraft":
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			// mc.thePlayer.jump();
			break;
		case "Verus":
			// mc.gameSettings.keyBindAttack.pressed = true;
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			// mc.timer.timerSpeed = 0.1F;
			break;

		case "VerusJump":

			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			// mc.timer.timerSpeed = 0.1F;
			break;

		case "VerusFloat":

			// mc.gameSettings.keyBindAttack.pressed = true;
//			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
//			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//					mc.thePlayer.posY, mc.thePlayer.posZ, false));
//			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			// setSpeed(9);
			// mc.timer.timerSpeed = 0.1F;
			break;
		case "OldNCP":

			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			// mc.timer.timerSpeed = 0.1F;
			break;
		case "Karhu":

			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			mc.thePlayer.motionY = -1F;
			// mc.timer.timerSpeed = 0.1F;
			break;
		case "LuckyNetwork":

			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			// mc.timer.timerSpeed = 0.1F;
			break;
		case "BlocksMC":

			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			// mc.timer.timerSpeed = 0.1F;
			break;

		case "MushBW":
			if (!Start) {
				setSpeed(0);
				mc.thePlayer.motionX = 0F;
				mc.thePlayer.motionZ = 0F;
			}
			break;

		}
		if (mc.thePlayer.onGround) {
			// mc.thePlayer.jump();
		}

		super.onEnable();
	}

	@Override
	public void onEvent(Event event) {
		BlocksMC = ((Slider) this.getSetting("BlocksMCSpeed").getSetting()).curValue;
		BlocksMCTimer = ((Slider) this.getSetting("BlocksMCTimer").getSetting()).curValue;
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {

		case "Motion":
			motion = ((Slider) this.getSetting("MotionSpeed").getSetting()).curValue;
			setSpeed(Speed.getSpeed());
			mc.thePlayer.motionY = 0;
			if (mc.gameSettings.keyBindJump.pressed) {
				// mc.gameSettings.keyBindJump.pressed = true;
				mc.thePlayer.motionY = 1;
				//setSpeed(0);
			} else {
				if (mc.gameSettings.keyBindSneak.pressed) {
					mc.thePlayer.motionY = -1;
				}
			}
			mc.thePlayer.onGround = true;
			break;

		case "Mush":
			   if (mc.gameSettings.keyBindJump.isPressed() || (mc.thePlayer.motionY < - 0.4)) {
                   if (!mc.thePlayer.onGround) {
                     //  mc.thePlayer.jump();
                	   if(mc.thePlayer.ticksExisted % 2 == 1) {
                	   mc.thePlayer.motionY = -1F;
                	   }else {
                		   mc.thePlayer.motionY = -0.05F;
                	   }
                	   if(event instanceof EventTick) {
                	   mc.thePlayer.onGround = true;
                	   }
                      // mc.thePlayer.sendQueue.addToSendQueueDirect(new C03PacketPlayer( true));
                      // counter ++;
                   }
               }

			break;

		case "Cubecraft":
			if (event instanceof EventPacket) {
				if (mc.thePlayer.ticksExisted % 5 == 0 && !mc.gameSettings.keyBindJump.pressed) {
					event.setCancelled(true);
				} else {
					if (mc.gameSettings.keyBindJump.pressed && mc.thePlayer.ticksExisted % 2 == 0) {
						event.setCancelled(true);

					}
				}
			}
			if (event instanceof PlayerMoveEvent) {
				mc.gameSettings.keyBindSprint.pressed = false;
				double x = mc.thePlayer.posX;
				double y = mc.thePlayer.posY;
				double z = mc.thePlayer.posZ;
				// if (mc.thePlayer.ticksExisted % 3 == 1) {
				// mc.timer.timerSpeed = 1F;
				// }else {
				// mc.timer.timerSpeed = 0.05F;
				// }
				// mc.thePlayer.motionY = 0f;
				((PlayerMoveEvent) event).setY(0.0D);
				if (mc.gameSettings.keyBindJump.pressed) {
					((PlayerMoveEvent) event).setY(4);
					mc.timer.timerSpeed = 0.1F;
					// ((PlayerMoveEvent) event).setZ(-0.1);
					// ((PlayerMoveEvent) event).setX(-0.1);
				}
				if (mc.gameSettings.keyBindForward.pressed) {
					if (mc.thePlayer.ticksExisted % 1 == 0) {
						mc.timer.timerSpeed = 0.1F;
						double yaw1 = Math.toRadians(mc.thePlayer.rotationYaw);
						double speed1 = 14;
						double xm = -Math.sin(yaw1) * speed1;
						double zm = Math.cos(yaw1) * speed1;
						((PlayerMoveEvent) event).setX(xm);
						if (mc.gameSettings.keyBindJump.pressed) {

						} else
							((PlayerMoveEvent) event).setY(0);
						((PlayerMoveEvent) event).setZ(zm);
					} else {
						((PlayerMoveEvent) event).setY(-0.0D);
						((PlayerMoveEvent) event).setX(0);
						((PlayerMoveEvent) event).setZ(0);
					}

				}
			}

			break;

		case "VerusJump":
			// if(mc.thePlayer.hurtTime != 0) {

			// }else {
			// mc.timer.timerSpeed = 1F;
			// }

			if (mc.thePlayer.hurtTime > 0) {
				verusdmg = true;
			}

			if (!verusdmg) {
				// mc.timer.timerSpeed = 1F;
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

				if(mc.thePlayer.hurtTime !=0 ) {
					mc.timer.timerSpeed = (float) BlocksMCTimer;
				}
					
						// if (!mc.thePlayer.onGround) {
						mc.thePlayer.motionY *= 0F;
						// mc.timer.timerSpeed = 0.4F;
						mc.thePlayer.onGround = true;
						// mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
						// }else {
						// mc.timer.timerSpeed = 1F;
						// }
						
						if (mc.thePlayer.isMoving()) {
							setSpeed(BlocksMC);
						} else {
							setSpeed(0);
						}
					
		
			}

			break;

		case "WatchDug":

			mc.gameSettings.keyBindJump.pressed = false;

			if (mc.thePlayer.onGround) {

				mc.thePlayer.motionY = 0.42F;
			} else {

				final float Y = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.06, -0.15F);
				if (mc.thePlayer.ticksExisted % 2 == 1) {
					if (mc.gameSettings.keyBindSneak.pressed) {
						mc.thePlayer.motionY = -0.5F;
					} else {
						mc.thePlayer.motionY = -Y;
					}
					setSpeed(0.4);
				} else {
					if (mc.gameSettings.keyBindJump.pressed) {
						mc.thePlayer.motionY = 0.1F;
						mc.thePlayer.onGround = true;
					} else {
						mc.thePlayer.motionY = Y;
						mc.thePlayer.onGround = false;

					}
				}
			}

			break;
		case "VerusColide":
			// if(mc.thePlayer.ticksExisted % 6 ==0) {

//			if (mc.gameSettings.keyBindJump.pressed) {
//				mc.thePlayer.motionY = 2F;
//			} else {
//				mc.thePlayer.motionY = 0F;
//			}

			// }
//			mc.thePlayer.motionY = 0F;

//			if(mc.thePlayer.hurtTime !=0) {
//				verusdmg = true;
//			}
//			if (!verusdmg) {
//				mc.timer.timerSpeed = 1F;
//				setSpeed(-0.12);
//				mc.gameSettings.keyBindForward.pressed = false;
//				mc.gameSettings.keyBindBack.pressed = false;
//				mc.gameSettings.keyBindLeft.pressed = false;
//				mc.gameSettings.keyBindRight.pressed = false;
//			} else {
			mc.gameSettings.keyBindForward.pressed = true;
//			}
//			// if (mc.thePlayer.fallDistance > 0F) {
//			if (verusdmg) {

			if (mc.thePlayer.hurtTime != 0) {
				verusdmg = true;
			}

			if (!verusdmg) {
				setSpeed(getSpeed());
			}

			if (mc.gameSettings.keyBindJump.pressed) {
				mc.thePlayer.motionY = 1F;
				mc.timer.timerSpeed = 0.7F;

			} else {
				mc.timer.timerSpeed = 1F;
				if (mc.gameSettings.keyBindSneak.pressed) {
					mc.thePlayer.motionY = -1F;
					mc.timer.timerSpeed = 0.7F;
				} else {
					mc.thePlayer.motionY = 0F;
					mc.timer.timerSpeed = 1F;
				}
			}

//
//			
//			if(mc.gameSettings.keyBindSneak.pressed) {
//				mc.thePlayer.motionY = -0.42F;
//			
//			}
//			
//			 if(!mc.gameSettings.keyBindJump.pressed || !mc.gameSettings.keyBindSneak.pressed) {
//				 mc.thePlayer.motionY = 0F;
//			 }

			if (event instanceof EventNoClip) {
				// mc.thePlayer.onGround = true;
				((EventNoClip) event).noClip = true;

				setSpeed(3.2);

				mc.thePlayer.onGround = true;

				// if (verusdmg) {
				// mc.thePlayer.onGround = true;

				// }

//						if(mc.thePlayer.ticksExisted % 2 == 0) {
//							mc.thePlayer.motionX = 0F;
//							mc.thePlayer.motionZ = 0F;
//						}

			}
//			if (event instanceof PlayerMoveEvent) {
//
//				if (mc.thePlayer.hurtTime > 0) {
//					Speed.setSpeed1(PlayerMoveEvent.INSTANCE, 3.2);
//				} else {
//					Speed.setSpeed1(PlayerMoveEvent.INSTANCE, 3);
//				}
//
//				mc.thePlayer.onGround = true;
//
//				// if (verusdmg) {
//				// mc.thePlayer.onGround = true;
//
//				// }
//
////					if(mc.thePlayer.ticksExisted % 2 == 0) {
////						mc.thePlayer.motionX = 0F;
////						mc.thePlayer.motionZ = 0F;
////					}
//
//			}

			// setSpeed(2);
			// mc.timer.timerSpeed = 1F;

			// setSpeed(getSpeed());
			// mc.timer.timerSpeed = 0.1F;

			// }
			// mc.timer.timerSpeed = 0.3F;
			break;
		case "VerusFloat":

			if (event instanceof EventTick) {
				mc.thePlayer.onGround = true;
				if (mc.gameSettings.keyBindJump.pressed) {
					mc.thePlayer.motionY = 1F;
				} else {
					mc.thePlayer.motionY = 0F;
					// setSpeed(Speed.getSpeed());
				}

				// if(mc.thePlayer.hurtTime != 0) {
				// mc.timer.timerSpeed = 5F;
				// }else {
				// setSpeed(Speed.getSpeed());

				// if(mc.thePlayer.hurtTime !=0) {
				// setSpeed(Speed.getSpeed());
				// }
//				//}
//				if(mc.thePlayer.ticksExisted % 40 == 0) {
//					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//							mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
//					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//							mc.thePlayer.posY, mc.thePlayer.posZ, false));
//					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//							mc.thePlayer.posY, mc.thePlayer.posZ, true));
//				}
			}

			if (event instanceof EventNoClip) {

				// if(mc.thePlayer.ticksExisted % 3 == 1) {
				((EventNoClip) event).noClip = true;
				// }else {
				// ((EventNoClip) event).noClip = false;
				// }

			}
//			if (mc.thePlayer.hurtTime != 0) {
//				mc.timer.timerSpeed = 0.4F;
//				setSpeed(9);
//			}
//			// setSpeed(0.3);
//			mc.gameSettings.keyBindJump.pressed = true;
//			if (mc.thePlayer.ticksExisted % 3 == 0) {
//
//				mc.thePlayer.onGround = true;
//				mc.thePlayer.motionY = -0.053F;
//				// setSpeed(0.5);
//				mc.timer.timerSpeed = 0.3F;
//			} else {
//				mc.timer.timerSpeed = 0.3F;
//			}
			break;
		case "Funcraft":
			if (mc.thePlayer.onGround) {
				mc.thePlayer.motionY = 0.42F;
			} else {

				mc.thePlayer.motionY = 1E-4;
				double YPos = mc.thePlayer.posY - 1E-10D;

				mc.thePlayer.setPosition(mc.thePlayer.posX, YPos, mc.thePlayer.posZ);
			}
			Speed.setSpeed(Speed.getSpeed());

			break;
		case "Karhu":

			// mc.thePlayer.onGround = true;
			if (mc.thePlayer.hurtTime > 0) {

				verusdmg = true;
			}
			if (verusdmg) {
				// if (mc.thePlayer.fallDistance > 0) {
				KaroSpeed = ((Slider) this.getSetting("KaroSpeed").getSetting()).curValue;
				setSpeed(9);
				// }

				if (mc.thePlayer.hurtTime != 0) {
					mc.timer.timerSpeed = 0.4F;
				} else {
					mc.timer.timerSpeed = 0.2F;
				}
				// if (mc.thePlayer.fallDistance > 0) {

				// mc.timer.timerSpeed = 0.3F;
				// mc.thePlayer.motionY = 0F;
				// mc.thePlayer.onGround = true;
				// mc.gameSettings.keyBindJump.pressed = true;
				// }
				if (mc.thePlayer.fallDistance > 0) {
					mc.thePlayer.onGround = true;
					mc.thePlayer.motionY = -0.005F;
				} else {
					mc.thePlayer.onGround = false;
				}
			} else {
				mc.thePlayer.motionX = 0F;
				mc.thePlayer.motionZ = 0F;
			}

			break;
		case "LuckyNetwork":

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
				if (mc.thePlayer.onGround && mc.thePlayer.hurtTime > 0) {
					mc.thePlayer.motionY = 0.92F;
				}
				KaroSpeed = ((Slider) this.getSetting("LuckySpeed").getSetting()).curValue;
				setSpeed(KaroSpeed);

//					double speed1 = 0.8D;
//					double xm = -Math.sin(yaw) * speed1;
//					double zm = Math.cos(yaw) * speed1;

				if (mc.thePlayer.ticksExisted % 2 == 1) {

					mc.timer.timerSpeed = 0.32F;
				} else {
					mc.timer.timerSpeed = 0.3F;

				}

			}
//				if (mc.thePlayer.hurtTime != 0 ) {
//					//mc.thePlayer.motionY = 0.0F;
//				}
//				if (mc.thePlayer.hurtTime == 0) {
//					if (mc.thePlayer.ticksExisted % 2 == 1) {
//					mc.timer.timerSpeed = 0.2F;
//					}else {
//						mc.timer.timerSpeed = 0.1F;
//					}
//				}
//				 if (mc.thePlayer.ticksExisted % 2 == 1) {
//					 mc.thePlayer.motionY = 0.0F;
//				 }

			break;

		case "Rededark":
			mc.thePlayer.onGround = true;
			if (mc.thePlayer.fallDistance > 1.5) {
				mc.gameSettings.keyBindJump.pressed = true;
				mc.timer.timerSpeed = 0.3F;
				setSpeed(9);
			} else {
				mc.gameSettings.keyBindJump.pressed = false;
			}
			break;

		case "MushBW":
			if (time.hasReached(7000)) {
				Start = true;
				time.reset();
			}
			if (!Start) {
				mc.gameSettings.keyBindForward.pressed = false;
			}
			if (!Start) {
				setSpeed(0);
				mc.thePlayer.motionX = 0F;
				mc.thePlayer.motionZ = 0F;
			}
			if (Start) {
				setSpeed(4);
				if (mc.thePlayer.onGround && !high) {
					cubedmg = true;
				}
				if (cubedmg) {
					PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY = 0);
					if (mc.thePlayer.ticksExisted % 2 == 0) {
						PlayerMoveEvent.INSTANCE.setY(mc.thePlayer.motionY += 0.03F);
						if (!high) {
							setSpeed(5);
						}
					}
				}
			}
			break;
		}
		if (event instanceof EventRender2D && event.isPre()) {
			switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {

			case "MushBW":
				if (!Start) {
					GuiIngame.drawRect2(ScaledResolution.INSTANCE.getScaledWidth() / 2F + 8,
							ScaledResolution.INSTANCE.getScaledHeight() / 2f - 7,
							ScaledResolution.INSTANCE.getScaledWidth() / 2f + 82,
							ScaledResolution.INSTANCE.getScaledHeight() / 2f + 17, Color.black.getRGB());
					GuiIngame.drawRect2(ScaledResolution.INSTANCE.getScaledWidth() / 2F + 10,
							ScaledResolution.INSTANCE.getScaledHeight() / 2F - 5,
							ScaledResolution.INSTANCE.getScaledWidth() / 2F + 10
									+ 70 * Math.min(1, (System.currentTimeMillis() - startTime) / 7000f),
							ScaledResolution.INSTANCE.getScaledHeight() / 2F + 15, Color.green.getRGB());
				}
			}
		}
		if (event instanceof EventPreMotion) {
			if (((CheckBox) this.getSetting("Bobbing").getSetting()).state) {

				mc.thePlayer.cameraYaw = 0.1f;
			}

			double yaw = Math.toRadians(mc.thePlayer.rotationYawHead);
			double x1 = -Math.sin(yaw) * speed;
			double z1 = Math.cos(yaw) * speed;

			switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {

			case "Vanilla":
				mc.thePlayer.capabilities.allowFlying = true;
				mc.thePlayer.capabilities.isFlying = true;
				// mc.thePlayer.motionY = 0f;
				 setSpeed(0.1);
				break;
			case "Verus":
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
				if (verusdmg) {
					final float tmm = (float) MathHelper.getRandomDoubleInRange(new Random(), 93, 95);
					if (time.hasReached((long) 1F)) {
						turn = !turn;
						time.reset();
					}
					if (turn) {
						mc.timer.timerSpeed = 0.07F;
					}
					if (!turn) {
						mc.timer.timerSpeed = 0.08F;
					}
					this.setSpeed(0);
					if (mc.gameSettings.keyBindForward.pressed) {
						if (mc.gameSettings.keyBindJump.pressed) {
							if (mc.thePlayer.ticksExisted % 5 == 0) {
								mc.thePlayer.motionY = 3F;
							} else {
								mc.thePlayer.motionY = 0F;
							}
						}
						if (mc.gameSettings.keyBindSneak.pressed) {
							if (mc.thePlayer.ticksExisted % 5 == 0) {
								mc.thePlayer.motionY = -3F;
							} else {
								mc.thePlayer.motionY = 0F;
							}
						}
						if (!mc.gameSettings.keyBindSneak.pressed && !mc.gameSettings.keyBindForward.pressed) {
							mc.thePlayer.motionY = 0F;
						}
						double x = mc.thePlayer.posX;
						double y = mc.thePlayer.posY;
						double z = mc.thePlayer.posZ;
						double speed1 = 9D;
						double xm = -Math.sin(yaw) * speed1;
						double zm = Math.cos(yaw) * speed1;
						mc.thePlayer.setPosition(x + xm, y, z + zm);
						if (mc.thePlayer.ticksExisted % 1 == 0) {
							MineplexSpeed = ((Slider) this.getSetting("VerusSpeed").getSetting()).curValue;
							mc.timer.timerSpeed = (float) MineplexSpeed;
							double yaw2 = Math.toRadians(mc.thePlayer.rotationYaw);
						}
						mc.thePlayer.onGround = true;
						mc.thePlayer.motionY = 0F;
					}
				}
				break;
			case "BlocksMC":
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
				if (verusdmg) {
					final float tmm = (float) MathHelper.getRandomDoubleInRange(new Random(), 93, 95);
					if (time.hasReached((long) 1F)) {
						turn = !turn;
						time.reset();
					}
					if (turn) {
						mc.timer.timerSpeed = 0.6F;
					}
					if (!turn) {
						mc.timer.timerSpeed = 0.8F;
					}

					mc.thePlayer.motionY = 0F;
					if (mc.gameSettings.keyBindSneak.pressed) {
						mc.thePlayer.motionY = -2F;
						mc.timer.timerSpeed = 0.4F;
					} else {
						mc.thePlayer.motionY = 0F;
					}
					if (mc.gameSettings.keyBindForward.pressed) {
						if (mc.thePlayer.hurtTime != 0) {
							mc.timer.timerSpeed = 0.8F;
						}
						// mc.thePlayer.setSprinting(true);
						double x = mc.thePlayer.posX;
						double y = mc.thePlayer.posY;
						double z = mc.thePlayer.posZ;
						double speed1 = 2D;
						double speed2 = 0D;
						double xm = -Math.sin(yaw) * speed1;
						double zm = Math.cos(yaw) * speed1;
						double xm1 = -Math.sin(yaw) * speed2;
						double zm1 = Math.cos(yaw) * speed2;
						// mc.thePlayer.setPosition(x + xm, y, z + zm);
						if (mc.thePlayer.ticksExisted % 2 == 0) {
							// setSpeed(-0.5);
						} else {

							// setSpeed(2);

						}

						mc.timer.timerSpeed = 0.6F;
						
						setSpeed(BlocksMC);
						if (mc.gameSettings.keyBindJump.pressed) {
							mc.thePlayer.motionY = 2F;
							mc.timer.timerSpeed = 0.5F;

						}

						if (mc.thePlayer.ticksExisted % 2 == 1) {

							// mc.timer.timerSpeed = 0.3F;
						} else {
							// mc.timer.timerSpeed = 0.32F;

						}
						// mc.thePlayer.onGround = fal;
					} else {
						// mc.thePlayer.motionX = 0F;
						// mc.thePlayer.motionZ = 0F;
					}
				}
				break;
			case "OldNCP":
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
				if (verusdmg) {

					if (mc.gameSettings.keyBindForward.pressed) {

						double x = mc.thePlayer.posX;
						double y = mc.thePlayer.posY;
						double z = mc.thePlayer.posZ;
//						double speed1 = 0.8D;
//						double xm = -Math.sin(yaw) * speed1;
//						double zm = Math.cos(yaw) * speed1;
						double Y = mc.thePlayer.motionY = 0;
						mc.thePlayer.setPosition(x, y + Y, z);

						// MineplexSpeed = ((Slider)
						// this.getSetting("VerusSpeed").getSetting()).curValue;
						mc.timer.timerSpeed = (float) 0.4;

						// mc.thePlayer.onGround = true;
					}
					if (mc.thePlayer.isMoving()) {
						this.setSpeed(3);
					}
					if (mc.gameSettings.keyBindJump.pressed) {
						double x = mc.thePlayer.posX;
						double y = mc.thePlayer.posY;
						double z = mc.thePlayer.posZ;
						double Y = mc.thePlayer.motionY = 0.2;
						mc.thePlayer.setPosition(x, y + Y, z);

					}
					if (mc.gameSettings.keyBindSneak.pressed) {
						double x = mc.thePlayer.posX;
						double y = mc.thePlayer.posY;
						double z = mc.thePlayer.posZ;
						double Y = mc.thePlayer.motionY = -0.2;
						mc.thePlayer.setPosition(x, y + Y, z);

					}

				}
				break;

			case "VanillaBypass":

				// mc.timer.timerSpeed = 0.2F;
				if (mc.thePlayer.fallDistance > 1F) {
					mc.thePlayer.motionY = 0.42F;
					setSpeed(4);
				} else {
					setSpeed(0);
				}

				if (mc.thePlayer.ticksExisted % 4 == 0) {

					mc.thePlayer.motionY = 0.3F;
				} else {
					mc.thePlayer.motionY = -0.1F;
				}

				if (mc.gameSettings.keyBindJump.pressed) {
					mc.thePlayer.motionY = 0.5F;
				}
				if (mc.gameSettings.keyBindSneak.pressed) {
					mc.thePlayer.motionY = -1F;
				}
				break;
			case "Hypixel":

				if (mc.thePlayer.onGround) {

					final NetHandlerPlayClient netHandler = mc.getMinecraft().getNetHandler();
					final EntityPlayerSP player = mc.getMinecraft().thePlayer;
					final double x = player.posX;
					final double y = player.posY;
					final double z = player.posZ;
					for (int i = 0; i < Speed.getMaxFallDist() / 0.05510000046342611 + 0.2; ++i) {
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
						Speed.setSpeed5(0.4);

					}
				}

//				float Motion = (float) MathHelper.getRandomDoubleInRange(new Random(), 8.6, 8.4);
//				float Motion2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.5, 0.4);
//
//				if (mc.thePlayer.fallDistance > 3.0F)
//					mc.thePlayer.motionY = 0.0;
//				if (mc.thePlayer.hurtTime > 0) {
//
//					if (!mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindBack.pressed
//							&& !mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed) {
//						mc.thePlayer.motionX = 0;
//						mc.thePlayer.motionZ = 0;
//					}
//					float speed = 30.5f;
//					if (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
//						PlayerUtil.setSpeed(speed);
//						if (!mc.thePlayer.getActivePotionEffects().isEmpty()) {
//							if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null) {
//								PlayerUtil.setSpeed(speed
//										+ (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() / 5));
//							}
//						}
//					}
//					if (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
//						PlayerUtil.setSpeed(Motion);
//						if (!mc.thePlayer.getActivePotionEffects().isEmpty()) {
//							if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null) {
//								PlayerUtil.setSpeed(Motion2
//										+ (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() / 5));
//							}
//						}
//					}
//					mc.timer.timerSpeed = 1.0f;
//					mc.thePlayer.motionY = 0.0;
//				}

				break;
			case "Redesky":
				/*
				 * mc.thePlayer.motionY = 0; //mc.thePlayer.onGround = true;
				 * 
				 * double x3 = -Math.sin(yaw) * speed; double z3 = Math.cos(yaw) * speed;
				 * 
				 * BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.4D,
				 * mc.thePlayer.posZ); Block block = mc.theWorld.getBlockState(pos).getBlock();
				 * 
				 * 
				 * // mc.getNetHandler().getNetworkManager().sendPacket(new
				 * C03PacketPlayer(true));
				 * 
				 * //mc.thePlayer.setPosition(mc.thePlayer.posX + x1, mc.thePlayer.posY,
				 * mc.thePlayer.posZ + z1); if (block.getMaterial() != Material.air) { if
				 * (time.hasReached(850)) { speed = 3;
				 * 
				 * // mc.thePlayer.setPosition(mc.thePlayer.posX + x1, mc.thePlayer.posY + 3D,
				 * mc.thePlayer.posZ + z1);
				 * mc.getNetHandler().getNetworkManager().sendPacket(new
				 * C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x1,
				 * mc.thePlayer.posY + 3D, mc.thePlayer.posZ + z1, true));
				 * 
				 * time.reset();
				 * 
				 * } } else { mc.gameSettings.keyBindForward.pressed = false;
				 * 
				 * mc.thePlayer.motionZ = 0; mc.thePlayer.motionX = 0;
				 * 
				 * if (time.hasReached(180)) { speed = 4;
				 * 
				 * mc.thePlayer.setPosition(mc.thePlayer.posX + x3, mc.thePlayer.posY,
				 * mc.thePlayer.posZ + z3);
				 * mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
				 * mc.getNetHandler().getNetworkManager().sendPacket(new
				 * C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x3,
				 * mc.thePlayer.posY, mc.thePlayer.posZ + z3, false));
				 * 
				 * 
				 * time.reset(); } }
				 */

				// mc.thePlayer.onGround = true;

				double x3 = -Math.sin(yaw) * speed;
				double z3 = Math.cos(yaw) * speed;

				BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.3D, mc.thePlayer.posZ);
				Block block = mc.theWorld.getBlockState(pos).getBlock();

				// mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));

				// mc.thePlayer.setPosition(mc.thePlayer.posX + x1, mc.thePlayer.posY,
				// mc.thePlayer.posZ + z1);
				if (block.getMaterial() == Material.air && mc.thePlayer.fallDistance != 0) {
					mc.thePlayer.motionY = 0;
					mc.gameSettings.keyBindForward.pressed = false;

					mc.thePlayer.motionZ = 0;
					mc.thePlayer.motionX = 0;

					if (time.hasReached(180)) {
						speed = 1;

						mc.thePlayer.setPosition(mc.thePlayer.posX + x3, mc.thePlayer.posY, mc.thePlayer.posZ + z3);
						// mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
						mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
								mc.thePlayer.posX + x3, mc.thePlayer.posY, mc.thePlayer.posZ + z3, false));

					}
				}

				break;

			case "LibreCraft":

				if (mc.thePlayer.ticksExisted % 4 == 0) {
					mc.thePlayer.motionY = 0.08F;
				} else {
					mc.thePlayer.motionY = -0.008F;
				}

				if (mc.gameSettings.keyBindSneak.pressed) {
					mc.thePlayer.motionY = -1;

				}

				if (mc.gameSettings.keyBindJump.pressed) {
					mc.thePlayer.motionY = 1;

				}
				mc.thePlayer.onGround = true;
				if (mc.thePlayer.moveForward > 0) {
					mc.thePlayer.motionX = -Math.sin(yaw) * 9.0;
					mc.thePlayer.motionZ = Math.cos(yaw) * 9.0;
				}

				break;

			case "MCCentral":
				mc.thePlayer.motionY = 0;
				mc.thePlayer.onGround = true;
				// PlayerUtil.setSpeed(2);
				if (mc.thePlayer.moveForward > 0) {
					mc.thePlayer.motionX = -Math.sin(yaw) * 1.3;
					mc.thePlayer.motionZ = Math.cos(yaw) * 1.3;
				}
				break;
			case "Rededark2":
				RededarkSpeed = ((Slider) this.getSetting("RededarkSpeed").getSetting()).curValue;
				if (mc.thePlayer.fallDistance > 0.05F) {
					mc.thePlayer.motionY = 0;
					if (mc.thePlayer.isMoving()) {
						this.setSpeed(RededarkSpeed);
					} else {
						mc.thePlayer.motionX = 0F;
						mc.thePlayer.motionZ = 0F;
					}
				}

				break;
			}
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

	public static float getSpeed() {
		return (float) Math.sqrt(mc.getMinecraft().thePlayer.motionX * mc.getMinecraft().thePlayer.motionX
				+ mc.getMinecraft().thePlayer.motionZ * mc.getMinecraft().thePlayer.motionZ);
	}

}
