package rip.athena.client.gui.menu;

import org.lwjgl.input.*;
import net.minecraft.client.gui.*;
import java.awt.*;
import java.net.*;
import org.apache.commons.io.*;
import net.minecraft.util.*;
import com.google.gson.*;
import java.io.*;

public class SessionGui extends GuiScreen
{
    private GuiScreen previousScreen;
    private String status;
    private GuiTextField sessionField;
    private ScaledResolution sr;
    
    public SessionGui(final GuiScreen previousScreen) {
        this.status = "Session:";
        this.previousScreen = previousScreen;
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sr = new ScaledResolution(this.mc);
        (this.sessionField = new GuiTextField(1, this.mc.fontRendererObj, this.sr.getScaledWidth() / 2 - 100, this.sr.getScaledHeight() / 2, 200, 20)).setMaxStringLength(32767);
        this.sessionField.setFocused(true);
        this.buttonList.add(new GuiButton(998, this.sr.getScaledWidth() / 2 - 100, this.sr.getScaledHeight() / 2 + 30, 200, 20, "Login"));
        super.initGui();
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.mc.fontRendererObj.drawString(this.status, this.sr.getScaledWidth() / 2 - this.mc.fontRendererObj.getStringWidth(this.status) / 2, this.sr.getScaledHeight() / 2 - 30, Color.WHITE.getRGB());
        this.sessionField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 998) {
            try {
                final String session = this.sessionField.getText();
                String username;
                String uuid;
                String token;
                if (session.contains(":")) {
                    username = session.split(":")[0];
                    uuid = session.split(":")[1];
                    token = session.split(":")[2];
                }
                else {
                    final HttpURLConnection c = (HttpURLConnection)new URL("https://api.minecraftservices.com/minecraft/profile/").openConnection();
                    c.setRequestProperty("Content-type", "application/json");
                    c.setRequestProperty("Authorization", "Bearer " + this.sessionField.getText());
                    c.setDoOutput(true);
                    final JsonObject json = new JsonParser().parse(IOUtils.toString(c.getInputStream())).getAsJsonObject();
                    username = json.get("name").getAsString();
                    uuid = json.get("id").getAsString();
                    token = session;
                }
                this.mc.session = new Session(username, uuid, token, "mojang");
                this.status = "§aSuccess: Logged into " + username;
            }
            catch (Exception e) {
                this.status = "§cError: Couldn't set session (check mc logs)";
                e.printStackTrace();
            }
        }
        super.actionPerformed(button);
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        this.sessionField.textboxKeyTyped(typedChar, keyCode);
        if (1 == keyCode) {
            this.mc.displayGuiScreen(this.previousScreen);
        }
        else {
            super.keyTyped(typedChar, keyCode);
        }
    }
}
