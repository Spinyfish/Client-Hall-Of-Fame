package de.fanta.clickgui.defaultgui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.utils.RenderUtil;

import java.util.ArrayList;

public class SettingsContainer {
    private float x;
    private float y;
    public Module curModule;
    private CategoryPanel panel;
    public boolean scroll;
    private float scrollY;
    private float settingY;

    private ArrayList<CheckBox> checkBoxes;
    private ArrayList<Slider> sliders;
    private ArrayList<DropdownBox> dropdownBoxes;
    private ArrayList<TextField> textFields;

    public SettingsContainer(CategoryPanel panel, Module curModule, float x, float y) {
        this.panel = panel;
        this.curModule = curModule;
        this.x = x;
        this.y = y;

        this.checkBoxes = new ArrayList<>();
        this.sliders = new ArrayList<>();
        this.dropdownBoxes = new ArrayList<>();
        this.textFields = new ArrayList<>();

        float settingY = 5;
        //To change x
        for(int i = 0; i < curModule.settings.size(); i++) {
            Setting s = curModule.settings.get(i);

            if(s.getSetting() instanceof de.fanta.setting.settings.Slider) {
                settingY += 5;
                sliders.add(new Slider(s, panel, x + 75, y + settingY));
                settingY += 2;
            }

            if(s.getSetting() instanceof de.fanta.setting.settings.CheckBox) {
                settingY += 5;
                checkBoxes.add(new CheckBox(s, panel, x + 75, y + settingY - 5));
            }

            if(s.getSetting() instanceof de.fanta.setting.settings.TextField) {
                settingY += 5;
                textFields.add(new TextField(s, panel, x + 75, y + settingY - 5));
            }

            if(s.getSetting() instanceof de.fanta.setting.settings.DropdownBox) {
                //Y to change the distance between the other boxes ---> make it to check itsself
                settingY += 30;

                DropdownBox dropdownBox = new DropdownBox(s, panel, x + 75, y + settingY - 10);
                dropdownBoxes.add(dropdownBox);

                if(i == curModule.settings.size() - 1)
                    settingY += dropdownBox.buttonY;
            }

            settingY += 10;
        }

        if(settingY > 215) {
            scroll = true;
            this.settingY = settingY;
        }
    }

    public void drawSettingsContainer(float mouseX, float mouseY) {
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        GlStateManager.pushMatrix();
        RenderUtil.scissorBox(x + xOff, y + yOff + 0.5F, x + xOff + 325, y + yOff + 212.5F);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        this.checkBoxes.forEach(checkBox -> checkBox.drawCheckBox(mouseX, mouseY));
        this.dropdownBoxes.forEach(dropdownBox -> dropdownBox.drawDropdownBox(mouseX, mouseY));
        this.sliders.forEach(slider -> slider.drawSlider(mouseX, mouseY));
        this.textFields.forEach(textField -> textField.drawTextField(mouseX, mouseY));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    public void settingsContainerClicked(float mouseX, float mouseY, int mouseButton) {
        float yOff = panel.cateButton.panel.dragY;

        this.checkBoxes.forEach(checkBox -> {
            float checkBoxPos = checkBox.y + yOff;
            if(checkBoxPos < y + yOff || checkBoxPos > y + yOff + 212.5F)
                return;

            checkBox.checkBoxClicked(mouseX, mouseY, mouseButton);
        });
        this.dropdownBoxes.forEach(dropdownBox -> {
            float dropdownBoxPos = dropdownBox.y + yOff;
            if(dropdownBoxPos < y + yOff || dropdownBoxPos > y + yOff + 212.5F)
                return;

            dropdownBox.dropdownBoxClicked(mouseX, mouseY, mouseButton);
        });
        this.sliders.forEach(slider -> {
            float sliderPos = slider.y + yOff;
            if(sliderPos < y + yOff || sliderPos > y + yOff + 212.5F)
                return;

            slider.sliderClicked(mouseX, mouseY, mouseButton);
        });

        this.textFields.forEach(textField -> {
            float textFieldPos = textField.y + yOff;
            if(textFieldPos < y + yOff || textFieldPos > y + yOff + 212.5F)
                return;

            textField.textFieldClicked(mouseX, mouseY, mouseButton);
        });

    }

    public void settingsContainerReleased(float mouseX, float mouseY, int state) {
        this.sliders.forEach(slider -> slider.sliderReleased(mouseX, mouseY, state));
    }

    public void settingsContainerHandleInput() {
        int mouseWheel = Mouse.getEventDWheel() / 10;

        scrollY -= mouseWheel;

        if(scroll && mouseWheel != 0) {
            float diff = settingY - 215;

            if(scrollY > -diff) {
                scrollY = -diff;
                return;
            }

            if(scrollY + diff < -diff - 12) {
                scrollY += mouseWheel;
                return;
            }

            this.checkBoxes.forEach(checkBox -> checkBox.y -= mouseWheel);
            this.textFields.forEach(textField -> textField.y -= mouseWheel);
            this.dropdownBoxes.forEach(dropdownBox -> {
                dropdownBox.y -= mouseWheel;

                for(DropdownButton button : dropdownBox.dropdownButtons)
                    button.y -= mouseWheel;
            });
            this.sliders.forEach(slider -> slider.y -= mouseWheel);
        }
    }
}
