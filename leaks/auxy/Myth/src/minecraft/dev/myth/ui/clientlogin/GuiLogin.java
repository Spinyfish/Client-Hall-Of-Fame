/**
 * @project Myth
 * @author Skush/Duzey
 * @at 12.08.2022
 */
package dev.myth.ui.clientlogin;

import club.antiskid.annotations.Obfuscate;
import dev.myth.api.utils.encryption.AES256;
import dev.myth.api.utils.encryption.EncryptionUtil;
import dev.myth.api.utils.encryption.TempKeyUtil;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.animation.Animation;
import dev.myth.api.utils.render.animation.Easings;
import dev.myth.api.utils.render.shader.ShaderProgram;
import dev.myth.main.ClientMain;
import dev.myth.managers.ShaderManager;
import dev.myth.ui.GuiPasswordField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import xyz.vyra.lib.auth.requests.VyraLoginRequest;
import xyz.vyra.lib.auth.responses.VyraLoginResponse;
import xyz.vyra.lib.util.VyraHwidUtil;

import java.awt.*;
import java.io.*;

@Obfuscate
public class GuiLogin extends GuiScreen {

    public GuiPasswordField passwordField;
    public GuiTextField usernameField;
    public final Animation buttonAnimation = new Animation();
    private String status;

    private ShaderProgram[] backgroundShaders = new ShaderProgram[]{
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER1,
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER2,
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER3,
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER4
    };

    @Override
    public void initGui() {
        super.initGui();
        usernameField = new GuiTextField(1, Minecraft.getMinecraft().fontRendererObj, width / 2 - 100, height / 2 - 50, 200, 20);
        passwordField = new GuiPasswordField(0, width / 2 - 100, height / 2 - 20, 200, 20);

        if (ClientMain.INSTANCE.isLoggedIn()) {
            mc.displayGuiScreen(new GuiMainMenu());
            return;
        }

        if (new File("myth/account").exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("myth/account"));
                String line = AES256.decryptStr(reader.readLine(), EncryptionUtil.getEncryptionKey(), EncryptionUtil.getSalt());
                reader.close();
                if (line == null || !line.contains(":")) {
                    return;
                }
                String[] split = line.split(":");
                usernameField.setText(AES256.decryptStr(split[0], EncryptionUtil.getEncryptionKey(), EncryptionUtil.getSalt()));
                passwordField.setText(AES256.decryptStr(split[1], EncryptionUtil.getEncryptionKey(), EncryptionUtil.getSalt()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        backgroundShaders[GuiMainMenu.bgId].doRender();

        final float x = sr.getScaledWidth() / 2F - 125;
        final float y = sr.getScaledHeight() / 2F - 125;
        RenderUtil.drawRoundedRect(x, y, 250, 250, 5, new Color(16, 16, 16, 180).getRGB(), 0, 0);

        buttonAnimation.updateAnimation();
        buttonAnimation.animate(MouseUtil.isHovered(mouseX, mouseY, x + 10, y + 220 - 10 * buttonAnimation.getValue(), 230, 20) ? 1 : 0, 800, Easings.CIRC_OUT);
        RenderUtil.drawRect(x + 10, y + 220 - 10 * buttonAnimation.getValue(), 230, 20, new Color(42, 82, 190).getRGB());

        if (this.status != null)
            FontLoaders.SFUI_20.drawString(this.status, (sr.getScaledWidth() / 2F) - FontLoaders.SFUI_20.getStringWidth(this.status) / 2.0F, (float) (y + 15), -1);

        FontLoaders.SFUI_20.drawString("Login", x + 107, (float) (y + 226 - 10 * buttonAnimation.getValue()), -1);
        usernameField.drawTextBox();
        passwordField.drawTextBox();
        if (!usernameField.isFocused() && usernameField.getText().isEmpty())
            FontLoaders.SFUI_18.drawString("Username", width / 2 - 95, height / 2 - 43, Color.GRAY.getRGB());
        if (!passwordField.isFocused() && passwordField.getText().isEmpty())
            FontLoaders.SFUI_18.drawString("Password", width / 2 - 95, height / 2 - 13, Color.GRAY.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (typedChar == '\t') {
            if (!usernameField.isFocused() && !passwordField.isFocused()) {
                usernameField.setFocused(true);
            } else {
                usernameField.setFocused(passwordField.isFocused());
                usernameField.setFocused(!usernameField.isFocused());
            }
        }
        usernameField.textboxKeyTyped(typedChar, keyCode);
        passwordField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        usernameField.mouseClicked(mouseX, mouseY, mouseButton);
        passwordField.mouseClicked(mouseX, mouseY, mouseButton);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final float x = sr.getScaledWidth() / 2F - 125;
        final float y = sr.getScaledHeight() / 2F - 125;
        if (MouseUtil.isHovered(mouseX, mouseY, x + 10, y + 220 - 10 * buttonAnimation.getValue(), 230, 20) && mouseButton == 0) {
            this.status = this.login(usernameField.getText(), passwordField.getText());
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * Try to login
     */
    private String login(final String username, final String password) {
        /** Crash the Client if the username or uid has already been replaced before we even try to log in */
        if (!ClientMain.INSTANCE.getUsername().equals("MythClarinet1337") || ClientMain.INSTANCE.getUid() != Integer.MAX_VALUE || ClientMain.INSTANCE.isLoggedIn()) {
            System.exit(420);
            return null;
        }
        VyraLoginResponse response = null;
        try {
            response = (VyraLoginResponse) new VyraLoginRequest(username, password, VyraHwidUtil.getSimpleHWID(), 1).send();
            if (response.getStatus() == VyraLoginResponse.StatusType.SUCCESS) {
                ClientMain.INSTANCE.setUsername(response.getUsername());
                ClientMain.INSTANCE.setUid(69);
                ClientMain.INSTANCE.setLoggedIn(true);
            } else throw new RuntimeException("Ich hasse mein Leben");
        } catch (Exception e) {
            if (response == null)
                return "Something seems to be wrong with the API";
            return response.getMessage();
        }
        final String tempKey = AES256.encryptStr("AdolfDripler" + username + "|" + password + "5Y9NJeVl1sKasLx*#Yje8Y0%QpknOIgy" + username, EncryptionUtil.getEncryptionKey(), EncryptionUtil.getSalt());
        ClientMain.INSTANCE.setTempKey(tempKey);
        if (!TempKeyUtil.getDecryptedTempKey().equals("AdolfDripler" + username + "|" + password + "5Y9NJeVl1sKasLx*#Yje8Y0%QpknOIgy" + username)) {
            System.exit(420);
            return null;
        }
        this.mc.displayGuiScreen(new GuiMainMenu());
        ClientMain.INSTANCE.setLoggedIn(true);

        try (FileOutputStream fileOut = new FileOutputStream("myth/account", false)) {
            String account = AES256.encryptStr(AES256.encryptStr(username, EncryptionUtil.getEncryptionKey(), EncryptionUtil.getSalt()) + ":" + AES256.encryptStr(password, EncryptionUtil.getEncryptionKey(), EncryptionUtil.getSalt()), EncryptionUtil.getEncryptionKey(), EncryptionUtil.getSalt());
            if (account == null) return ":rofl:";
            fileOut.write(account.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Success!";
    }
}