package net.optifine.gui;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.gui.GuiButtonShaderOption;
import net.optifine.util.StrUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TooltipProviderShaderOptions extends TooltipProviderOptions {
    public String[] getTooltipLines(final GuiButton btn, final int width) {
        if (!(btn instanceof GuiButtonShaderOption)) {
            return null;
        } else {
            final GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption) btn;
            final ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
            final String[] astring = this.makeTooltipLines(shaderoption, width);
            return astring;
        }
    }

    private String[] makeTooltipLines(final ShaderOption so, final int width) {
        final String s = so.getNameText();
        final String s1 = Config.normalize(so.getDescriptionText()).trim();
        final String[] astring = this.splitDescription(s1);
        final GameSettings gamesettings = Config.getGameSettings();
        String s2 = null;

        if (!s.equals(so.getName()) && gamesettings.advancedItemTooltips) {
            s2 = "\u00a78" + Lang.get("of.general.id") + ": " + so.getName();
        }

        String s3 = null;

        if (so.getPaths() != null && gamesettings.advancedItemTooltips) {
            s3 = "\u00a78" + Lang.get("of.general.from") + ": " + Config.arrayToString(so.getPaths());
        }

        String s4 = null;

        if (so.getValueDefault() != null && gamesettings.advancedItemTooltips) {
            final String s5 = so.isEnabled() ? so.getValueText(so.getValueDefault()) : Lang.get("of.general.ambiguous");
            s4 = "\u00a78" + Lang.getDefault() + ": " + s5;
        }

        final List<String> list = new ArrayList();
        list.add(s);
        list.addAll(Arrays.asList(astring));

        if (s2 != null) {
            list.add(s2);
        }

        if (s3 != null) {
            list.add(s3);
        }

        if (s4 != null) {
            list.add(s4);
        }

        final String[] astring1 = this.makeTooltipLines(width, list);
        return astring1;
    }

    private String[] splitDescription(String desc) {
        if (desc.length() <= 0) {
            return new String[0];
        } else {
            desc = StrUtils.removePrefix(desc, "//");
            final String[] astring = desc.split("\\. ");

            for (int i = 0; i < astring.length; ++i) {
                astring[i] = "- " + astring[i].trim();
                astring[i] = StrUtils.removeSuffix(astring[i], ".");
            }

            return astring;
        }
    }

    private String[] makeTooltipLines(final int width, final List<String> args) {
        final FontRenderer fontrenderer = Config.getMinecraft().fontRendererObj;
        final List<String> list = new ArrayList();

        for (int i = 0; i < args.size(); ++i) {
            final String s = args.get(i);

            if (s != null && s.length() > 0) {
                for (final String s1 : fontrenderer.listFormattedStringToWidth(s, width)) {
                    list.add(s1);
                }
            }
        }

        final String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}
