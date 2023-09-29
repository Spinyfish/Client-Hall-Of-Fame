package de.fanta.fontrenderer;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class FontManager {

    private final HashMap<String, TTFFontRenderer> fonts = new HashMap<>();
    private final TTFFontRenderer defaultFront;

    public FontManager() {
        addAll();
        defaultFront = getFont("Arial 20");
    }

    private void addAll() {
        final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        final ConcurrentLinkedQueue<TextureData> textureQueue = new ConcurrentLinkedQueue<>();
        addFonts(executorService, textureQueue);
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!textureQueue.isEmpty()) {
                final TextureData textureData = textureQueue.poll();
                GlStateManager.bindTexture(textureData.getTextureId());

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, textureData.getWidth(), textureData.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureData.getBuffer());
            }
        }
    }

    private void addFonts(final ThreadPoolExecutor executorService, final ConcurrentLinkedQueue<TextureData> textureQueue) {
        for (int i : new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 200})
        {
            try {
                // Arial
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Fanta/fonts/arial/Arial.ttf");
                Font font = Font.createFont(Font.PLAIN, istream);
                font = font.deriveFont(Font.PLAIN, i);
                this.fonts.put("Arial " + i, new TTFFontRenderer(executorService, textureQueue, font));

                // Roboto
                istream = getClass().getResourceAsStream("/assets/minecraft/Fanta/fonts/roboto/Roboto-Bold.ttf");
                font = Font.createFont(Font.PLAIN, istream);
                font = font.deriveFont(Font.PLAIN, i);
                this.fonts.put("Roboto bold " + i, new TTFFontRenderer(executorService, textureQueue, font));


                istream = getClass().getResourceAsStream("/assets/minecraft/Fanta/fonts/roboto/Roboto-Medium.ttf");
                font = Font.createFont(Font.PLAIN, istream);
                font = font.deriveFont(Font.PLAIN, i);
                this.fonts.put("Roboto medium " + i, new TTFFontRenderer(executorService, textureQueue, font));

                // Arrow icons
                istream = getClass().getResourceAsStream("/assets/minecraft/Fanta/fonts/arrow/arrows-regular.ttf");
                font = Font.createFont(Font.PLAIN, istream);
                font = font.deriveFont(Font.PLAIN, i);
                this.fonts.put("Arrow " + i, new TTFFontRenderer(executorService, textureQueue, font));

                // Category Icons
                istream = getClass().getResourceAsStream("/assets/minecraft/Fanta/fonts/icon/icon-font.otf");
                font = Font.createFont(Font.PLAIN, istream);
                font = font.deriveFont(Font.PLAIN, i);
                this.fonts.put("ICON " + i, new TTFFontRenderer(executorService, textureQueue, font));

            } catch (Exception ignored) {}
        }
    }

    public final TTFFontRenderer getFont(final String name) {
        return fonts.getOrDefault(name, defaultFront);
    }

}
