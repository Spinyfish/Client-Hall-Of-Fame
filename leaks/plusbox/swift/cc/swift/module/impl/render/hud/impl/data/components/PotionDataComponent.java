package cc.swift.module.impl.render.hud.impl.data.components;

import cc.swift.Swift;
import cc.swift.module.impl.render.hud.HudFont;
import cc.swift.module.impl.render.hud.impl.data.DataComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.String.join;
import static java.util.Collections.nCopies;

public class PotionDataComponent extends DataComponent {


    @Override
    public void render(HudFont font, float posX, float posY) {
        int scaledHeight = Swift.INSTANCE.getSr().getScaledHeight();
        int scaledWidth = Swift.INSTANCE.getSr().getScaledWidth();

        int counter = 1;
        float y = 10;

        Minecraft mc = Minecraft.getMinecraft();
        ArrayList<PotionEffect> potionEffects = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        Collections.sort(potionEffects, Comparator.comparingDouble(effect -> font.getStringWidth(I18n.format(Potion.potionTypes[effect.getPotionID()].getName()) + " " + Potion.getDurationString(effect))));
        Collections.reverse(potionEffects);
        for (PotionEffect effect : potionEffects) {
            final Potion potion = Potion.potionTypes[effect.getPotionID()];
            final StringBuilder stringBuilder = new StringBuilder(I18n.format(potion.getName()));

            stringBuilder.append(" ").append(getRomanNumber(effect.getAmplifier() + 1));

            // You can also do /247 for the § symbol
            if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                stringBuilder.append("§7§6 ").append(Potion.getDurationString(effect));
            } else if (effect.getDuration() < 300) {
                stringBuilder.append("§7§c ").append(Potion.getDurationString(effect));
            } else if (effect.getDuration() > 600) {
                stringBuilder.append("§7§7 ").append(Potion.getDurationString(effect));
            }

            final String text = String.valueOf(stringBuilder);
            final Color color = new Color(potion.getLiquidColor());

            font.drawStringWithShadow(text, (int) ((scaledWidth - font.getStringWidth(text) - 1) - posX),
                    (int) (scaledHeight - font.getFontHeight() - posY + y), color.getRGB());


            counter++;
            y -= mc.fontRendererObj.FONT_HEIGHT + 1;

        }
        this.lines = counter;
    }



    // Insane stackoverflow
    public String getRomanNumber(int number) {
        try {
            return join("", nCopies(number, "I"))
                    .replace("IIIII", "V")
                    .replace("IIII", "IV")
                    .replace("VV", "X")
                    .replace("VIV", "IX")
                    .replace("XXXXX", "L")
                    .replace("XXXX", "XL")
                    .replace("LL", "C")
                    .replace("LXL", "XC")
                    .replace("CCCCC", "D")
                    .replace("CCCC", "CD")
                    .replace("DD", "M")
                    .replace("DCD", "CM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
