package shadersmod.client;

import net.minecraft.client.gui.*;

public class GuiButtonShaderOption extends GuiButton
{
    private ShaderOption shaderOption;
    
    public GuiButtonShaderOption(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final ShaderOption shaderOption, final String text) {
        super(buttonId, x, y, widthIn, heightIn, text);
        this.shaderOption = null;
        this.shaderOption = shaderOption;
    }
    
    public ShaderOption getShaderOption() {
        return this.shaderOption;
    }
}
