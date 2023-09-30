/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package Reality.Realii.guis.login;

import Reality.Realii.Client;
import Reality.Realii.managers.FileManager;
import Reality.Realii.utils.cheats.player.Helper;
import Reality.Realii.utils.render.RenderUtil;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;

import java.io.IOException;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiAltManager
extends GuiScreen {
    private static Minecraft mc = Minecraft.getMinecraft();
    private GuiButton login;
    private GuiButton remove;
    private GuiButton rename;
    private AltLoginThread loginThread;
    private int offset;
    public Alt selectedAlt = null;
    private String status = "\u00a7eWaiting...";

    public GuiAltManager() {
        FileManager.saveAlts();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                if (this.loginThread == null) {
                    mc.displayGuiScreen(null);
                    break;
                }
                if (!this.loginThread.getStatus().equals("Logging in...") && !this.loginThread.getStatus().equals("Do not hit back! Logging in...")) {
                    mc.displayGuiScreen(null);
                    break;
                }
                this.loginThread.setStatus("Do not hit back! Logging in...");
                break;
            }
            case 1: {
                String user = this.selectedAlt.getUsername();
                String pass = this.selectedAlt.getPassword();
                this.loginThread = new AltLoginThread(user, pass);
                this.loginThread.start();
                break;
            }
            case 2: {
                if (this.loginThread != null) {
                    this.loginThread = null;
                }
                Client.instance.getAltManager();
                AltManager.getAlts().remove(this.selectedAlt);
                this.status = "\u00a7cRemoved.";
                this.selectedAlt = null;
                FileManager.saveAlts();
                break;
            }
            case 3: {
                mc.displayGuiScreen(new GuiAddAlt(this));
                break;
            }
            case 4: {
                mc.displayGuiScreen(new GuiAltLogin(this));
                break;
            }
            case 5: {
                Client.instance.getAltManager();
                Client.instance.getAltManager();
                Alt randomAlt = AltManager.alts.get(new Random().nextInt(AltManager.alts.size()));
                String user1 = randomAlt.getUsername();
                String pass1 = randomAlt.getPassword();
                this.loginThread = new AltLoginThread(user1, pass1);
                this.loginThread.start();
                break;
            }
            case 6: {
                mc.displayGuiScreen(new GuiRenameAlt(this));
                break;
            }
            case 7: {
                Client.instance.getAltManager();
                Alt lastAlt = AltManager.lastAlt;
                if (lastAlt == null) {
                    if (this.loginThread == null) {
                        this.status = "?cThere is no last used alt!";
                        break;
                    }
                    this.loginThread.setStatus("?cThere is no last used alt!");
                    break;
                }
                String user2 = lastAlt.getUsername();
                String pass2 = lastAlt.getPassword();
                this.loginThread = new AltLoginThread(user2, pass2);
                this.loginThread.start();
            }
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            if (wheel < 0) {
                this.offset += 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            } else if (wheel > 0) {
                this.offset -= 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            }
        }
        this.drawDefaultBackground();
        Helper.mc.fontRendererObj.drawStringWithShadow(GuiAltManager.mc.session.getUsername(), 10.0f, 10.0f, -7829368);
        Client.instance.getAltManager();
        Helper.mc.fontRendererObj.drawCenteredString("Account Manager - " + AltManager.getAlts().size() + " alts", this.width / 2, 10, -1);
        Helper.mc.fontRendererObj.drawCenteredString(this.loginThread == null ? this.status : this.loginThread.getStatus(), this.width / 2, 20, -1);
        GL11.glPushMatrix();
        this.prepareScissorBox(0.0f, 33.0f, this.width, this.height - 50);
        GL11.glEnable((int)3089);
        int y = 38;
        Client.instance.getAltManager();
        for (Alt alt : AltManager.getAlts()) {
            if (!this.isAltInArea(y)) continue;
            String name = alt.getMask().equals("") ? alt.getUsername() : alt.getMask();
            String pass = alt.getPassword().equals("") ? "\u00a7cCracked" : alt.getPassword().replaceAll(".", "*");
            if (alt == this.selectedAlt) {
                if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown((int)0)) {
                    RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2142943931);
                } else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                    RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2142088622);
                } else {
                    RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2144259791);
                }
            } else if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown((int)0)) {
                RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2146101995);
            } else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2145180893);
            }
            Helper.mc.fontRendererObj.drawCenteredString(name, this.width / 2, y - this.offset, -1);
            Helper.mc.fontRendererObj.drawCenteredString(pass, this.width / 2, y - this.offset + 10, 5592405);
            y += 26;
        }
        GL11.glDisable((int)3089);
        GL11.glPopMatrix();
        super.drawScreen(par1, par2, par3);
        if (this.selectedAlt == null) {
            this.login.enabled = false;
            this.remove.enabled = false;
            this.rename.enabled = false;
        } else {
            this.login.enabled = true;
            this.remove.enabled = true;
            this.rename.enabled = true;
        }
        if (Keyboard.isKeyDown((int)200)) {
            this.offset -= 26;
            if (this.offset < 0) {
                this.offset = 0;
            }
        } else if (Keyboard.isKeyDown((int)208)) {
            this.offset += 26;
            if (this.offset < 0) {
                this.offset = 0;
            }
        }
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 24, 75, 20, "Cancel"));
        this.login = new GuiButton(1, this.width / 2 - 154, this.height - 48, 70, 20, "Login");
        this.buttonList.add(this.login);
        this.remove = new GuiButton(2, this.width / 2 - 74, this.height - 24, 70, 20, "Remove");
        this.buttonList.add(this.remove);
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 76, this.height - 48, 75, 20, "Add"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 74, this.height - 48, 70, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(8, this.width / 2 - 100, this.height - 100, 90, 30, "Microsoft"));
        this.buttonList.add(new GuiButton(5, this.width / 2 + 4, this.height - 48, 70, 20, "Random"));
        this.rename = new GuiButton(6, this.width / 2 + 4, this.height - 24, 70, 20, "Edit");
        this.buttonList.add(this.rename);
        this.rename = new GuiButton(7, this.width / 2 - 154, this.height - 24, 70, 20, "Last Alt");
        this.buttonList.add(this.rename);
        this.login.enabled = false;
        this.remove.enabled = false;
        this.rename.enabled = false;
    }

    private boolean isAltInArea(int y) {
        if (y - this.offset <= this.height - 50) {
            return true;
        }
        return false;
    }

    private boolean isMouseOverAlt(int x, int y, int y1) {
        if (x >= 52 && y >= y1 - 4 && x <= this.width - 52 && y <= y1 + 20 && x >= 0 && y >= 33 && x <= this.width && y <= this.height - 50) {
            return true;
        }
        return false;
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        if (this.offset < 0) {
            this.offset = 0;
        }
        int y = 38 - this.offset;
        Client.instance.getAltManager();
        for (Alt alt : AltManager.getAlts()) {
            if (this.isMouseOverAlt(par1, par2, y)) {
                if (alt == this.selectedAlt) {
                    this.actionPerformed((GuiButton)this.buttonList.get(1));
                    return;
                }
                this.selectedAlt = alt;
            }
            y += 26;
        }
        try {
            super.mouseClicked(par1, par2, par3);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

         
          // Or using refresh token: authenticator.loginWithRefreshToken("refresh token");
           // Or using your own way: authenticator.loginWithTokens("access token", "refresh token");
		
        
        
    }

    public void prepareScissorBox(float x, float y, float x2, float y2) {
        int factor = new ScaledResolution(mc).getScaleFactor();
        GL11.glScissor((int)((int)(x * (float)factor)), (int)((int)(((float)new ScaledResolution(mc).getScaledHeight() - y2) * (float)factor)), (int)((int)((x2 - x) * (float)factor)), (int)((int)((y2 - y) * (float)factor)));
    }

    public void renderBackground(int par1, int par2) {
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glDisable((int)3008);
        this.drawDefaultBackground();
        Tessellator var3 = Tessellator.instance;
        var3.getWorldRenderer().startDrawingQuads();
        var3.getWorldRenderer().addVertexWithUV(0.0, par2, -90.0, 0.0, 1.0);
        var3.getWorldRenderer().addVertexWithUV(par1, par2, -90.0, 1.0, 1.0);
        var3.getWorldRenderer().addVertexWithUV(par1, 0.0, -90.0, 1.0, 0.0);
        var3.getWorldRenderer().addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
        var3.draw();
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2929);
        GL11.glEnable((int)3008);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

