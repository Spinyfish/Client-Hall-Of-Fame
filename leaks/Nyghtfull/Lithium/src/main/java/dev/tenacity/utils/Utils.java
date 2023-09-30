package dev.tenacity.utils;

import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

public interface Utils {
    Minecraft mc = Minecraft.getMinecraft();
    IFontRenderer fr = mc.fontRendererObj;

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    FontUtil.FontType lithiumFont = FontUtil.FontType.LITHIUM,
            iconFont = FontUtil.FontType.ICON,
            neverloseFont = FontUtil.FontType.NEVERLOSE,
            tahomaFont = FontUtil.FontType.TAHOMA,
            rubikFont = FontUtil.FontType.RUBIK;


    //Regular Fonts
    CustomFont lithiumFont12 = lithiumFont.size(12),
            lithiumFont14 = lithiumFont.size(14),
            lithiumFont16 = lithiumFont.size(16),
            lithiumFont18 = lithiumFont.size(18),
            lithiumFont20 = lithiumFont.size(20),
            lithiumFont22 = lithiumFont.size(22),
            lithiumFont24 = lithiumFont.size(24),
            lithiumFont26 = lithiumFont.size(26),
            lithiumFont28 = lithiumFont.size(28),
            lithiumFont32 = lithiumFont.size(32),
            lithiumFont40 = lithiumFont.size(40),
            lithiumFont80 = lithiumFont.size(80);

    //Bold Fonts
    CustomFont lithiumBoldFont12 = lithiumFont12.getBoldFont(),
            lithiumBoldFont14 = lithiumFont14.getBoldFont(),
            lithiumBoldFont16 = lithiumFont16.getBoldFont(),
            lithiumBoldFont18 = lithiumFont18.getBoldFont(),
            lithiumBoldFont20 = lithiumFont20.getBoldFont(),
            lithiumBoldFont22 = lithiumFont22.getBoldFont(),
            lithiumBoldFont24 = lithiumFont24.getBoldFont(),
            lithiumBoldFont26 = lithiumFont26.getBoldFont(),
            lithiumBoldFont28 = lithiumFont28.getBoldFont(),
            lithiumBoldFont32 = lithiumFont32.getBoldFont(),
            lithiumBoldFont40 = lithiumFont40.getBoldFont(),
            lithiumBoldFont80 = lithiumFont80.getBoldFont();

    //Icon Fontsor i
    CustomFont iconFont16 = iconFont.size(16),
            iconFont20 = iconFont.size(20),
            iconFont26 = iconFont.size(26),
            iconFont35 = iconFont.size(35),
            iconFont40 = iconFont.size(40);


}
