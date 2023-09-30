package de.fanta.module.impl.movement;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventNoClip;
import de.fanta.events.listeners.EventPacket;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.ColorUtils;
import de.fanta.utils.PathFinder;
import de.fanta.utils.PlayerUtil;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class Teleport extends Module {
	public Teleport() {
		super("Teleport", 0, Type.Movement, Color.orange);
	}

	private BlockPos pos = null;
	private boolean teleported;
	private List<Vec3> positions;

	@Override
	public void onEnable() {
		this.teleported = false;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		double blockX = mc.objectMouseOver.getBlockPos().getX() + 1.5;
		double blockY = mc.objectMouseOver.getBlockPos().getY() + 1;
		double blockZ = mc.objectMouseOver.getBlockPos().getZ() + 1.5;
		pos = new BlockPos(0, 0, 0);
		super.onDisable();
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof EventUpdate) {
			if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null
					&& mc.objectMouseOver.getBlockPos() != null) {
				//PlayerUtil.verusdmg();
				double blockX = mc.objectMouseOver.getBlockPos().getX() + 1.5;
				double blockY = mc.objectMouseOver.getBlockPos().getY() + 1;
				double blockZ = mc.objectMouseOver.getBlockPos().getZ() + 1.5;
				pos = new BlockPos(blockX, blockY, blockZ);
				if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.gameSettings.keyBindSneak.pressed) {
					//mc.timer.timerSpeed = 0.8F;
				} else {
					mc.timer.timerSpeed = 1F;
				}
				ChatUtil.sendChatMessageWithPrefix("Selected position: " + blockX + " " + blockY + " " + blockZ);
				if (mc.gameSettings.keyBindSneak.pressed && !teleported) {
					mc.thePlayer.onGround = true;
					this.teleported = true;
					positions = calculatePath(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
							new Vec3(pos.getX(), pos.getY(), pos.getZ()));
					positions.add(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
					ChatUtil.sendChatMessage("" + positions.size());
					for (Vec3 vec : positions) {
						mc.getNetHandler().addToSendQueue(
								new C03PacketPlayer.C04PacketPlayerPosition(vec.xCoord, vec.yCoord, vec.zCoord, true));
					}
					Client.INSTANCE.moduleManager.getModule("Teleport").setState(false);
				//	mc.thePlayer.setPosition(pos.getX(), pos.getY(), pos.getZ());
					//mc.getNetHandler().addToSendQueue(
					//		new C03PacketPlayer.C04PacketPlayerPosition(pos.getX(), pos.getY(), pos.getZ(), false));
					for (int i = 0; i < 5; i++)
						mc.thePlayer.sendQueue.addToSendQueue((Packet) new C03PacketPlayer(true));
				}
			}
		}
		if (event instanceof EventRender3D) {
//			if (pos != null) {
//				GL11.glLineWidth(2);
//				GL11.glBegin(GL11.GL_LINE_SMOOTH);
//				GL11.glDisable(GL11.GL_DEPTH_TEST);
//				GL11.glColor3d(1.0, 1.0, 1.0);
//				GL11.glBegin(GL11.GL_LINE_LOOP);
//
//				GL11.glVertex3d(pos.getX() - mc.getRenderManager().renderPosX - 0.5,
//						pos.getY() - mc.getRenderManager().renderPosY,
//						pos.getZ() - mc.getRenderManager().renderPosZ - 0.5);
//				GL11.glVertex3d(0, 0, 0);
//				GL11.glEnd();
//				GL11.glColor4d(1, 1, 1, 1);
//				GL11.glEnable(GL11.GL_DEPTH_TEST);
//				GL11.glDisable(GL11.GL_LINE_SMOOTH);
//			}

		}
	}

	private List<Vec3> calculatePath(Vec3 startPos, Vec3 endPos) {
		final PathFinder pathfinder = new PathFinder(startPos, endPos);
		pathfinder.calculatePath(5000);

		int i = 0;
		Vec3 lastLoc = null;
		Vec3 lastDashLoc = null;
		final List<Vec3> path = new ArrayList<Vec3>();
		final List<Vec3> pathFinderPath = pathfinder.getPath();
		for (final Vec3 pathElm : pathFinderPath) {
			if (i == 0 || i == pathFinderPath.size() - 1) {
				if (lastLoc != null) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
				}
				path.add(pathElm.addVector(0.5, 0, 0.5));
				lastDashLoc = pathElm;
			} else {
				boolean canContinue = true;
				if (pathElm.squareDistanceTo(lastDashLoc) > 15) {
					canContinue = false;
				} else {
					final double smallX = Math.min(lastDashLoc.xCoord, pathElm.xCoord);
					final double smallY = Math.min(lastDashLoc.yCoord, pathElm.yCoord);
					final double smallZ = Math.min(lastDashLoc.zCoord, pathElm.zCoord);
					final double bigX = Math.max(lastDashLoc.xCoord, pathElm.xCoord);
					final double bigY = Math.max(lastDashLoc.yCoord, pathElm.yCoord);
					final double bigZ = Math.max(lastDashLoc.zCoord, pathElm.zCoord);
					cordsLoop: for (int x = (int) smallX; x <= bigX; x++) {
						for (int y = (int) smallY; y <= bigY; y++) {
							for (int z = (int) smallZ; z <= bigZ; z++) {
								if (!PathFinder.checkPositionValidity(x, y, z, false)) {
									canContinue = false;
									break cordsLoop;
								}
							}
						}
					}
				}
				if (!canContinue) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
					lastDashLoc = lastLoc;
				}
			}
			lastLoc = pathElm;
			i++;
		}
		return path;
	}

}