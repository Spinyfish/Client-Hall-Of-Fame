package me.eldodebug.soar.utils.font;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class FontUtils {
	
    public static volatile int completed;

	private static int scale;
	private static int prevScale;
	
    public static MinecraftFontRenderer
    		regular12,
    		regular16,
    		regular20,
    		regular22,
    		regular24,
    		regular40,
    		regular_bold18, regular_bold20,
    		regular_bold22, regular_bold26, 
    		regular_bold24, 
    		regular_bold30, regular_bold36,
    		regular_bold40,
    		icon18, icon20, icon24;
    
    public static Font
    		regular12_, regular16_,
			regular20_, regular24_,
			regular22_,
			regular40_,
			regular_bold18_, regular_bold20_,
			regular_bold22_, regular_bold26_,
			regular_bold24_,
			regular_bold30_, regular_bold36_,
			regular_bold40_,
			icon18_, icon20_, icon24_;
	
    public static void init() {
		
        Map<String, Font> locationMap = new HashMap<>();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        
        scale = sr.getScaleFactor();
        
        if(scale != prevScale) {
        	prevScale = scale;

            FontUtils.regular12_ = FontUtils.getFont(locationMap, "regular.ttf", 12);
            FontUtils.regular12 = new MinecraftFontRenderer(FontUtils.regular12_);
            FontUtils.regular16_ = FontUtils.getFont(locationMap, "regular.ttf", 16);
            FontUtils.regular16 = new MinecraftFontRenderer(FontUtils.regular16_);
            FontUtils.regular20_ = FontUtils.getFont(locationMap, "regular.ttf", 20);
            FontUtils.regular20 = new MinecraftFontRenderer(FontUtils.regular20_);
            FontUtils.regular22_ = FontUtils.getFont(locationMap, "regular.ttf", 22);
            FontUtils.regular22 = new MinecraftFontRenderer(FontUtils.regular22_);
            FontUtils.regular24_ = FontUtils.getFont(locationMap, "regular.ttf", 24);
            FontUtils.regular24 = new MinecraftFontRenderer(FontUtils.regular24_);
            FontUtils.regular40_ = FontUtils.getFont(locationMap, "regular.ttf", 40);
            FontUtils.regular40 = new MinecraftFontRenderer(FontUtils.regular40_);

            FontUtils.regular_bold18_ = FontUtils.getFont(locationMap, "regular_bold.ttf", 18);
            FontUtils.regular_bold18 = new MinecraftFontRenderer(FontUtils.regular_bold18_);
            FontUtils.regular_bold20_ = FontUtils.getFont(locationMap, "regular_bold.ttf", 20);
            FontUtils.regular_bold20 = new MinecraftFontRenderer(FontUtils.regular_bold20_);
            FontUtils.regular_bold22_ = FontUtils.getFont(locationMap, "regular_bold.ttf", 22);
            FontUtils.regular_bold22 = new MinecraftFontRenderer(FontUtils.regular_bold22_);
            FontUtils.regular_bold24_ = FontUtils.getFont(locationMap, "regular_bold.ttf", 24);
            FontUtils.regular_bold24 = new MinecraftFontRenderer(FontUtils.regular_bold24_);
            FontUtils.regular_bold26_ = FontUtils.getFont(locationMap, "regular_bold.ttf", 26);
            FontUtils.regular_bold26 = new MinecraftFontRenderer(FontUtils.regular_bold26_);
            FontUtils.regular_bold30_ = FontUtils.getFont(locationMap, "regular_bold.ttf", 30);
            FontUtils.regular_bold30 = new MinecraftFontRenderer(FontUtils.regular_bold30_);
            FontUtils.regular_bold36_ = FontUtils.getFont(locationMap, "regular_bold.ttf", 36);
            FontUtils.regular_bold36 = new MinecraftFontRenderer(FontUtils.regular_bold36_);
            FontUtils.regular_bold40_ = FontUtils.getFont(locationMap, "regular_bold.ttf", 40);
            FontUtils.regular_bold40 = new MinecraftFontRenderer(FontUtils.regular_bold40_);
            
            FontUtils.icon18_ = FontUtils.getFont(locationMap, "icon.ttf", 18);
            FontUtils.icon18 = new MinecraftFontRenderer(FontUtils.icon18_);
            FontUtils.icon20_ = FontUtils.getFont(locationMap, "icon.ttf", 20);
            FontUtils.icon20 = new MinecraftFontRenderer(FontUtils.icon20_);
            FontUtils.icon24_ = FontUtils.getFont(locationMap, "icon.ttf", 24);
            FontUtils.icon24 = new MinecraftFontRenderer(FontUtils.icon24_);
        }
    }
    
    public static Font getFont(Map<String, Font> locationMap, String location, float size) {
        Font font;
        
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        
        size = size * ((float) sr.getScaleFactor() / 2);
        
        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(Font.PLAIN, size);
            } else {
                InputStream is = Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("soar/fonts/" + location)).getInputStream();
                locationMap.put(location, font = Font.createFont(0, is));
                font = font.deriveFont(Font.PLAIN, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            font = new Font("default", Font.PLAIN, +10);
        }
        return font;
    }

    public static boolean hasLoaded() {
        return completed >= 3;
    }
}
