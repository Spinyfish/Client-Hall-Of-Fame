package de.fanta.module.impl.movement;

import java.awt.Color;
import java.util.Random;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.module.impl.combat.TestAura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.Colors;
import de.fanta.utils.FriendSystem;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.RotationUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import static org.lwjgl.opengl.GL11.*;
import net.minecraft.util.MathHelper;

public class TargetStrafe extends Module {
	public TargetStrafe() {
		super("TargetStrafe", 0, Type.Movement, Color.cyan);

		this.settings.add(new Setting("Watchdog", new CheckBox(false)));
		this.settings.add(new Setting("Circle", new CheckBox(false)));
		this.settings.add(new Setting("NoVoid", new CheckBox(false)));
		this.settings
				.add(new Setting("StrafeMode", new DropdownBox("JumpOnly", new String[] { "JumpOnly", "Normal" })));
		this.settings.add(new Setting("Range", new Slider(0.1, 6, 0.1, 4)));
		this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
	}

	public static boolean space;
	private int direction = -1;
	public static double range;

	@Override
	public void onEvent(Event event) {
		range = ((Slider) this.getSetting("Range").getSetting()).curValue;
		if (((CheckBox) this.getSetting("Watchdog").getSetting()).state) {
			if (mc.thePlayer.fallDistance < 2) {
				if (event instanceof EventTick && !Client.INSTANCE.moduleManager.getModule("Speed").isState()) {
					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
							mc.thePlayer.posY, mc.thePlayer.posZ, false));
				}
			}
		}
		if (mc.thePlayer.moveForward > 0) {
		}

		if (event instanceof EventRender3D) {
			for (Object e2 : mc.theWorld.getLoadedEntityList()) {
				Entity entity = (Entity) e2;
				if (TestAura.target != null && Client.INSTANCE.moduleManager.getModule("TestAura").isState()
						&& ((CheckBox) this.getSetting("Circle").getSetting()).state) {
					// if (Check(entity)) {
					drawCircle(mc.timer.renderPartialTicks, range);
					// }
				}

			}
		}
		if (event instanceof PlayerMoveEvent) {
			doStrafeAtSpeed(PlayerMoveEvent.INSTANCE, Speed.getSpeed());
		}

		if (mc.thePlayer.isCollidedHorizontally) {
			SD();
		}

		if (!isBlockUnder()) {
			this.SD();
		}
		if (mc.getMinecraft().gameSettings.keyBindRight.pressed) {
			direction = -1;
		} else {
			if (mc.getMinecraft().gameSettings.keyBindLeft.pressed) {
				direction = 1;
			}
		}
	}

	private void SD() {
		if (direction == 1) {
			direction = -1;
		} else {
			direction = 1;
		}
	}

	public boolean doStrafeAtSpeed(PlayerMoveEvent event, double moveSpeed) {
		boolean strafe = canStrafe();
		if (TestAura.target != null) {
			if (strafe) {
				float[] rotations = RotationUtil.getRotations(TestAura.target);
				final float Strafe = (float) MathHelper.getRandomDoubleInRange(new Random(), 1.5, 2);
				switch (((DropdownBox) this.getSetting("StrafeMode").getSetting()).curOption) {
				case "JumpOnly":
					if (mc.gameSettings.keyBindJump.pressed) {
						if (!isBlockUnder()) {
							if (mc.thePlayer.getDistanceToEntity(TestAura.target) <= 0F)
								Speed.setSpeed1(event, moveSpeed, rotations[0], direction, 0);
							else
								Speed.setSpeed1(event, moveSpeed, rotations[0], direction, 1);
						} else {
							if (!((CheckBox) this.getSetting("NoVoid").getSetting()).state) {
								if (mc.thePlayer.getDistanceToEntity(TestAura.target) <= range)
									Speed.setSpeed1(event, moveSpeed, rotations[0], direction, 0);
								else
									Speed.setSpeed1(event, moveSpeed, rotations[0], direction, 1);
							}
						}
					}
					break;
				case "Normal":
					if (!isBlockUnder()) {
						if (mc.thePlayer.getDistanceToEntity(TestAura.target) <= 0F)
							Speed.setSpeed1(event, moveSpeed, rotations[0], direction, 0);
						else
							Speed.setSpeed1(event, moveSpeed, rotations[0], direction, 1);
					} else {
						if (!((CheckBox) this.getSetting("NoVoid").getSetting()).state) {
							if (mc.thePlayer.getDistanceToEntity(TestAura.target) <= range)
								Speed.setSpeed1(event, moveSpeed, rotations[0], direction, 0);
							else
								Speed.setSpeed1(event, moveSpeed, rotations[0], direction, 1);

						}
					}
					break;
				}

			}

		}
		return strafe;
	}

	public boolean canStrafe() {
		return Client.INSTANCE.moduleManager.getModule("TestAura").isState() && TestAura.target != null
				&& !FriendSystem.isFriendString(TestAura.target.getName());
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

	private boolean Check(Entity e2) {
		if (!e2.isEntityAlive())
			return false;
		if (e2 == mc.thePlayer)
			return false;
		if (e2 instanceof EntityPlayer)
			return true;
		return false;
	}

	private void drawCircle(float partialTicks, double rad) {
		if (Killaura.hasTarget()) {
			glPushMatrix();
			glDisable(GL_TEXTURE_2D);
			RenderUtil.startDrawing();
			glDisable(GL_DEPTH_TEST);
			glDepthMask(false);
			glLineWidth(1.0f);
			glBegin(GL_LINE_STRIP);

			double x = TestAura.target.lastTickPosX
					+ (TestAura.target.posX - TestAura.target.lastTickPosX) * partialTicks
					- mc.getRenderManager().viewerPosX;
			double y = Killaura.kTarget.lastTickPosY
					+ (TestAura.target.posY - TestAura.target.lastTickPosY) * partialTicks
					- mc.getRenderManager().viewerPosY;
			Color color;
			double z = TestAura.target.lastTickPosZ
					+ (TestAura.target.posZ - TestAura.target.lastTickPosZ) * partialTicks
					- mc.getRenderManager().viewerPosZ;
			int[] rgb = Colors.getRGB(getColor2());
			float r = ((float) 1 / 255) * rgb[0];
			float g = ((float) 1 / 255) * rgb[1];
			float b = ((float) 1 / 255) * rgb[2];

			double pix2 = Math.PI * 2.0D;

			for (int i = 0; i <= 90; ++i) {
				glColor3f(r, g, b);
				glVertex3d(x + rad * Math.cos(i * pix2 / 45.0), y, z + rad * Math.sin(i * pix2 / 45.0));
			}

			glEnd();
			glDepthMask(true);
			glEnable(GL_DEPTH_TEST);
			RenderUtil.stopDrawing();
			glEnable(GL_TEXTURE_2D);
			glPopMatrix();
		}
	}

	public int getColor2() {
		try {
			return ((ColorValue) getSetting("Color").getSetting()).color;
		} catch (Exception e) {
			return Color.white.getRGB();
		}
	}
}
