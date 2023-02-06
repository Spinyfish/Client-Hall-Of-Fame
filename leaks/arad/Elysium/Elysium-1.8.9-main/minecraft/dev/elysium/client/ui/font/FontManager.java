package dev.elysium.client.ui.font;

import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import dev.elysium.client.Elysium;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class FontManager {

    private TTFFontRenderer defaultFont;

    final private boolean IS_DEV_MODE = false;

    public FontManager getInstance() {
        return instance;
    }

    public TTFFontRenderer getFont(String key) {
    	
        return fonts.getOrDefault(key.toUpperCase(), defaultFont);
    }

    private FontManager instance;

    private HashMap<String, TTFFontRenderer> fonts = new HashMap<>();
    File directory = new File(Minecraft.getMinecraft().mcDataDir, "/Elysium/customfonts/");

    long startOfLoading = 0;
    public FontManager() {
        System.out.println("\n[FontManager] Initialized!");
        startOfLoading = System.currentTimeMillis();
        instance = this;
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        ConcurrentLinkedQueue<TextureData> textureQueue = new ConcurrentLinkedQueue<>();
        defaultFont = new TTFFontRenderer(executorService, textureQueue, new Font("Calibri", Font.PLAIN, 18));

        File directory = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath().substring(0,Minecraft.getMinecraft().mcDataDir.getAbsolutePath().lastIndexOf(".")-5) +
                "src/minecraft/assets/minecraft/Elysium/customfonts");

        System.out.println("\n[FontManager] Started scanning for fonts...\n");

        try {
            for(String fontName : directory.list()) {
                if(!fontName.contains("."))
                    continue;
                String displayname = fontName.substring(0,fontName.lastIndexOf("."));
                System.out.println("Font name : " + fontName);
                String path = "/assets/minecraft/Elysium/customfonts/" + fontName;
                System.out.println("[FontManager] Font Discovered : " + displayname + " (" + fontName.split("\\.")[1] + ")");

                int[] sizes = new int[] {16,20};

                switch(displayname) {
                    case "SFM":
                        sizes = new int[] {16};
                        break;
                    case "SFL":
                        sizes = new int[] {14};
                        break;
                    case "Poppins-SemiBold":
                        sizes = new int[] {12,20,28,16};
                        break;
                    case "Poppins-Medium":
                        sizes = new int[] {18,30};
                        break;
                    case "Poppins":
                        sizes = new int[] {14,16};
                        break;
                    case "Elysium":
                        sizes = new int[] {34};
                        break;
                    case "Chocolate":
                        sizes = new int[] {48};
                        break;
                    case "ely":
                        sizes = new int[] {16, 64, 68, 72};
                        break;
                }

                if(IS_DEV_MODE) {
                    for(int i = 14; i<=34; i++) {
                        if(i > 30) i++;

                        InputStream istream = getClass().getResourceAsStream(path);
                        Font myFont = Font.createFont(Font.PLAIN, istream);
                        myFont = myFont.deriveFont(Font.PLAIN, i);
                        fonts.put(displayname.toUpperCase() + " " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
                    }
                } else {
                    for (int i : sizes) {
                        InputStream istream = getClass().getResourceAsStream(path);
                        Font myFont = Font.createFont(Font.PLAIN, istream);
                        myFont = myFont.deriveFont(Font.PLAIN, i);
                        fonts.put(displayname.toUpperCase() + " " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
                    }
                }


            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        System.out.println("\n####################################\n[FontManager] Done scanning for fonts!\n####################################\n");

        executorService.shutdown();
        int i = 0;
        long lastCycleCompletion = System.currentTimeMillis();
        boolean msgSent = false;

        System.out.println("[FontManager] Starting cycles...\n");

        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!textureQueue.isEmpty()) {
                TextureData textureData = textureQueue.poll();
                GlStateManager.bindTexture(textureData.getTextureId());

                // Sets the texture parameter stuff.
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

                // Uploads the texture to opengl.
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, textureData.getWidth(), textureData.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureData.getBuffer());

                if((System.currentTimeMillis() - lastCycleCompletion) > 1000 && !msgSent) {
                    msgSent = true;
                    System.out.println("\n##################################################\n[FontManager] Finishing up... (Might Take A While)\n##################################################\n");
                }
            }
            lastCycleCompletion = System.currentTimeMillis();
            if(!msgSent && i++ % 50 == 49) System.out.println("[FontManager] Cycle completed! Cycles -> "  + i);
        }
        System.out.println("[FontManager] Loaded fonts! It took " + (System.currentTimeMillis() - startOfLoading) + "ms in total.");
        System.out.println("[FontManager] Average Loading Time Per Font " + (System.currentTimeMillis() - startOfLoading)/ directory.length() + "ms\n");
    }
}
