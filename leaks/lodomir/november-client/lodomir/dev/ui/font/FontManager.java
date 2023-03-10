/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.ui.font;

import java.awt.Font;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.ui.font.TextureData;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public final class FontManager {
    private final HashMap<String, TTFFontRenderer> fonts = new HashMap();
    public final TTFFontRenderer defaultFont;

    public TTFFontRenderer getFont(String key) {
        return this.fonts.getOrDefault(key, this.defaultFont);
    }

    public FontManager() {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
        ConcurrentLinkedQueue<TextureData> textureQueue = new ConcurrentLinkedQueue<TextureData>();
        this.defaultFont = new TTFFontRenderer(executorService, textureQueue, new Font("PRODUCT SANS", 0, 20));
        try {
            Font myFont;
            InputStream istream;
            for (int i : new int[]{6, 7, 8, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40}) {
                istream = this.getClass().getResourceAsStream("/assets/minecraft/november/fonts/product_sans.ttf");
                myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, i);
                this.fonts.put("PRODUCT SANS " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40}) {
                istream = this.getClass().getResourceAsStream("/assets/minecraft/november/fonts/icons.ttf");
                myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, i);
                this.fonts.put("ICONS " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40}) {
                istream = this.getClass().getResourceAsStream("/assets/minecraft/november/fonts/tahoma.ttf");
                myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, i);
                this.fonts.put("TAHOMA " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40}) {
                istream = this.getClass().getResourceAsStream("/assets/minecraft/november/fonts/Comfortaa-Regular.ttf");
                myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, i);
                this.fonts.put("COMFORTAA " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40}) {
                istream = this.getClass().getResourceAsStream("/assets/minecraft/november/fonts/SFR_Regular.ttf");
                myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, i);
                this.fonts.put("SFR " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40}) {
                istream = this.getClass().getResourceAsStream("/assets/minecraft/november/fonts/SF-UI-Regular.ttf");
                myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, i);
                this.fonts.put("SFUI " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40}) {
                istream = this.getClass().getResourceAsStream("/assets/minecraft/november/fonts/sfui-bold.ttf");
                myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, i);
                this.fonts.put("SFUI BOLD " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40}) {
                istream = this.getClass().getResourceAsStream("/assets/minecraft/november/fonts/NotoSans-Regular.ttf");
                myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, i);
                this.fonts.put("NOTO " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 72}) {
                istream = this.getClass().getResourceAsStream("/assets/minecraft/november/fonts/NotoSans-Black.ttf");
                myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, i);
                this.fonts.put("NOTO BOLD " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!textureQueue.isEmpty()) {
                TextureData textureData = textureQueue.poll();
                GlStateManager.bindTexture(textureData.getTextureId());
                GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
                GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
                GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)textureData.getWidth(), (int)textureData.getHeight(), (int)0, (int)6408, (int)5121, (ByteBuffer)textureData.getBuffer());
            }
        }
    }
}

