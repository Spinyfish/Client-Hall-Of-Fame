package crispy.fonts.greatfont;

import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class FontManager {
    public TTFRenderer hud = null;
    private final ResourceLocation darrow = new ResourceLocation("SF-UI-Display-Regular.otf");
    private final FontManager instance;
    @Getter
    private final TTFFontRenderer defaultFont;
    private final HashMap<String, TTFFontRenderer> fonts = new HashMap<>();

    public FontManager() {
        instance = this;
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        ConcurrentLinkedQueue<TextureData> textureQueue = new ConcurrentLinkedQueue<>();
        defaultFont = new TTFFontRenderer(executorService, textureQueue, new Font("Verdana", Font.PLAIN, 18));
        try {
            for (int i : new int[]{16, 36, 20}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/SigmaThin.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("JELLO1 " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{16, 18, 20, 28, 30, 32}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/Applefnt.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("clean " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{12, 14, 20, 22, 28, 30, 42}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/Roboto-Regular.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("ROBO " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{20}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/Comfortaa-Regular.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("comfort " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{20, 26}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/Karla-Bold.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("karla-bold " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{20}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/Karla-Regular.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("karla " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{20}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/Gilroy-Light.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("gilroy " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{38}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/Icon-Font.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("icon " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{14, 38}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/config.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("config " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{16}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/notifications.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("noto " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{16}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Client/fonts/ps-bold.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("bold " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }

        } catch (Exception ignored) {

        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!textureQueue.isEmpty()) {
                TextureData textureData = textureQueue.poll();
                GlStateManager.bindTexture(textureData.getTextureId());

                // Sets the texture parameter stuff.
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                // Uploads the texture to opengl.
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, textureData.getWidth(), textureData.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureData.getBuffer());
            }
        }
    }

    public FontManager getInstance() {
        return instance;
    }

    public TTFFontRenderer getFont(String key) {
        return fonts.getOrDefault(key, defaultFont);
    }

    public void setup() {
        this.hud = new TTFRenderer("Arial", 0, 18);
    }
}
