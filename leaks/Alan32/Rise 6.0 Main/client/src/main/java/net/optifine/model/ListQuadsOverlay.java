package net.optifine.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListQuadsOverlay {
    private final List<BakedQuad> listQuads = new ArrayList();
    private final List<IBlockState> listBlockStates = new ArrayList();
    private final List<BakedQuad> listQuadsSingle = Arrays.asList(new BakedQuad[1]);

    public void addQuad(final BakedQuad quad, final IBlockState blockState) {
        if (quad != null) {
            this.listQuads.add(quad);
            this.listBlockStates.add(blockState);
        }
    }

    public int size() {
        return this.listQuads.size();
    }

    public BakedQuad getQuad(final int index) {
        return this.listQuads.get(index);
    }

    public IBlockState getBlockState(final int index) {
        return index >= 0 && index < this.listBlockStates.size() ? this.listBlockStates.get(index) : Blocks.air.getDefaultState();
    }

    public List<BakedQuad> getListQuadsSingle(final BakedQuad quad) {
        this.listQuadsSingle.set(0, quad);
        return this.listQuadsSingle;
    }

    public void clear() {
        this.listQuads.clear();
        this.listBlockStates.clear();
    }
}
