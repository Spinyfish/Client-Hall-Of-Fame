package me.kansio.client.gui.alt;

import lombok.Getter;
import lombok.Setter;
import me.kansio.client.utils.chat.NameUtil;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.net.URI;

public final class GuiAltManager extends GuiScreen {
    private GuiTextField password;
    private final GuiScreen previousScreen;
    private GuiTextField username;
    private AltLoginThread thread;
    @Getter @Setter private String crackedStatus;



    public GuiAltManager(GuiScreen previousScreen) {
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
                    String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if (!data.contains(":")) break;
                    String[] credentials = data.split(":");
                    username.setText(credentials[0]);
                    password.setText(credentials[1]);
                    break;

                case 3:
                    thread = null;
                    thread = new AltLoginThread(RandomStringUtils.random(14, true, true), "");
                    thread.start();
                    break;

                case 4:
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI("http://sellix.io/drilledalts"));
                    }
                    break;

                case 5:
                    mc.displayGuiScreen(new GuiMicrosoftAltManager(this));
                    break;
                case 6:
                    thread = null;
                    thread = new AltLoginThread(NameUtil.generateName(), "");
                    thread.start();
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
        this.drawCenteredString(font, thread == null ? (crackedStatus == null ? EnumChatFormatting.GRAY + "Idle" : EnumChatFormatting.GREEN + crackedStatus) : thread.getStatus(),
                (int) (width / 2f),
                29,
                -1);
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
        buttonList.add(new GuiButton(3, width / 2 - 100, var3 + 72 + 12 + 48 + 24, "Generate Cracked Account"));
        buttonList.add(new GuiButton(5, width / 2 - 100, var3 + 72 + 12 + 48 + 24 + 24, "Microsoft Login"));
        buttonList.add(new GuiButton(4, width / 2 - 100, var3 + 72 + 12 + 48 + 24 * 3, "DrilledAlts (Good)"));
        buttonList.add(new GuiButton(6, width / 2 - 100, var3 + 72 + 12 + 48 + 24 + 24 * 3, "Generate Real Looking Name"));

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
