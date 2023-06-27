package de.jcm.discordgamesdk;

import java.util.function.*;
import java.util.*;
import de.jcm.discordgamesdk.image.*;
import java.awt.image.*;

public class ImageManager
{
    private final long pointer;
    private final Core core;
    
    ImageManager(final long pointer, final Core core) {
        this.pointer = pointer;
        this.core = core;
    }
    
    public void fetch(final ImageHandle handle, final boolean refresh, final BiConsumer<Result, ImageHandle> callback) {
        this.core.execute(() -> this.fetch(this.pointer, handle.getType().ordinal(), handle.getId(), handle.getSize(), refresh, Objects.requireNonNull(callback)));
    }
    
    public ImageDimensions getDimensions(final ImageHandle handle) {
        final Object ret = this.core.execute(() -> this.getDimensions(this.pointer, handle.getType().ordinal(), handle.getId(), handle.getSize()));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (ImageDimensions)ret;
    }
    
    public byte[] getData(final ImageHandle handle, final ImageDimensions dimensions) {
        return this.getData(handle, dimensions.getWidth() * dimensions.getHeight() * 4);
    }
    
    public byte[] getData(final ImageHandle handle, final int length) {
        final Object ret = this.core.execute(() -> this.getData(this.pointer, handle.getType().ordinal(), handle.getId(), handle.getSize(), length));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (byte[])ret;
    }
    
    public BufferedImage getAsBufferedImage(final ImageHandle handle, final ImageDimensions dimensions) {
        final byte[] data = this.getData(handle, dimensions);
        final BufferedImage image = new BufferedImage(dimensions.getWidth(), dimensions.getHeight(), 6);
        image.getRaster().setDataElements(0, 0, dimensions.getWidth(), dimensions.getHeight(), data);
        return image;
    }
    
    public BufferedImage getAsBufferedImage(final ImageHandle handle) {
        final ImageDimensions dimensions = this.getDimensions(handle);
        return this.getAsBufferedImage(handle, dimensions);
    }
    
    private native void fetch(final long p0, final int p1, final long p2, final int p3, final boolean p4, final BiConsumer<Result, ImageHandle> p5);
    
    private native Object getDimensions(final long p0, final int p1, final long p2, final int p3);
    
    private native Object getData(final long p0, final int p1, final long p2, final int p3, final int p4);
}
