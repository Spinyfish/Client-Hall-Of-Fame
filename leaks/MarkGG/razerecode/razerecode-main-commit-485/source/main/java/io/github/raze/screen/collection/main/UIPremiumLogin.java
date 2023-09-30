package io.github.raze.screen.collection.main;

import io.github.raze.utilities.collection.fonts.CFontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class UIPremiumLogin extends GuiScreen {

    private GuiTextField username;
    private GuiTextField password;

    public UIPremiumLogin() {

    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:

                break;
            case 3:
                mc.displayGuiScreen(new UIMainMenu());
                break;
        }
    }

    public void render(int mouseX, int mouseY, float partialTicks) {

        mc.getTextureManager().bindTexture(new ResourceLocation("raze/background/background.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0,0,0, width, height, width, height);
        drawRect(0, 0, width, height, 0x66000000);

        CFontUtil.Jello_Light_40.getRenderer().drawString (
                "Raze Premium",
                width / 2.0D - 55,
                height / 2.0D - 95,
                Color.WHITE
        );

        CFontUtil.Jello_Light_20.getRenderer().drawString (
                username.getText(),
                10,
                10,
                Color.WHITE
        );

        CFontUtil.Jello_Light_20.getRenderer().drawString (
                "Username:",
                width / 2.0D - 155,
                height / 2.0D - 67,
                Color.WHITE
        );

        CFontUtil.Jello_Light_20.getRenderer().drawString (
                "Password:",
                width / 2.0D - 155,
                height / 2.0D - 41,
                Color.WHITE
        );

        username.drawTextBox();
        password.drawTextBox();

        super.render(mouseX, mouseY, partialTicks);
    }

    public void initialize() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 - 20, "Login"));
        buttonList.add(new GuiButton(3, width / 2 - 100, height / 2, "Back"));
        username = new GuiTextField(eventButton, mc.fontRenderer, width / 2 - 100, height / 2 - 70, 199, 20);
        password = new GuiTextField(eventButton, mc.fontRenderer, width / 2 - 100, height / 2 - 45, 199, 20);
    }

    protected void type(char typedChar, int keyCode) {
        username.textboxKeyTyped(typedChar, keyCode);
        password.textboxKeyTyped(typedChar, keyCode);
        if (typedChar == '\t' && (username.isFocused()))
            username.setFocused(!username.isFocused());
        if (typedChar == '\r')
            actionPerformed(buttonList.get(0));
        if (typedChar == '\t' && (username.isFocused()))
            username.setFocused(!username.isFocused());
        if (typedChar == '\r')
            actionPerformed(buttonList.get(0));
    }

    protected void release(int mouseX, int mouseY, int state) {
        super.release(mouseX, mouseY, state);
        username.mouseClicked(mouseX, mouseY, state);
        password.mouseClicked(mouseX, mouseY, state);
    }
}
