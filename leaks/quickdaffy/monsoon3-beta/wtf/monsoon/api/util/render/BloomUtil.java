/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.api.util.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.event.EventBloom;

public class BloomUtil {
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void bloom() {
        ScaledResolution sr = new ScaledResolution(mc);
        if (BloomUtil.mc.displayWidth != BloomUtil.framebuffer.framebufferWidth || BloomUtil.mc.displayHeight != BloomUtil.framebuffer.framebufferHeight) {
            framebuffer.deleteFramebuffer();
            framebuffer = new Framebuffer(BloomUtil.mc.displayWidth, BloomUtil.mc.displayHeight, false);
        }
        EventBloom bloom = new EventBloom();
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        Wrapper.getEventBus().post(bloom);
        framebuffer.unbindFramebuffer();
        RoundedUtils.test(BloomUtil.framebuffer.framebufferTexture, Color.WHITE);
        GL11.glBindTexture((int)3553, (int)BloomUtil.framebuffer.framebufferTexture);
        RoundedUtils.rect(0.0f, 0.0f, sr.getScaledWidth(), sr.getScaledHeight());
    }
}

