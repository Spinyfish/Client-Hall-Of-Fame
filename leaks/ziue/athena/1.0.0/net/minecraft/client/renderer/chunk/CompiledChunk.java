package net.minecraft.client.renderer.chunk;

import java.util.*;
import net.minecraft.tileentity.*;
import net.minecraft.client.renderer.*;
import com.google.common.collect.*;
import net.minecraft.util.*;

public class CompiledChunk
{
    public static final CompiledChunk DUMMY;
    private final boolean[] layersUsed;
    private final boolean[] layersStarted;
    private boolean empty;
    private final List<TileEntity> tileEntities;
    private SetVisibility setVisibility;
    private WorldRenderer.State state;
    
    public CompiledChunk() {
        this.layersUsed = new boolean[EnumWorldBlockLayer.values().length];
        this.layersStarted = new boolean[EnumWorldBlockLayer.values().length];
        this.empty = true;
        this.tileEntities = (List<TileEntity>)Lists.newArrayList();
        this.setVisibility = new SetVisibility();
    }
    
    public boolean isEmpty() {
        return this.empty;
    }
    
    protected void setLayerUsed(final EnumWorldBlockLayer layer) {
        this.empty = false;
        this.layersUsed[layer.ordinal()] = true;
    }
    
    public boolean isLayerEmpty(final EnumWorldBlockLayer layer) {
        return !this.layersUsed[layer.ordinal()];
    }
    
    public void setLayerStarted(final EnumWorldBlockLayer layer) {
        this.layersStarted[layer.ordinal()] = true;
    }
    
    public boolean isLayerStarted(final EnumWorldBlockLayer layer) {
        return this.layersStarted[layer.ordinal()];
    }
    
    public List<TileEntity> getTileEntities() {
        return this.tileEntities;
    }
    
    public void addTileEntity(final TileEntity tileEntityIn) {
        this.tileEntities.add(tileEntityIn);
    }
    
    public boolean isVisible(final EnumFacing facing, final EnumFacing facing2) {
        return this.setVisibility.isVisible(facing, facing2);
    }
    
    public void setVisibility(final SetVisibility visibility) {
        this.setVisibility = visibility;
    }
    
    public WorldRenderer.State getState() {
        return this.state;
    }
    
    public void setState(final WorldRenderer.State stateIn) {
        this.state = stateIn;
    }
    
    static {
        DUMMY = new CompiledChunk() {
            @Override
            protected void setLayerUsed(final EnumWorldBlockLayer layer) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void setLayerStarted(final EnumWorldBlockLayer layer) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean isVisible(final EnumFacing facing, final EnumFacing facing2) {
                return false;
            }
        };
    }
}
