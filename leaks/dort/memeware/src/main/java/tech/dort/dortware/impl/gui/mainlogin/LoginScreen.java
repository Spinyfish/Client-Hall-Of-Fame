package tech.dort.dortware.impl.gui.mainlogin;

import com.mojang.realmsclient.gui.ChatFormatting;
import jdk.nashorn.api.scripting.URLReader;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import oshi.SystemInfo;
import tech.dort.dortware.impl.utils.render.RenderUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginScreen extends GuiScreen {
    private GuiTextField password;
    private GuiTextField username;
    public GuiMainMenu guiMainMenu;
    public String currentStatusText = ChatFormatting.GRAY + "Idle...";

    public LoginScreen() {
        //change if client gets cracked
        EncryptionUtil.setKey("9%%(CZXg<hFg=MqQ5Q#u2%^?:m2pB]b<cDDrw(Vh*^n>3A;w~gnv]M-hHJ26ak`zV(aY)c(sh[;/*{qbs:#WQ>s?2Y$[6`A2.x(V]WPFCT5*nJ(33]4JF,[>FQ%g9w+<Qpz].?FLJ}YGTrx)Dw\\F$*z\\R)uQKgRHb.y;6<6[JFh>g7LBt@_>w!/HA-<ded)Ek]?54/jyHbuEW#d{q~F\"8#Bs&k~3VKhSQhq[<3@3!s\\m<t3WX'Uf8F&v+R<+dXVF");
        guiMainMenu = new GuiMainMenu();
    }

    public String calculateUniqueHardwareIdentifier() throws Exception {
        SystemInfo systemInfo = new SystemInfo();
        StringBuilder stringBuilder = new StringBuilder(System.getenv("PROCESSOR_IDENTIFIER"));
        Arrays.stream(systemInfo.getHardware().getProcessors()).forEach(processor -> {
            stringBuilder.append(processor.getIdentifier())
                    .append(processor.isCpu64bit())
                    .append(processor.getStepping());
        });
        stringBuilder.append(System.getProperty("user.name"));
        stringBuilder.append(System.getenv("COMPUTERNAME"));
        return textToBase64SHA512(stringBuilder.toString());
    }

    private String textToBase64SHA512(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] sha1hash;
        md.update(text.getBytes(StandardCharsets.ISO_8859_1), 0, text.length());
        sha1hash = md.digest();
        return bytesToHex(sha1hash);
    }

    private String bytesToHex(byte[] data) {
        BigInteger no = new BigInteger(1, data);
        StringBuilder hash = new StringBuilder(no.toString(16));
        while (hash.length() < 32) {
            hash.insert(0, "0");
        }
        return hash.toString();
    }

    @Override
    public void initGui() {
        currentStatusText = ChatFormatting.GRAY + "Login...";
        int var3 = height / 4 + 24;
        this.buttonList.add(new GuiButton(0, width / 2 - 75, height / 3 + 70, 150, 20, "Login"));
        this.username = new GuiTextField(var3, this.mc.fontRendererObj, width / 2 - 75, 170, 150, 20);
        this.password = new GuiTextField(var3, this.mc.fontRendererObj, width / 2 - 75, 210, 150, 20);

        this.username.setText("");
        this.password.setText("");

        this.username.setFocused(true);

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void actionPerformed(GuiButton button) {

        switch (button.id) {
            case 1: {
                mc.shutdown();
                break;
            }
            case 0: {
                if (this.checkLogin(this.username.getText(), this.password.getText())) {
                    System.out.println("Login Confirmed");
                    mc.displayGuiScreen(new GuiMainMenu());
                    this.onGuiClosed();
                } else {
                    this.currentStatusText = ChatFormatting.DARK_RED + "Login failed!";
                }
                break;
            }
        }
    }

    private boolean checkLogin(String username, String password) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new URLReader(new URL("https://auth.dortware.club/check?username=" + username + "&password=" + password + "&processorInformation=" + calculateUniqueHardwareIdentifier())));
            String line = bufferedReader.readLine();
            return Boolean.parseBoolean(EncryptionUtil.decrypt(line));
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        this.drawDefaultBackground();


        GL11.glPushMatrix();
        GL11.glScaled(2, 2, 1);
        GL11.glPopMatrix();

        final FontRenderer fr = mc.fontRendererObj;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtils.drawRoundedRect(width / 2f - 80, height / 4f + 30, (width / 2f - 200) - 120, 105, 5, 0x60000000);
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.drawCenteredString(fr, "Meme Login, Please enter your username/password.", width / 2f, 135, -1);
        this.drawCenteredString(fr, this.currentStatusText, width / 2f, 115, -1);


        if (this.username.getText().isEmpty()) {
            fr.drawString("Username", width / 2f - 72, 176, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            fr.drawString("Password", width / 2f - 72, 216, -7829368);
        }

        super.drawScreen(x2, y2, z2);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (key == Keyboard.KEY_ESCAPE) {
            mc.currentScreen = this;
        }

        if (character == '\t') {
            if (!this.username.isFocused() && !this.password.isFocused()) {
                this.username.setFocused(true);
            } else {
                this.username.setFocused(this.password.isFocused());
                this.password.setFocused(!this.username.isFocused());
            }
        }

        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }

        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) throws IOException {
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
        super.mouseClicked(x2, y2, button);
    }


    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}