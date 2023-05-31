// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.util;

import org.newdawn.slick.opengl.renderer.Renderer;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.renderer.SGL;

public class MaskUtil
{
    protected static SGL GL;
    
    public static void defineMask() {
        MaskUtil.GL.glDepthMask(true);
        MaskUtil.GL.glClearDepth(1.0f);
        MaskUtil.GL.glClear(256);
        MaskUtil.GL.glDepthFunc(519);
        MaskUtil.GL.glEnable(2929);
        MaskUtil.GL.glDepthMask(true);
        MaskUtil.GL.glColorMask(false, false, false, false);
    }
    
    public static void finishDefineMask() {
        MaskUtil.GL.glDepthMask(false);
        MaskUtil.GL.glColorMask(true, true, true, true);
    }
    
    public static void drawOnMask() {
        MaskUtil.GL.glDepthFunc(514);
    }
    
    public static void drawOffMask() {
        MaskUtil.GL.glDepthFunc(517);
    }
    
    public static void resetMask() {
        GL11.glDepthMask(true);
        GL11.glClearDepth(0.0);
        GL11.glClear(256);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
    }
    
    static {
        MaskUtil.GL = Renderer.get();
    }
}
