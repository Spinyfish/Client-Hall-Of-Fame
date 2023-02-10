package dev.myth.api.utils.font;

import java.awt.Font;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class FontLoaders {

    public static final CFontRenderer BOLD_13 = new CFontRenderer(FontLoaders.getFonts("bold.ttf",14), true, true);
    public static final CFontRenderer BOLD = new CFontRenderer(FontLoaders.getFonts("bold.ttf",17), true, true);
    public static final CFontRenderer BOLD_18 = new CFontRenderer(FontLoaders.getFonts("bold.ttf",18), true, true);
    public static final CFontRenderer BOLD_22 = new CFontRenderer(FontLoaders.getFonts("bold.ttf",22), true, true);
    public static final CFontRenderer URBANE_18 = new CFontRenderer(FontLoaders.getFonts("Urbane.ttf",18), true, true);
    public static final CFontRenderer NOVO_FONT_16 = new CFontRenderer(FontLoaders.getFonts("novofont.ttf",16), true, true);
    public static final CFontRenderer NOVO_FONT_18 = new CFontRenderer(FontLoaders.getFonts("novofont.ttf",18), true, true);
    public static final CFontRenderer NOVO_FONT_19 = new CFontRenderer(FontLoaders.getFonts("novofont.ttf",19), true, true);
    public static final CFontRenderer NOVO_FONT = new CFontRenderer(FontLoaders.getFonts("novofont.ttf",18), true, true);
    public static final CFontRenderer NOVO_FONT_20 = new CFontRenderer(FontLoaders.getFonts("novofont.ttf",20), true, true);
    public static final CFontRenderer NOVO_FONT_30 = new CFontRenderer(FontLoaders.getFonts("novofont.ttf",30), true, true);
    public static final CFontRenderer NOVO_FONT_35 = new CFontRenderer(FontLoaders.getFonts("novofont.ttf",35), true, true);
    public static final CFontRenderer NOVO_FONT_22 = new CFontRenderer(FontLoaders.getFonts("novofont.ttf",22), true, true);
    public static final CFontRenderer JAPAN = new CFontRenderer(FontLoaders.getFonts("japan.ttf",40), true, true);
    public static final CFontRenderer FLUX = new CFontRenderer(FontLoaders.getFonts("roboto.ttf",19), true, true);
    public static final CFontRenderer ARIMO = new CFontRenderer(FontLoaders.getFonts("Arimo-Regular.ttf",19), true, true);
    public static final CFontRenderer ICON = new CFontRenderer(FontLoaders.getFonts("Icon.ttf",30), true, true);
    public static final CFontRenderer ICON_40 = new CFontRenderer(FontLoaders.getFonts("Icon.ttf",40), true, true);
    public static final CFontRenderer SFUI_12 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",12), true, true);
    public static final CFontRenderer SFUI_13 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",13), true, true);
    public static final CFontRenderer SFUI_14 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",14), true, true);
    public static final CFontRenderer SFUI_15 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",15), true, true);
    public static final CFontRenderer SFUI_16 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",16), true, true);
    public static final CFontRenderer SFUI_18 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",18), true, true);
    public static final CFontRenderer SFUI_19 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",19), true, true);
    public static final CFontRenderer SFUI_20 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",20), true, true);
    public static final CFontRenderer SFUI_22 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",22), true, true);
    public static final CFontRenderer SFUI_17 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",17), true, true);
    public static final CFontRenderer SFUI_40 = new CFontRenderer(FontLoaders.getFonts("SF-UI.ttf",40), true, true);
    public static final CFontRenderer BRAINS = new CFontRenderer(FontLoaders.getFonts("brains.ttf",18), true, true);
    public static final CFontRenderer CEDO = new CFontRenderer(FontLoaders.getFonts("cedo.ttf",20), true, true);
    public static final CFontRenderer FLUX_ICON_45 = new CFontRenderer(FontLoaders.getFonts("fluxIcon.ttf",45), true, true);
    public static final CFontRenderer FLUX_ICON_25 = new CFontRenderer(FontLoaders.getFonts("fluxIcon.ttf",25), true, true);
    public static final CFontRenderer FLUX_ICON_30 = new CFontRenderer(FontLoaders.getFonts("fluxIcon.ttf",30), true, true);
    public static final CFontRenderer FLUX_ICON_20 = new CFontRenderer(FontLoaders.getFonts("fluxIcon.ttf",20), true, true);
    public static final CFontRenderer CHILL = new CFontRenderer(FontLoaders.getFonts("chill.ttf",20), true, true);
    public static final CFontRenderer BIG = new CFontRenderer(FontLoaders.getFonts("big.ttf",15), true, true);
    public static final CFontRenderer QUICK_SAND = new CFontRenderer(FontLoaders.getFonts("quicksand.ttf", 20), true, true);
    public static final CFontRenderer TAHOMA_9 = new CFontRenderer(new Font("Tahoma", Font.PLAIN, 9), false, true);
    public static final CFontRenderer TAHOMA_11 = new CFontRenderer(new Font("Tahoma", Font.PLAIN, 11), false, true);
    public static final CFontRenderer SKEET_ICONS = new CFontRenderer(FontLoaders.getFonts("icons.ttf",40), true, true);
    public static final CFontRenderer SMALLEST_PIXEL = new CFontRenderer(FontLoaders.getFonts("smallestpixel.ttf",10), false, true);

    private static Font getFonts(String fontName, int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("myth/fonts/" + fontName)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
}
