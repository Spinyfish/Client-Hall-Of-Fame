package io.github.raze.screen.collection.main;

import io.github.raze.utilities.collection.fonts.CFontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class UICredits extends GuiScreen
{
    private GuiScreen parent;

    public UICredits()
    {

    }
    public void initialize()
    {
        super.initialize();
        buttonList.add(new GuiButton(1, width / 2 - 100, height - 25, 200, 20, "Back"));
    }


    protected void actionPerformed(GuiButton guiButton) throws IOException
    {
        if (guiButton.id == 1)
        {
            mc.displayGuiScreen(parent);
        }
    }


    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
    }


    public void render(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(new ResourceLocation("raze/background/background.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0,0,0, width, height, width, height);
        drawRect(0, 0, width, height, 0x66000000);

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);
        GlStateManager.popMatrix();

        // Devs

        CFontUtil.Jello_Medium_24.getRenderer().drawString (
                "Raze Developers:",
                (double) (this.width - this.fontRendererObj.getStringWidth("Raze Developers:")) / 2,
                30,
                Color.WHITE
        );

        CFontUtil.Jello_Medium_24.getRenderer().drawString (
                "MarkGG#8181",
                (double) (this.width - this.fontRendererObj.getStringWidth("MarkGG#8181")) / 2,
                42,
                Color.WHITE
        );

        CFontUtil.Jello_Medium_24.getRenderer().drawString (
                "Kellohylly#2833",
                (double) (this.width - this.fontRendererObj.getStringWidth("Kellohylly#2833")) / 2,
                54,
                Color.WHITE
        );

        CFontUtil.Jello_Medium_24.getRenderer().drawString (
                "Liticane#9797",
                (double) (this.width - this.fontRendererObj.getStringWidth("Liticane#9797")) / 2,
                66,
                Color.WHITE
        );

        CFontUtil.Jello_Medium_24.getRenderer().drawString (
                "Szypko#1344",
                (double) (this.width - this.fontRendererObj.getStringWidth("Szypko#1344")) / 2,
                78,
                Color.WHITE
        );

        CFontUtil.Jello_Medium_24.getRenderer().drawString (
                "Spinyfish#4884",
                (double) (this.width - this.fontRendererObj.getStringWidth("Spinyfish#4884")) / 2,
                90,
                Color.WHITE
        );

        CFontUtil.Jello_Medium_24.getRenderer().drawString (
                "Thanks to:",
                (double) (this.width - this.fontRendererObj.getStringWidth("Thanks to:")) / 2,
                150,
                Color.WHITE
        );

        CFontUtil.Jello_Medium_24.getRenderer().drawString (
                "Thanks to arch32#3617 for helping with bypasses",
                (double) (this.width - this.fontRendererObj.getStringWidth("Thanks to arch32#3617 for helping with bypasses")) / 2,
                162,
                Color.WHITE
        );


        super.render(mouseX, mouseY, partialTicks);
    }
}
