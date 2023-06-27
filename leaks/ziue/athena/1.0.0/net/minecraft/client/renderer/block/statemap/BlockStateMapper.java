package net.minecraft.client.renderer.block.statemap;

import net.minecraft.block.*;
import com.google.common.collect.*;
import net.minecraft.block.state.*;
import net.minecraft.client.resources.model.*;
import com.google.common.base.*;
import java.util.*;

public class BlockStateMapper
{
    private Map<Block, IStateMapper> blockStateMap;
    private Set<Block> setBuiltInBlocks;
    
    public BlockStateMapper() {
        this.blockStateMap = (Map<Block, IStateMapper>)Maps.newIdentityHashMap();
        this.setBuiltInBlocks = (Set<Block>)Sets.newIdentityHashSet();
    }
    
    public void registerBlockStateMapper(final Block p_178447_1_, final IStateMapper p_178447_2_) {
        this.blockStateMap.put(p_178447_1_, p_178447_2_);
    }
    
    public void registerBuiltInBlocks(final Block... p_178448_1_) {
        Collections.addAll(this.setBuiltInBlocks, p_178448_1_);
    }
    
    public Map<IBlockState, ModelResourceLocation> putAllStateModelLocations() {
        final Map<IBlockState, ModelResourceLocation> map = (Map<IBlockState, ModelResourceLocation>)Maps.newIdentityHashMap();
        for (final Block block : Block.blockRegistry) {
            if (!this.setBuiltInBlocks.contains(block)) {
                map.putAll(((IStateMapper)Objects.firstNonNull((Object)this.blockStateMap.get(block), (Object)new DefaultStateMapper())).putStateModelLocations(block));
            }
        }
        return map;
    }
}
