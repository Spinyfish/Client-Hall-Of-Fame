package arithmo.gui.altmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import crispy.Crispy;

import crispy.util.file.Filer;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiTheAltening extends GuiScreen{
    private PasswordField password;
    public AltLoginThread thread;
    public static Filer AlteningToken = new Filer("AlteningToken", "Crispy");
    private final GuiScreen previousScreen;
    public GuiTheAltening(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }
    @Override
    public void drawScreen(int x, int y, float z) {
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        Gui.drawRect(0.0, 0.0, res.getScaledWidth(), res.getScaledHeight(), Colors.getColor(0));
        this.password.drawTextBox();
        this.drawCenteredString(this.mc.fontRendererObj, "Altening", this.width / 2, 20, -1);
        this.drawCenteredString(this.mc.fontRendererObj, this.thread == null ? (Object)((Object) EnumChatFormatting.GRAY) + "Idle..." : this.thread.getStatus(), this.width / 2, 29, -1);


    }
    public void initGui() {
        int var3 = this.height / 4 + 24;

        this.password = new PasswordField(this.mc.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
        this.password.setFocused(true);
        if(!AlteningToken.read().isEmpty()) {

            password.setText(AlteningToken.read().get(0));
        }
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, "Login"));
        Keyboard.enableRepeatEvents(true);

    }
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents((boolean)false);
    }
    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            switch (button.id) {
                case 0: {
                    Runnable run  = () -> {


                        try {

                            URL url = new URL("http://api.thealtening.com/v1/generate?info=true&token=" + password.getText());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                            String read = reader.readLine();
                            String reading = read;

                            final JsonObject jsonObject = new JsonParser().parse(reading).getAsJsonObject();
                            thread = new AltLoginThread(jsonObject.get("token").getAsString(), "Crispy");
                            thread.run();


                            if (!thread.interrupted()) {
                                AltManager.registry.add(new Alt(jsonObject.get("token").getAsString(), "Altening", jsonObject.get("username").getAsString()));
                                try {
                                    Crispy.INSTANCE.getAltFile() .getFile(Alts.class).saveFile();
                                } catch (Exception localException) {
                                }
                                AlteningToken.clear();
                                AlteningToken.write(password.getText());

                            }

                        } catch (Exception e) {

                        }
                    };
                    new Thread(run).start();

                    break;
                }
            }
        } catch (Exception e) {

        }
    }
    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (Exception e) {

        }
        if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        this.password.textboxKeyTyped(character, key);

    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        try {
            super.mouseClicked(x, y, button);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.password.mouseClicked(x, y, button);
    }
    @Override
    public void updateScreen() {
        this.password.updateCursorCounter();
    }

    public ArrayList<String> getLinesFromURL(final String urlString) {
        final ArrayList<String> lines = new ArrayList<String>();
        try {
            final URL url = new URL(urlString);
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

}
