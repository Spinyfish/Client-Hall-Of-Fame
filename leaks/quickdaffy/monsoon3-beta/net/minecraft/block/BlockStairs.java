/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs
extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<EnumHalf> HALF = PropertyEnum.create("half", EnumHalf.class);
    public static final PropertyEnum<EnumShape> SHAPE = PropertyEnum.create("shape", EnumShape.class);
    private static final int[][] field_150150_a = new int[][]{{4, 5}, {5, 7}, {6, 7}, {4, 6}, {0, 1}, {1, 3}, {2, 3}, {0, 2}};
    private final Block modelBlock;
    private final IBlockState modelState;
    private boolean hasRaytraced;
    private int rayTracePass;

    protected BlockStairs(IBlockState modelState) {
        super(modelState.getBlock().blockMaterial);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, EnumHalf.BOTTOM).withProperty(SHAPE, EnumShape.STRAIGHT));
        this.modelBlock = modelState.getBlock();
        this.modelState = modelState;
        this.setHardness(this.modelBlock.blockHardness);
        this.setResistance(this.modelBlock.blockResistance / 3.0f);
        this.setStepSound(this.modelBlock.stepSound);
        this.setLightOpacity(255);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        if (this.hasRaytraced) {
            this.setBlockBounds(0.5f * (float)(this.rayTracePass % 2), 0.5f * (float)(this.rayTracePass / 4 % 2), 0.5f * (float)(this.rayTracePass / 2 % 2), 0.5f + 0.5f * (float)(this.rayTracePass % 2), 0.5f + 0.5f * (float)(this.rayTracePass / 4 % 2), 0.5f + 0.5f * (float)(this.rayTracePass / 2 % 2));
        } else {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    public void setBaseCollisionBounds(IBlockAccess worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getValue(HALF) == EnumHalf.TOP) {
            this.setBlockBounds(0.0f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f);
        } else {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
        }
    }

    public static boolean isBlockStairs(Block blockIn) {
        return blockIn instanceof BlockStairs;
    }

    public static boolean isSameStair(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return BlockStairs.isBlockStairs(block) && iblockstate.getValue(HALF) == state.getValue(HALF) && iblockstate.getValue(FACING) == state.getValue(FACING);
    }

    public int func_176307_f(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState iblockstate4;
        Block block3;
        boolean flag;
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = iblockstate.getValue(FACING);
        EnumHalf blockstairs$enumhalf = iblockstate.getValue(HALF);
        boolean bl = flag = blockstairs$enumhalf == EnumHalf.TOP;
        if (enumfacing == EnumFacing.EAST) {
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.east());
            Block block = iblockstate1.getBlock();
            if (BlockStairs.isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF)) {
                EnumFacing enumfacing1 = iblockstate1.getValue(FACING);
                if (enumfacing1 == EnumFacing.NORTH && !BlockStairs.isSameStair(blockAccess, pos.south(), iblockstate)) {
                    return flag ? 1 : 2;
                }
                if (enumfacing1 == EnumFacing.SOUTH && !BlockStairs.isSameStair(blockAccess, pos.north(), iblockstate)) {
                    return flag ? 2 : 1;
                }
            }
        } else if (enumfacing == EnumFacing.WEST) {
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.west());
            Block block1 = iblockstate2.getBlock();
            if (BlockStairs.isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF)) {
                EnumFacing enumfacing2 = iblockstate2.getValue(FACING);
                if (enumfacing2 == EnumFacing.NORTH && !BlockStairs.isSameStair(blockAccess, pos.south(), iblockstate)) {
                    return flag ? 2 : 1;
                }
                if (enumfacing2 == EnumFacing.SOUTH && !BlockStairs.isSameStair(blockAccess, pos.north(), iblockstate)) {
                    return flag ? 1 : 2;
                }
            }
        } else if (enumfacing == EnumFacing.SOUTH) {
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.south());
            Block block2 = iblockstate3.getBlock();
            if (BlockStairs.isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF)) {
                EnumFacing enumfacing3 = iblockstate3.getValue(FACING);
                if (enumfacing3 == EnumFacing.WEST && !BlockStairs.isSameStair(blockAccess, pos.east(), iblockstate)) {
                    return flag ? 2 : 1;
                }
                if (enumfacing3 == EnumFacing.EAST && !BlockStairs.isSameStair(blockAccess, pos.west(), iblockstate)) {
                    return flag ? 1 : 2;
                }
            }
        } else if (enumfacing == EnumFacing.NORTH && BlockStairs.isBlockStairs(block3 = (iblockstate4 = blockAccess.getBlockState(pos.north())).getBlock()) && blockstairs$enumhalf == iblockstate4.getValue(HALF)) {
            EnumFacing enumfacing4 = iblockstate4.getValue(FACING);
            if (enumfacing4 == EnumFacing.WEST && !BlockStairs.isSameStair(blockAccess, pos.east(), iblockstate)) {
                return flag ? 1 : 2;
            }
            if (enumfacing4 == EnumFacing.EAST && !BlockStairs.isSameStair(blockAccess, pos.west(), iblockstate)) {
                return flag ? 2 : 1;
            }
        }
        return 0;
    }

    public int func_176305_g(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState iblockstate4;
        Block block3;
        boolean flag;
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = iblockstate.getValue(FACING);
        EnumHalf blockstairs$enumhalf = iblockstate.getValue(HALF);
        boolean bl = flag = blockstairs$enumhalf == EnumHalf.TOP;
        if (enumfacing == EnumFacing.EAST) {
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.west());
            Block block = iblockstate1.getBlock();
            if (BlockStairs.isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF)) {
                EnumFacing enumfacing1 = iblockstate1.getValue(FACING);
                if (enumfacing1 == EnumFacing.NORTH && !BlockStairs.isSameStair(blockAccess, pos.north(), iblockstate)) {
                    return flag ? 1 : 2;
                }
                if (enumfacing1 == EnumFacing.SOUTH && !BlockStairs.isSameStair(blockAccess, pos.south(), iblockstate)) {
                    return flag ? 2 : 1;
                }
            }
        } else if (enumfacing == EnumFacing.WEST) {
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.east());
            Block block1 = iblockstate2.getBlock();
            if (BlockStairs.isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF)) {
                EnumFacing enumfacing2 = iblockstate2.getValue(FACING);
                if (enumfacing2 == EnumFacing.NORTH && !BlockStairs.isSameStair(blockAccess, pos.north(), iblockstate)) {
                    return flag ? 2 : 1;
                }
                if (enumfacing2 == EnumFacing.SOUTH && !BlockStairs.isSameStair(blockAccess, pos.south(), iblockstate)) {
                    return flag ? 1 : 2;
                }
            }
        } else if (enumfacing == EnumFacing.SOUTH) {
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.north());
            Block block2 = iblockstate3.getBlock();
            if (BlockStairs.isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF)) {
                EnumFacing enumfacing3 = iblockstate3.getValue(FACING);
                if (enumfacing3 == EnumFacing.WEST && !BlockStairs.isSameStair(blockAccess, pos.west(), iblockstate)) {
                    return flag ? 2 : 1;
                }
                if (enumfacing3 == EnumFacing.EAST && !BlockStairs.isSameStair(blockAccess, pos.east(), iblockstate)) {
                    return flag ? 1 : 2;
                }
            }
        } else if (enumfacing == EnumFacing.NORTH && BlockStairs.isBlockStairs(block3 = (iblockstate4 = blockAccess.getBlockState(pos.south())).getBlock()) && blockstairs$enumhalf == iblockstate4.getValue(HALF)) {
            EnumFacing enumfacing4 = iblockstate4.getValue(FACING);
            if (enumfacing4 == EnumFacing.WEST && !BlockStairs.isSameStair(blockAccess, pos.west(), iblockstate)) {
                return flag ? 1 : 2;
            }
            if (enumfacing4 == EnumFacing.EAST && !BlockStairs.isSameStair(blockAccess, pos.east(), iblockstate)) {
                return flag ? 2 : 1;
            }
        }
        return 0;
    }

    public boolean func_176306_h(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState iblockstate4;
        Block block3;
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = iblockstate.getValue(FACING);
        EnumHalf blockstairs$enumhalf = iblockstate.getValue(HALF);
        boolean flag = blockstairs$enumhalf == EnumHalf.TOP;
        float f = 0.5f;
        float f1 = 1.0f;
        if (flag) {
            f = 0.0f;
            f1 = 0.5f;
        }
        float f2 = 0.0f;
        float f3 = 1.0f;
        float f4 = 0.0f;
        float f5 = 0.5f;
        boolean flag1 = true;
        if (enumfacing == EnumFacing.EAST) {
            f2 = 0.5f;
            f5 = 1.0f;
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.east());
            Block block = iblockstate1.getBlock();
            if (BlockStairs.isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF)) {
                EnumFacing enumfacing1 = iblockstate1.getValue(FACING);
                if (enumfacing1 == EnumFacing.NORTH && !BlockStairs.isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f5 = 0.5f;
                    flag1 = false;
                } else if (enumfacing1 == EnumFacing.SOUTH && !BlockStairs.isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.5f;
                    flag1 = false;
                }
            }
        } else if (enumfacing == EnumFacing.WEST) {
            f3 = 0.5f;
            f5 = 1.0f;
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.west());
            Block block1 = iblockstate2.getBlock();
            if (BlockStairs.isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF)) {
                EnumFacing enumfacing2 = iblockstate2.getValue(FACING);
                if (enumfacing2 == EnumFacing.NORTH && !BlockStairs.isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f5 = 0.5f;
                    flag1 = false;
                } else if (enumfacing2 == EnumFacing.SOUTH && !BlockStairs.isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.5f;
                    flag1 = false;
                }
            }
        } else if (enumfacing == EnumFacing.SOUTH) {
            f4 = 0.5f;
            f5 = 1.0f;
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.south());
            Block block2 = iblockstate3.getBlock();
            if (BlockStairs.isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF)) {
                EnumFacing enumfacing3 = iblockstate3.getValue(FACING);
                if (enumfacing3 == EnumFacing.WEST && !BlockStairs.isSameStair(blockAccess, pos.east(), iblockstate)) {
                    f3 = 0.5f;
                    flag1 = false;
                } else if (enumfacing3 == EnumFacing.EAST && !BlockStairs.isSameStair(blockAccess, pos.west(), iblockstate)) {
                    f2 = 0.5f;
                    flag1 = false;
                }
            }
        } else if (enumfacing == EnumFacing.NORTH && BlockStairs.isBlockStairs(block3 = (iblockstate4 = blockAccess.getBlockState(pos.north())).getBlock()) && blockstairs$enumhalf == iblockstate4.getValue(HALF)) {
            EnumFacing enumfacing4 = iblockstate4.getValue(FACING);
            if (enumfacing4 == EnumFacing.WEST && !BlockStairs.isSameStair(blockAccess, pos.east(), iblockstate)) {
                f3 = 0.5f;
                flag1 = false;
            } else if (enumfacing4 == EnumFacing.EAST && !BlockStairs.isSameStair(blockAccess, pos.west(), iblockstate)) {
                f2 = 0.5f;
                flag1 = false;
            }
        }
        this.setBlockBounds(f2, f, f4, f3, f1, f5);
        return flag1;
    }

    public boolean func_176304_i(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState iblockstate4;
        Block block3;
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = iblockstate.getValue(FACING);
        EnumHalf blockstairs$enumhalf = iblockstate.getValue(HALF);
        boolean flag = blockstairs$enumhalf == EnumHalf.TOP;
        float f = 0.5f;
        float f1 = 1.0f;
        if (flag) {
            f = 0.0f;
            f1 = 0.5f;
        }
        float f2 = 0.0f;
        float f3 = 0.5f;
        float f4 = 0.5f;
        float f5 = 1.0f;
        boolean flag1 = false;
        if (enumfacing == EnumFacing.EAST) {
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.west());
            Block block = iblockstate1.getBlock();
            if (BlockStairs.isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF)) {
                EnumFacing enumfacing1 = iblockstate1.getValue(FACING);
                if (enumfacing1 == EnumFacing.NORTH && !BlockStairs.isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.0f;
                    f5 = 0.5f;
                    flag1 = true;
                } else if (enumfacing1 == EnumFacing.SOUTH && !BlockStairs.isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f4 = 0.5f;
                    f5 = 1.0f;
                    flag1 = true;
                }
            }
        } else if (enumfacing == EnumFacing.WEST) {
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.east());
            Block block1 = iblockstate2.getBlock();
            if (BlockStairs.isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF)) {
                f2 = 0.5f;
                f3 = 1.0f;
                EnumFacing enumfacing2 = iblockstate2.getValue(FACING);
                if (enumfacing2 == EnumFacing.NORTH && !BlockStairs.isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.0f;
                    f5 = 0.5f;
                    flag1 = true;
                } else if (enumfacing2 == EnumFacing.SOUTH && !BlockStairs.isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f4 = 0.5f;
                    f5 = 1.0f;
                    flag1 = true;
                }
            }
        } else if (enumfacing == EnumFacing.SOUTH) {
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.north());
            Block block2 = iblockstate3.getBlock();
            if (BlockStairs.isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF)) {
                f4 = 0.0f;
                f5 = 0.5f;
                EnumFacing enumfacing3 = iblockstate3.getValue(FACING);
                if (enumfacing3 == EnumFacing.WEST && !BlockStairs.isSameStair(blockAccess, pos.west(), iblockstate)) {
                    flag1 = true;
                } else if (enumfacing3 == EnumFacing.EAST && !BlockStairs.isSameStair(blockAccess, pos.east(), iblockstate)) {
                    f2 = 0.5f;
                    f3 = 1.0f;
                    flag1 = true;
                }
            }
        } else if (enumfacing == EnumFacing.NORTH && BlockStairs.isBlockStairs(block3 = (iblockstate4 = blockAccess.getBlockState(pos.south())).getBlock()) && blockstairs$enumhalf == iblockstate4.getValue(HALF)) {
            EnumFacing enumfacing4 = iblockstate4.getValue(FACING);
            if (enumfacing4 == EnumFacing.WEST && !BlockStairs.isSameStair(blockAccess, pos.west(), iblockstate)) {
                flag1 = true;
            } else if (enumfacing4 == EnumFacing.EAST && !BlockStairs.isSameStair(blockAccess, pos.east(), iblockstate)) {
                f2 = 0.5f;
                f3 = 1.0f;
                flag1 = true;
            }
        }
        if (flag1) {
            this.setBlockBounds(f2, f, f4, f3, f1, f5);
        }
        return flag1;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        this.setBaseCollisionBounds(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        boolean flag = this.func_176306_h(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        if (flag && this.func_176304_i(worldIn, pos)) {
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.modelBlock.randomDisplayTick(worldIn, pos, state, rand);
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        this.modelBlock.onBlockClicked(worldIn, pos, playerIn);
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        this.modelBlock.onBlockDestroyedByPlayer(worldIn, pos, state);
    }

    @Override
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
        return this.modelBlock.getMixedBrightnessForBlock(worldIn, pos);
    }

    @Override
    public float getExplosionResistance(Entity exploder) {
        return this.modelBlock.getExplosionResistance(exploder);
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return this.modelBlock.getBlockLayer();
    }

    @Override
    public int tickRate(World worldIn) {
        return this.modelBlock.tickRate(worldIn);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        return this.modelBlock.getSelectedBoundingBox(worldIn, pos);
    }

    @Override
    public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion) {
        return this.modelBlock.modifyAcceleration(worldIn, pos, entityIn, motion);
    }

    @Override
    public boolean isCollidable() {
        return this.modelBlock.isCollidable();
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return this.modelBlock.canCollideCheck(state, hitIfLiquid);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return this.modelBlock.canPlaceBlockAt(worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.onNeighborBlockChange(worldIn, pos, this.modelState, Blocks.air);
        this.modelBlock.onBlockAdded(worldIn, pos, this.modelState);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        this.modelBlock.breakBlock(worldIn, pos, this.modelState);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
        this.modelBlock.onEntityCollidedWithBlock(worldIn, pos, entityIn);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.modelBlock.updateTick(worldIn, pos, state, rand);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        return this.modelBlock.onBlockActivated(worldIn, pos, this.modelState, playerIn, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
        this.modelBlock.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
    }

    @Override
    public MapColor getMapColor(IBlockState state) {
        return this.modelBlock.getMapColor(this.modelState);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        iblockstate = iblockstate.withProperty(FACING, placer.getHorizontalFacing()).withProperty(SHAPE, EnumShape.STRAIGHT);
        return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double)hitY <= 0.5) ? iblockstate.withProperty(HALF, EnumHalf.BOTTOM) : iblockstate.withProperty(HALF, EnumHalf.TOP);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
        MovingObjectPosition[] amovingobjectposition = new MovingObjectPosition[8];
        IBlockState iblockstate = worldIn.getBlockState(pos);
        int i = iblockstate.getValue(FACING).getHorizontalIndex();
        boolean flag = iblockstate.getValue(HALF) == EnumHalf.TOP;
        int[] aint = field_150150_a[i + (flag ? 4 : 0)];
        this.hasRaytraced = true;
        for (int j = 0; j < 8; ++j) {
            this.rayTracePass = j;
            if (Arrays.binarySearch(aint, j) >= 0) continue;
            amovingobjectposition[j] = super.collisionRayTrace(worldIn, pos, start, end);
        }
        for (int k : aint) {
            amovingobjectposition[k] = null;
        }
        MovingObjectPosition movingobjectposition1 = null;
        double d1 = 0.0;
        for (MovingObjectPosition movingobjectposition : amovingobjectposition) {
            double d0;
            if (movingobjectposition == null || !((d0 = movingobjectposition.hitVec.squareDistanceTo(end)) > d1)) continue;
            movingobjectposition1 = movingobjectposition;
            d1 = d0;
        }
        return movingobjectposition1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(HALF, (meta & 4) > 0 ? EnumHalf.TOP : EnumHalf.BOTTOM);
        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
        return iblockstate;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        if (state.getValue(HALF) == EnumHalf.TOP) {
            i |= 4;
        }
        return i |= 5 - state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (this.func_176306_h(worldIn, pos)) {
            switch (this.func_176305_g(worldIn, pos)) {
                case 0: {
                    state = state.withProperty(SHAPE, EnumShape.STRAIGHT);
                    break;
                }
                case 1: {
                    state = state.withProperty(SHAPE, EnumShape.INNER_RIGHT);
                    break;
                }
                case 2: {
                    state = state.withProperty(SHAPE, EnumShape.INNER_LEFT);
                }
            }
        } else {
            switch (this.func_176307_f(worldIn, pos)) {
                case 0: {
                    state = state.withProperty(SHAPE, EnumShape.STRAIGHT);
                    break;
                }
                case 1: {
                    state = state.withProperty(SHAPE, EnumShape.OUTER_RIGHT);
                    break;
                }
                case 2: {
                    state = state.withProperty(SHAPE, EnumShape.OUTER_LEFT);
                }
            }
        }
        return state;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING, HALF, SHAPE);
    }

    public static enum EnumShape implements IStringSerializable
    {
        STRAIGHT("straight"),
        INNER_LEFT("inner_left"),
        INNER_RIGHT("inner_right"),
        OUTER_LEFT("outer_left"),
        OUTER_RIGHT("outer_right");

        private final String name;

        private EnumShape(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public static enum EnumHalf implements IStringSerializable
    {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        private EnumHalf(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}

