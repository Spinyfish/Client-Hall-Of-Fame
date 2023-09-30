/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.LWJGLException
 *  org.lwjgl.opengl.ARBDebugOutput
 *  org.lwjgl.opengl.ARBDebugOutputCallback
 *  org.lwjgl.opengl.ARBDebugOutputCallback$Handler
 *  org.lwjgl.opengl.ContextAttribs
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLContext
 *  org.lwjgl.opengl.PixelFormat
 */
package net.optifine;

import java.nio.IntBuffer;
import net.minecraft.src.Config;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.ARBDebugOutputCallback;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

public class GlDebugHandler
implements ARBDebugOutputCallback.Handler {
    public static void createDisplayDebug() throws LWJGLException {
        boolean flag = GLContext.getCapabilities().GL_ARB_debug_output;
        ContextAttribs contextattribs = new ContextAttribs().withDebug(true);
        Display.create((PixelFormat)new PixelFormat().withDepthBits(24), (ContextAttribs)contextattribs);
        ARBDebugOutput.glDebugMessageCallbackARB((ARBDebugOutputCallback)new ARBDebugOutputCallback((ARBDebugOutputCallback.Handler)new GlDebugHandler()));
        ARBDebugOutput.glDebugMessageControlARB((int)4352, (int)4352, (int)4352, (IntBuffer)null, (boolean)true);
        GL11.glEnable((int)33346);
    }

    public void handleMessage(int source, int type, int id, int severity, String message) {
        if (!(message.contains("glBindFramebuffer") || message.contains("Wide lines") || message.contains("shader recompiled"))) {
            Config.dbg("[LWJGL] source: " + this.getSource(source) + ", type: " + this.getType(type) + ", id: " + id + ", severity: " + this.getSeverity(severity) + ", message: " + message);
            new Throwable("StackTrace").printStackTrace();
        }
    }

    public String getSource(int source) {
        switch (source) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WIN";
            }
            case 33352: {
                return "SHADER";
            }
            case 33353: {
                return "EXT";
            }
            case 33354: {
                return "APP";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return this.getUnknown(source);
    }

    public String getType(int type) {
        switch (type) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED";
            }
            case 33358: {
                return "UNDEFINED";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
        }
        return this.getUnknown(type);
    }

    public String getSeverity(int severity) {
        switch (severity) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
        }
        return this.getUnknown(severity);
    }

    private String getUnknown(int token) {
        return "Unknown (0x" + Integer.toHexString(token).toUpperCase() + ")";
    }
}

