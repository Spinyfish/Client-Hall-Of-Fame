package de.jcm.discordgamesdk.image;

import java.util.*;

public class ImageHandle
{
    private ImageType type;
    private long id;
    private int size;
    
    public ImageHandle(final ImageType type, final long id, final int size) {
        this.type = Objects.requireNonNull(type);
        this.id = id;
        if (size < 16) {
            throw new IllegalArgumentException("size is smaller than 16: " + size);
        }
        if (size > 256) {
            throw new IllegalArgumentException("size is greater than 2048: " + size);
        }
        if ((size & size - 1) != 0x0) {
            throw new IllegalArgumentException("size is not a power of 2: " + size);
        }
        this.size = size;
    }
    
    ImageHandle(final int type, final long id, final int size) {
        this(ImageType.values()[type], id, size);
    }
    
    public ImageType getType() {
        return this.type;
    }
    
    public void setType(final ImageType type) {
        this.type = Objects.requireNonNull(type);
    }
    
    public long getId() {
        return this.id;
    }
    
    public void setId(final long id) {
        this.id = id;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void setSize(final int size) {
        if ((size & size - 1) != 0x0) {
            throw new IllegalArgumentException("size is not a power of 2: " + size);
        }
        this.size = size;
    }
    
    @Override
    public String toString() {
        return "ImageHandle{type=" + this.type + ", id=" + this.id + ", size=" + this.size + '}';
    }
}
