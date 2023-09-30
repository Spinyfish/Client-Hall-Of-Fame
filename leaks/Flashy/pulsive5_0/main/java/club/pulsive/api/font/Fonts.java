package club.pulsive.api.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @antja03
 **/
public class Fonts {

    public static FontRenderer sf;
    public static FontRenderer popBold20, popBig, popMedSmall, popRegSmall;
    public static FontRenderer popMed20;
    public static FontRenderer sfSmall;
    public static FontRenderer grayclifftitle;
    public static FontRenderer sigma;
    public static FontRenderer mainTitle;
    public static FontRenderer google;
    public static FontRenderer googleMedium;
    public static FontRenderer googleSmall;
    public static FontRenderer moon;
    public static FontRenderer moontitle;
    public static FontRenderer icons22, icons20, icons18, icons17, icons30, icons15;
    public static FontRenderer tahoma;
    public static FontRenderer jetbrains;
    public static FontRenderer astolfo;
    public static FontRenderer moonSmall;
    public static FontRenderer fontforflashy;
    public static FontRenderer fontforflashytitle;
    public static FontRenderer badcache;
    public static FontRenderer undefeated;
    public static void createFonts() {
        sf = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/SF-UI-Display-Regular.ttf"), 18, Font.PLAIN), true, true);
        sfSmall = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/SF-UI-Display-Regular.ttf"), 16, Font.PLAIN), true, true);
        grayclifftitle = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/greycliffbold.otf"), 20, Font.PLAIN), true, true);
        sigma = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/SF-UI-Display-Bold.ttf"), 18, Font.PLAIN), true, true);
        mainTitle = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/greycliffbold.otf"), 26, Font.PLAIN), true, true);
        moon = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/modern.ttf"), 18, Font.PLAIN), true, true);
        moontitle = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/modern.ttf"), 20, Font.PLAIN), true, true);
        tahoma = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/tahoma.ttf"), 17, Font.PLAIN), true, true);
        jetbrains = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/JetBrainsMono-Bold.ttf"), 18, Font.PLAIN), true, true);
        popMedSmall = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/Poppins-Medium.ttf"), 20, Font.PLAIN), true, true);
        popRegSmall = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/Poppins-Regular.ttf"), 20, Font.PLAIN), true, true);
        moonSmall = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/modern.ttf"), 15, Font.PLAIN), true, true);
        fontforflashy = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/FontForFlashy.ttf"), 18, Font.PLAIN), true, true);
        fontforflashytitle = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/FontForFlashy.ttf"), 20, Font.PLAIN), true, true);
        undefeated = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/undefeated.ttf"), 20, Font.PLAIN), true, true);
        google = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/Regular.ttf"), 16, Font.PLAIN), true, true);
        googleMedium = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/Medium.ttf"), 18, Font.PLAIN), true, true);
        googleSmall = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/Regular.ttf"), 15, Font.PLAIN), true, true);
        astolfo = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/SF-Pro-Text-Regular.ttf"), 18, Font.PLAIN), true, true);
        badcache = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/SFUIText-Light.ttf"), 18, Font.PLAIN), true, true);
        icons22 = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/icons2.ttf"), 20, Font.PLAIN), true, true);
        icons20 = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/icons2.ttf"), 20, Font.PLAIN), true, true);
        icons18 = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/icons2.ttf"), 18, Font.PLAIN), true, true);
        icons17 = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/icons2.ttf"), 17, Font.PLAIN), true, true);
        icons15 = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/icons2.ttf"), 15, Font.PLAIN), true, true);
        icons30 = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/icons2.ttf"), 30, Font.PLAIN), true, true);
        popMed20 = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/Poppins-Medium.ttf"), 25, Font.PLAIN), true, true);
        popBold20 = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/Poppins-Bold.ttf"), 25, Font.PLAIN), true, true);
        popBig = new FontRenderer(
                fontFromTTF(new ResourceLocation("pulsabo/fonts/Poppins-SemiBold.ttf"), 30, Font.PLAIN), true, true);
    }

    public static FontRenderer createFontRenderer(int fontSize) {
        return new FontRenderer(
                fontFromTTF(new ResourceLocation("client/ElliotSans-Medium.ttf"), fontSize, Font.PLAIN), true, true);
    }

    public static Font fontFromTTF(ResourceLocation fontLocation, float fontSize, int fontType) {
        Font output = null;
        try {
            output = Font.createFont(fontType, Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
            output = output.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

}