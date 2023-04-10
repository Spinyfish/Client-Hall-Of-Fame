package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class StateMap extends StateMapperBase
{
    private final IProperty name;
    private final String suffix;
    private final List ignored;

    private StateMap(IProperty name, String suffix, List ignored)
    {
        this.name = name;
        this.suffix = suffix;
        this.ignored = ignored;
    }

    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        LinkedHashMap var2 = Maps.newLinkedHashMap(state.getProperties());
        String var3;

        if (this.name == null)
        {
            var3 = ((ResourceLocation)Block.blockRegistry.getNameForObject(state.getBlock())).toString();
        }
        else
        {
            var3 = this.name.getName((Comparable)var2.remove(this.name));
        }

        if (this.suffix != null)
        {
            var3 = var3 + this.suffix;
        }

        Iterator var4 = this.ignored.iterator();

        while (var4.hasNext())
        {
            IProperty var5 = (IProperty)var4.next();
            var2.remove(var5);
        }

        return new ModelResourceLocation(var3, this.getPropertyString(var2));
    }

    StateMap(IProperty p_i46211_1_, String p_i46211_2_, List p_i46211_3_, Object p_i46211_4_)
    {
        this(p_i46211_1_, p_i46211_2_, p_i46211_3_);
    }

    public static class Builder
    {
        private IProperty name;
        private String suffix;
        private final List ignored = Lists.newArrayList();

        public StateMap.Builder withName(IProperty builderPropertyIn)
        {
            this.name = builderPropertyIn;
            return this;
        }

        public StateMap.Builder withSuffix(String builderSuffixIn)
        {
            this.suffix = builderSuffixIn;
            return this;
        }

        public StateMap.Builder ignore(IProperty ... p_178442_1_)
        {
            Collections.addAll(this.ignored, p_178442_1_);
            return this;
        }

        public StateMap build()
        {
            return new StateMap(this.name, this.suffix, this.ignored, null);
        }
    }
}
