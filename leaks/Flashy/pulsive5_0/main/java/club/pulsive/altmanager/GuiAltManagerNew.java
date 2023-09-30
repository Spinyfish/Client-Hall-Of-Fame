package club.pulsive.altmanager;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import club.pulsive.altmanager.guishit.PasswordField;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.module.impl.visual.HUD;
import club.pulsive.impl.util.customui.CustomButton;
import club.pulsive.impl.util.render.*;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;
import club.pulsive.impl.util.render.animations.SmoothStep;
import club.pulsive.impl.util.render.secondary.GuiTextFieldCustom;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import club.pulsive.impl.util.render.shaders.Blur;
import club.pulsive.impl.util.render.shaders.MainMenu;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;


public final class GuiAltManagerNew extends GuiScreen {
    private final GuiScreen previousScreen;
    private final AltManager altmgr = Pulsive.INSTANCE.getAltManager();
    private AltLoginThread thread;
    private GuiTextFieldCustom username;
    private PasswordField password;
    private GuiTextFieldCustom combo;
    private AltList list;
    private Alt selectedAlt = null;
    private GuiButton loginButton;
    private GuiButton deleteButton;
    public static Animation ease = new SmoothStep(500, 1);

    public GuiAltManagerNew(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;

    }
    private long initTime;


    @Override
    public void initGui() {
        initTime = System.currentTimeMillis();
        ease.setDirection(Direction.FORWARDS);
        ease.reset();
        //loading the alts file
        altmgr.load();

        list = new AltList(mc, 30, width - 238, 42, width, height, 27, height);

        //adding buttons
        buttonList.clear();

        buttonList.add(new CustomButton(0, (int) (width - 200 + 192 - 192 / 2.1f), 30 + 34 * 2, (int) (192 / 2.1f), 25, "Login"));
        buttonList.add(new CustomButton(1, (int) (width - 200 + 192 - 192 / 2.1f), 30 + 34 * 2 + 29, (int) (192 / 2.1f), 25,  "Add Login"));

        buttonList.add(new CustomButton(2, width - 200, 30 + 34 * 2, (int) (192 / 2.1f), 25,"Clipboard"));

        buttonList.add(new CustomButton(3, width - 200, 30 + 34 * 2 + 29, (int) (192 / 2.1f), 25,"Add Account from Clipboard"));

         buttonList.add(new CustomButton(42069, 30, 143, ((width / 2 - 32) / 5) *4+ 5, 22, "Microcum Login"));
        buttonList.add(new CustomButton(4, 4, 4, 60, 22,"Back"));



        loginButton = new CustomButton(5, width - 200 + 10, height - 50 - 60 + 25, 194 - 20, 25,"Login");
        deleteButton = new CustomButton(6, width - 200 + 10, height - 50 - 60 + 25 + 30, 194 - 20, 25, "Delete");

        //adding text fields
        username = new GuiTextFieldCustom(3, mc.fontRendererObj, width - 200, 30 + 34, (int) (192 / 2.1f), 25);
        password = new PasswordField(3, mc.fontRendererObj, (int) (width - 200 + 192 - 192 / 2.1f), 30 + 34, (int) (192 / 2.1f), 25);
        combo = new GuiTextFieldCustom(3, mc.fontRendererObj, width - 200, 30, 192, 25);

        username.setMaxStringLength(128);
        password.setMaxStringLength(128);
        combo.setMaxStringLength(256);

        //uncomment this to change the add account from clipboard button to generate cracked account button
        buttonList.get(3).displayString = "Cracked";

        //removing alt list background
        list.setOverlayBackground(false);
        Keyboard.enableRepeatEvents(true);

        //resetting the status
        altmgr.setStatus("§7Idle...");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String statusString = altmgr.getStatus();
        ScaledResolution scaledRes = new ScaledResolution(mc);

        //drawing the background
        club.pulsive.impl.util.render.shaders.MainMenu.setTime((System.currentTimeMillis() - initTime) / 1000);
        club.pulsive.impl.util.render.shaders.MainMenu.nigger2();
        Gui.drawRect(0,0,this.width,this.height, new Color(31,31,31, 230).getRGB());
        Blur.renderBlur(30);
        RenderUtil.scaleStart(width / 2, height / 2, (float) ease.getOutput());

        list.drawScreen(mouseX, mouseY, partialTicks);
        //drawRect(0,0, width, 146, 0xFF32333F);




        //title
        Fonts.grayclifftitle.drawCenteredString("Alt Manager", width / 2f, 8, -1);




        //status
        Fonts.moon.drawStringWithShadow(statusString, width - Fonts.moon.getStringWidth(statusString) - 8, 8, -1);

        Fonts.moon.drawCenteredString("Current User:", 120, 4, -1);
        Fonts.moon.drawCenteredString(mc.getSession().getUsername(), 120, 16, -1);



        if(selectedAlt != null) {
            RoundedUtil.drawRoundedRect(width - 200, 30 + 34 * 2 + 27 + 31, width - 220 + 212, height -10, 8, 0xFF151515);
            Date date = new Date(selectedAlt.getCreationdate());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String hiddenPassword = !selectedAlt.getPassword().equals("") ? (selectedAlt.getPassword().substring(0, selectedAlt.getPassword().length() < 3 ? 0 : 3) +
                    selectedAlt.getPassword().replaceAll("(?s).", "*").substring(selectedAlt.getPassword().length() < 3 ? 0 : 3)) : "Cracked account";

            // Gui.drawRect(width - 204, 146, width, height, 0xFF32333F);

            Fonts.popMedSmall.drawString(selectedAlt.getUsername(), width - 200 + 10, 164, -1);
            Fonts.popRegSmall.drawString(hiddenPassword,width - 200 + 10,
                    176, 0xFFAAAAAA);
            Fonts.popRegSmall.drawString("Created " + dateFormat.format(date) + " at " + timeFormat.format(date), width - 200 + 10,
                    188, 0x90AAAAAA);

            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            RenderUtil.drawImage(new ResourceLocation("pulsabo/images/alexbody.png"), width - 210, height / 2 - 30, 200, 200);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();

            loginButton.drawButton(mc, mouseX, mouseY);
            deleteButton.drawButton(mc, mouseX, mouseY);
        } else {

            //Gui.drawRect(width - 204, 146, width, height, 0xFF32333F);
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            //  Gui.drawRect(width - 204, 146, width, 198, 0xFF32333F);


        }
        //Draw.drawRectangle(0, ScaledResolution.getScaledHeight() - 60, 30000, ScaledResolution.getScaledHeight(), 0xFF535562);

        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        RenderUtil.drawImage(new ResourceLocation("pulsabo/images/icon.png"), this.width - width + 30, this.height - 70, 50, 50);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        Draw.drawRectangle(0, 0, 0, 0, -1);
        //drawing the list

        for (GuiButton guiButton : this.buttonList) {

            guiButton.drawButton(this.mc, mouseX, mouseY);
        }
        //drawing the text boxes
        username.drawTextBox();
        password.drawTextBox();
        combo.drawTextBox();

        if (username.getText().isEmpty())
            Fonts.popRegSmall.drawStringWithShadow("E-Mail", width - 190, 30 + 34 * 1 + 25 / 2 - 4, 0xFFAAAAAA);

        if (password.getText().isEmpty())
            Fonts.popRegSmall.drawStringWithShadow("Password", (int) (width - 200 + 192 - 192 / 2.1f) + 12, 30 + 34 * 1 + 25 / 2 - 4, 0xFFAAAAAA);

        if (combo.getText().isEmpty())
            Fonts.popRegSmall.drawStringWithShadow("E-Mail : Password", width - 190, 30 + 25 / 2 - 4, 0xFFAAAAAA);
        Fonts.moon.drawCenteredString("alts.zone offer quality alts.", width - width + 60, height - 14, -1);
        RenderUtil.scaleEnd();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        //random string generator
        int targetStringLength = 16 - 7 - 4;
        int leftLimit = 97;
        int rightLimit = 123;
        Random random = new Random();
        String generatedString = "Pulsive" + "_" + random.ints(leftLimit, rightLimit).limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString() + Minecraft.getSystemTime() % 1000L;



        switch (button.id) {
            case 0:

                if(combo.getText().contains("@alt")) {

                    thread = new AltLoginThread(username.getText(), "xd");



                    thread.start();
                } else {
                    if (!combo.getText().contains(":"))
                        thread = new AltLoginThread(username.getText(), password.getText());
                    else {
                        String[] comboCredentials = combo.getText().split(":", 2);

                        thread = new AltLoginThread(comboCredentials[0], comboCredentials[1]);
                    }

                    thread.start();
                }
                //login button

                break;

            case 1:
                //add alt button
                String[] comboCredentials = combo.getText().split(":", 2);

                if (combo.getText().contains(":") && comboCredentials.length > 1) {
                    altmgr.addAlt(new Alt(comboCredentials[0], comboCredentials[1]));
                    LogManager.getLogger().info("Added account \"" + comboCredentials[0] + ":" + comboCredentials[1] + "\".");
                    altmgr.setStatus(EnumChatFormatting.GREEN + "Added account \"" + comboCredentials[0] + ":" + comboCredentials[1] + "\".");
                    combo.setText("");
                } else if (altmgr.isValidCrackedAlt(username.getText()) ||
                        (!username.getText().isEmpty() && !password.getText().isEmpty())) {
                    altmgr.addAlt(new Alt(username.getText(), password.getText()));
                    altmgr.setStatus(EnumChatFormatting.GREEN + "Added account \"" + username.getText() + (password.getText().isEmpty() ? "" : ":" + password.getText()) + "\".");
                    LogManager.getLogger().info("Added account \"" + username.getText() + (password.getText().isEmpty() ? "" : ":" + password.getText()) + "\".");
                    username.setText("");
                    password.setText("");
                } else {
                    altmgr.setStatus(EnumChatFormatting.RED + "Failed to add account.");
                }

                altmgr.save();
                break;
            case 42069:{
                String[] comboCredentials1 = combo.getText().split(":", 2);
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                try {

                    MicrosoftAuthResult result = authenticator.loginWithCredentials(comboCredentials1[0], comboCredentials1[1]);
                    MinecraftProfile profile = result.getProfile();
                    mc.session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "microsoft");
                } catch (MicrosoftAuthenticationException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 2:
                //clipboard login
                if(GuiScreen.getClipboardString().contains("@alt")) {

                    String[] combo = GuiScreen.getClipboardString().replace("\n", "").replace(" ", "").split(":", 2);

                    thread = new AltLoginThread(combo[0], "xd");
                    thread.start();


                } else {
                    if (GuiScreen.getClipboardString().contains(":")) {
                        String[] combo = GuiScreen.getClipboardString().replace("\n", "").replace(" ", "").split(":", 2);

                        thread = new AltLoginThread(combo[0], combo[1]);
                        thread.start();
                    } else {
                        altmgr.setStatus(EnumChatFormatting.RED + "Invalid clipboard.");
                    }
                }

                break;

            case 3:
                //uncomment this to login into a random cracked alt instead of adding a clipboard account
                thread = new AltLoginThread(generatedString, "");
                thread.start();

                //adding alt from clipboard
                /*
                if (GuiScreen.getClipboardString().contains(":")) {
                    String[] combo = GuiScreen.getClipboardString().replace("\n", "").replace(" ", "").split(":", 2);
                    altmgr.addAlt(new Alt(combo[0], combo[1]));
                    altmgr.setStatus(EnumChatFormatting.GREEN + "Added account \"" + combo[0] + (combo[1].isEmpty() ? "" : ":" + combo[1]) + "\".");
                    Logger.info("Added account \"" + combo[0] + (combo[1].isEmpty() ? "" : ":" + combo[1]) + "\".");
                } else {
                    altmgr.setStatus(EnumChatFormatting.RED + "Invalid clipboard.");
                }
                 */
                break;

            case 4:
                //back button
                mc.displayGuiScreen(previousScreen);
                break;

            case 5:
                //back button



                break;
        }
    }


    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (character == '\r') {
            actionPerformed(buttonList.get(0));
        }

        username.textboxKeyTyped(character, key);
        password.textboxKeyTyped(character, key);
        combo.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(selectedAlt != null) {
            if(loginButton.mousePressed(mc, mouseX, mouseY)) {
                thread = new AltLoginThread(selectedAlt.getUsername(), selectedAlt.getPassword());
                thread.start();
            }

            if(deleteButton.mousePressed(mc, mouseX, mouseY)) {
                altmgr.getAlts().remove(selectedAlt);
                selectedAlt = null;
            }
        }
        ScaledResolution sr = new ScaledResolution(mc);
        if(mouseX > 0 && mouseY < sr.getScaledHeight() - 0 && mouseX < 200 && mouseY > sr.getScaledHeight() - 80) {
            try {
                Desktop.getDesktop().browse(new URL("https://alts.zone").toURI());
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        username.mouseClicked(mouseX, mouseY, button);
        password.mouseClicked(mouseX, mouseY, button);
        combo.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void handleMouseInput() throws IOException {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();
        password.updateCursorCounter();
        combo.updateCursorCounter();
    }

    public void playClickSound() {
    }

    //alts list
    class AltList extends Slot {
        public AltList(Minecraft mcIn, int x, int slotWidth, int slotHeight, int width, int height, int top, int bottom) {
            super(mcIn, x, slotWidth, slotHeight, width, height, top, bottom);
        }

        @Override
        protected int getSize() {
            return altmgr.getAlts().size();
        }

        //logging in the alt when double left clikced
        @Override
        protected void elementClicked(int slotHeight, boolean isDoubleClick, int mouseX, int mouseY) {
            selectedAlt = altmgr.getAlts().get(slotHeight);
        }

        //removing the alt when double right clicked
        @Override
        protected void elementRightClicked(int slotHeight, boolean isDoubleClick, int mouseX, int mouseY) {

        }

        //don't delete this
        @Override
        protected boolean isSelected(int slotIndex) {
            return false;
        }

        //don't delete this
        @Override
        protected void drawBackground() {

        }

        //drawing the alt slot
        @Override
        protected void drawSlot(int entryID, int p_180791_2_, int y, int slotHeight, int mouseXIn, int mouseYIn) {
            Alt alt = Pulsive.INSTANCE.getAltManager().getAlts().get(entryID);
            Date date = new Date(alt.getCreationdate());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, 0, 0);

            //alt background
            RoundedUtil.drawRoundedRect(0, y, getSlotWidth(),  y + (slotHeight - 4), 5,  0xFF151515);
            // drawRect(0, y + (slotHeight - 1), getSlotWidth(), y + slotHeight, Hud.prop_color.getValue().getRGB());
            //drawRect(getListWidth() - (slotHeight - 10), y + 10, getListWidth() - 10, y + (slotHeight - 10), 0xFFFF0000);

            //alt password if premium (you can see 3 first characters and the others are hidden)
            String hiddenPassword = !alt.getPassword().equals("") ? (alt.getPassword().substring(0, alt.getPassword().length() < 3 ? 0 : 3) +
                    alt.getPassword().replaceAll("(?s).", "*").substring(alt.getPassword().length() < 3 ? 0 : 3)) : "Cracked account";

            //alt username/mail


            Fonts.popMedSmall.drawStringWithShadow(alt.getUsername(),40,
                    y  + 3, -1);

            //alt password
            Fonts.popRegSmall.drawStringWithShadow("" + hiddenPassword,40 ,
                    y + 2 + 13, 0xFFAAAAAA);

            RenderUtil.drawImage(new ResourceLocation("pulsabo/images//head.jpg"), 5, y + 5, 28, 28);

            //alt face


            GlStateManager.popMatrix();
        }

        //changing position of scollbar (replace return width - 6; by 0 to put the scroll bar at the left side of the screen)
        @Override
        protected int getScrollBarX() {
            return width - 4;
        }
    }
    public void renderPlayerModelTexture(final double x, final double y, final float width, final float height, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        glEnable(GL_BLEND);
        Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, width, height, 64, 64);
        glDisable(GL_BLEND);
    }
}
