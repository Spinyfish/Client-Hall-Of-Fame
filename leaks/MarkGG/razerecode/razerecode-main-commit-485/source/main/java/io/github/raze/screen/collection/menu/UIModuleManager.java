package io.github.raze.screen.collection.menu;

import io.github.raze.Raze;
import io.github.raze.modules.collection.client.ModuleManager;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.screen.collection.menu.components.impl.modules.CategoryComponent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

public class UIModuleManager extends GuiScreen implements GuiYesNoCallback {

    public ArrayList<CategoryComponent> panels;

    public UIModuleManager() {
        panels = new ArrayList<CategoryComponent>();

        int panelX = 5;
        int panelY = 5;

        for (ModuleCategory moduleCategory : ModuleCategory.values()) {
            CategoryComponent component = new CategoryComponent(moduleCategory, panelX, panelY, 120, 25);
            panelX += component.getWidth() + 5;
            panels.add(component);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        for (CategoryComponent panel : panels) {
            panel.render(mouseX, mouseY, partialTicks);
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (CategoryComponent panel : panels) {
            panel.click(mouseX, mouseY, mouseButton);
        }

        super.click(mouseX, mouseY, mouseButton);
    }

    @Override
    public void release(int mouseX, int mouseY, int state) {
        for (CategoryComponent panel : panels) {
            panel.release(mouseX, mouseY, state);
            panel.setDragging(false);
        }

        super.release(mouseX, mouseY, state);
    }

    @Override
    public void type(char typed, int keyCode) throws IOException {
        for (CategoryComponent panel : panels) {
            panel.type(typed, keyCode);
        }

        super.type(typed, keyCode);
    }

    @Override
    public void close() {
        Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(ModuleManager.class).setEnabled(false);
        super.close();
    }
}
