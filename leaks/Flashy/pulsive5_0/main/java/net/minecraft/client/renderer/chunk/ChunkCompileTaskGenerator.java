package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ChunkCompileTaskGenerator
{
    private final RenderChunk renderChunk;
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Runnable> listFinishRunnables = Lists.<Runnable>newArrayList();
    private final Type type;
    private RegionRenderCacheBuilder regionRenderCacheBuilder;
    private CompiledChunk compiledChunk;
    private Status status = Status.PENDING;
    private boolean finished;

    public ChunkCompileTaskGenerator(RenderChunk renderChunkIn, Type typeIn)
    {
        this.renderChunk = renderChunkIn;
        this.type = typeIn;
    }

    public Status getStatus()
    {
        return this.status;
    }

    public RenderChunk getRenderChunk()
    {
        return this.renderChunk;
    }

    public CompiledChunk getCompiledChunk()
    {
        return this.compiledChunk;
    }

    public void setCompiledChunk(CompiledChunk compiledChunkIn)
    {
        this.compiledChunk = compiledChunkIn;
    }

    public RegionRenderCacheBuilder getRegionRenderCacheBuilder()
    {
        return this.regionRenderCacheBuilder;
    }

    public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilderIn)
    {
        this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
    }

    public void setStatus(Status statusIn)
    {
        this.lock.lock();

        try
        {
            this.status = statusIn;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public void finish()
    {
        this.lock.lock();

        try
        {
            if (this.type == Type.REBUILD_CHUNK && this.status != Status.DONE)
            {
                this.renderChunk.setNeedsUpdate(true);
            }

            this.finished = true;
            this.status = Status.DONE;

            for (Runnable runnable : this.listFinishRunnables)
            {
                runnable.run();
            }
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public void addFinishRunnable(Runnable p_178539_1_)
    {
        this.lock.lock();

        try
        {
            this.listFinishRunnables.add(p_178539_1_);

            if (this.finished)
            {
                p_178539_1_.run();
            }
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public ReentrantLock getLock()
    {
        return this.lock;
    }

    public Type getType()
    {
        return this.type;
    }

    public boolean isFinished()
    {
        return this.finished;
    }

    public static enum Status
    {
        PENDING,
        COMPILING,
        UPLOADING,
        DONE;
    }

    public static enum Type
    {
        REBUILD_CHUNK,
        RESORT_TRANSPARENCY;
    }
}
