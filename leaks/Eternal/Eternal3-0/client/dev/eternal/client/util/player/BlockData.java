package dev.eternal.client.util.player;

import dev.eternal.client.util.IMinecraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A data class used for things such as Scaffold, Or block placement automation.
 *
 * @param block
 * @param pos
 * @param direction
 */

public record BlockData(Block block, BlockPos pos, EnumFacing direction) implements IMinecraft {

  private static final List<Material> invalidMaterials = List.of(Material.vine, Material.air, Material.lava,
      Material.water, Material.cake, Material.circuits, Material.anvil, Material.carpet);

  /**
   * Instantiates a {@link BlockData} instance
   *
   * @param block     The block at the {@link BlockPos} specified in the second parameter.
   * @param pos       The block position to place at.
   * @param direction The direction to place at.
   */
  public BlockData {
  }

  /**
   * Gets the block that is at the {@link BlockPos} specified.
   *
   * @return The block that is at the specified {@link BlockPos},
   * most likely an instance of {@link BlockAir}
   */
  @Override
  public Block block() {
    return block;
  }

  /**
   * Gets the position used to place the block.
   *
   * @return The {@link BlockPos} to place at
   */
  @Override
  public BlockPos pos() {
    return pos;
  }

  /**
   * Gets the direction used to place the block.
   *
   * @return The {@link EnumFacing} to place at
   */
  @Override
  public EnumFacing direction() {
    return direction;
  }

  /**
   * Incomplete method that returns either null (if no near-by {@link Block} is found),
   * or a {@link BlockData} instance containing data needed to place a block at the {@link BlockPos} provided
   *
   * @param blockPos The position to place at.
   * @return Either {@code null} or a {@link BlockData} instance
   */
  @Nullable
  public static BlockData of(BlockPos blockPos) {
    for (EnumFacing enumFacing : EnumFacing.VALUES) {
      if (enumFacing == EnumFacing.UP)
        continue;
      BlockPos offsetPosition = blockPos.offset(enumFacing);
      WorldClient worldClient = mc.theWorld;
      IBlockState blockState = worldClient.getBlockState(offsetPosition);
      if (!invalidMaterials.contains(blockState.getBlock().getMaterial()) && blockState.getBlock().isFullBlock()) {
        return new BlockData(blockState.getBlock(), offsetPosition, enumFacing.getOpposite());
      }
    }
    return null;
  }

}