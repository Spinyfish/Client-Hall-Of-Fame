package me.kansio.client.gui.alt;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.text.MessageFormat;

public final class GuiMicrosoftAltManager extends GuiScreen {
    private GuiTextField password;
    private final GuiScreen previousScreen;
    private GuiTextField username;
    private AltLoginThread thread;
    @Getter @Setter private String crackedStatus;



    public GuiMicrosoftAltManager(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            switch (button.id) {
                case 1:
                    mc.displayGuiScreen(previousScreen);
                    break;

                case 0:
                    thread = new AltLoginThread(username.getText(), password.getText());
                    thread.start();
                    break;

                case 2:
                    MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                    MicrosoftAuthResult result = authenticator.loginWithCredentials(username.getText(), password.getText());
                    crackedStatus = MessageFormat.format( "Logged as {0}", result.getProfile().getName());
                    break;

                default:
                    break;
            }
        } catch (Throwable var11) {
            //REMOVE ME LATER: throw new RuntimeException();
        }
    }

    @Override
    public void drawScreen(int x, int y2, float z) {
        final FontRenderer font = mc.fontRendererObj;
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        //Gui.drawRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(50, 50, 50).getRGB());
        drawDefaultBackground();
        username.drawTextBox();
        password.drawTextBox();
        this.drawCenteredString(font, "Account Login", (int) (width / 2F), 20, -1);
        this.drawCenteredString(font, thread == null ? (crackedStatus == null ? EnumChatFormatting.GRAY + "Idle" : EnumChatFormatting.GREEN + crackedStatus) : thread.getStatus(), (int) (width / 2f), 29, -1);
        if (username.getText().isEmpty()) {
            font.drawStringWithShadow("Username", width / 2F - 96, 66, -7829368);
        }
        if (password.getText().isEmpty()) {
            font.drawStringWithShadow("Password", width / 2F - 96, 106, -7829368);
        }
        super.drawScreen(x, y2, z);
    }

    @Override
    public void initGui() {
        int var3 = height / 4 + 24;
        buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Login"));
        buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 24, I18n.format("gui.cancel")));
        buttonList.add(new GuiButton(2, width / 2 - 100, var3 + 72 + 12 + 48, "Clipboard"));
        username = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
        password = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
        username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!username.isFocused() && !password.isFocused()) {
                username.setFocused(true);
            } else {
                username.setFocused(password.isFocused());
                password.setFocused(!username.isFocused());
            }
        }
        if (character == '\r') {
            actionPerformed(buttonList.get(0));
        }
        username.textboxKeyTyped(character, key);
        password.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y2, int button) {
        try {
            super.mouseClicked(x, y2, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        username.mouseClicked(x, y2, button);
        password.mouseClicked(x, y2, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();
        password.updateCursorCounter();
    }
}
