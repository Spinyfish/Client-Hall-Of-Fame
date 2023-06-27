package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.creativetab.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.block.state.*;
import net.minecraft.util.*;

public class BlockSilverfish extends Block
{
    public static final PropertyEnum<EnumType> VARIANT;
    
    public BlockSilverfish() {
        super(Material.clay);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockSilverfish.VARIANT, EnumType.STONE));
        this.setHardness(0.0f);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }
    
    @Override
    public int quantityDropped(final Random random) {
        return 0;
    }
    
    public static boolean canContainSilverfish(final IBlockState blockState) {
        final Block block = blockState.getBlock();
        return blockState == Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE) || block == Blocks.cobblestone || block == Blocks.stonebrick;
    }
    
    @Override
    protected ItemStack createStackedBlock(final IBlockState state) {
        switch (state.getValue(BlockSilverfish.VARIANT)) {
            case COBBLESTONE: {
                return new ItemStack(Blocks.cobblestone);
            }
            case STONEBRICK: {
                return new ItemStack(Blocks.stonebrick);
            }
            case MOSSY_STONEBRICK: {
                return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.MOSSY.getMetadata());
            }
            case CRACKED_STONEBRICK: {
                return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.CRACKED.getMetadata());
            }
            case CHISELED_STONEBRICK: {
                return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.CHISELED.getMetadata());
            }
            default: {
                return new ItemStack(Blocks.stone);
            }
        }
    }
    
    @Override
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops")) {
            final EntitySilverfish entitysilverfish = new EntitySilverfish(worldIn);
            entitysilverfish.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
            worldIn.spawnEntityInWorld(entitysilverfish);
            entitysilverfish.spawnExplosionParticle();
        }
    }
    
    @Override
    public int getDamageValue(final World worldIn, final BlockPos pos) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        return iblockstate.getBlock().getMetaFromState(iblockstate);
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        for (final EnumType blocksilverfish$enumtype : EnumType.values()) {
            list.add(new ItemStack(itemIn, 1, blocksilverfish$enumtype.getMetadata()));
        }
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockSilverfish.VARIANT, EnumType.byMetadata(meta));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockSilverfish.VARIANT).getMetadata();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockSilverfish.VARIANT });
    }
    
    static {
        VARIANT = PropertyEnum.create("variant", EnumType.class);
    }
    
    public enum EnumType implements IStringSerializable
    {
        STONE(0, "stone") {
            @Override
            public IBlockState getModelBlock() {
                return Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE);
            }
        }, 
        COBBLESTONE(1, "cobblestone", "cobble") {
            @Override
            public IBlockState getModelBlock() {
                return Blocks.cobblestone.getDefaultState();
            }
        }, 
        STONEBRICK(2, "stone_brick", "brick") {
            @Override
            public IBlockState getModelBlock() {
                return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT);
            }
        }, 
        MOSSY_STONEBRICK(3, "mossy_brick", "mossybrick") {
            @Override
            public IBlockState getModelBlock() {
                return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
            }
        }, 
        CRACKED_STONEBRICK(4, "cracked_brick", "crackedbrick") {
            @Override
            public IBlockState getModelBlock() {
                return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
            }
        }, 
        CHISELED_STONEBRICK(5, "chiseled_brick", "chiseledbrick") {
            @Override
            public IBlockState getModelBlock() {
                return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
            }
        };
        
        private static final EnumType[] META_LOOKUP;
        private final int meta;
        private final String name;
        private final String unlocalizedName;
        
        private EnumType(final int meta, final String name) {
            this(meta, name, name);
        }
        
        private EnumType(final int meta, final String name, final String unlocalizedName) {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }
        
        public int getMetadata() {
            return this.meta;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= EnumType.META_LOOKUP.length) {
                meta = 0;
            }
            return EnumType.META_LOOKUP[meta];
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }
        
        public abstract IBlockState getModelBlock();
        
        public static EnumType forModelBlock(final IBlockState model) {
            for (final EnumType blocksilverfish$enumtype : values()) {
                if (model == blocksilverfish$enumtype.getModelBlock()) {
                    return blocksilverfish$enumtype;
                }
            }
            return EnumType.STONE;
        }
        
        static {
            META_LOOKUP = new EnumType[values().length];
            for (final EnumType blocksilverfish$enumtype : values()) {
                EnumType.META_LOOKUP[blocksilverfish$enumtype.getMetadata()] = blocksilverfish$enumtype;
            }
        }
    }
}
