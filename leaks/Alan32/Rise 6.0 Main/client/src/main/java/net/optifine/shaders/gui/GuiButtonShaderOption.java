package net.optifine.shaders.gui;

import net.minecraft.client.gui.GuiButton;
import net.optifine.shaders.config.ShaderOption;

public class GuiButtonShaderOption extends GuiButton {
    private ShaderOption shaderOption = null;

    public GuiButtonShaderOption(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final ShaderOption shaderOption, final String text) {
        super(buttonId, x, y, widthIn, heightIn, text);
        this.shaderOption = shaderOption;
    }

    public ShaderOption getShaderOption() {
        return this.shaderOption;
    }

    public void valueChanged() {
    }

    public boolean isSwitchable() {
        return true;
    }
}
