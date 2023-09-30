package io.github.raze.screen.collection.main;

import io.github.raze.utilities.collection.fonts.CFontUtil;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class UIMainMenu extends GuiScreen {

    public UIMainMenu() {

    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 2:
                mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 3:
                mc.displayGuiScreen(new UIAltManager());
                break;
            case 4:
                mc.displayGuiScreen(new GuiOptions(this,mc.gameSettings));
                break;
            case 5:
                mc.displayGuiScreen(new UICredits());
                break;
            case 6:
                mc.shutdown();
                break;
            case 7:
                break;
        }
    }

    public void render(int mouseX, int mouseY, float partialTicks) {

        mc.getTextureManager().bindTexture(new ResourceLocation("raze/background/background.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0,0,0, width, height, width, height);

        CFontUtil.Jello_Light_40.getRenderer().drawString (
                "Raze",
                width / 2.0D - 25,
                height / 2.0D - 70,
                Color.WHITE
        );

        CFontUtil.Jello_Light_20.getRenderer().drawString (
                "Welcome to Raze, " + mc.session.getUsername() + "!",
                10,
                height - 20,
                Color.WHITE
        );

        super.render(mouseX, mouseY, partialTicks);
    }

    public void initialize() {
        buttonList.clear();
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 - 40, "Singleplayer"));
        buttonList.add(new GuiButton(2, width / 2 - 100, height / 2 - 20, "Multiplayer"));
        buttonList.add(new GuiButton(3, width / 2 - 100, height / 2, "Alt Manager"));
        buttonList.add(new GuiButton(4, width / 2, height / 2 + 20, 100, 20, "Options"));
        buttonList.add(new GuiButton(5, width / 2 - 100, height / 2 + 20, 100, 20, "Credits"));
        buttonList.add(new GuiButton(6, width / 2 - 100, height / 2 + 40, "Quit"));
    //    buttonList.add(new GuiButton(7, width / 2 - 100, height / 2 - 270, "Activate Premium"));
    }
}