package io.github.raze.screen.collection.menu;

import io.github.raze.modules.system.BaseModule;
import io.github.raze.screen.collection.menu.components.impl.SettingComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class UISettingManager extends GuiScreen {

    public GuiScreen parent;
    public BaseModule module;

    public SettingComponent settings;

    public UISettingManager(GuiScreen parent, BaseModule module) {
        this.parent = parent;
        this.module = module;

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        this.settings = new SettingComponent(
                this.module,
                scaledResolution.getScaledWidth() / 2.0D - 125,
                scaledResolution.getScaledHeight() / 2.0D - 125,
                250, 250
        );
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        settings.render(mouseX, mouseY, partialTicks);

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void click(int mouseX, int mouseY, int mouseButton) throws IOException {

        settings.click(mouseX, mouseY, mouseButton);

        super.click(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void release(int mouseX, int mouseY, int state) {

        settings.release(mouseX, mouseY, state);

        super.release(mouseX, mouseY, state);
    }

    @Override
    protected void type(char typedChar, int keyCode) throws IOException {

        settings.type(typedChar, keyCode);

        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(parent);
        }
    }
}
