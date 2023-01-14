package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;

public class CustomGuis {
   private static final Minecraft mc = Config.getMinecraft();
   private static CustomGuiProperties[][] guiProperties = (CustomGuiProperties[][])null;
   public static final boolean isChristmas = isChristmas();

   public static ResourceLocation getTextureLocation(ResourceLocation loc) {
      if (guiProperties == null) {
         return loc;
      } else {
         GuiScreen guiscreen = mc.currentScreen;
         if (!(guiscreen instanceof GuiContainer)) {
            return loc;
         } else {
            return loc.getResourceDomain().equals("minecraft") && loc.getResourcePath().startsWith("textures/gui/") ? loc : loc;
         }
      }
   }

   public static void update() {
      guiProperties = (CustomGuiProperties[][])null;
      if (Config.isCustomGuis()) {
         List<List<CustomGuiProperties>> list = new ArrayList<>();
         IResourcePack[] airesourcepack = Config.getResourcePacks();

         for(int i = airesourcepack.length - 1; i >= 0; --i) {
            IResourcePack iresourcepack = airesourcepack[i];
            update(iresourcepack, list);
         }

         guiProperties = propertyListToArray(list);
      }
   }

   private static CustomGuiProperties[][] propertyListToArray(List<List<CustomGuiProperties>> listProps) {
      if (listProps.isEmpty()) {
         return (CustomGuiProperties[][])null;
      } else {
         CustomGuiProperties[][] acustomguiproperties = new CustomGuiProperties[CustomGuiProperties.EnumContainer.VALUES.length][];

         for(int i = 0; i < acustomguiproperties.length; ++i) {
            if (listProps.size() > i) {
               List<CustomGuiProperties> list = listProps.get(i);
               if (list != null) {
                  CustomGuiProperties[] acustomguiproperties1 = list.toArray(new CustomGuiProperties[0]);
                  acustomguiproperties[i] = acustomguiproperties1;
               }
            }
         }

         return acustomguiproperties;
      }
   }

   private static void update(IResourcePack rp, List<List<CustomGuiProperties>> listProps) {
      String[] astring = ResUtils.collectFiles(rp, "optifine/gui/container/", ".properties", null);
      Arrays.sort((Object[])astring);

      for(String s : astring) {
         Config.dbg("CustomGuis: " + s);

         try {
            ResourceLocation resourcelocation = new ResourceLocation(s);
            InputStream inputstream = rp.getInputStream(resourcelocation);
            if (inputstream == null) {
               Config.warn("CustomGuis file not found: " + s);
            } else {
               Properties properties = new PropertiesOrdered();
               properties.load(inputstream);
               inputstream.close();
               CustomGuiProperties customguiproperties = new CustomGuiProperties(properties, s);
               if (customguiproperties.isValid(s)) {
                  addToList(customguiproperties, listProps);
               }
            }
         } catch (FileNotFoundException var11) {
            Config.warn("CustomGuis file not found: " + s);
         } catch (Exception var12) {
            var12.printStackTrace();
         }
      }
   }

   private static void addToList(CustomGuiProperties cgp, List<List<CustomGuiProperties>> listProps) {
      if (cgp.getContainer() == null) {
         warn("Invalid container: " + cgp.getContainer());
      } else {
         int i = cgp.getContainer().ordinal();

         while(listProps.size() <= i) {
            listProps.add(null);
         }

         List<CustomGuiProperties> list = listProps.get(i);
         if (list == null) {
            list = new ArrayList<>();
            listProps.set(i, list);
         }

         list.add(cgp);
      }
   }

   private static boolean isChristmas() {
      Calendar calendar = Calendar.getInstance();
      return calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;
   }

   private static void warn(String str) {
      Config.warn("[CustomGuis] " + str);
   }
}
