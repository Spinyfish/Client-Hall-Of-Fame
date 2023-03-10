/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import net.minecraft.client.gui.GuiButton;
import shadersmod.client.ShaderOption;

public class GuiButtonShaderOption
extends GuiButton {
    private ShaderOption shaderOption = null;

    public GuiButtonShaderOption(int buttonId, int x, int y, int widthIn, int heightIn, ShaderOption shaderOption, String text) {
        super(buttonId, x, y, widthIn, heightIn, text);
        this.shaderOption = shaderOption;
    }

    public ShaderOption getShaderOption() {
        return this.shaderOption;
    }
}

