package intentions.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Node {
	
	private boolean walkable;
	private boolean vclipableDown;
	private boolean vclipableUp;
	private BlockPos blockPos;
	public Node parent;
	private BlockPos vclipPosDown;
	private BlockPos vclipPosUp;
	
	public void setVclipableDown(boolean vclipable) {
		this.vclipableDown = vclipable;
	}
	
	public void setVclipableUp(boolean vclipable) {
		this.vclipableUp = vclipable;
	}
	
	public void setVclipPosDown(BlockPos vclipPosDown) {
		this.vclipPosDown = vclipPosDown;
	}
	
	public void setVclipPosUp(BlockPos vclipPosUp) {
		this.vclipPosUp = vclipPosUp;
	}
	
	public BlockPos getVclipPosDown() {
		return vclipPosDown;
	}
	
	public BlockPos getVclipPosUp() {
		return vclipPosUp;
	}
	
	public int id = 0;

	public boolean isVclipableUp() {
		return vclipableUp;
	}
	
	public boolean isVclipableDown() {
		return vclipableDown;
	}
	
	public Node setId(int id) {
		this.id = id;
		return this;
	}
	
	/**
	 * g cost and h cost added together
	 * 
	 */
	public double fCost;
	
	/**
	 * Distance from start node
	 * 
	 */
	public double gCost;
	
	/**
	 * Distance from end node
	 * 
	 */
	public double hCost;
	
	public Node(boolean walkable, BlockPos blockPos) {
		this.walkable = walkable;
		this.blockPos = blockPos;
	}
	
	public BlockPos getBlockpos() {
		return blockPos;
	}
	
	public boolean isWalkable() {
		return walkable;
	}
	
	private static final double range10 = 1.0000000000000000;
	
	private static final double range14 = 1.4142135623730951;
	
	private static final double range17 = 1.7320508075688772;
	
	public static double distance(BlockPos b1, BlockPos b2) {
//		int distX = Math.abs(b1.getX() - b2.getX());
//		int distY = Math.abs(b1.getY() - b2.getY());
//		int distZ = Math.abs(b1.getZ() - b2.getZ());
//		
//		double range = 0;
//		
//		for(int x = 0; x < distX; x++) {
//			range += range10;
//			for(int y = 0; y < distY; y++) {
//				range += range14;
//				for(int z = 0; z < distZ; z++) {
//					range += range17;
//				}
//			}
//		}
//		return range;
		int f = (b1.getX() - b2.getX());
		int f1 = (b1.getY() - b2.getY());
		int f2 = (b1.getZ() - b2.getZ());
		return Math.sqrt(f * f + f1 * f1 + f2 * f2);
	}
	
}
