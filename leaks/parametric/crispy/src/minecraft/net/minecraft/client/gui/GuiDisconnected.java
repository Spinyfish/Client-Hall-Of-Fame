package net.minecraft.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import arithmo.gui.altmanager.AltLoginThread;
import arithmo.gui.altmanager.GuiTheAltening;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import crispy.Crispy;
import crispy.features.hacks.impl.misc.SsmBotter;
import crispy.util.time.TimeHelper;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected extends GuiScreen
{
    public AltLoginThread thread;
    private String reason;
    private IChatComponent message;
    private List multilineMessage;
    private final GuiScreen parentScreen;
    TimeHelper timer = new TimeHelper();
    private int field_175353_i;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp)
    {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {}

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        timer.reset();
        if(Crispy.INSTANCE.getHackManager().getHack(SsmBotter.class).isEnabled()) {
            Runnable run = () -> {


                try {
                    URL url = new URL("http://api.thealtening.com/v1/generate?info=true&token=" + GuiTheAltening.AlteningToken.read().get(0));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String read = reader.readLine();
                    String reading = read;

                    final JsonObject jsonObject = new JsonParser().parse(reading).getAsJsonObject();
                    thread = new AltLoginThread(jsonObject.get("token").getAsString(), "Crispy");
                    thread.run();

                    if (!thread.interrupted()) {
                        mc.displayGuiScreen(new GuiConnecting(this, mc, GuiConnecting.lastIp, GuiConnecting.lastPort));


                    }

                } catch (Exception e) {

                }
            };
            new Thread(run).start();
        }
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(1, (int) (this.width / 2 - 100) , (int) (this.height / 1.8 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT), "Reconnect"));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, (int) (this.height / 1.6 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT), "Generate Altening Alt"));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
        if(button.id == 1) {

            mc.displayGuiScreen(new GuiConnecting(this, mc,  GuiConnecting.lastIp, GuiConnecting.lastPort));
        }
        if(button.id == 2) {

            Runnable run = () -> {


                try {
                    URL url = new URL("http://api.thealtening.com/v1/generate?info=true&token=" + GuiTheAltening.AlteningToken.read().get(0));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String read = reader.readLine();
                    String reading = read;

                    final JsonObject jsonObject = new JsonParser().parse(reading).getAsJsonObject();
                    thread = new AltLoginThread(jsonObject.get("token").getAsString(), "Crispy");
                    thread.run();

                    if (!thread.interrupted()) {
                        mc.displayGuiScreen(new GuiConnecting(this, mc, GuiConnecting.lastIp, GuiConnecting.lastPort));


                    }

                } catch (Exception e) {

                }
            };
            new Thread(run).start();

        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {

        if(timer.hasReached(3000) && Crispy.INSTANCE.getHackManager().getHack(SsmBotter.class).isEnabled()) {
            timer.reset();
            mc.displayGuiScreen(new GuiConnecting(this, mc,  GuiConnecting.lastIp, GuiConnecting.lastPort));
        }
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int var4 = this.height / 2 - this.field_175353_i / 2;
        this.drawCenteredString(this.mc.fontRendererObj, this.thread == null ? (Object)((Object)EnumChatFormatting.GRAY) + "Idle..." : this.thread.getStatus(), this.width / 2, 29, -1);

        if (this.multilineMessage != null)
        {
            for (Iterator var5 = this.multilineMessage.iterator(); var5.hasNext(); var4 += this.fontRendererObj.FONT_HEIGHT)
            {
                String var6 = (String)var5.next();

                this.drawCenteredString(this.fontRendererObj, var6, this.width / 2, var4, 16777215);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
