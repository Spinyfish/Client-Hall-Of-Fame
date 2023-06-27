package net.minecraft.client.renderer.chunk;

import java.util.concurrent.locks.*;
import net.minecraft.client.renderer.*;
import com.google.common.collect.*;
import java.util.*;

public class ChunkCompileTaskGenerator
{
    private final RenderChunk renderChunk;
    private final ReentrantLock lock;
    private final List<Runnable> listFinishRunnables;
    private final Type type;
    private RegionRenderCacheBuilder regionRenderCacheBuilder;
    private CompiledChunk compiledChunk;
    private Status status;
    private boolean finished;
    
    public ChunkCompileTaskGenerator(final RenderChunk renderChunkIn, final Type typeIn) {
        this.lock = new ReentrantLock();
        this.listFinishRunnables = (List<Runnable>)Lists.newArrayList();
        this.status = Status.PENDING;
        this.renderChunk = renderChunkIn;
        this.type = typeIn;
    }
    
    public Status getStatus() {
        return this.status;
    }
    
    public RenderChunk getRenderChunk() {
        return this.renderChunk;
    }
    
    public CompiledChunk getCompiledChunk() {
        return this.compiledChunk;
    }
    
    public void setCompiledChunk(final CompiledChunk compiledChunkIn) {
        this.compiledChunk = compiledChunkIn;
    }
    
    public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
        return this.regionRenderCacheBuilder;
    }
    
    public void setRegionRenderCacheBuilder(final RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
        this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
    }
    
    public void setStatus(final Status statusIn) {
        this.lock.lock();
        try {
            this.status = statusIn;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void finish() {
        this.lock.lock();
        try {
            if (this.type == Type.REBUILD_CHUNK && this.status != Status.DONE) {
                this.renderChunk.setNeedsUpdate(true);
            }
            this.finished = true;
            this.status = Status.DONE;
            for (final Runnable runnable : this.listFinishRunnables) {
                runnable.run();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addFinishRunnable(final Runnable p_178539_1_) {
        this.lock.lock();
        try {
            this.listFinishRunnables.add(p_178539_1_);
            if (this.finished) {
                p_178539_1_.run();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public ReentrantLock getLock() {
        return this.lock;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public boolean isFinished() {
        return this.finished;
    }
    
    public enum Status
    {
        PENDING, 
        COMPILING, 
        UPLOADING, 
        DONE;
    }
    
    public enum Type
    {
        REBUILD_CHUNK, 
        RESORT_TRANSPARENCY;
    }
}
