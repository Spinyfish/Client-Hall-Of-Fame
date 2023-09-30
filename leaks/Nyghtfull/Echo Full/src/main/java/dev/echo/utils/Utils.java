package dev.echo.utils;

import dev.echo.utils.font.CustomFont;
import dev.echo.utils.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

public interface Utils {
    Minecraft mc = Minecraft.getMinecraft();
    IFontRenderer fr = mc.fontRendererObj;

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    FontUtil.FontType echoFont = FontUtil.FontType.ECHO,
            iconFont = FontUtil.FontType.ICON,
            neverloseFont = FontUtil.FontType.NEVERLOSE,
            tahomaFont = FontUtil.FontType.TAHOMA,
            rubikFont = FontUtil.FontType.RUBIK,
            sansFont = FontUtil.FontType.SANS;

    //Regular Fonts
    CustomFont echoFont12 = echoFont.size(12),
            echoFont14 = echoFont.size(14),
            echoFont16 = echoFont.size(16),
            echoFont18 = echoFont.size(18),
            echoFont20 = echoFont.size(20),
            echoFont22 = echoFont.size(22),
            echoFont24 = echoFont.size(24),
            echoFont26 = echoFont.size(26),
            echoFont28 = echoFont.size(28),
            echoFont32 = echoFont.size(32),
            echoFont40 = echoFont.size(40),
            echoFont80 = echoFont.size(80);

    //Bold Fonts
    CustomFont echoBoldFont14 = echoFont14.getBoldFont(),
            echoBoldFont16 = echoFont16.getBoldFont(),
            echoBoldFont18 = echoFont18.getBoldFont(),
            echoBoldFont20 = echoFont20.getBoldFont(),
            echoBoldFont22 = echoFont22.getBoldFont(),
            echoBoldFont24 = echoFont24.getBoldFont(),
            echoBoldFont26 = echoFont26.getBoldFont(),
            echoBoldFont28 = echoFont28.getBoldFont(),
            echoBoldFont32 = echoFont32.getBoldFont(),
            echoBoldFont40 = echoFont40.getBoldFont(),
            echoBoldFont80 = echoFont80.getBoldFont();

    //Icon Fontsor i
    CustomFont iconFont16 = iconFont.size(16),
            iconFont20 = iconFont.size(20),
            iconFont26 = iconFont.size(26),
            iconFont35 = iconFont.size(35),
            iconFont40 = iconFont.size(40);


}
