// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.opengl.renderer;

import org.lwjgl.opengl.EXTSecondaryColor;
import org.lwjgl.opengl.GLContext;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.DoubleBuffer;
import org.lwjgl.opengl.GL11;

public class ImmediateModeOGLRenderer implements SGL
{
    private int width;
    private int height;
    private float[] current;
    protected float alphaScale;
    
    public ImmediateModeOGLRenderer() {
        this.current = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        this.alphaScale = 1.0f;
    }
    
    public void initDisplay(final int width, final int height) {
        this.width = width;
        this.height = height;
        final String extensions = GL11.glGetString(7939);
        GL11.glEnable(3553);
        GL11.glShadeModel(7425);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClearDepth(1.0);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(5888);
    }
    
    public void enterOrtho(final int xsize, final int ysize) {
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, (double)this.width, (double)this.height, 0.0, 1.0, -1.0);
        GL11.glMatrixMode(5888);
        GL11.glTranslatef((float)((this.width - xsize) / 2), (float)((this.height - ysize) / 2), 0.0f);
    }
    
    public void glBegin(final int geomType) {
        GL11.glBegin(geomType);
    }
    
    public void glBindTexture(final int target, final int id) {
        GL11.glBindTexture(target, id);
    }
    
    public void glBlendFunc(final int src, final int dest) {
        GL11.glBlendFunc(src, dest);
    }
    
    public void glCallList(final int id) {
        GL11.glCallList(id);
    }
    
    public void glClear(final int value) {
        GL11.glClear(value);
    }
    
    public void glClearColor(final float red, final float green, final float blue, final float alpha) {
        GL11.glClearColor(red, green, blue, alpha);
    }
    
    public void glClipPlane(final int plane, final DoubleBuffer buffer) {
        GL11.glClipPlane(plane, buffer);
    }
    
    public void glColor4f(final float r, final float g, final float b, float a) {
        a *= this.alphaScale;
        GL11.glColor4f(this.current[0] = r, this.current[1] = g, this.current[2] = b, this.current[3] = a);
    }
    
    public void glColorMask(final boolean red, final boolean green, final boolean blue, final boolean alpha) {
        GL11.glColorMask(red, green, blue, alpha);
    }
    
    public void glCopyTexImage2D(final int target, final int level, final int internalFormat, final int x, final int y, final int width, final int height, final int border) {
        GL11.glCopyTexImage2D(target, level, internalFormat, x, y, width, height, border);
    }
    
    public void glDeleteTextures(final IntBuffer buffer) {
        GL11.glDeleteTextures(buffer);
    }
    
    public void glDisable(final int item) {
        GL11.glDisable(item);
    }
    
    public void glEnable(final int item) {
        GL11.glEnable(item);
    }
    
    public void glEnd() {
        GL11.glEnd();
    }
    
    public void glEndList() {
        GL11.glEndList();
    }
    
    public int glGenLists(final int count) {
        return GL11.glGenLists(count);
    }
    
    public void glGetFloat(final int id, final FloatBuffer ret) {
        GL11.glGetFloat(id, ret);
    }
    
    public void glGetInteger(final int id, final IntBuffer ret) {
        GL11.glGetInteger(id, ret);
    }
    
    public void glGetTexImage(final int target, final int level, final int format, final int type, final ByteBuffer pixels) {
        GL11.glGetTexImage(target, level, format, type, pixels);
    }
    
    public void glLineWidth(final float width) {
        GL11.glLineWidth(width);
    }
    
    public void glLoadIdentity() {
        GL11.glLoadIdentity();
    }
    
    public void glNewList(final int id, final int option) {
        GL11.glNewList(id, option);
    }
    
    public void glPointSize(final float size) {
        GL11.glPointSize(size);
    }
    
    public void glPopMatrix() {
        GL11.glPopMatrix();
    }
    
    public void glPushMatrix() {
        GL11.glPushMatrix();
    }
    
    public void glReadPixels(final int x, final int y, final int width, final int height, final int format, final int type, final ByteBuffer pixels) {
        GL11.glReadPixels(x, y, width, height, format, type, pixels);
    }
    
    public void glRotatef(final float angle, final float x, final float y, final float z) {
        GL11.glRotatef(angle, x, y, z);
    }
    
    public void glScalef(final float x, final float y, final float z) {
        GL11.glScalef(x, y, z);
    }
    
    public void glScissor(final int x, final int y, final int width, final int height) {
        GL11.glScissor(x, y, width, height);
    }
    
    public void glTexCoord2f(final float u, final float v) {
        GL11.glTexCoord2f(u, v);
    }
    
    public void glTexEnvi(final int target, final int mode, final int value) {
        GL11.glTexEnvi(target, mode, value);
    }
    
    public void glTranslatef(final float x, final float y, final float z) {
        GL11.glTranslatef(x, y, z);
    }
    
    public void glVertex2f(final float x, final float y) {
        GL11.glVertex2f(x, y);
    }
    
    public void glVertex3f(final float x, final float y, final float z) {
        GL11.glVertex3f(x, y, z);
    }
    
    public void flush() {
    }
    
    public void glTexParameteri(final int target, final int param, final int value) {
        GL11.glTexParameteri(target, param, value);
    }
    
    public float[] getCurrentColor() {
        return this.current;
    }
    
    public void glDeleteLists(final int list, final int count) {
        GL11.glDeleteLists(list, count);
    }
    
    public void glClearDepth(final float value) {
        GL11.glClearDepth((double)value);
    }
    
    public void glDepthFunc(final int func) {
        GL11.glDepthFunc(func);
    }
    
    public void glDepthMask(final boolean mask) {
        GL11.glDepthMask(mask);
    }
    
    public void setGlobalAlphaScale(final float alphaScale) {
        this.alphaScale = alphaScale;
    }
    
    public void glLoadMatrix(final FloatBuffer buffer) {
        GL11.glLoadMatrix(buffer);
    }
    
    public void glGenTextures(final IntBuffer ids) {
        GL11.glGenTextures(ids);
    }
    
    public void glGetError() {
        GL11.glGetError();
    }
    
    public void glTexImage2D(final int target, final int i, final int dstPixelFormat, final int width, final int height, final int j, final int srcPixelFormat, final int glUnsignedByte, final ByteBuffer textureBuffer) {
        GL11.glTexImage2D(target, i, dstPixelFormat, width, height, j, srcPixelFormat, glUnsignedByte, textureBuffer);
    }
    
    public void glTexSubImage2D(final int glTexture2d, final int i, final int pageX, final int pageY, final int width, final int height, final int glBgra, final int glUnsignedByte, final ByteBuffer scratchByteBuffer) {
        GL11.glTexSubImage2D(glTexture2d, i, pageX, pageY, width, height, glBgra, glUnsignedByte, scratchByteBuffer);
    }
    
    public boolean canTextureMirrorClamp() {
        return GLContext.getCapabilities().GL_EXT_texture_mirror_clamp;
    }
    
    public boolean canSecondaryColor() {
        return GLContext.getCapabilities().GL_EXT_secondary_color;
    }
    
    public void glSecondaryColor3ubEXT(final byte b, final byte c, final byte d) {
        EXTSecondaryColor.glSecondaryColor3ubEXT(b, c, d);
    }
}
