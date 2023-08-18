/* November.lol © 2023 */
package lol.november.feature.module.impl.player.scaffold;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class Placement {

  private BlockPos pos;
  private EnumFacing facing;
  private Vec3 hitVec;

  public Placement(BlockPos pos, EnumFacing facing) {
    this.pos = pos;
    this.facing = facing;
  }

  public BlockPos getPos() {
    return pos;
  }

  public void setPos(BlockPos pos) {
    this.pos = pos;
  }

  public EnumFacing getFacing() {
    return facing;
  }

  public void setFacing(EnumFacing facing) {
    this.facing = facing;
  }

  public Vec3 getHitVec() {
    return hitVec;
  }

  public void setHitVec(Vec3 hitVec) {
    this.hitVec = hitVec;
  }
}
