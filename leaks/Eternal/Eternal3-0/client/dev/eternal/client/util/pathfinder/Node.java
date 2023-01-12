package dev.eternal.client.util.pathfinder;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.BlockPos;

@Getter
@Setter
public class Node implements Comparable<Node> {

  private Node parent;
  private BlockPos blockPos;
  private BlockPos arrayPos;
  private NodeType nodeType;
  private boolean open, checked, solid, path;
  private int gCost, hCost, fCost;

  public Node(BlockPos blockPos, BlockPos arrayPos) {
    this.blockPos = blockPos;
    this.arrayPos = arrayPos;
    this.nodeType = NodeType.UNDEFINED;
  }

  @Override
  public int compareTo(Node o) {
    int compare = Integer.compare(fCost, o.fCost);
    if (compare == 0) {
      compare = Integer.compare(gCost, o.gCost);
    }
    return -compare;
  }

  public enum NodeType {
    UNDEFINED,
    SCAFFOLD,
    FALL,
    JUMP,
    TOWER,
    WALK,
    SWIM
  }

}
