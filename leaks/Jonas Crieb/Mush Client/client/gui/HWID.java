package client.gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import client.Client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class HWID extends GuiScreen {
    private static final Logger logger = LogManager.getLogger();

    private static GuiTextField hwid;

    String status = "";

    boolean tahoma;

    private void setStatus(String s) {
        this.status = s;
    }

    private String getStatus() {
        return this.status;
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        hwid = new GuiTextField(123123, this.mc.fontRendererObj, width / 2 - 100, width / 2 - 20 - 100, 200, 20);
        hwid.setMaxStringLength(16);
        this.buttonList.add(new GuiButton(1001, width / 2 - 100, width / 2 + 20 - 100, "Log in"));
    }

    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1001:
                try {
                    String[] strings = requestURLSRC("https://pastebin.com/raw/4PPaSJDN").split("!");
                    boolean contains = false;
                    byte b;
                    int i;
                    String[] arrayOfString1;
                    for (i = (arrayOfString1 = strings).length, b = 0; b < i; ) {
                        String s = arrayOfString1[b];
                        if (s.equalsIgnoreCase(String.valueOf(HWID.getHWID2()) + ":" + s.split(":")[1] + ":" + s.split(":")[2])) {
                            //Client.IiIIIiiiiiiiiII = true;
                            Client.user = s.split(":")[1];
                            Client.rank = s.split(":")[2];
                            this.mc.displayGuiScreen((GuiScreen)new GuiMainMenu());
                            //final Fard webhook = new Fard(Client.webcock);
                            //webhook.addEmbed(new Fard.EmbedObject().addField("Log IN cockS", HWID.getHWID2(), false).addField("OS Username:", System.getProperty("user.name"), false).setTitle("Usar Name: " + Client.user));
                            //webhook.execute();


                        }

                        b++;
                    }
                } catch (Exception e) {
                    try {
                        HWID.getHWID2();
                        //this.mc.shutdownMinecraftApplet();
                    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                break;
        }
    }
  /*
  public static String getHWID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    String s = "";
    String main = String.valueOf(System.getenv("PROCESSOR_IDENTIFIER")) + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
    byte[] bytes = main.getBytes("UTF-8");
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    byte[] md5 = messageDigest.digest(bytes);
    int i = 0;
    byte b;
    int j;
    byte[] arrayOfByte1;
    for (j = (arrayOfByte1 = md5).length, b = 0; b < j; ) {
      byte b1 = arrayOfByte1[b];
      s = String.valueOf(s) + Integer.toHexString(b1 & 0xFF);
      if (i != md5.length - 1)
        s = (new StringBuilder(String.valueOf(s))).toString();
      i++;
      b++;
    }
    return s;
  }

   */

    public static String fun() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String s = "";
        String main = String.valueOf(System.getenv("PROCESSOR_IDENTIFIER")) + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
        byte[] bytes = main.getBytes("UTF-8");
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] md5 = messageDigest.digest(bytes);
        int i = 0;
        byte b;
        int j;
        byte[] arrayOfByte1;
        for (j = (arrayOfByte1 = md5).length, b = 0; b < j; ) {
            byte b1 = arrayOfByte1[b];
            s = String.valueOf(s) + Integer.toHexString(b1 & 0xFF);
            if (i != md5.length - 1)
                s = String.valueOf(s) + "-nigger-";
            i++;
            b++;
        }
        return s;
    }

    public static String requestURLSRC(String BLviCHHy76v5Ch39PB3hpcX7W2qe45YaBPQyn285Dcg27) throws IOException {
        URL urlObject = new URL(BLviCHHy76v5Ch39PB3hpcX7W2qe45YaBPQyn285Dcg27);
        URLConnection urlConnection = urlObject.openConnection();
        return AP2iKAwcS2gFL8cX8z944ZiJp2zS54T68Tp39nr2rJAwh(urlConnection.getInputStream());
    }

    private static String AP2iKAwcS2gFL8cX8z944ZiJp2zS54T68Tp39nr2rJAwh(InputStream L58C336iNBkwz86u4QV3HcDJ94i34gWv4gpzbqBC5ZCdG) throws IOException
    {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(L58C336iNBkwz86u4QV3HcDJ94i34gWv4gpzbqBC5ZCdG, "UTF-8")))
        {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void drawScreen(int x2, int y2, float z2) {
       //RenderUtil.drawImg(new ResourceLocation("bg.jpg"), 0.0D, 0.0D, this.width, this.height);
        super.drawScreen(x2, y2, z2);
        //FontRenderer fr = this.tahoma ? Client.INSTANCE.getFontManager().getFont("Mercurian 18", true) : Client.INSTANCE.getFontManager().getFont("Display 18", true);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        String text = "Mercurian Authorization";
        float x = sr.getScaledWidth() / 2.0F;
        float y = sr.getScaledHeight() / 2.0F;
        float width = 260.0F;
        float height = 20.0F;
        //RenderUtil.drawRoundedRect13((x - 130.0F), (y - 10.0F + 3.0F), 260.0D, 20.0D, 9.0D, new Color(39, 42, 48, 0));
        mc.fontRendererObj.drawStringWithShadow("Mercurian Authorization", x, y, Color.GRAY.hashCode());
    }

    public void mouseClicked(int x2, int y2, int z2) {
        try {
            super.mouseClicked(x2, y2, z2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hwid.mouseClicked(x2, y2, z2);
    }
    public static String getHWID2() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String s = "";
        String main = System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
        byte[] bytes = main.getBytes("UTF-8");
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] md5 = messageDigest.digest(bytes);
        int i = 0;
        for (byte b : md5) {
            s = s + Integer.toHexString(b & 0xFF | 0x300).substring(0, 3);
            i++;
        }
        return s;
    }

    protected void keyTyped(char character, int key) {
        hwid.textboxKeyTyped(character, key);
        if (character == '\r')
            actionPerformed(this.buttonList.get(0));
    }
}
