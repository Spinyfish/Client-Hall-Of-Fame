package de.fanta.module.impl.world;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.github.creeper123123321.viafabric.ViaFabric;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.MoveFlyingEvent;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.EventSilentMove;
import de.fanta.events.listeners.EventSycItem;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.module.impl.movement.Speed;
import de.fanta.module.impl.world.Scaffold.BlockData;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.RotationUtil;
import de.fanta.utils.Rotations;
import de.fanta.utils.ScaffoldRot;
import de.fanta.utils.TimeUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import us.myles.ViaVersion.api.Via;

public class Scaffold extends Module {

	public Scaffold() {
		super("Scaffold", Keyboard.KEY_M, Type.World, Color.magenta);
		this.settings.add(new Setting("Legit", new CheckBox(false)));
		this.settings.add(new Setting("NoBob", new CheckBox(false)));
		this.settings.add(new Setting("Swing", new CheckBox(false)));
		this.settings.add(new Setting("Sprint", new CheckBox(false)));
		this.settings.add(new Setting("Matrix", new CheckBox(false)));
		this.settings.add(new Setting("NCP", new CheckBox(false)));
		this.settings.add(new Setting("AAC", new CheckBox(false)));
		this.settings.add(new Setting("TP", new CheckBox(false)));
		this.settings.add(new Setting("NCPTower", new CheckBox(false)));
		this.settings.add(new Setting("CubecraftTower", new CheckBox(false)));
		this.settings.add(new Setting("NoAttack", new CheckBox(false)));
		this.settings.add(new Setting("OnlyAirPlace", new CheckBox(false)));
		this.settings.add(new Setting("MoveAbleTower", new CheckBox(false)));

	}

	@Override
	public void onEnable() {
		posY = mc.thePlayer.posY;
		Client.INSTANCE.moduleManager.getModule("Sprint").setState(true);
		// if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof
		// EntityPlayer) {
		// if (mc.entityRenderer.theShaderGroup != null) {
		// mc.entityRenderer.theShaderGroup.deleteShaderGroup();
		// }
		// mc.entityRenderer.loadShader2(new
		// ResourceLocation("shaders/post/blur.json"));
		// }
		// Speed.setSpeed(0);

		if (((CheckBox) this.getSetting("TP").getSetting()).state) {
			Minecraft.getMinecraft().thePlayer.motionY = 0.11F;
		}

		super.onEnable();

	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1F;
		if (((CheckBox) this.getSetting("NoAttack").getSetting()).state) {
			Client.INSTANCE.moduleManager.getModule("Killaura").setState(true);
		}
		Client.INSTANCE.moduleManager.getModule("Sprint").setState(true);
		mc.gameSettings.keyBindRight.pressed = false;
		mc.gameSettings.keyBindLeft.pressed = false;
		if (mc.entityRenderer.theShaderGroup != null) {
			mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			mc.entityRenderer.theShaderGroup = null;
		}
		mc.gameSettings.keyBindSneak.pressed = false;
		super.onDisable();

	}

	int add = 0;
	public int slot;
	public static BlockData data;
	public static float[] lastRot;
	private final int[] forbiddenBlocks = { 5 };
	public static float lastYaw, lastPitch;
	public boolean turn;
	public boolean downwards;
	private double posY;
	TimeUtil time = new TimeUtil();
	TimeUtil time2 = new TimeUtil();

	@Override

	public void onEvent(Event event) {

		if (((CheckBox) this.getSetting("NoAttack").getSetting()).state) {
			Client.INSTANCE.moduleManager.getModule("Killaura").setState(false);
		}

		if (((CheckBox) this.getSetting("NCPTower").getSetting()).state) {
			if (mc.gameSettings.keyBindJump.pressed) {
			}
		}
		if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && !mc.gameSettings.keyBindJump.isKeyDown()) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
			downwards = true;
		} else
			downwards = false;
		if (event instanceof EventSycItem) {
			if (getBlockSlot() != -1) {
				EventSycItem.INSTANCE.slot = this.slot = getBlockSlot();
			}
		}
//		if (event instanceof EventRender2D) {
//			BlockPos blockPos = new BlockPos(mc.getMinecraft().thePlayer.posX, mc.getMinecraft().thePlayer.posY - 1,
//					mc.getMinecraft().thePlayer.posZ);
//			if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
//				mc.thePlayer.setSprinting(false);
//			} else {
//				mc.thePlayer.setSprinting(true);
//			}
//		}

		if (event instanceof Event) {
			if (!((CheckBox) this.getSetting("Sprint").getSetting()).state) {
				mc.thePlayer.setSprinting(false);
			} else {
				if (mc.thePlayer.isMoving()) {
					// mc.thePlayer.setSprinting(true);
				}
			}
		}
		
		if (event instanceof EventPreMotion) {
			if (((CheckBox) this.getSetting("NoBob").getSetting()).state) {
				this.mc.gameSettings.viewBobbing = true;
				this.mc.thePlayer.distanceWalkedModified = 0.0F;
			}
			if (((CheckBox) this.getSetting("TP").getSetting()).state) {
				if (mc.gameSettings.keyBindForward.pressed) {
					mc.thePlayer.motionY = 0F;
					double x = mc.thePlayer.posX;
					double y = mc.thePlayer.posY;
					double z = mc.thePlayer.posZ;
					mc.gameSettings.keyBindSprint.pressed = false;
					if (mc.thePlayer.ticksExisted % 2 == 0) {
						double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
						double speed1 = 1.2D;
						double xm = -Math.sin(yaw) * speed1;
						double zm = Math.cos(yaw) * speed1;
						mc.thePlayer.setPosition(x + xm, y, z + zm);

					}
				}
			}
			mc.gameSettings.keyBindSprint.pressed = false;

			BlockPos blockPos = new BlockPos(mc.getMinecraft().thePlayer.posX, mc.getMinecraft().thePlayer.posY - 1,
					mc.getMinecraft().thePlayer.posZ);
			
			if( mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air ) {
			//	mc.gameSettings.keyBindSneak.pressed = true;
			}else {
				mc.gameSettings.keyBindSneak.pressed = false;
				time.reset();
			}
			if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
			
				mc.gameSettings.keyBindSneak.pressed = true;
				
			

			}
			if (mc.theWorld.getBlockState(blockPos).getBlock() != Blocks.air) {
				mc.gameSettings.keyBindSneak.pressed = false;
			}
//				
			
			
			if(mc.thePlayer.ticksExisted % 2 == 0) {
			//	mc.gameSettings.keyBindSneak.pressed = false;
			}
			if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("Matrix")
					.getSetting()).state) {
				mc.gameSettings.keyBindSneak.pressed = false;
			}
			Rotations.yaw = mc.thePlayer.rotationYawHead;
			Rotations.pitch = mc.thePlayer.rotationPitchHead;
			// mc.thePlayer.rotationYawHead = Rotations.yaw;
			// mc.thePlayer.rotationPitchHead = Rotations.pitch;

			if (data == null)
				return;
			float[] rotation = Rotations.rotationrecode7(this.data);
			float[] rotation2 = Rotations.rotationrecode2(this.data);
			// mc.rightClickDelayTimer = (int) 4F;
			
			((EventPreMotion) event).setYaw(mc.thePlayer.rotationYaw + 180);
			// final float PitchRandomice = (float) MathHelper.getRandomDoubleInRange(new
			// Random(), 82,5F, 82.5);
			((EventPreMotion) event).setPitch(82);
			// ChatUtil.sendChatMessage(""+ rotation[1]);
			if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
				// mc.thePlayer.rotationPitchHead = 82.5F;

			} else {
				if (Minecraft.getMinecraft().thePlayer.isMoving()) {
					final float PitchRandomice2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 90, 90);
					// mc.thePlayer.rotationPitchHead = PitchRandomice2;
				}
			}

			if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("Matrix")
					.getSetting()).state) {

				final float RotationPitch2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 80.009F, 80.0049F);
				((EventPreMotion) event).setPitch(RotationPitch2);
				if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
					final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 79.1210,
							79.2650);
					final float RotationPitch3 = (float) MathHelper.getRandomDoubleInRange(new Random(), 82.5, 82.4);
					final float RotationYAW = (float) MathHelper.getRandomDoubleInRange(new Random(), 173.5F, 173.6F);
					((EventPreMotion) event).setPitch(RotationPitch);
					((EventPreMotion) event).setYaw(mc.thePlayer.rotationYaw + RotationYAW);

					// ((EventPreMotion) event).setPitch(RotationPitch3);
				}
			}

			Rotations.setYaw(mc.thePlayer.rotationYaw + 180, 180);
			Rotations.setPitch(90, 90);

			if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("NCP").getSetting()).state) {
				final float yaw_ = updateRotation(mc.thePlayer.rotationYawHead, rotation2[0], 180);
				((EventPreMotion) event).setYaw(rotation2[0]);
				//ChatUtil.sendChatMessage(""+rotation2[1] );
				((EventPreMotion) event).setPitch(rotation2[1]);

				mc.gameSettings.keyBindSneak.pressed = false;

				if (((CheckBox) this.getSetting("NCPTower").getSetting()).state) {
					if (((CheckBox) this.getSetting("MoveAbleTower").getSetting()).state) {

						if (mc.gameSettings.keyBindJump.pressed) {
							// mc.thePlayer.onGround = true;
							// mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1D,
							// mc.thePlayer.posZ);
							// mc.thePlayer.motionY = 0.2D;
							if (time.hasReached(2000)) {
								time.reset();
							} else if (mc.thePlayer.ticksExisted % 3 == 0)
								mc.thePlayer.motionY = 0.4196;
							// mc.timer.timerSpeed = 10F;
						}
						time.reset();

					} else {

						if (!mc.thePlayer.isMoving()) {
							if (mc.gameSettings.keyBindJump.pressed) {
								// mc.thePlayer.onGround = true;
								// mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1D,
								// mc.thePlayer.posZ);
								// mc.thePlayer.motionY = 0.2D;
								if (time.hasReached(2000)) {
									time.reset();
								} else if (mc.thePlayer.ticksExisted % 3 == 0)
									mc.thePlayer.motionY = 0.4196;
								// mc.timer.timerSpeed = 10F;
							}
							time.reset();

						}

					}
				}

			}
			if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("AAC").getSetting()).state) {
				((EventPreMotion) event).setYaw(mc.thePlayer.rotationYaw + 180);
				((EventPreMotion) event).setPitch(82.5F);
				mc.gameSettings.keyBindSneak.pressed = false;

				if (((CheckBox) this.getSetting("CubecraftTower").getSetting()).state) {
					if (!mc.thePlayer.isMoving()) {
						if (mc.gameSettings.keyBindJump.pressed) {
							// mc.thePlayer.onGround = true;
							// mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1D,
							// mc.thePlayer.posZ);
							// mc.thePlayer.motionY = 0.2D;
							if (time.hasReached(2000)) {
								time.reset();
							} else if (mc.thePlayer.ticksExisted % 3 == 0)
								mc.thePlayer.motionY = 0.4196;
							mc.timer.timerSpeed = 1.2F;
						}
						time.reset();
					} else {
						mc.timer.timerSpeed = 1F;
					}

				}

			}

			if (mc.thePlayer.ticksExisted % 100 == 0) {
				// mc.thePlayer.motionY = 0.42F;
			}

		}
		if (event instanceof EventRender2D) {

		}
		if (event instanceof EventUpdate) {
			mc.gameSettings.keyBindSprint.pressed = false;
			final float tmm = (float) MathHelper.getRandomDoubleInRange(new Random(), 93, 95);
			if (time2.hasReached((long) tmm)) {
				if (time.hasReached(10)) {
					turn = !turn;
					time.reset();
				}
				time2.reset();
			}
			if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("Matrix")
					.getSetting()).state) {
//				mc.gameSettings.keyBindRight.pressed = turn;
//				mc.gameSettings.keyBindLeft.pressed = !turn;
				BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
				if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
					mc.thePlayer.motionX *= 0.508000000F;
					mc.thePlayer.motionZ *= 0.508000000F;
				}
			}

			// this.data = grabBlock();
			data = find(new Vec3(0, 0, 0));

			if (this.data != null && getBlockSlot() != -1) {

				mc.playerController.updateController();
				Vec3 hitVec = (new Vec3(BlockData.getPos())).addVector(0.5D, 0.5D, 0.5D)
						.add((new Vec3(BlockData.getFacing().getDirectionVec())).multi(0.5D));
				if (this.slot != -1) {
					if (!((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("AAC")
							.getSetting()).state
							|| !((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("NCP")
									.getSetting()).state) {
						rightClickMouse(mc.thePlayer.inventory.getStackInSlot(this.slot), this.slot);
					}
					if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("AAC")
							.getSetting()).state
							|| ((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("NCP")
									.getSetting()).state) {

						if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
								mc.thePlayer.inventory.getStackInSlot(slot), BlockData.getPos(), BlockData.getFacing(),
								hitVec)) {
							mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());

						}
					}
				}
			}
		}

	}

	public BlockData grabBlock() {
		EnumFacing[] invert = { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST,
				EnumFacing.WEST };

		// Minecraft.thePlayer.getPositionVector()).yCoord

		if (downwards) {
			posY = mc.thePlayer.getPositionVector().yCoord - 1;

		}

		BlockPos pos = (new BlockPos((mc.thePlayer.getPositionVector()).xCoord, posY,
				(mc.thePlayer.getPositionVector()).zCoord)).offset(EnumFacing.DOWN);

		if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir))
			return null;

		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos offset = pos.offset(facing);

			if (!(mc.theWorld.getBlockState(offset).getBlock() instanceof BlockAir))
				return new BlockData(invert[facing.ordinal()], offset);
		}

		BlockPos[] offsets = { new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1),
				new BlockPos(0, 0, -1) };

		for (BlockPos offset : offsets) {
			BlockPos offsetPos = pos.add(offset.getX(), 0, offset.getZ());

			if (mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
				for (EnumFacing facing : EnumFacing.values()) {
					BlockPos blockOffset = offsetPos.offset(facing);

					if (!(mc.theWorld.getBlockState(blockOffset).getBlock() instanceof BlockAir))
						return new BlockData(invert[facing.ordinal()], blockOffset);
				}
			}
		}
		return null;
	}

//
	public void rightClickMouse(ItemStack itemstack, int slot) {
		if (!mc.playerController.func_181040_m()) {
			mc.rightClickDelayTimer = 4;
			try {
				switch (mc.objectMouseOver.typeOfHit) {
				case ENTITY:
					if (mc.playerController.func_178894_a(mc.thePlayer, mc.objectMouseOver.entityHit,
							mc.objectMouseOver)) {
					} else if (mc.playerController.interactWithEntitySendPacket(mc.thePlayer,
							mc.objectMouseOver.entityHit)) {
					}

					break;

				case BLOCK:
					BlockPos blockpos = mc.objectMouseOver.getBlockPos();

					if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
						int i = itemstack != null ? itemstack.stackSize : 0;

						if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos,
								mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec))
							if (!(((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("Swing")
									.getSetting()).state)) {
								if(ViaFabric.clientSideVersion == 47) {
									mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
								}else {
									mc.thePlayer.sendQueue.addToSendQueue(new CAnimateHandPacket());
								}
					
							}

						BlockPos blockPos = new BlockPos(mc.getMinecraft().thePlayer.posX,
								mc.getMinecraft().thePlayer.posY - 1, mc.getMinecraft().thePlayer.posZ);
						if ((((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("Swing")
								.getSetting()).state)) {
							if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
								mc.thePlayer.swingItem();
							} else {
								mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
							//	mc.thePlayer.swingItem();
							}
							// mc.thePlayer.swingItem();

						}
						if (itemstack == null)
							return;
						if (itemstack.stackSize == 0)
							mc.thePlayer.inventory.mainInventory[slot] = null;
						if (((CheckBox) this.getSetting("Swing").getSetting()).state) {
							if (itemstack.stackSize != i || mc.playerController.isInCreativeMode())
								mc.entityRenderer.itemRenderer.resetEquippedProgress();
						}
					}
				default:
					break;
				}
			} catch (NullPointerException e) {

			}
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	public int getBlockSlot() {
		for (int i = 0; i < 9; i++) {
			ItemStack s = mc.thePlayer.inventory.getStackInSlot(i);
			if (s != null && s.getItem() instanceof ItemBlock
					&& !Arrays.asList(forbiddenBlocks).contains(s.getItem().getBlockId()))
				return i;
		}
		return -1;
	}

	private Vec3 getPositionByFace(BlockPos position, EnumFacing facing) {
		Vec3 offset = new Vec3((double) facing.getDirectionVec().getX() / 2.0,
				(double) facing.getDirectionVec().getY() / 2.0, (double) facing.getDirectionVec().getZ() / 2.0);
		Vec3 point = new Vec3((double) position.getX() + 0.5, (double) position.getY() + 0.5,
				(double) position.getZ() + 0.5);
		return point.add(offset);
	}

	private boolean rayTrace(Vec3 origin, Vec3 position) {
		Vec3 difference = position.subtract(origin);
		int steps = 10;
		double x = difference.xCoord / (double) steps;
		double y = difference.yCoord / (double) steps;
		double z = difference.zCoord / (double) steps;
		Vec3 point = origin;
		for (int i = 0; i < steps; ++i) {
			BlockPos blockPosition = new BlockPos(point = point.addVector(x, y, z));
			IBlockState blockState = mc.getMinecraft().theWorld.getBlockState(blockPosition);
			if (blockState.getBlock() instanceof BlockLiquid || blockState.getBlock() instanceof BlockAir)
				continue;
			AxisAlignedBB boundingBox = blockState.getBlock().getCollisionBoundingBox(mc.getMinecraft().theWorld,
					blockPosition, blockState);
			if (boundingBox == null) {
				boundingBox = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
			}
			if (!boundingBox.offset(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ())
					.isVecInside(point))
				continue;
			return true;
		}
		return false;
	}

	private BlockData find(Vec3 offset3) {

		double x = mc.getMinecraft().thePlayer.posX;
		double y = mc.getMinecraft().thePlayer.posY;
		double z = mc.getMinecraft().thePlayer.posZ;

		EnumFacing[] invert = new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH,
				EnumFacing.EAST, EnumFacing.WEST };
		BlockPos position = new BlockPos(new Vec3(x, y, z).add(offset3)).offset(EnumFacing.DOWN);
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos offset = position.offset(facing);
			if (mc.getMinecraft().theWorld.getBlockState(offset).getBlock() instanceof BlockAir || rayTrace(
					mc.getMinecraft().thePlayer.getLook(0.0f), getPositionByFace(offset, invert[facing.ordinal()])))
				continue;
			return new BlockData(invert[facing.ordinal()], offset);
		}
		BlockPos[] offsets = new BlockPos[] { new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1),
				new BlockPos(0, 0, 1), new BlockPos(0, 0, 2), new BlockPos(0, 0, -2), new BlockPos(2, 0, 0),
				new BlockPos(-2, 0, 0) };
		for (BlockPos offset : offsets) {
			BlockPos offsetPos = position.add(offset.getX(), 0, offset.getZ());
			if (!(mc.getMinecraft().theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir))
				continue;
			for (EnumFacing facing : EnumFacing.values()) {
				BlockPos offset2 = offsetPos.offset(facing);
				if (mc.getMinecraft().theWorld.getBlockState(offset2).getBlock() instanceof BlockAir
						|| rayTrace(mc.getMinecraft().thePlayer.getLook(0.01f),
								getPositionByFace(offset, invert[facing.ordinal()])))
					continue;
				return new BlockData(invert[facing.ordinal()], offset2);
			}
		}
		return null;
	}

	public static class BlockData {
		private static EnumFacing facing;

		private static BlockPos pos;

		public BlockData(EnumFacing facing, BlockPos pos) {
			BlockData.facing = facing;
			BlockData.pos = pos;
		}

		public static EnumFacing getFacing() {
			return facing;
		}

		public static BlockPos getPos() {
			return pos;
		}
	}

	public static float[] rotationrecode7(BlockData blockData) {
		double x = blockData.getPos().getX() + 0.5D - mc.thePlayer.posX
				+ blockData.getFacing().getFrontOffsetX() / 2.0D;
		double z = blockData.getPos().getZ() + 0.5D - mc.thePlayer.posZ
				+ blockData.getFacing().getFrontOffsetZ() / 2.0D;
		double y = blockData.getPos().getY() + 0.5D;
		double ymax = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
		double allmax = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) (Math.atan2(ymax, allmax) * 180.0D / Math.PI);
		if (yaw < 0.0F)
			yaw += 360.0F;
		return new float[] { yaw, pitch };
	}

	public static int getBlockCount() {
		int itemCount = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() != null && stack.getItem() instanceof ItemBlock) {
				itemCount += stack.stackSize;
			}
		}
		return itemCount;
	}

	public float updateRotation(float current, float needed, float speed) {
		float f = MathHelper.wrapAngleTo180_float(needed - current);
		if (f > speed)
			f = speed;
		if (f < -speed)
			f = -speed;
		return current + f;
	}

}
