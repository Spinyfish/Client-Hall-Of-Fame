package v4n1ty.UI;

import java.io.IOException;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.GuiScreen;
import v4n1ty.UI.GuiCustomButton;
import v4n1ty.utils.render.RoundedUtils;

public class MainMenu extends GuiScreen
{
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        final ScaledResolution sr = new ScaledResolution(this.mc);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("v4n1ty/background.jpg"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0f, 0.0f, this.width, this.height, (float)this.width, (float)this.height);
        this.drawGradientRect(0, this.height - 120, this.width, this.height, 0, -16777216);
        RoundedUtils.drawRoundedRect((float)(sr.getScaledWidth() / 2 - 300), (float)(sr.getScaledHeight() / 2 - 200), (float)(sr.getScaledWidth() / 2 + 300), 525.0f, 100.0f, new Color(0, 0, 0, 170).getRGB());
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.width / 2.0f, this.height / 2.0f, 0.0f);
        GlStateManager.translate((float)(-(this.width / 2)), (float)(-(this.height / 2)), 0.0f);
        mc.fontRendererObj.drawStringWithShadow("V4n1ty", (float)(sr.getScaledWidth() / 2), (float)(sr.getScaledHeight() / 2 - 150), -1);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.width / 2.0f, this.height / 2.0f, 0.0f);
        GlStateManager.translate((float)(-(this.width / 2)), (float)(-(this.height / 2)), 0.0f);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiCustomButton(1, 562, this.height / 2 - 40 - 10, 160, 20, "Singleplayer"));
        this.buttonList.add(new GuiCustomButton(2, 562, this.height / 2 - 15 - 10, 160, 20, "Multiplayer"));
        this.buttonList.add(new GuiCustomButton(3, 562, this.height / 2 + 10 - 10, 160, 20, "Settings"));
        this.buttonList.add(new GuiCustomButton(4, 562, this.height / 2 + 35 - 10, 160, 20, "Quit"));
        super.initGui();
    }

    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 3) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (button.id == 4) {
            this.mc.shutdown();
        }
        super.actionPerformed(button);
    }
}