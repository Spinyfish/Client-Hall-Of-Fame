// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.opengl;

import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;

public class EmptyImageData implements ImageData
{
    private int width;
    private int height;
    
    public EmptyImageData(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    public int getDepth() {
        return 32;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public ByteBuffer getImageBufferData() {
        return BufferUtils.createByteBuffer(this.getTexWidth() * this.getTexHeight() * 4);
    }
    
    public int getTexHeight() {
        return InternalTextureLoader.get2Fold(this.height);
    }
    
    public int getTexWidth() {
        return InternalTextureLoader.get2Fold(this.width);
    }
    
    public int getWidth() {
        return this.width;
    }
}
