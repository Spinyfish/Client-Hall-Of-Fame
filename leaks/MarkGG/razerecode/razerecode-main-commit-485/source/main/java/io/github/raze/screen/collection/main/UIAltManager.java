package io.github.raze.screen.collection.main;

import io.github.raze.utilities.collection.fonts.CFontUtil;
import io.github.raze.utilities.collection.math.RandomUtil;
import net.minecraft.client.gui.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class UIAltManager extends GuiScreen {

    private GuiTextField username;
    private String status = EnumChatFormatting.GRAY + "Status: Waiting...";

    public UIAltManager() {

    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                if (!username.getText().isEmpty() && !(username.getText().length() >= 17)) {
                    status = EnumChatFormatting.GREEN + "Status: Logged in.";
                    mc.session = new Session(username.getText(), "", "", "legacy");
                } else if (username.getText().length() >= 17) {
                    status = EnumChatFormatting.RED + "Error: Username can't be longer than 16 characters";
                    return;
                } else {
                    status = EnumChatFormatting.RED + "Error: Username can't be empty";
                    return;
                }

                break;
            case 2:
                status =  EnumChatFormatting.GREEN + "Status: Logged in into a random account.";
                mc.session = new Session(RandomUtil.randomString(10), "", "", "legacy");
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
                "Alt Manager",
                (double) width / 2 - 55,
                (double) height / 2 - 70,
                Color.WHITE
        );

        CFontUtil.Jello_Light_20.getRenderer().drawString (
                mc.session.getUsername(),
                10,
                10,
                Color.WHITE
        );

        CFontUtil.Jello_Light_20.getRenderer().drawString (
                status,
                10,
                height - 20,
                Color.GREEN
        );

        username.drawTextBox();

        super.render(mouseX, mouseY, partialTicks);
    }

    public void initialize() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 - 20, "Login Cracked"));
        buttonList.add(new GuiButton(2, width / 2 - 100, height / 2, "Random Username"));
        buttonList.add(new GuiButton(3, width / 2 - 100, height / 2 + 20, "Back"));
        username = new GuiTextField(eventButton, mc.fontRenderer, width / 2 - 100, height / 2 - 45, 199, 20);
    }

    protected void type(char typedChar, int keyCode) {
        username.textboxKeyTyped(typedChar, keyCode);
        if (typedChar == '\t' && (username.isFocused()))
            username.setFocused(!username.isFocused());
        if (typedChar == '\r')
            actionPerformed(buttonList.get(0));
    }

    protected void release(int mouseX, int mouseY, int state) {
        super.release(mouseX, mouseY, state);
        username.mouseClicked(mouseX, mouseY, state);
    }
}
