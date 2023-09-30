package dev.africa.pandaware.manager.script.module.setting;

import dev.africa.pandaware.impl.script.Script;
import dev.africa.pandaware.impl.setting.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.awt.*;

@Getter
@RequiredArgsConstructor
public class ScriptSettingManager {
    private final Script script;

    public BooleanSetting registerBoolean(String name, boolean value) {
        val booleanSetting = new BooleanSetting(name, value);

        this.script.getModule().registerSettings(booleanSetting);

        return booleanSetting;
    }

    public NumberSetting registerNumber(String name,
                                        double maxValue, double minValue,
                                        double value, double increase) {
        val numberSetting = new NumberSetting(name, maxValue, minValue, value, increase);

        this.script.getModule().registerSettings(numberSetting);

        return numberSetting;
    }

    public NumberRangeSetting registerNumberRange(String name,
                                                  double maxValue, double minValue,
                                                  double firstValue, double secondValue,
                                                  double increase) {
        val numberRangeSetting = new NumberRangeSetting(name, maxValue, minValue, firstValue, secondValue, increase);

        this.script.getModule().registerSettings(numberRangeSetting);

        return numberRangeSetting;
    }

    public ColorSetting registerColor(String name, int red, int green, int blue) {
        val colorSetting = new ColorSetting(name, new Color(red, green, blue));

        this.script.getModule().registerSettings(colorSetting);

        return colorSetting;
    }

    public TextBoxSetting registerText(String name, String value) {
        val textBoxSetting = new TextBoxSetting(name, value);

        this.script.getModule().registerSettings(textBoxSetting);

        return textBoxSetting;
    }
}
