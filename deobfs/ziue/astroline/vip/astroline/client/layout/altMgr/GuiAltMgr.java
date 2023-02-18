/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.thealtening.auth.TheAlteningAuthentication
 *  com.thealtening.auth.service.AlteningServiceType
 *  fr.litarvan.openauth.microsoft.MicrosoftAuthResult
 *  fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException
 *  fr.litarvan.openauth.microsoft.MicrosoftAuthenticator
 *  fr.litarvan.openauth.microsoft.model.response.MinecraftProfile
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Session
 *  net.minecraft.util.Session$Type
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.altMgr.Alt
 *  vip.astroline.client.layout.altMgr.AltButton
 *  vip.astroline.client.layout.altMgr.AltScrollList
 *  vip.astroline.client.layout.altMgr.GuiAltMgr$AddAltDialog
 *  vip.astroline.client.layout.altMgr.GuiAltMgr$ClearAllDialog
 *  vip.astroline.client.layout.altMgr.GuiAltMgr$DeleteAltDialog
 *  vip.astroline.client.layout.altMgr.GuiAltMgr$DirectLoginDialog
 *  vip.astroline.client.layout.altMgr.GuiAltMgr$EditAltDialog
 *  vip.astroline.client.layout.altMgr.GuiAltMgr$KingAltInputDialog
 *  vip.astroline.client.layout.altMgr.GuiAltMgr$MicrosoftLoginDialog
 *  vip.astroline.client.layout.altMgr.dialog.DialogWindow
 *  vip.astroline.client.layout.altMgr.kingAlts.AltJson
 *  vip.astroline.client.layout.altMgr.kingAlts.KingAlts
 *  vip.astroline.client.layout.altMgr.kingAlts.ProfileJson
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.storage.utils.login.LoginUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 *  yarukon.oauth.OAuthService
 */
package vip.astroline.client.layout.altMgr;

import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.altMgr.Alt;
import vip.astroline.client.layout.altMgr.AltButton;
import vip.astroline.client.layout.altMgr.AltScrollList;
import vip.astroline.client.layout.altMgr.GuiAltMgr;
import vip.astroline.client.layout.altMgr.dialog.DialogWindow;
import vip.astroline.client.layout.altMgr.kingAlts.AltJson;
import vip.astroline.client.layout.altMgr.kingAlts.KingAlts;
import vip.astroline.client.layout.altMgr.kingAlts.ProfileJson;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.storage.utils.login.LoginUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;
import yarukon.oauth.OAuthService;

public class GuiAltMgr
extends GuiScreen {
    public GuiScreen parent;
    public AltScrollList list;
    public static CopyOnWriteArrayList<Alt> alts = new CopyOnWriteArrayList();
    public ArrayList<AltButton> cButtons = new ArrayList();
    public Alt selectedAlt = null;
    public String apiString = "King Alts: API Key not set";
    public static int premiumAlts = 0;
    public static int crackedAlts = 0;
    public DialogWindow popupDialog;
    public static OAuthService oAuthService = new OAuthService();

    public GuiAltMgr(GuiScreen parent) {
        this.parent = parent;
    }

    public void initGui() {
        this.cButtons.clear();
        this.cButtons.add(new AltButton(0, (float)(this.width / 2 - 120), (float)(this.height - 52), 57.0f, 20.0f, "Use"));
        this.cButtons.add(new AltButton(1, (float)(this.width / 2) - 60.0f, (float)(this.height - 52), 117.0f, 20.0f, "Direct Login"));
        this.cButtons.add(new AltButton(2, (float)(this.width / 2 - 120), (float)(this.height - 28), 57.0f, 20.0f, "Star"));
        this.cButtons.add(new AltButton(3, (float)(this.width / 2 - 60), (float)(this.height - 28), 57.0f, 20.0f, "Add"));
        this.cButtons.add(new AltButton(4, (float)(this.width / 2), (float)(this.height - 28), 57.0f, 20.0f, "Edit"));
        this.cButtons.add(new AltButton(5, (float)(this.width / 2 + 60), (float)(this.height - 52), 57.0f, 20.0f, "Delete"));
        this.cButtons.add(new AltButton(6, (float)(this.width / 2 + 60), (float)(this.height - 28), 57.0f, 20.0f, "Back"));
        this.cButtons.add(new AltButton(7, 5.0f, 5.0f, 110.0f, 20.0f, "Import Alts"));
        this.cButtons.add(new AltButton(8, (float)(this.width - 85), 5.0f, 80.0f, 20.0f, "Clear All"));
        this.cButtons.add(new AltButton(9, (float)(this.width - 85 - 85), 5.0f, 80.0f, 20.0f, "King Alts"));
        if (KingAlts.API_KEY.length() > 3) {
            this.cButtons.add(new AltButton(10, 5.0f, 30.0f, 110.0f, 20.0f, "Add Alt by King Alts"));
        }
        this.cButtons.add(new AltButton(13, (float)(this.width / 2 - 200), (float)(this.height - 28), 77.0f, 20.0f, "Microsoft Login"));
        this.list = new AltScrollList(this, alt -> {
            this.selectedAlt = alt;
        }, alt -> this.doClickButton(this.cButtons.get(0)));
        new /* Unavailable Anonymous Inner Class!! */.start();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.popupDialog != null && this.popupDialog.destroy) {
            this.popupDialog = null;
        }
        RenderUtil.drawImg((ResourceLocation)new ResourceLocation("astroline/images/bg.jpg"), (double)0.0, (double)0.0, (double)this.width, (double)this.height);
        FontManager.sans16.drawCenteredString("Current name: " + Minecraft.getMinecraft().getSession().getUsername() + EnumChatFormatting.WHITE + " (" + (this.mc.getSession().getSessionType() == Session.Type.LEGACY ? "Cracked" : "Premium") + ")", (float)this.width / 2.0f, 5.0f, -1);
        FontManager.sans16.drawCenteredString("Premium: " + premiumAlts + ", Cracked: " + crackedAlts, (float)this.width / 2.0f, 15.0f, -1);
        FontManager.sans16.drawCenteredString(this.apiString, (float)this.width / 2.0f, 25.0f, -1);
        for (AltButton button : this.cButtons) {
            button.drawButton(mouseX, mouseY);
        }
        if (this.list != null) {
            this.list.doDraw((float)this.width / 2.0f - 120.0f, 40.0f, 237.0f, (float)(this.height - 100), mouseX, mouseY);
        }
        if (this.popupDialog != null) {
            RenderUtil.drawRect((float)0.0f, (float)0.0f, (float)this.width, (float)this.height, (int)-2013265920);
            this.popupDialog.draw(mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void updateScreen() {
        this.cButtons.get((int)0).isEnabled = !alts.isEmpty() && this.selectedAlt != null;
        this.cButtons.get((int)2).isEnabled = !alts.isEmpty() && this.selectedAlt != null;
        this.cButtons.get((int)4).isEnabled = !alts.isEmpty() && this.selectedAlt != null;
        boolean bl = this.cButtons.get((int)5).isEnabled = !alts.isEmpty() && this.selectedAlt != null;
        if (this.cButtons.get((int)2).isEnabled) {
            if (this.selectedAlt != null) {
                this.cButtons.get((int)2).displayString = this.selectedAlt.isStarred() ? "Unstar" : "Star";
            }
        } else {
            this.cButtons.get((int)2).displayString = "Star";
        }
        if (this.selectedAlt != null && !alts.contains(this.selectedAlt)) {
            this.selectedAlt = null;
        }
        if (this.popupDialog == null) return;
        this.popupDialog.updateScreen();
    }

    public void handleMouseInput() throws IOException {
        if (this.popupDialog == null) {
            this.list.handleMouseInput();
        }
        super.handleMouseInput();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.popupDialog == null) {
            for (AltButton button : this.cButtons) {
                if (!button.isHovered() || !button.isEnabled) continue;
                button.playPressSound(this.mc.getSoundHandler());
                this.doClickButton(button);
            }
            this.list.onClick(mouseX, mouseY, mouseButton);
        } else {
            this.popupDialog.mouseClick(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    public void doClickButton(AltButton button) {
        switch (button.buttonID) {
            case 0: {
                new Thread(() -> {
                    this.apiString = "Logging in...";
                    if (this.selectedAlt.getEmail().contains("@alt.com")) {
                        TheAlteningAuthentication.theAltening().updateService(AlteningServiceType.THEALTENING);
                        String reply = LoginUtils.login((String)this.selectedAlt.getEmail(), (String)"THEALTENING");
                        this.apiString = "" + reply;
                        if (reply != null) return;
                        this.apiString = "Logged as " + this.mc.getSession().getUsername();
                        this.selectedAlt.setChecked(this.mc.getSession().getUsername());
                        return;
                    }
                    if (this.selectedAlt.isCracked()) {
                        LoginUtils.changeCrackedName((String)this.selectedAlt.getEmail());
                        this.apiString = "Logged as " + this.mc.getSession().getUsername();
                    } else {
                        Object reply = null;
                        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                        try {
                            MicrosoftAuthResult result = null;
                            try {
                                result = authenticator.loginWithCredentials(this.selectedAlt.getEmail(), this.selectedAlt.getPassword());
                            }
                            catch (MicrosoftAuthenticationException e) {
                                throw new RuntimeException(e);
                            }
                            MinecraftProfile profile = result.getProfile();
                            Minecraft.getMinecraft().session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "microsoft");
                            System.out.println("Logged in with " + Minecraft.getMinecraft().session.getUsername());
                            Astroline.currentAlt = new String[]{this.selectedAlt.getEmail(), this.selectedAlt.getPassword()};
                        }
                        catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                        this.apiString = reply;
                        if (reply != null) return;
                        this.apiString = "Logged as " + this.mc.getSession().getUsername();
                        this.selectedAlt.setChecked(this.mc.getSession().getUsername());
                    }
                }).start();
                break;
            }
            case 1: {
                this.popupDialog = new DirectLoginDialog(this);
                this.popupDialog.makeCenter((float)this.width, (float)this.height);
                break;
            }
            case 2: {
                Alt alt = alts.get(this.list.getSelectedIndex());
                alt.setStarred(!alt.isStarred());
                GuiAltMgr.sortAlts();
                break;
            }
            case 3: {
                this.popupDialog = new AddAltDialog(this);
                this.popupDialog.makeCenter((float)this.width, (float)this.height);
                break;
            }
            case 4: {
                this.popupDialog = new EditAltDialog(this, this.selectedAlt);
                this.popupDialog.makeCenter((float)this.width, (float)this.height);
                break;
            }
            case 5: {
                this.popupDialog = new DeleteAltDialog(this, this.selectedAlt);
                this.popupDialog.makeCenter((float)this.width, (float)this.height);
                break;
            }
            case 6: {
                this.mc.displayGuiScreen(this.parent);
                break;
            }
            case 7: {
                new Thread((Runnable)new /* Unavailable Anonymous Inner Class!! */).start();
                break;
            }
            case 8: {
                this.popupDialog = new ClearAllDialog(this);
                this.popupDialog.makeCenter((float)this.width, (float)this.height);
                break;
            }
            case 9: {
                this.popupDialog = new KingAltInputDialog(this);
                this.popupDialog.makeCenter((float)this.width, (float)this.height);
                break;
            }
            case 10: {
                new Thread(() -> {
                    this.apiString = "Generating Alt...";
                    AltJson json = KingAlts.getAlt();
                    if (json.getMessage() != null) {
                        this.apiString = "\u00a7cERROR: " + json.getMessage();
                    } else {
                        alts.add(0, new Alt(json.getEmail(), json.getPassword(), null));
                        GuiAltMgr.sortAlts();
                        ProfileJson profile = KingAlts.getProfile();
                        this.apiString = profile.getMessage() != null ? "\u00a7cERROR: " + profile.getMessage() : "You have generated " + profile.getGeneratedToday() + " alt" + (profile.getGeneratedToday() > 1 ? "s" : "") + " today.";
                    }
                }).start();
                break;
            }
            case 12: {
                this.openUrl("https://kw.baoziwl.com");
                break;
            }
            case 13: {
                this.popupDialog = new MicrosoftLoginDialog(this);
                this.popupDialog.makeCenter((float)this.width, (float)this.height);
                new Thread(() -> oAuthService.authWithNoRefreshToken()).start();
                break;
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.popupDialog != null) {
            this.popupDialog.keyTyped(typedChar, keyCode);
        }
        if (keyCode == 1 && this.popupDialog == null) {
            this.mc.displayGuiScreen(this.parent);
        }
        super.keyTyped(typedChar, keyCode);
    }

    public static void sortAlts() {
        CopyOnWriteArrayList<Alt> newAlts = new CopyOnWriteArrayList<Alt>();
        premiumAlts = 0;
        crackedAlts = 0;
        for (Alt value : alts) {
            if (!value.isStarred()) continue;
            newAlts.add(value);
        }
        for (Alt alt : alts) {
            if (alt.isCracked() || alt.isStarred()) continue;
            newAlts.add(alt);
        }
        for (Alt alt : alts) {
            if (!alt.isCracked() || alt.isStarred()) continue;
            newAlts.add(alt);
        }
        for (int i = 0; i < newAlts.size(); ++i) {
            for (int i2 = 0; i2 < newAlts.size(); ++i2) {
                if (i == i2 || !((Alt)newAlts.get(i)).getEmail().equals(((Alt)newAlts.get(i2)).getEmail()) || ((Alt)newAlts.get(i)).isCracked() != ((Alt)newAlts.get(i2)).isCracked()) continue;
                newAlts.remove(i2);
            }
        }
        Iterator iterator = newAlts.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                alts = newAlts;
                return;
            }
            Alt newAlt = (Alt)iterator.next();
            if (newAlt.isCracked()) {
                ++crackedAlts;
                continue;
            }
            ++premiumAlts;
        }
    }

    public void openUrl(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        }
        catch (Exception ex) {
            System.out.println("An error occurred while trying to open a url!");
        }
    }

    static /* synthetic */ Minecraft access$000(GuiAltMgr x0) {
        return x0.mc;
    }

    static /* synthetic */ Minecraft access$100(GuiAltMgr x0) {
        return x0.mc;
    }

    static /* synthetic */ Minecraft access$200(GuiAltMgr x0) {
        return x0.mc;
    }
}
