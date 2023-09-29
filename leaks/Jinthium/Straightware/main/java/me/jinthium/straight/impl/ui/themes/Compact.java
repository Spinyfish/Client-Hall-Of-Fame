package me.jinthium.straight.impl.ui.themes;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import me.jinthium.straight.api.clickgui.theme.Theme;
import me.jinthium.straight.api.config.LocalConfig;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.settings.*;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.file.FileUtils;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import org.lwjglx.input.Keyboard;

public class Compact extends Theme {

    @Override
    public void render(){
        ImGui.setNextWindowSize(768, 600, ImGuiCond.Always);
        if(ImGui.begin("Click Gui", ImGuiWindowFlags.NoResize)){
            for(Module.Category category : Module.Category.values()){
                if(Client.INSTANCE.getModuleManager().getModulesInCategory(category).isEmpty() || category == Module.Category.SCRIPTS)
                    continue;


                if(!(category == Module.Category.COMBAT)) ImGui.sameLine();
                if(ImGui.button(category.getIcon() + " " + category.getCategoryName())){
                    Client.INSTANCE.getCInterface().setCurrentCategory(category);
                }
            }

            ImGui.sameLine();
            if(ImGui.button("Config")) {
                Client.INSTANCE.getCInterface().setCurrentCategory(Module.Category.CONFIG);
            }

            ImGui.sameLine();
            if(ImGui.button("Scripts")) {
                Client.INSTANCE.getCInterface().setCurrentCategory(Module.Category.SCRIPTS);
            }

            if(Client.INSTANCE.getCInterface().getCurrentCategory() == Module.Category.CONFIG){
                ImGui.inputText("Config ", Client.INSTANCE.getCInterface().getConfigName());

                if(ImGui.button("Save/Create")){
                    Client.INSTANCE.getConfigManager().saveConfig(Client.INSTANCE.getCInterface().getConfigName().get());
                }

                ImGui.sameLine();
                if(ImGui.button("Delete")){
                    Client.INSTANCE.getConfigManager().delete(Client.INSTANCE.getCInterface().getConfigName().get());
                }
                ImGui.separator();
                for(LocalConfig config : Client.INSTANCE.getConfigManager().localConfigs){
                    String loadData = FileUtils.readFile(config.getFile());
                    if(Client.INSTANCE.getConfigManager().localConfigs.indexOf(config) % 8 == 0){
                        if(ImGui.button(config.getName())){
                            Client.INSTANCE.getConfigManager().loadConfig(loadData, true);
                        }
                    }else{
                        ImGui.sameLine();
                        if(ImGui.button(config.getName())){
                            Client.INSTANCE.getConfigManager().loadConfig(loadData, true);
                        }
                    }
                }
            }

            if(Client.INSTANCE.getCInterface().getCurrentCategory() == Module.Category.SCRIPTS){
                if(ImGui.button("Reload")){
                    Client.INSTANCE.getScriptManager().reloadScripts();
                }
            }



            for(Module module: Client.INSTANCE.getModuleManager().getModulesInCategory(Client.INSTANCE.getCInterface().getCurrentCategory())) {
                StringBuilder stringBuilder1 = new StringBuilder();
                for (int i = 0; i < Client.INSTANCE.getModuleManager().getModulesInCategory(Client.INSTANCE.getCInterface().getCurrentCategory()).indexOf(module); i++) {
                    stringBuilder1.append(" ");
                }
                if (ImGui.collapsingHeader(module.getName())) {
                    if (ImGui.checkbox("Enabled" + stringBuilder1, module.isEnabled())) {
                        module.toggle();
                    }

//                    if (ImGui.beginMenu("Settings" + stringBuilder1)) {
//
//
//                if(ImGui.checkbox("Settings" + stringBuilder1.toString(), module.isExtended())){
//                    module.setExtended(!module.isExtended());
//                }

                    ImGui.indent();
                    for (Setting setting : module.getSettingsList()) {
                        if (setting == null || module.getSettingsList().isEmpty() || setting.cannotBeShown())
                            continue;

                        if (setting instanceof KeybindSetting) {
                            KeybindSetting keybindSetting = (KeybindSetting) setting;
                            StringBuilder stringBuilder = new StringBuilder();
                            String funny = (Client.INSTANCE.getCInterface().getBinding() == module && Client.INSTANCE.getCInterface().isListening()) ? "Keybind: LISTENING" : "Keybind: " + Keyboard.getKeyName(keybindSetting.getCode());

                            for (int i = 0; i < Client.INSTANCE.getModuleManager().getModulesInCategory(Client.INSTANCE.getCInterface().getCurrentCategory()).indexOf(module); i++) {
                                stringBuilder.append(" ");
                            }

                            if (ImGui.button(funny + stringBuilder)) {
                                System.out.println("aaa");
                                Client.INSTANCE.getCInterface().setBinding(module);
                                Client.INSTANCE.getCInterface().setListening(true);
                            }
                        }

                        if(setting instanceof NewModeSetting nms){
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < Client.INSTANCE.getModuleManager().getModulesInCategory(Client.INSTANCE.getCInterface().getCurrentCategory()).indexOf(module); i++) {
                                stringBuilder.append(" ");
                            }

                            for(ModuleMode<?> mode : nms.getValues()){
                                if(mode == module.getCurrentMode() && !mode.getSettings().isEmpty()){
                                    for(Setting settingss : mode.getSettings().keySet()){
                                        if(!module.getSettingsList().contains(settingss)) {
                                            settingss.addParent(nms, r -> (mode == module.getCurrentMode()));
                                            module.addSettings(settingss);
                                        }
                                    }
                                }
                            }

                            if(ImGui.beginCombo(nms.getName() + stringBuilder, nms.getCurrentMode().getName())){
                                for(ModuleMode<?> mode : nms.getValues()){
                                    if(ImGui.selectable(mode.getName())){
                                        nms.setValue(mode);
                                    }
                                }
                                ImGui.endCombo();
                            }
                        }

                        if (setting instanceof ModeSetting) {
                            StringBuilder stringBuilder = new StringBuilder();
                            ModeSetting modeSetting = (ModeSetting) setting;
                            for (int i = 0; i < Client.INSTANCE.getModuleManager().getModulesInCategory(Client.INSTANCE.getCInterface().getCurrentCategory()).indexOf(module); i++) {
                                stringBuilder.append(" ");
                            }

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
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < Client.INSTANCE.getModuleManager().getModulesInCategory(Client.INSTANCE.getCInterface().getCurrentCategory()).indexOf(module); i++) {
                                stringBuilder.append(" ");
                            }


                            final NumberSetting numberSetting = (NumberSetting) setting;
                            if (ImGui.sliderFloat(numberSetting.getName() + stringBuilder, numberSetting.getImEquivalent().getData(), (float) numberSetting.getMinValue(), (float) numberSetting.getMaxValue(), numberSetting.getIncrement() == 1 ? "%.0f" : "%.2f")) {
                                numberSetting.setValue(MathUtils.round(numberSetting.getImEquivalent().get(), 2, numberSetting.getIncrement()));
                                numberSetting.getImEquivalent().set(numberSetting.getValue().floatValue());
                            }
                        }
                        if (setting instanceof MultiBoolSetting) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < Client.INSTANCE.getModuleManager().getModulesInCategory(Client.INSTANCE.getCInterface().getCurrentCategory()).indexOf(module); i++) {
                                stringBuilder.append(" ");
                            }

                            MultiBoolSetting multiBoolSetting = (MultiBoolSetting) setting;
                            ImGui.text(multiBoolSetting.getName() + stringBuilder);

                            for (BooleanSetting bool : multiBoolSetting.getBoolSettings()) {
                                if (ImGui.checkbox(bool.getName() + stringBuilder, bool.isEnabled())) {
                                    bool.toggle();
                                }
                            }
                        }
                        if (setting instanceof BooleanSetting) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < Client.INSTANCE.getModuleManager().getModulesInCategory(Client.INSTANCE.getCInterface().getCurrentCategory()).indexOf(module); i++) {
                                stringBuilder.append(" ");
                            }

                            BooleanSetting booleanSetting = (BooleanSetting) setting;
                            if (ImGui.checkbox(booleanSetting.getName() + stringBuilder, booleanSetting.isEnabled())) {
                                booleanSetting.toggle();
                            }
                        }

                        if (setting instanceof ColorSetting) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < Client.INSTANCE.getModuleManager().getModulesInCategory(Client.INSTANCE.getCInterface().getCurrentCategory()).indexOf(module); i++) {
                                stringBuilder.append(" ");
                            }

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
    }
}
