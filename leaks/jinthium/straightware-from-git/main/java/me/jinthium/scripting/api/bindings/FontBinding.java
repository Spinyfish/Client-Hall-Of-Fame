package me.jinthium.scripting.api.bindings;


import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.utils.font.AbstractFontRenderer;
import me.jinthium.straight.impl.utils.font.CustomFont;
import me.jinthium.straight.impl.utils.font.FontUtil;
import net.minecraft.client.gui.FontRenderer;

import java.util.Arrays;

public class FontBinding implements MinecraftInstance, Util {

    public AbstractFontRenderer getCustomFont(String fontName, int fontSize) {
        FontUtil.FontType fontType = Arrays.stream(FontUtil.FontType.values()).filter(fontType1 -> fontType1.name().equals(fontName)).findFirst().orElse(FontUtil.FontType.NORMAL);
        return fontType.size(fontSize);
    }

    public FontRenderer getMinecraftFontRenderer() {
        return mc.fontRendererObj;
    }

    public CustomFont getNormalFont18() {
        return normalFont19;
    }

    public CustomFont getNormalFont20() {
        return normalFont20;
    }

    public CustomFont getIconFont20() {
        return iconFont20;
    }

    public CustomFont getNormalFont22() {
        return normalFont22;
    }

    public CustomFont getNormalFont40() {
        return normalFont40;
    }
}
