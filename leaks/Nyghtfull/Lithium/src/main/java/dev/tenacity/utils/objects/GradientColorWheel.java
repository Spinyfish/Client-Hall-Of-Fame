package dev.tenacity.utils.objects;

import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.ColorSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.render.ColorUtil;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class GradientColorWheel {
    private Color color1, color2, color3, color4;
    private ModeSetting colorMode;
    private ColorSetting colorSetting;

    public GradientColorWheel(){
        color1 = Color.BLACK;
        color2 = Color.BLACK;
        color3 = Color.BLACK;
        color4 = Color.BLACK;
    }

    public ModeSetting createModeSetting(String name, String... extraModes) {
        List<String> modesList = new ArrayList<>();
        modesList.add("Sync");
        modesList.add("Custom");
        modesList.addAll(Arrays.asList(extraModes));

        colorMode = new ModeSetting(name, "Sync", modesList.toArray(new String[0]));
        colorSetting = new ColorSetting("Custom Color", Color.PINK);
        colorSetting.addParent(colorMode, modeSetting -> modeSetting.is("Custom"));
        return colorMode;
    }

    public void setColorsForMode(String mode, Color color){
        setColorsForMode(mode, color, color, color, color);
    }


    public void setColorsForMode(String mode, Color color1, Color color2, Color color3, Color color4) {
        if (colorMode.is(mode)) {
            this.color1 = color1;
            this.color2 = color2;
            this.color3 = color3;
            this.color4 = color4;
        }
    }

    public void setColors() {
        int secondIndex = HUDMod.drawRadialGradients() ? 90 : 35;
        if (colorMode.is("Sync")) {
            if (HUDMod.isRainbowTheme()) {
                color1 = HUDMod.color1.getRainbow().getColor(0);
                color2 = HUDMod.color1.getRainbow().getColor(secondIndex);
                color3 = HUDMod.color1.getRainbow().getColor(180);
                color4 = HUDMod.color1.getRainbow().getColor(270);
            } else {
                setWheel(HUDMod.getClientColors());
            }
        } else if (colorMode.is("Custom")) {
            if (colorSetting.isRainbow()) {
                color1 = colorSetting.getRainbow().getColor(0);
                color2 = colorSetting.getRainbow().getColor(secondIndex);
                color3 = colorSetting.getRainbow().getColor(180);
                color4 = colorSetting.getRainbow().getColor(270);
            } else {
                setWheel(Pair.of(colorSetting.getColor(), colorSetting.getAltColor()));
            }
        }

        if(!HUDMod.drawRadialGradients()){
            color4 = color1;
            color3 = color2;
        }
    }

    private void setWheel(Pair<Color, Color> colors) {
        int secondIndex = HUDMod.drawRadialGradients() ? 90 : 35;
        color1 = ColorUtil.interpolateColorsBackAndForth(15, 0, colors.getFirst(), colors.getSecond(), false);
        color2 = ColorUtil.interpolateColorsBackAndForth(15, secondIndex, colors.getFirst(), colors.getSecond(), false);
        color3 = ColorUtil.interpolateColorsBackAndForth(15, 180, colors.getFirst(), colors.getSecond(), false);
        color4 = ColorUtil.interpolateColorsBackAndForth(15, 270, colors.getFirst(), colors.getSecond(), false);
    }


}
