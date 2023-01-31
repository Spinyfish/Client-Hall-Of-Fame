package net.optifine.player;

import intent.AquaDev.aqua.gui.novolineOld.themesScreen.ThemeScreen;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

public class CapeUtils {
   private static final Pattern PATTERN_USERNAME = Pattern.compile("[a-zA-Z0-9_]+");

   public static void downloadCape(final AbstractClientPlayer player) {
      String s = player.getNameClear();
      if (s != null && !s.isEmpty()) {
         String s1 = ThemeScreen.themeHero ? "https://i.imgur.com/qGpDLqJ.png" : "";
         String s2 = ThemeScreen.themeHero ? "https://i.imgur.com/qGpDLqJ.png" : "";
         final ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s2);
         TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
         ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);
         if (itextureobject != null && itextureobject instanceof ThreadDownloadImageData) {
            ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData)itextureobject;
            if (threaddownloadimagedata.imageFound != null) {
               if (threaddownloadimagedata.imageFound) {
                  player.setLocationOfCape(resourcelocation);
               }

               return;
            }
         }

         IImageBuffer iimagebuffer = new IImageBuffer() {
            ImageBufferDownload ibd = new ImageBufferDownload();

            @Override
            public BufferedImage parseUserSkin(BufferedImage image) {
               return CapeUtils.parseCape(image);
            }

            @Override
            public void skinAvailable() {
               player.setLocationOfCape(resourcelocation);
            }
         };
         ThreadDownloadImageData threaddownloadimagedata1 = new ThreadDownloadImageData((File)null, s1, (ResourceLocation)null, iimagebuffer);
         threaddownloadimagedata1.pipeline = true;
         texturemanager.loadTexture(resourcelocation, threaddownloadimagedata1);
      }
   }

   public static BufferedImage parseCape(BufferedImage img) {
      int i = 64;
      int j = 32;
      int k = img.getWidth();

      for(int l = img.getHeight(); i < k || j < l; j *= 2) {
         i *= 2;
      }

      BufferedImage bufferedimage = new BufferedImage(i, j, 2);
      Graphics graphics = bufferedimage.getGraphics();
      graphics.drawImage(img, 0, 0, (ImageObserver)null);
      graphics.dispose();
      return bufferedimage;
   }

   public static boolean isElytraCape(BufferedImage imageRaw, BufferedImage imageFixed) {
      return imageRaw.getWidth() > imageFixed.getHeight();
   }

   public static void reloadCape(AbstractClientPlayer player) {
      String s = player.getNameClear();
      ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s);
      TextureManager texturemanager = Config.getTextureManager();
      ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);
      if (itextureobject instanceof SimpleTexture) {
         SimpleTexture simpletexture = (SimpleTexture)itextureobject;
         simpletexture.deleteGlTexture();
         texturemanager.deleteTexture(resourcelocation);
      }

      player.setLocationOfCape((ResourceLocation)null);
      player.setElytraOfCape(false);
      downloadCape(player);
   }
}
