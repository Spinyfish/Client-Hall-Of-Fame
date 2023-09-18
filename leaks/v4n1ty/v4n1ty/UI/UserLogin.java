package v4n1ty.UI;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import oshi.SystemInfo;
import v4n1ty.utils.misc.EncryptionUtil;
import v4n1ty.utils.render.RoundedUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class UserLogin extends GuiScreen {
    private GuiTextField password;
    public static GuiTextField username;
    public MainMenu mainMenu;
    public String currentStatusText = ChatFormatting.GRAY + "Idle...";

    public UserLogin() {
        EncryptionUtil.setKey("9%%(CZXg<hFg=MqQ5Q#u2%^?:m2pB]b<cDDrw(Vh*^n>3A;w~gnv]M-hHJ26ak`zV(aY)c(sh[;/*{qbs:#WQ>s?2Y$[6`A2.x(V]WPFCT5*nJ(33]4JF,[>FQ%g9w+<Qpz].?FLJ}YGTrx)Dw\\F$*z\\R)uQKgRHb.y;6<6[JFh>g7LBt@_>w!/HA-<ded)Ek]?54/jyHbuEW#d{q~F\"8#Bs&k~3VKhSQhq[<3@3!s\\m<t3WX'Uf8F&v+R<+dXVF");
        mainMenu = new MainMenu();
    }

    public static String getHashedHWID() {
        try {
            String hwid = null;
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String hwidString = System.getProperty("os.name") +
                        System.getProperty("os.arch") +
                        System.getProperty("os.version") +
                        Runtime.getRuntime().availableProcessors() +
                        System.getenv("PROCESSOR_IDENTIFIER") +
                        System.getenv("PROCESSOR_ARCHITECTURE") +
                        System.getenv("PROCESSOR_ARCHITEW6432") +
                        System.getenv("NUMBER_OF_PROCESSORS");

                byte[] hashBytes = digest.digest(hwidString.getBytes(StandardCharsets.UTF_8));
                hwid = Arrays.toString(hashBytes);
            } catch (Exception ignored) {
                //
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(hwid != null ? hwid.getBytes() : new byte[0]);

            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = String.format("%02x", b);
                hexStringBuilder.append(hex);
            }

            return hexStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            return "######################";
        }
    }

    public static String getUsername() {
        return username.getText();
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
                    mc.displayGuiScreen(new MainMenu());
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
            if (username.equals("Coccocoahelper") && password.equals("Iamnotcoccocoa")) {
                return true;
            } else if (username.equals("Dimeus") && password.equals("Swing007*")){
                return true;
            } else if (username.equals("4sit") && password.equals("chinesecomunist")){
                return true;
            }
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
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.drawCenteredString(fr, "Please enter your username/password.", (int) (width / 2f), 135, -1);
        this.drawCenteredString(fr, this.currentStatusText, (int) (width / 2f), 115, -1);


        if (this.username.getText().isEmpty()) {
            fr.drawString("Username", (int) (width / 2f - 72), 176, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            fr.drawString("Password", (int) (width / 2f - 72), 216, -7829368);
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