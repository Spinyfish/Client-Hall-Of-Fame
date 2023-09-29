package de.verschwiegener.atero.ui.mainmenu;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import de.verschwiegener.atero.util.LoginUtil;
import de.verschwiegener.atero.util.components.CustomGuiButton;
import de.verschwiegener.atero.util.components.CustomGuiTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.net.Proxy;

public class AccountManagerScreen extends GuiScreen {
    public GuiTextField clipboard;

    public GuiScreen oldscreen;

    public String status;

    public AccountManagerScreen(GuiScreen old) {
        this.oldscreen = old;
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new CustomGuiButton(1, width / 2 - 100, height / 4 + 96 + 12, "Login"));
        this.buttonList.add(new CustomGuiButton(3, width / 2 - 100, height / 4 + 96 + 36 + 24, "Altening"));
        this.buttonList.add(new CustomGuiButton(2, width / 2 - 100, height / 4 + 96 + 36, "Back"));
        this.clipboard = new CustomGuiTextField(3, this.fontRendererObj, width / 2 - 100, 76, 200, 20);
        this.clipboard.setMaxStringLength(50);
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void updateScreen() {
        this.clipboard.updateCursorCounter();
    }

    protected void mouseClicked(int x, int y, int m) {
        this.clipboard.mouseClicked(x, y, m);
        try {
            super.mouseClicked(x, y, m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void keyTyped(char c, int i) {
        this.clipboard.textboxKeyTyped(c, i);
        if (c == '\t') ;
        if (c == '\r')
            try {
                actionPerformed(this.buttonList.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        ScaledResolution s1 = new ScaledResolution(Minecraft.getMinecraft());
        if (this.status != null) {
            drawString(this.fontRendererObj, "§a" + this.status, 10, 10, 0);
        } else {
            drawString(this.fontRendererObj, "Nick: " + this.mc.session.getUsername(), 10, 10, -1);
        }
        this.clipboard.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 2) {
            this.mc.displayGuiScreen(this.oldscreen);
        } else if (button.id == 3) {
            final LoginUtil loginUtil = LoginUtil.getInstance();
            if (this.clipboard.getText().contains("@alt")) {
                loginUtil.login(this.clipboard.getText());
            } else {
                loginUtil.generate(this.clipboard.getText());
            }
          //  this.mc.displayGuiScreen((GuiScreen)new GuiAltening(this));
        } else {
            String[] args = this.clipboard.getText().split(":");
            if (args[1].isEmpty()) {
                if (!args[0].trim().isEmpty()) {
                    this.mc.session = new Session(args[0].trim(), "-", "-", "Legacy");
                    this.status = "Loggin Cracked: " + args[0].toString();
                } else {
                    this.status = "Cracked Login failed";
                }
            } else if (!args[0].trim().isEmpty()) {
                YggdrasilUserAuthentication a = (YggdrasilUserAuthentication) (new YggdrasilAuthenticationService(Proxy.NO_PROXY, "")).createUserAuthentication(Agent.MINECRAFT);
                a.setUsername(args[0].trim());
                a.setPassword(args[1].trim());
                try {
                    a.logIn();
                    this.mc.session = new Session(a.getSelectedProfile().getName(), a.getSelectedProfile().getId().toString(), a.getAuthenticatedToken(), "mojang");
                    this.status = "Logged in as: " + this.mc.session.getUsername();
                } catch (Exception e) {
                    this.status = "Login failed";
                }
            }
        }
    }
}

