package felix.util.visual;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import felix.Client;
import felix.gui.alt.gui.components.GuiPasswordField;
import felix.gui.menu.ClientMainMenu;
import felix.util.font.FontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;


import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

public class UserLogin extends GuiScreen {
    private GuiPasswordField password;
    private GuiTextField username;
    public static GuiMainMenu guiMainMenu;
    public static String Status = ChatFormatting.GRAY + "Idle...";
    public static Scanner scanner;
    String F = "";
    public UserLogin()
    {
        this.guiMainMenu = new GuiMainMenu();
    }

    public String HWID() throws Exception {
        String hwid = textToSHA1(String.valueOf(System.getenv("PROCESSOR_IDENTIFIER"))
                + System.getenv("COMPUTERNAME") + System.getProperty("user.name"));
        StringSelection stringSelection = new StringSelection(hwid);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
        return hwid;
    }

    private String textToSHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return bytesToHex(sha1hash);
    }

    private String bytesToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        int i = 0;
        while (i < data.length) {
            int halfbyte = data[i] >>> 4 & 15;
            int two_halfs = 0;
            do {
                if (halfbyte >= 0 && halfbyte <= 9) {
                    buf.append((char)(48 + halfbyte));
                } else {
                    buf.append((char)(97 + (halfbyte - 10)));
                }
                halfbyte = data[i] & 15;
            } while (two_halfs++ < 1);
            ++i;
        }
        return buf.toString();
    }

    @Override
    public void initGui()
    {
        this.Status = ChatFormatting.GRAY + "Login...";
        int var3 = height / 4 + 24;
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 3 + 110, "Login"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 3 + 140, "Exit Game"));
        this.username = new GuiTextField(var3, this.mc.fontRendererObj, width / 2 - 100, 170, 200, 20);
        this.password = new GuiPasswordField(this.mc.fontRendererObj, width / 2 - 100, 210, 200, 20);

        this.username.setText("");
        this.password.setText("");

        this.username.setFocused(true);

        Keyboard.enableRepeatEvents(true);
    }
    @Override
    protected void actionPerformed(GuiButton button)  {

        switch (button.id)
        {
            case 1:
            {
                // mc.displayGuiScreen(guiMainMenu);
                mc.shutdown();
                break;
            }
            case 0:
            {
                if(this.CheckCreds(this.username.getText(), this.password.getText())) {
                    System.out.println("Login Confirmed");
                    //Judgement.INSTANCE.username = this.username.getText();
                    //Judgement.INSTANCE.password = this.password.getText();
                       mc.displayGuiScreen(new ClientMainMenu());
                    this.onGuiClosed();

                } else {
                     this.Status = ChatFormatting.DARK_RED + "Login failed!";
                }
                break;
            }
        }
    }

    private boolean CheckCreds(String username, String password) {
        boolean found = false;
        try {
            F = HWID();
            final URL url = new URL("https://pastebin.com/raw/h2TfKFZq");
            //TODO url here
            try {
                UserLogin.scanner = new Scanner(url.openStream());
                while (UserLogin.scanner.hasNextLine()) {
                    if (found) {
                        break;
                    }

                    final String creds = UserLogin.scanner.nextLine();

                    if (creds.contains(":")) {
                        String[] args = creds.split(":");

                        if (username.equals(args[0])) {

                            System.out.println("Confirmed username, " + username);
                            this.Status = "Confirmed username, " + username;
                            if (password.equals(args[1])) {
                                System.out.println("Confirmed password, " + password);
                                this.Status = "Confirmed password, " + password;
                                if (F.equals(args[2])) {
                                    System.out.println("Setting user, " + username);
                                    found = true;
                                    Client.INSTANCE.user = username;
                                    System.out.println("Confirmed HWID, Username, and Password | User: " + Client.INSTANCE.user);
                                    this.Status = ChatFormatting.GREEN + "Confirmed HWID, Username, and Password | User: " + Client.INSTANCE.user;
                                    this.username.setText("");
                                    this.password.setText("");
                                } else {
                                    this.Status = ChatFormatting.DARK_RED + "Denied HWID, " + F;
                                    System.out.println("Denied hwid, " + F + ", " + args[2]);
                                    found = false;
                                }

                            } else {
                                this.Status = ChatFormatting.DARK_RED + "Denied password, " + password;
                                System.out.println("Denied password, " + password);
                                found = false;
                            }
                        } else {
                            this.Status = ChatFormatting.DARK_RED + "Denied username, " + username;
                            System.out.println("Denied username, " + username);
                            found = false;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return found;
    }
    @Override
    public void drawScreen(int x2, int y2, float z2)
    {
        this.drawDefaultBackground();



        GL11.glPushMatrix();
        GL11.glScaled(2, 2, 1);
        GL11.glPopMatrix();
        //   this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        // this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);

        final FontRenderer fr = Client.INSTANCE.getFontManager().getFont("Display 16", true);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtil.drawRoundedRect(width / 2 - 150, 100/*height / 50*/, (width / 2 - 100) - 80/*this.width - 300*/, height / 4 + 24 + 72 + 12 + 72 + 25, 5, 0x99000000);
        this.username.drawTextBox();
        this.password.drawTextBox();
        fr.drawCenteredString("Felix Login - Log in with your Client Login", width / 2, 135, -1);
        fr.drawCenteredString(this.Status, width / 2, 115, -1);

        fr.drawCenteredString("Made by Jinthium", width / 2, height - 10, -1);

        if (this.username.getText().isEmpty())
        {
            fr.drawString("Username", width / 2 - 96, 176, -7829368);
        }
        if (this.password.getText().isEmpty())
        {
            fr.drawString("Password", width / 2 - 96, 216, -7829368);
        }

        super.drawScreen(x2, y2, z2);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try
        {
            super.keyTyped(character, key);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if(key == Keyboard.KEY_ESCAPE){
            mc.currentScreen = this;
        }

        if (character == '\t')
        {
            if (!this.username.isFocused() && !this.password.isFocused())
            {
                this.username.setFocused(true);
            }
            else
            {
                this.username.setFocused(this.password.isFocused());
                this.password.setFocused(!this.username.isFocused());
            }
        }

        if (character == '\r')
        {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }

        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button)
    {
        try
        {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
    }


    @Override
    public void updateScreen()
    {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}