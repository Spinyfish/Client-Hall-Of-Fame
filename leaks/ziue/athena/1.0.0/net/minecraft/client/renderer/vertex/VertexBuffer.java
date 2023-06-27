package net.minecraft.client.renderer.vertex;

import net.minecraft.client.renderer.*;
import java.nio.*;
import org.lwjgl.opengl.*;

public class VertexBuffer
{
    private int glBufferId;
    private final VertexFormat vertexFormat;
    private int count;
    
    public VertexBuffer(final VertexFormat vertexFormatIn) {
        this.vertexFormat = vertexFormatIn;
        this.glBufferId = OpenGlHelper.glGenBuffers();
    }
    
    public void bindBuffer() {
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
    }
    
    public void func_181722_a(final ByteBuffer p_181722_1_) {
        this.bindBuffer();
        OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, p_181722_1_, 35044);
        this.unbindBuffer();
        this.count = p_181722_1_.limit() / this.vertexFormat.getNextOffset();
    }
    
    public void drawArrays(final int mode) {
        GL11.glDrawArrays(mode, 0, this.count);
    }
    
    public void unbindBuffer() {
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
    }
    
    public void deleteGlBuffers() {
        if (this.glBufferId >= 0) {
            OpenGlHelper.glDeleteBuffers(this.glBufferId);
            this.glBufferId = -1;
        }
    }
}
