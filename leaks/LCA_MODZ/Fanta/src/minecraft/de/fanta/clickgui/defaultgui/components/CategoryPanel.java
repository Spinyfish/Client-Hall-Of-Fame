package de.fanta.clickgui.defaultgui.components;

import java.util.ArrayList;

import de.fanta.Client;
import de.fanta.module.Module;

public class CategoryPanel {
    public CategoryButton cateButton;
    public float lengthModule;
    private SettingsContainer settingsContainer;
    private float x;
    private float y;

    public ArrayList<ModuleButton> moduleButtons = new ArrayList<>();

    public CategoryPanel(CategoryButton cateButton, float x, float y) {
        this.cateButton = cateButton;
        this.x = x;
        this.y = y;

        float moduleY = 4;
        for (Module module : Client.INSTANCE.moduleManager.modules) {
            if(module.type.equals(cateButton.type)) {
                moduleButtons.add(new ModuleButton(module, this, x + 2, y + moduleY));

                float length =   Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(module.name);
                if(length > lengthModule) lengthModule = length;

                moduleY += 15;
            }
        }
    }

    public void drawCategory(float mouseX, float mouseY) {
        moduleButtons.forEach(moduleButton -> moduleButton.drawButton(mouseX, mouseY));
        if(settingsContainer != null) settingsContainer.drawSettingsContainer(mouseX, mouseY);
    }

    public void categoryClicked(float mouseX, float mouseY, int mouseButton) {
        moduleButtons.forEach(moduleButton -> {
            moduleButton.buttonClicked(mouseX, mouseY, mouseButton);

            if(moduleButton.isOpened && !moduleButton.module.settings.isEmpty()) {
                if(settingsContainer == null || settingsContainer.curModule != moduleButton.module)
                    settingsContainer = new SettingsContainer(this, moduleButton.module, x, y);
            }
        });

        if(settingsContainer != null) settingsContainer.settingsContainerClicked(mouseX, mouseY, mouseButton);
    }

    public void categoryReleased(float mouseX, float mouseY, int state) {
        if(settingsContainer != null) settingsContainer.settingsContainerReleased(mouseX, mouseY, state);
    }

    public void categoryHandleInput() {
        if(settingsContainer != null) settingsContainer.settingsContainerHandleInput();
    }
}
