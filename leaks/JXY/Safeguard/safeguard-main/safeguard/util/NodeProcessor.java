package intentions.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class NodeProcessor {
	
	public ArrayList<Node> path = new ArrayList<Node>();
	public ArrayList<Node> triedPaths = new ArrayList<Node>();
	
	ArrayList<Node> openNodes = new ArrayList<Node>();
	HashMap<Integer, Node> hashOpenNodes = new HashMap<Integer, Node>();
	HashMap<Integer, Node> hashClosedNodes = new HashMap<Integer, Node>();
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	private ArrayList<Node> getNeighbors(Node node) {
		ArrayList<Node> neighbors = new ArrayList<Node>();
		BlockPos nodeBlockPos = node.getBlockpos();
		if(node.isVclipableDown()) {
			neighbors.add(createNode(node.getVclipPosDown()));
		}
		if(node.isVclipableUp()) {
			neighbors.add(createNode(node.getVclipPosUp()));
		}
		for(Object p : BlockPos.getAllInBox(nodeBlockPos.add(1, 1, 1), nodeBlockPos.add(-1, -1, -1))) {
			BlockPos pos = (BlockPos)p;
			if(pos.equals(nodeBlockPos)) {
				continue;
			}
			if(pos.getX() > nodeBlockPos.getX() && pos.getZ() > nodeBlockPos.getZ()) {
				continue;
			}
			if(pos.getX() < nodeBlockPos.getX() && pos.getZ() < nodeBlockPos.getZ()) {
				continue;
			}
			if(pos.getX() < nodeBlockPos.getX() && pos.getZ() > nodeBlockPos.getZ()) {
				continue;
			}
			if(pos.getX() > nodeBlockPos.getX() && pos.getZ() < nodeBlockPos.getZ()) {
				continue;
			}
			if(pos.getY() < nodeBlockPos.getY() && !isPassable(TeleportUtils.getBlock(pos.up(2)))) {
				continue;
			}
			if(pos.getY() > nodeBlockPos.getY() && !isPassable(TeleportUtils.getBlock(nodeBlockPos.up(2)))) {
				continue;
			}
			Node created = createNode(pos);
			if(created != null) {
				neighbors.add(created);
			}
		}
		return neighbors;
	}
	
	long totalTime = 0;
	
	public void closeNode(Node node) {
		openNodes.remove(node);
		hashOpenNodes.remove(node.id);
		hashClosedNodes.put(node.id, node);
	}
	
	public void openNode(Node node) {
		hashOpenNodes.put(node.id, node);
		openNodes.add(node);
	}
	
	public void getPath(BlockPos start, BlockPos finish) {
		totalTime = 0;
		hashOpenNodes.clear();
		hashClosedNodes.clear();
		openNodes.clear();
		Node startNode = createNode(start);
		Node endNode = createNode(finish);
//		Jigsaw.chatMessage(finish.toString());
		
//		Jigsaw.chatMessage(endNode.getBlockpos().toString());
		
		openNode(startNode);
		
		long now1 = System.nanoTime();
		
		int count = 0;
		while(hashOpenNodes.values().size() > 0) {
			Node currentNode = openNodes.get(0);
			
			if(count > 20) {
				path = null;
//				Jigsaw.chatMessage("Neighbor: " + (totalTime / 1000000));
//				Jigsaw.chatMessage("Whole: " + ((System.nanoTime() - now1) / 1000000));
				return;
			}

			double minFCost = currentNode.fCost;
			for(int i = 1; i < openNodes.size(); i++) {
				Node openedNode = openNodes.get(i);
				double fCost = openedNode.fCost;
				if(fCost < minFCost || fCost == currentNode.fCost && openedNode.hCost < currentNode.hCost) {
					minFCost = fCost;
					currentNode = openedNode;
				}
			}
			
			closeNode(currentNode);
			
			triedPaths.add(currentNode);

			if(currentNode.getBlockpos().equals(endNode.getBlockpos())) { //path has been found
				endNode.parent = currentNode;
				retracePath(startNode, endNode);
				return;
			}
			
//			long now = System.nanoTime();
			
			for(Node neighbor : getNeighbors(currentNode)) {
				boolean vclipableDown = false;
				boolean vclipableUp = false;
				if((!neighbor.isWalkable() && !((vclipableDown = neighbor.isVclipableDown()) || (vclipableUp = neighbor.isVclipableUp()))) || isNodeClosed(neighbor)) {
					continue;
				}
				if(vclipableDown) {
					neighbor = createNode(neighbor.getBlockpos().up());
				}
				if(vclipableUp) {
					neighbor = createNode(neighbor.getBlockpos().down());
				}
				double newMovementCostToNeighbor = 
						currentNode.gCost + Node.distance(currentNode.getBlockpos(), endNode.getBlockpos());
				boolean isOpen = isNodeOpen(neighbor);
				if(newMovementCostToNeighbor < neighbor.gCost || !isOpen) {
					neighbor.gCost = newMovementCostToNeighbor;
					neighbor.hCost = Node.distance(neighbor.getBlockpos(), endNode.getBlockpos());
					neighbor.parent = currentNode;
					if(!isOpen) {
						openNode(neighbor);
					}
				}
			}
//			totalTime += System.nanoTime() - now;
			count++;
		}
	}
	
	private boolean isNodeOpen(Node node) {
		boolean open = hashOpenNodes.get(node.id) != null;
		return open;
	}
	
	private boolean isNodeClosed(Node node) {
		boolean closed = hashClosedNodes.get(node.id) != null;
		return closed;
	}
	
	private void retracePath(Node startNode, Node endNode) {
		ArrayList<Node> path = new ArrayList<Node>();
		
		Node currentNode = endNode;
		
		while(!currentNode.equals(startNode)) {
			path.add(currentNode);
			currentNode = currentNode.parent;
		}
		path.add(startNode);
		Collections.reverse(path);
		
		this.path = path;
	}
	
	int nextID = 0;
	
	public Node createNode(BlockPos pos) {
		Node node = new Node(!isStairBlock(TeleportUtils.getBlock(pos.down())) && isPassable(TeleportUtils.getBlock(pos)) && isPassable(TeleportUtils.getBlock(pos.up())), pos).setId(pos.hashCode());
		
//		Node node = new Node(isWalkable(Utils.getBlock(pos.down())) && isPassable(Utils.getBlock(pos)) && isPassable(Utils.getBlock(pos.up())), pos).setId(pos.hashCode());
		
		node.setVclipableDown(isVclipableDown(node));
		node.setVclipableUp(isVclipableUp(node));
		return node;
	}
	
	public static boolean isPassable(Block block) {
		return block.getMaterial() == Material.air || (block.getMaterial() == Material.plants && !(block instanceof BlockLilyPad)) || block.getMaterial() == Material.vine 
				|| block.getMaterial() == Material.water || block.getMaterial() == Material.circuits /*block.getMaterial() == Material.web ||*/ || 
				block instanceof BlockSign || block instanceof BlockWallSign || block instanceof BlockLadder;
	}
	
	public static boolean isWalkable(Block block) {
		return !(block.getMaterial() == Material.air) && (block instanceof BlockLilyPad || block.getMaterial() == Material.vine 
				/*block.getMaterial() == Material.web ||*/ || block instanceof BlockLadder || block.isFullBlock());
	}
	
	public static boolean isStairBlock(Block block) {
		return block instanceof BlockFence || block instanceof BlockFenceGate|| block instanceof BlockWall;
	}
	
	public static boolean isVclipableDown(Node node) {
		if(!node.isWalkable()) {
			return false;
		}
		BlockPos blockPos = node.getBlockpos();
		if(isPassable(TeleportUtils.getBlock(blockPos.down()))) {
			return false;
		}
		for(int i = 1; i < 9; i++) {
			BlockPos pos = new BlockPos(blockPos.getX(), blockPos.getY() - i, blockPos.getZ());
			if(isPassable(TeleportUtils.getBlock(pos)) && i < 8
					&& isPassable(TeleportUtils.getBlock(pos.up()))) {
				node.setVclipPosDown(pos);
				return true;
			}
		}
		return false;
	}
	
	public static boolean isVclipableUp(Node node) {
		if(!node.isWalkable()) {
			return false;
		}
		BlockPos blockPos = node.getBlockpos();
		if(isPassable(TeleportUtils.getBlock(blockPos.up()))) {
			return false;
		}
		for(int i = 1; i < 9; i++) {
			BlockPos pos = new BlockPos(blockPos.getX(), blockPos.getY() + i, blockPos.getZ());
			if(isPassable(TeleportUtils.getBlock(pos)) && i < 8
					&& isPassable(TeleportUtils.getBlock(pos.up()))) {
				node.setVclipPosUp(pos);
				return true;
			}
		}
		return false;
	}
	
}
