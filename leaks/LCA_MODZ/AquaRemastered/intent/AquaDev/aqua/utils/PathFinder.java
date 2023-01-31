package intent.AquaDev.aqua.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class PathFinder {
   public static Vec3 startPos;
   public static Vec3 endPos;
   private ArrayList<Vec3> path = new ArrayList<>();
   private ArrayList<PathFinder.Node> nodes = new ArrayList<>();
   private ArrayList<PathFinder.Node> activeNodes = new ArrayList<>();
   private boolean nearest = true;
   private static Vec3[] coords = new Vec3[]{
      new Vec3(1.0, 0.0, 0.0), new Vec3(-1.0, 0.0, 0.0), new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, 0.0, -1.0), new Vec3(0.0, 1.0, 0.0), new Vec3(0.0, -1.0, 0.0)
   };

   public PathFinder(Vec3 startPos, Vec3 endPos) {
      PathFinder.startPos = startPos;
      PathFinder.endPos = endPos;
   }

   public ArrayList<Vec3> getPath() {
      return this.path;
   }

   public void calculatePath(int iterations) {
      this.path.clear();
      this.activeNodes.clear();
      ArrayList<Vec3> initPath = new ArrayList<>();
      initPath.add(startPos);
      this.activeNodes.add(new PathFinder.Node(startPos, null, initPath, startPos.squareDistanceTo(endPos), 0.0, 0.0));

      label29:
      for(int i = 0; i < iterations; ++i) {
         Collections.sort(this.activeNodes, new PathFinder.CompareNode());
         Iterator var4 = new ArrayList<>(this.activeNodes).iterator();
         if (var4.hasNext()) {
            PathFinder.Node node = (PathFinder.Node)var4.next();
            this.activeNodes.remove(node);
            this.nodes.add(node);

            for(Vec3 direction : coords) {
               Vec3 loc = node.getLoc().add(direction);
               if (checkPositionValidity(loc, false) && this.addHub(node, loc, 0.0)) {
                  break label29;
               }
            }
         }
      }

      Collections.sort(this.nodes, new PathFinder.CompareNode());
      this.path = this.nodes.get(0).getPath();
   }

   public static boolean checkPositionValidity(Vec3 loc, boolean checkGround) {
      return checkPositionValidity((int)loc.xCoord, (int)loc.yCoord, (int)loc.zCoord, checkGround);
   }

   public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
      BlockPos block1 = new BlockPos(x, y, z);
      BlockPos block2 = new BlockPos(x, y + 1, z);
      BlockPos block3 = new BlockPos(x, y - 1, z);
      return !isBlockSolid(block1)
         && !isBlockSolid(block2)
         && (isBlockSolid(block3) || !checkGround)
         && isSafeToWalkOn(block3)
         && block1.getBlock() == Blocks.air;
   }

   private static boolean isBlockSolid(BlockPos block) {
      return block.getBlock().isFullBlock()
         || block.getBlock() instanceof BlockSlab
         || block.getBlock() instanceof BlockStairs
         || block.getBlock() instanceof BlockCactus
         || block.getBlock() instanceof BlockChest
         || block.getBlock() instanceof BlockEnderChest
         || block.getBlock() instanceof BlockSkull
         || block.getBlock() instanceof BlockPane
         || block.getBlock() instanceof BlockFence
         || block.getBlock() instanceof BlockWall
         || block.getBlock() instanceof BlockGlass
         || block.getBlock() instanceof BlockPistonBase
         || block.getBlock() instanceof BlockPistonExtension
         || block.getBlock() instanceof BlockPistonMoving
         || block.getBlock() instanceof BlockStainedGlass
         || block.getBlock() instanceof BlockTrapDoor
         || !(block.getBlock() instanceof BlockAir);
   }

   private static boolean isSafeToWalkOn(BlockPos block) {
      return !(Minecraft.getMinecraft().theWorld.getBlockState(block) instanceof BlockFence)
         && !(Minecraft.getMinecraft().theWorld.getBlockState(block) instanceof BlockWall);
   }

   public PathFinder.Node isHubExisting(Vec3 loc) {
      for(PathFinder.Node hub : this.nodes) {
         if (hub.getLoc().xCoord == loc.xCoord && hub.getLoc().yCoord == loc.yCoord && hub.getLoc().zCoord == loc.zCoord) {
            return hub;
         }
      }

      for(PathFinder.Node hub : this.activeNodes) {
         if (hub.getLoc().xCoord == loc.xCoord && hub.getLoc().yCoord == loc.yCoord && hub.getLoc().zCoord == loc.zCoord) {
            return hub;
         }
      }

      return null;
   }

   public boolean addHub(PathFinder.Node parent, Vec3 loc, double cost) {
      PathFinder.Node existingNode = this.isHubExisting(loc);
      double totalCost = cost;
      if (parent != null) {
         totalCost = cost + parent.getFCost();
      }

      if (existingNode == null) {
         if (loc.xCoord == endPos.xCoord && loc.yCoord == endPos.yCoord && loc.zCoord == endPos.zCoord || loc.squareDistanceTo(endPos) <= 3.0) {
            this.path.clear();
            this.path = parent.getPath();
            this.path.add(loc);
            return true;
         }

         ArrayList<Vec3> path = new ArrayList<>(parent.getPath());
         path.add(loc);
         this.activeNodes.add(new PathFinder.Node(loc, parent, path, loc.squareDistanceTo(endPos), cost, totalCost));
      }

      return false;
   }

   public class CompareNode implements Comparator<PathFinder.Node> {
      public int compare(PathFinder.Node o1, PathFinder.Node o2) {
         return (int)(o1.getSquareDistanceToFromTarget() + o1.getFCost() - (o2.getSquareDistanceToFromTarget() + o2.getFCost()));
      }
   }

   class Node {
      Vec3 loc = null;
      PathFinder.Node parent = null;
      ArrayList<Vec3> path;
      double squareDistanceToFromTarget;
      double hCost;
      double fCost;

      public Node(Vec3 loc, PathFinder.Node parent, ArrayList<Vec3> path, double squareDistanceToFromTarget, double cost, double totalCost) {
         this.loc = loc;
         this.parent = parent;
         this.path = path;
         this.squareDistanceToFromTarget = squareDistanceToFromTarget;
         this.hCost = cost;
         this.fCost = totalCost;
      }

      public Vec3 getLoc() {
         return this.loc;
      }

      public PathFinder.Node getParent() {
         return this.parent;
      }

      public ArrayList<Vec3> getPath() {
         return this.path;
      }

      public double getSquareDistanceToFromTarget() {
         return this.squareDistanceToFromTarget;
      }

      public double getHCost() {
         return this.hCost;
      }

      public void setLoc(Vec3 loc) {
         this.loc = loc;
      }

      public void setParent(PathFinder.Node parent) {
         this.parent = parent;
      }

      public void setPath(ArrayList<Vec3> path) {
         this.path = path;
      }

      public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
         this.squareDistanceToFromTarget = squareDistanceToFromTarget;
      }

      public void setHCost(double hCost) {
         this.hCost = hCost;
      }

      public double getFCost() {
         return this.fCost;
      }

      public void setFCost(double fCost) {
         this.fCost = fCost;
      }
   }
}
