package de.fanta.module.impl.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventBlockRightClick;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.module.Module;
import de.fanta.utils.Rotations;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;

public class ChestAura extends Module{
	
	private List<BlockPos> clickedChests = new ArrayList<>();
	
	public ChestAura() {
		super("Chestaura", 0, Type.Combat, Color.YELLOW);
	}

	@Override
	public void onEvent(Event event) {
		if(event instanceof EventPreMotion) {
			if(Killaura.hasTarget()) return;
			if(distance(getNearest().getPos().getX(), getNearest().getPos().getY(), getNearest().getPos().getZ(), (float) mc.thePlayer.posX, (float) mc.thePlayer.posY, (float) mc.thePlayer.posZ) > 3.5F) return;
			EventPreMotion preMotion = (EventPreMotion) event;
			TileEntityChest nearest = getNearest();
		
			lookAtPos(nearest.getPos().getX()+.5, nearest.getPos().getY()-.5, nearest.getPos().getZ()+.5);
			if(!clickedChests.contains(nearest.getPos())) {
				if(mc.currentScreen == null) mc.rightClickMouse();
				mc.thePlayer.rotationYawHead = Rotations.yaw;
				mc.thePlayer.rotationPitchHead = Rotations.pitch;
				((EventPreMotion) event).setPitch(Rotations.pitch);
				((EventPreMotion) event).setYaw(Rotations.yaw);
			}
		}
		if(event instanceof EventBlockRightClick) {
			BlockPos bp = ((EventBlockRightClick) event).getBlockpos();
			if(mc.theWorld.getBlockState(bp).getBlock() == Blocks.chest || mc.theWorld.getBlockState(bp).getBlock() == Blocks.trapped_chest || mc.theWorld.getBlockState(bp).getBlock() == Blocks.ender_chest) {
				this.clickedChests.add(bp);
			}
		}
	}
	
	public TileEntityChest getNearest() {
		TileEntityChest nearest = null;
		for(TileEntity te : mc.theWorld.loadedTileEntityList) {
			if(te instanceof TileEntityChest) {
				if(nearest == null) nearest = (TileEntityChest) te;
				else if(distance(nearest.getPos().getX(), nearest.getPos().getY(), nearest.getPos().getZ(), (float) mc.thePlayer.posX, (float) mc.thePlayer.posY, (float) mc.thePlayer.posZ) > distance(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), (float) mc.thePlayer.posX, (float) mc.thePlayer.posY, (float) mc.thePlayer.posZ)) {
					nearest = (TileEntityChest) te;
				}
			}
		}
		return nearest;
	}
	
	
	public float distance(float x1,float y1,float z1,float x2,float y2,float z2) {
		float dis=(float) Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1));
		return dis;
	}
	
	public static void lookAtPos(double x, double y, double z) {
        double dirx = mc.thePlayer.posX - x;
        double diry = mc.thePlayer.posY - y;
        double dirz = mc.thePlayer.posZ - z;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        float yaw = (float) Math.atan2(dirz, dirx);
        float pitch = (float) Math.asin(diry);
        pitch = (float) (pitch * 180.0D / Math.PI);
        yaw = (float) (yaw * 180.0D / Math.PI);
        yaw += 90.0D;
        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f3 = f2 * f2 * f2 * 1.2F;
        yaw -= yaw % f3;
        pitch -= pitch % (f3 * f2);
        Rotations.setYaw(yaw, 180F);
        Rotations.setPitch(pitch, 90F);
    }

}
