package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.gui.GuiButtonDownloadShaders;
import net.optifine.shaders.gui.GuiButtonEnumShaderOption;

import java.awt.*;

public class TooltipProviderEnumShaderOptions implements TooltipProvider {
    public Rectangle getTooltipBounds(final GuiScreen guiScreen, final int x, final int y) {
        int i = guiScreen.width - 450;
        int j = 35;

        if (i < 10) {
            i = 10;
        }

        if (y <= j + 94) {
            j += 100;
        }

        final int k = i + 150 + 150;
        final int l = j + 84 + 10;
        return new Rectangle(i, j, k - i, l - j);
    }

    public boolean isRenderBorder() {
        return true;
    }

    public String[] getTooltipLines(final GuiButton btn, final int width) {
        if (btn instanceof GuiButtonDownloadShaders) {
            return TooltipProviderOptions.getTooltipLines("of.options.shaders.DOWNLOAD");
        } else if (!(btn instanceof GuiButtonEnumShaderOption)) {
            return null;
        } else {
            final GuiButtonEnumShaderOption guibuttonenumshaderoption = (GuiButtonEnumShaderOption) btn;
            final EnumShaderOption enumshaderoption = guibuttonenumshaderoption.getEnumShaderOption();
            final String[] astring = this.getTooltipLines(enumshaderoption);
            return astring;
        }
    }

    private String[] getTooltipLines(final EnumShaderOption option) {
        return TooltipProviderOptions.getTooltipLines(option.getResourceKey());
    }
}
