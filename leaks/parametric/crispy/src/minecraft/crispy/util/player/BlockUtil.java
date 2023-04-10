package crispy.util.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUtil {
    public static final List<Block> blackList = Arrays.asList(Blocks.ender_chest, Blocks.chest, Blocks.trapped_chest, Blocks.crafting_table, Blocks.anvil, Blocks.brewing_stand, Blocks.hopper,
            Blocks.dropper, Blocks.dispenser, Blocks.trapdoor, Blocks.enchanting_table);
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] getDirectionToBlock(int var0, int var1, int var2, EnumFacing var3) {
        EntityEgg var4 = new EntityEgg(Minecraft.theWorld);
        var4.posX = (double) var0 + 0.5D;
        var4.posY = (double) var1 + 0.5D;
        var4.posZ = (double) var2 + 0.5D;
        var4.posX += (double) var3.getDirectionVec().getX() * 0.25D;
        var4.posY += (double) var3.getDirectionVec().getY() * 0.25D;
        var4.posZ += (double) var3.getDirectionVec().getZ() * 0.25D;
        return getDirectionToEntity(var4);
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        circleblocks.add(new BlockPos(x, y + plus_y, z));
                    }
                }
            }
        }
        return circleblocks;
    }


    private static float[] getDirectionToEntity(Entity var0) {
        return new float[]{getYaw(var0) + mc.thePlayer.rotationYaw, getPitch(var0) + mc.thePlayer.rotationPitch};
    }

    public static float[] getRotationNeededForBlock(EntityPlayer paramEntityPlayer, BlockPos pos) {
        double d1 = pos.getX() - paramEntityPlayer.posX;
        double d2 = pos.getY() + 0.5 - (paramEntityPlayer.posY + paramEntityPlayer.getEyeHeight());
        double d3 = pos.getZ() - paramEntityPlayer.posZ;
        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
        float f1 = (float) (Math.atan2(d3, d1) * 180.0D / Math.PI) - 90.0F;
        float f2 = (float) -(Math.atan2(d2, d4) * 180.0D / Math.PI);
        return new float[]{f1, f2};
    }

    public static Block getState(BlockPos pos) {
        return Minecraft.theWorld.getBlockState(pos).getBlock();
    }

    public static AxisAlignedBB getBoundingBox(BlockPos pos) {
        return new AxisAlignedBB(pos);
    }


    // this method is N3xuz_DK's I believe. credits to him.
    public static boolean isOnLiquid() {
        if (mc.thePlayer == null) return false;
        boolean onLiquid = false;
        final int y = (int) mc.thePlayer.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block = Minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && block.getMaterial() != Material.air) {
                    if (!(block instanceof BlockLiquid)) return false;
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static boolean isOnLiquid(double dist) {
        boolean onLiquid = Minecraft.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - dist, mc.thePlayer.posZ)).getBlock().getMaterial().isLiquid();

        return onLiquid;
    }

    public static boolean isTotalOnLiquid(double dist) {
        for (double x = mc.thePlayer.boundingBox.minX; x < mc.thePlayer.boundingBox.maxX; x += 0.01f) {

            for (double z = mc.thePlayer.boundingBox.minZ; z < mc.thePlayer.boundingBox.maxZ; z += 0.01f) {
                Block block = Minecraft.theWorld.getBlockState(new BlockPos(x, mc.thePlayer.posY - dist, z)).getBlock();
                if (!(block instanceof BlockLiquid) && !(block instanceof BlockAir)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void placeHeldItemUnderPlayer() {
        final BlockPos blockPos = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY - 1, Minecraft.getMinecraft().thePlayer.posZ);
        final Vec3 vec = new Vec3(blockPos).addVector(0.4F, 0.4F, 0.4F);
        Minecraft.getMinecraft().playerController.onPlayerRightClick(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, null, blockPos, EnumFacing.UP,
                vec.scale(0.4));
    }
    public static float getYaw(Entity var0) {
        double var1 = var0.posX - mc.thePlayer.posX;
        double var3 = var0.posZ - mc.thePlayer.posZ;
        double var5;

        if (var3 < 0.0D && var1 < 0.0D) {
            var5 = 90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else if (var3 < 0.0D && var1 > 0.0D) {
            var5 = -90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else {
            var5 = Math.toDegrees(-Math.atan(var1 / var3));
        }

        return MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float) var5));
    }

    public static float getPitch(Entity var0) {
        double var1 = var0.posX - mc.thePlayer.posX;
        double var3 = var0.posZ - mc.thePlayer.posZ;
        double var5 = var0.posY - 1.6D + (double) var0.getEyeHeight() - mc.thePlayer.posY;
        double var7 = MathHelper.sqrt_double(var1 * var1 + var3 * var3);
        double var9 = -Math.toDegrees(Math.atan(var5 / var7));
        return -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float) var9);
    }

    public static ValidResult valid(BlockPos pos) {
        // There are no entities to block placement,
        if (!Minecraft.theWorld.checkNoEntityCollision(new AxisAlignedBB(pos)))
            return ValidResult.NoEntityCollision;

        if (Minecraft.theWorld.getBlockState(pos.down()).getBlock() == Blocks.water)


            if (!checkForNeighbours(pos))
                return ValidResult.NoNeighbors;

        IBlockState l_State = Minecraft.theWorld.getBlockState(pos);

        if (l_State.getBlock() == Blocks.air) {
            final BlockPos[] l_Blocks =
                    {pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};

            for (BlockPos l_Pos : l_Blocks) {
                IBlockState l_State2 = Minecraft.theWorld.getBlockState(l_Pos);

                if (l_State2.getBlock() == Blocks.air)
                    continue;

                for (final EnumFacing side : EnumFacing.values()) {
                    final BlockPos neighbor = pos.offset(side);

                    boolean l_IsWater = Minecraft.theWorld.getBlockState(neighbor).getBlock() == Blocks.water;

                    if (Minecraft.theWorld.getBlockState(neighbor).getBlock().canCollideCheck(Minecraft.theWorld.getBlockState(neighbor), false)
                            || (l_IsWater)) {
                        return ValidResult.Ok;
                    }
                }
            }

            return ValidResult.NoNeighbors;
        }

        return ValidResult.AlreadyBlockThere;
        /*
         * final BlockPos[] l_Blocks = { pos.north(), pos.south(), pos.east(), pos.west(), pos.up() };
         *
         * for (BlockPos l_Pos : l_Blocks) { IBlockState l_State = mc.world.getBlockState(l_Pos);
         *
         * if (l_State.getBlock() == Blocks.AIR) continue;
         *
         * return ValidResult.Ok; }
         *
         * return ValidResult.NoNeighbors;
         */
    }

    public static boolean checkForNeighbours(BlockPos blockPos) {
        // check if we don't have a block adjacent to blockpos
        if (!hasNeighbour(blockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is
            // going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }

                if (side == EnumFacing.UP && Minecraft.theWorld.getBlockState(blockPos).getBlock() == Blocks.water) {
                    if (Minecraft.theWorld.getBlockState(blockPos.up()).getBlock() == Blocks.air)
                        return true;
                }
            }
            return false;
        }
        return true;
    }

    private static Vec3 getEyesPos() {
        return new Vec3(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);
    }

    public static float[] getLegitRotations(Vec3 vec) {
        Vec3 eyesPos = getEyesPos();

        double diffX = vec.xCoord - eyesPos.xCoord;
        double diffY = vec.yCoord - eyesPos.yCoord;
        double diffZ = vec.zCoord - eyesPos.zCoord;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]
                {Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw),
                        Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch)};
    }

    public static boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if (!Minecraft.theWorld.getBlockState(neighbour).getBlock().getMaterial().isReplaceable()) {
                return true;
            }
        }
        return false;
    }

    public static PlaceResult place(BlockPos pos, float p_Distance, boolean p_Rotate, boolean p_UseSlabRule) {
        return place(pos, p_Distance, p_Rotate, p_UseSlabRule, false);
    }

    public static PlaceResult place(BlockPos pos, float p_Distance, boolean p_Rotate, boolean p_UseSlabRule, boolean packetSwing) {
        IBlockState l_State = Minecraft.theWorld.getBlockState(pos);

        boolean l_Replaceable = l_State.getBlock().getMaterial().isReplaceable();

        boolean l_IsSlabAtBlock = l_State.getBlock() instanceof BlockSlab;

        if (!l_Replaceable && !l_IsSlabAtBlock)
            return PlaceResult.NotReplaceable;
        if (!checkForNeighbours(pos))
            return PlaceResult.Neighbors;

        if (!l_IsSlabAtBlock) {
            ValidResult l_Result = valid(pos);

            if (l_Result != ValidResult.Ok && !l_Replaceable)
                return PlaceResult.CantPlace;
        }

        if (p_UseSlabRule) {
            if (l_IsSlabAtBlock && !l_State.getBlock().isFullCube())
                return PlaceResult.CantPlace;
        }

        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();

            boolean l_IsWater = Minecraft.theWorld.getBlockState(neighbor).getBlock() == Blocks.water;

            if (Minecraft.theWorld.getBlockState(neighbor).getBlock().canCollideCheck(Minecraft.theWorld.getBlockState(neighbor), false)
                    || (l_IsWater)) {
                final Vec3 hitVec = new Vec3(neighbor).add(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.distanceTo(hitVec) <= p_Distance) {
                    final Block neighborPos = Minecraft.theWorld.getBlockState(neighbor).getBlock();

                    final boolean activated = neighborPos.onBlockActivated(Minecraft.theWorld, pos, Minecraft.theWorld.getBlockState(pos), mc.thePlayer, side, 0, 0, 0);

                    if (blackList.contains(neighborPos) || activated) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    }

                    boolean l_Result2 = mc.playerController.onPlayerRightClick(mc.thePlayer, Minecraft.theWorld, mc.thePlayer.getCurrentEquippedItem(), neighbor, side2, hitVec);
                    /*
                    I'll add it later
                     */
                    /*if (l_Result2 != EnumActionResult.FAIL)
                    {

                        if (packetSwing)
                            mc.thePlayer.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        else
                            mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                        if (activated)
                        {
                            mc.thePlayer.connection.sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.STOP_SNEAKING));
                        }
                        return PlaceResult.Placed;
                    }
                    */
                    if (l_Result2) {
                        if (activated) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                        }
                    } else {
                        return PlaceResult.CantPlace;
                    }
                }
            }
        }
        return PlaceResult.CantPlace;
    }

    public enum PlaceResult {
        NotReplaceable,
        Neighbors,
        CantPlace,
        Placed,
    }

    public enum ValidResult {
        NoEntityCollision,
        AlreadyBlockThere,
        NoNeighbors,
        Ok,
    }
}