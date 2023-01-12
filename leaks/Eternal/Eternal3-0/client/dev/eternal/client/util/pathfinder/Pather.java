package dev.eternal.client.util.pathfinder;

import dev.eternal.client.util.world.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.util.*;

/**
 * A* pathfinding algorithm class
 *
 * @author Eternal
 */
public class Pather {

  private static int xS, yS, zS;
  private static int blocksScanned;
  private static boolean reachedGoal;
  private static Node[][][] nodes;
  private static Node start, end, current;

  private static PriorityQueue<Node> queue;
  private static List<Node> path;

  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * @param blockPos The desination
   * @return A {@link PathInfo} class containing the amount of blocks scanned and the path to the
   * blockPos if one was found
   * @author Eternal
   */
  public static PathInfo getPath(BlockPos blockPos) {

    int xDiff = (int) (mc.thePlayer.posX - blockPos.getX());
    int yDiff = (int) (mc.thePlayer.posY - blockPos.getY());
    int zDiff = (int) (mc.thePlayer.posZ - blockPos.getZ());

    xS = Math.abs(xDiff) * 2 + 3;
    yS = Math.abs(yDiff) * 2 + 3;
    zS = Math.abs(zDiff) * 2 + 3;

    reachedGoal = false;
    blocksScanned = 0;
    nodes = new Node[xS][yS][zS];
    queue = new PriorityQueue<>(xS * yS * zS + 69, Comparator.reverseOrder());
    path = new ArrayList<>();

    int x = 0, y = 0, z = 0;
    while (x < xS && y < yS && z < zS) {
      Node node =
          new Node(
              new BlockPos(mc.thePlayer).add(x - xS / 2, y - yS / 2, z - zS / 2),
              new BlockPos(x, y, z));

      //      node.setSolid(!WorldUtil.isStandAble(node.getBlockPos()));
      //      node.setSolid(mc.theWorld.getBlockState(node.getBlockPos()).getBlock() != Blocks.air);

      nodes[x][y][z] = node;

      x++;
      if (x == xS) {
        x = 0;
        z++;
      }
      if (z == zS) {
        z = 0;
        y++;
      }
    }
    current = start = nodes[xS / 2][yS / 2][zS / 2];
    end = nodes[xS / 2 - xDiff][yS / 2 - yDiff][zS / 2 - zDiff];
    setCostOnNodes();
    search();
    Collections.reverse(path);
    return new PathInfo(blocksScanned, path);
  }

  private static void search() {
    while (!reachedGoal && current != null) {
      BlockPos blockPos = current.arrayPos();

      int x = blockPos.getX();
      int y = blockPos.getY();
      int z = blockPos.getZ();

      if (x + 1 < xS) openNode(nodes[x + 1][y][z]);
      if (y + 1 < yS) openNode(nodes[x][y + 1][z]);
      if (z + 1 < zS) openNode(nodes[x][y][z + 1]);

      if (x - 1 > 0) openNode(nodes[x - 1][y][z]);
      if (y - 1 > 0) openNode(nodes[x][y - 1][z]);
      if (z - 1 > 0) openNode(nodes[x][y][z - 1]);

      if (current.blockPos().equals(end.blockPos())) {
        reachedGoal = true;
        backTrace();
      } else {
        current = queue.poll();
      }
    }
  }

  private static void backTrace() {
    Node current = end;
    while (current != start) {
      current = current.parent();
      if (current != start) {
        current.path(true);
        path.add(current);
      }
    }
  }

  private static void openNode(Node node) {
    if (!node.open() && !node.checked() && !node.solid()) {
      blocksScanned++;
      node.open(true);
      queue.add(node);
      node.parent(current);
    }
  }

  private static void setCostOnNodes() {
    // Java moment
    Arrays.asList(nodes)
        .forEach(
            nodes1 ->
                Arrays.asList(nodes1)
                    .forEach(nodes2 -> Arrays.asList(nodes2).forEach(Pather::getCost)));
  }

  private static void getCost(Node node) {
    int xDistance = Math.abs(node.blockPos().getX() - start.blockPos().getX());
    int yDistance = Math.abs(node.blockPos().getY() - start.blockPos().getY());
    int zDistance = Math.abs(node.blockPos().getZ() - start.blockPos().getZ());
    node.gCost(xDistance + yDistance + zDistance);

    xDistance = Math.abs(node.blockPos().getX() - end.blockPos().getX());
    yDistance = Math.abs(node.blockPos().getY() - end.blockPos().getY());
    zDistance = Math.abs(node.blockPos().getZ() - end.blockPos().getZ());

    int betterPathCost = 0;
    for (int i = 1; i < 10; i++) {
      if (WorldUtil.canCollide(node.blockPos())) {
        betterPathCost = i * 2;
        break;
      }
    }

    if (!WorldUtil.isStandAble(node.blockPos())) betterPathCost += 10000;

    node.hCost(
        xDistance + yDistance + zDistance + betterPathCost /*
            + (mc.theWorld.getBlockState(node.getBlockPos().add(0, -1, 0)) instanceof BlockAir ? 0 : 55)
            + (mc.theWorld.getBlockState(node.getBlockPos()).getBlock() == Blocks.air ? 0 : 55)*/
        /*+ ((isTraversable(node.getBlockPos().add(0, -1, 0)))
        ? resistanceS.getValueI()
        : 0)
        + ((isTraversable(node.getBlockPos()))
        ? 0
        : resistanceB.getValueI())
        + (isNextToBlock(node.getBlockPos()) ? resistanceS.getValueI() : 0)*/);

    node.fCost(node.gCost() + node.hCost());
  }
}