package me.jinthium.straight.impl.ui.themes;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import me.jinthium.straight.api.clickgui.theme.Theme;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.settings.*;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import org.lwjglx.input.Keyboard;

public class Dropdown extends Theme {

    @Override
    public void render(){
        StringBuilder stringBuilder = new StringBuilder();
        int index = 1;
        float height = 20;
        float x = 25;
        for(Module.Category category : Module.Category.values()){
            if(category == Module.Category.SCRIPTS || category == Module.Category.CONFIG)
                continue;
//            ImGui.
            ImGui.setNextWindowSize(300, 300, ImGuiCond.Always);
            ImGui.setNextWindowPos(x, 40);
            if(ImGui.begin(category.getCategoryName(), ImGuiWindowFlags.NoResize)){
                for(Module module : Client.INSTANCE.getModuleManager().getModulesInCategory(category)){
                    stringBuilder.append(" ".repeat(Math.max(0, Client.INSTANCE.getModuleManager().getModules().indexOf(module))));

                    if(ImGui.collapsingHeader(module.getName() + stringBuilder)) {
                        if(ImGui.checkbox("Enabled" + stringBuilder, module.isEnabled())){
                            module.toggle();
                        }

                        ImGui.indent();
                        for (Setting setting : module.getSettingsList()) {
                            if (setting == null || module.getSettingsList().isEmpty() || setting.cannotBeShown())
                                continue;

                            if (setting instanceof KeybindSetting) {
                                KeybindSetting keybindSetting = (KeybindSetting) setting;
                                String funny = (Client.INSTANCE.getCInterface().getBinding() == module && Client.INSTANCE.getCInterface().isListening()) ? "Keybind: LISTENING" : "Keybind: " + Keyboard.getKeyName(keybindSetting.getCode());
                                if (ImGui.button(funny + stringBuilder)) {
                                    System.out.println("aaa");
                                    Client.INSTANCE.getCInterface().setBinding(module);
                                    Client.INSTANCE.getCInterface().setListening(true);
                                }
                            }

                            if (setting instanceof ModeSetting) {
                                ModeSetting modeSetting = (ModeSetting) setting;
                                if (ImGui.beginCombo(modeSetting.getName() + stringBuilder, modeSetting.getMode())) {
                                    for (String mode : modeSetting.modes) {
                                        if (ImGui.selectable(mode)) {
                                            modeSetting.setCurrentMode(mode);
                                        }
                                    }
                                    ImGui.endCombo();
                                }
                            }
                            if (setting instanceof NumberSetting) {
                                final NumberSetting numberSetting = (NumberSetting) setting;
                                if (ImGui.sliderFloat(numberSetting.getName() + stringBuilder, numberSetting.getImEquivalent().getData(), (float) numberSetting.getMinValue(), (float) numberSetting.getMaxValue(), numberSetting.getIncrement() == 1 ? "%.0f" : "%.2f")) {
                                    numberSetting.setValue(MathUtils.round(numberSetting.getImEquivalent().get(), 2, numberSetting.getIncrement()));
                                    numberSetting.getImEquivalent().set(numberSetting.getValue().floatValue());
                                }
                            }
                            if (setting instanceof MultiBoolSetting) {
                                MultiBoolSetting multiBoolSetting = (MultiBoolSetting) setting;
                                ImGui.text(multiBoolSetting.getName() + stringBuilder);

                                for (BooleanSetting bool : multiBoolSetting.getBoolSettings()) {
                                    if (ImGui.checkbox(bool.getName() + stringBuilder, bool.isEnabled())) {
                                        bool.toggle();
                                    }
                                }
                            }
                            if (setting instanceof BooleanSetting) {
                                BooleanSetting booleanSetting = (BooleanSetting) setting;
                                if (ImGui.checkbox(booleanSetting.getName() + stringBuilder, booleanSetting.isEnabled())) {
                                    booleanSetting.toggle();
                                }
                            }

                            if (setting instanceof ColorSetting) {
                                ColorSetting colorSetting = (ColorSetting) setting;
                                float[] color = ColorUtil.toGLColor(colorSetting.getColor().getRGB());
                                if (ImGui.colorEdit4(colorSetting.getName() + stringBuilder, color)) {
                                    colorSetting.setColor(ColorUtil.toColor(color));
                                }
                            }

                            if (setting instanceof StringSetting) {
                                StringSetting stringSetting = (StringSetting) setting;
                                if (ImGui.inputTextWithHint(stringSetting.getName() + " ", "Input Hud Name Here", stringSetting.getImEquiv(), ImGuiInputTextFlags.None)) {
                                    stringSetting.setString(stringSetting.getImEquiv().get());
                                }
                            }
                        }
                        ImGui.unindent();
                    }
                }
            }
            ImGui.end();
//            RenderUtil.scaleEnd();
            index++;
            x += 310;
        }
    }
}
