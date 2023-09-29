package me.jinthium.straight.api.clickgui.dropdown.components;

import me.jinthium.straight.api.clickgui.dropdown.DropdownClickGUI;
import me.jinthium.straight.api.clickgui.dropdown.components.config.ConfigButton;
import me.jinthium.straight.api.clickgui.dropdown.components.config.ConfigRect;
import me.jinthium.straight.api.clickgui.dropdown.components.settings.TextField;
import me.jinthium.straight.api.config.LocalConfig;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.modules.visual.ClickGui;
import me.jinthium.straight.impl.shaders.GaussianBlur;
import me.jinthium.straight.impl.ui.components.Button;
import me.jinthium.straight.impl.utils.ChatUtil;
import me.jinthium.straight.impl.utils.Scroll;
import me.jinthium.straight.impl.utils.file.FileUtils;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.src.Config;
import org.lwjglx.Sys;
import org.lwjglx.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfigPanel extends GuiScreen implements Util {

    private final CopyOnWriteArrayList<ConfigInstance> configRects = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ConfigButton> loadButtons = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ConfigButton> saveButtons = new CopyOnWriteArrayList<>();
    private ConfigInstance hoveringInstance;

    private Scroll scroll = new Scroll();
    private final TextField textField = new TextField(normalFont16);
    private boolean pressed = false;

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if(keyCode == Keyboard.KEY_ESCAPE){
            mc.displayGuiScreen(ClickGui.dropdownClickGui);
        }
        textField.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        GaussianBlur.startBlur();
        Gui.drawRect2(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), Color.black.getRGB());
        GaussianBlur.endBlur(8, 2);

        float rectX = scaledResolution.getScaledWidth() / 2f - 317.5f;
        float rectY = scaledResolution.getScaledHeight() / 2f - 150;
        float rectWidth = 635;
        float rectHeight = 300;

        RoundedUtil.drawRound(rectX, rectY, rectWidth, rectHeight, 8, new Color(40, 40, 40));
        normalFont40.drawStringWithShadow("Configs", rectX + 5, rectY + 5, -1);
        RoundedUtil.drawRound(rectX + 2, rectY + normalFont40.getHeight() + 3, rectWidth - 2, 4, 0, new Color(25, 25, 25));

        scroll.onScroll(50);
        RenderUtil.scissorStart(rectX + 4, rectY + normalFont40.getHeight() + 9, rectWidth - 2, 265);
        for (int i = 0; i < configRects.size(); i++) {
            ConfigInstance configInstance = configRects.get(i);

            float x = rectX + 5 + (i % 6) * 105;  // Increment x by 55 every 4th index
            float y = (float) ((rectY + normalFont40.getHeight() + 10 + (i / 6) * 105) + MathUtils.roundToHalf(scroll.getScroll()));  // Increment y by 55 every 4th index

            RoundedUtil.drawRound(x, y, 100, 100, 8, new Color(25, 25, 25));
            normalFont22.drawStringWithShadow(configInstance.getName(), x + 4, y + 6, -1);
            normalFont18.drawStringWithShadow(configInstance.getCreationDate(), x + 4, y + 8 + normalFont22.getHeight(), Color.lightGray.getRGB());

            if(HoveringUtil.isHovering(x, y, 100, 100, mouseX, mouseY))
                hoveringInstance = configInstance;

            loadButtons.add(new ConfigButton(x + 20, y + 10 + normalFont22.getHeight() + normalFont18.getHeight() + 4, configInstance, ConfigButton.ButtonMode.LOAD));
            saveButtons.add(new ConfigButton(x + 20, y + 10 + normalFont22.getHeight() + normalFont18.getHeight() + 28, configInstance, ConfigButton.ButtonMode.SAVE));
        }
        scroll.setMaxScroll(rectY + normalFont40.getHeight() + 10 + ((float) configRects.size() / 6) * 10);

        loadButtons.forEach(ConfigButton::draw);
        saveButtons.forEach(ConfigButton::draw);
        RenderUtil.scissorEnd();

//        RoundedUtil.drawRound((float) scaledResolution.getScaledWidth() / 2 - 150, 80, 300, 30, 4, new Color(30, 30, 30));
//
//        textField.setBackgroundText("config...");
//        textField.setXPosition(scaledResolution.getScaledWidth() / 2 - 140);
//        textField.setYPosition(85);
//        textField.setWidth(50);
//        textField.setHeight(10);
//        textField.setFill(new Color(30, 30, 30));
//        textField.setOutline(new Color(40, 40, 40));
//
//        textField.drawTextBox();
//
//        RoundedUtil.drawRound((float) scaledResolution.getScaledWidth() / 2 - 150, 100, 300, 300, 4, new Color(40, 40, 40));
//        float index = 0;
//        float initialX = (float) scaledResolution.getScaledWidth() / 2 - 150;
//        RenderUtil.scissorStart(initialX, 101, 300, 299);
//        for(ConfigRect configRect : configRects){
//            configRect.drawScreen(mouseX, mouseY);
//            configRect.setX(initialX);
//            configRect.setY((float) (100 + (index * 20) + MathUtils.roundToHalf(scroll.getScroll())));
//            index += 1 + (configRects.size() / 32f);
//
//            scroll.setMaxScroll(100 + (index * 3.4f));
//        }
//        RenderUtil.scissorEnd();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        pressed = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//        configRects.forEach(configRect -> configRect.mouseClicked(mouseX, mouseY, mouseButton));
        if(mouseButton == 0) {
            if(pressed)
                return;

            saveButtons.forEach(saveButton -> {
                if(HoveringUtil.isHovering(saveButton.x, saveButton.y, 60, 20, mouseX, mouseY) && !pressed){
                    pressed = true;
                    saveButton.configInstance.saveConfig();
                }
            });
            loadButtons.forEach(loadButton -> {
                if(HoveringUtil.isHovering(loadButton.x, loadButton.y, 60, 20, mouseX, mouseY) && !pressed){
                    pressed = true;
                    loadButton.configInstance.loadConfig();
                }
            });
        }
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        Client.INSTANCE.getConfigManager().collectConfigs();
        saveButtons.clear();
        loadButtons.clear();
        configRects.clear();
        for(LocalConfig localConfig : Client.INSTANCE.getConfigManager().localConfigs){
            configRects.add(new ConfigInstance(localConfig, localConfig.getName(), localConfig.getCreationDate()));
        }
        super.updateScreen();
    }

    public class ConfigButton {
        private final ConfigInstance configInstance;
        private final ButtonMode buttonMode;
        private final float x, y;

        public ConfigButton(float x, float y, ConfigInstance configInstance, ButtonMode buttonMode){
            this.x = x;
            this.y = y;
            this.configInstance = configInstance;
            this.buttonMode = buttonMode;
        }

        public void draw(){
//            Gui.drawRect2(x, y, 60, 20, -1);
            RoundedUtil.drawRound(this.x, this.y, 60, 20, 8, new Color(40, 40, 40));
            normalFont22.drawStringWithShadow(this.buttonMode.getModeName(), this.x + normalFont22.getStringWidth(this.buttonMode.getModeName()) / 2f + 3, this.y + normalFont22.getHeight() / 2f + 3, -1);
        }

        public void mousePress(int mouseX, int mouseY) {
//            System.out.printf("%s:%s%n", x, y);
            if(HoveringUtil.isHovering(x, y, 60, 20, mouseX, mouseY)){
                ChatUtil.printSystem(this.buttonMode.getModeName());
                switch(this.buttonMode){
                    case LOAD -> this.configInstance.loadConfig();
                    case SAVE -> this.configInstance.saveConfig();
                }
            }
        }

        private enum ButtonMode{
            LOAD("Load"),
            SAVE("Save");

            private final String modeName;

            ButtonMode(String modeName){
                this.modeName = modeName;
            }

            public String getModeName() {
                return modeName;
            }
        }
    }

    public class ConfigInstance {
        private final LocalConfig config;
        private final String name, creationDate;

        public ConfigInstance(LocalConfig config, String name, String creationDate){
            this.config = config;
            this.name = name;
            this.creationDate = creationDate;
        }

        public LocalConfig getConfig() {
            return config;
        }

        public String getName() {
            return name;
        }

        public String getCreationDate() {
            return creationDate;
        }

        public void saveConfig(){
            Client.INSTANCE.getConfigManager().saveConfig(this.name);
        }

        public void loadConfig(){
            String loadData = FileUtils.readFile(config.getFile());
            Client.INSTANCE.getConfigManager().loadConfig(loadData);
        }
    }
}
