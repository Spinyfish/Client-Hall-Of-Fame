package de.verschwiegener.atero.util.account;

import de.verschwiegener.atero.util.components.CustomGuiTextField;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiScreenAltening extends GuiScreen {

    private GuiScreen parentScreen;

    private CustomGuiTextField tokenField;

    public GuiScreenAltening(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        //bc886-c93ca@alt.com
        this.tokenField = new CustomGuiTextField(0, mc.fontRendererObj, this.width/2-100, this.height / 2, 200, 15);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
       // this.tokenField
        mc.getTextureManager().bindTexture( new ResourceLocation("atero/assets/background.jpg"));
        Gui.drawScaledCustomSizeModalRect(0,0,0,0, width, height,width,height,width,height);
     //   Gui.drawScaledCustomSizeModalRect(100,100,0,0, width, height,width,height,width - 50,height-50);
        tokenField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.tokenField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.tokenField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
