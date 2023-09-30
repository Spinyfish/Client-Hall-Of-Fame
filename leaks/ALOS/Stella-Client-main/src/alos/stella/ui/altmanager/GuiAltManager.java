package alos.stella.ui.altmanager;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.gui.*;
import net.minecraft.util.EnumChatFormatting;

public class GuiAltManager
extends GuiScreen {
    private GuiButton login;
    private GuiButton remove;
    private GuiButton rename;
    private AltLoginThread loginThread;
    private int offset;
    public Alt selectedAlt = null;
    private String status = EnumChatFormatting.GRAY + "No alts selected";

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                if (this.loginThread == null) {
                    this.mc.displayGuiScreen(null);
                    break;
                }
                if (!this.loginThread.getStatus().equals(EnumChatFormatting.YELLOW + "Attempting to log in") && !this.loginThread.getStatus().equals(EnumChatFormatting.RED + "Do not hit back!" + EnumChatFormatting.YELLOW + " Logging in...")) {
                    this.mc.displayGuiScreen(null);
                    break;
                }
                this.loginThread.setStatus(EnumChatFormatting.RED + "Failed to login! Please try again!" + EnumChatFormatting.YELLOW + " Logging in...");
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
                AltManager.registry.remove(this.selectedAlt);
                this.status = "\u00a7aRemoved.";
                this.selectedAlt = null;
                break;
            }
            case 3: {
                this.mc.displayGuiScreen(new alos.stella.ui.altmanager.GuiAddAlt(this));
                break;
            }
            case 4: {
                this.mc.displayGuiScreen(new GuiAltLogin(this));
                break;
            }
            
            case 6: {
                this.mc.displayGuiScreen(new GuiRenameAlt(this));
                break;
            }
           
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
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
        this.drawString(this.fontRendererObj, this.mc.session.getUsername(), 10, 10, -7829368);
        FontRenderer fontRendererObj = this.fontRendererObj;
        StringBuilder sb2 = new StringBuilder("Account Manager - ");
       
        this.drawCenteredString(fontRendererObj, sb2.append(AltManager.registry.size()).append(" alts").toString(), width / 2, 10, -1);
        this.drawCenteredString(this.fontRendererObj, this.loginThread == null ? this.status : this.loginThread.getStatus(), width / 2, 20, -1);
        Gui.drawRect((int) 50.0f, (int) 33.0f, width - 50, height - 50, -16777216);
        GL11.glPushMatrix();
        this.prepareScissorBox(0.0f, 33.0f, width, height - 50);
        GL11.glEnable(3089);
        int y2 = 38;
        for (Alt alt2 : AltManager.registry) {
            if (!this.isAltInArea(y2)) continue;
            String name = alt2.getMask().equals("") ? alt2.getUsername() : alt2.getMask();
            String pass = alt2.getPassword().equals("") ? "\u00a7cCracked" : alt2.getPassword().replaceAll(".", "*");
            if (alt2 == this.selectedAlt) {
                if (this.isMouseOverAlt(par1, par2, y2 - this.offset) && Mouse.isButtonDown(0)) {
                    Gui.drawRect((int) 52.0f, y2 - this.offset - 4, width - 52, y2 - this.offset + 20, -2142943931);
                } else if (this.isMouseOverAlt(par1, par2, y2 - this.offset)) {
                    Gui.drawRect((int) 52.0f, y2 - this.offset - 4, width - 52, y2 - this.offset + 20, -2142088622);
                } else {
                    Gui.drawRect((int) 52.0f, y2 - this.offset - 4, width - 52, y2 - this.offset + 20, -2144259791);
                }
            } else if (this.isMouseOverAlt(par1, par2, y2 - this.offset) && Mouse.isButtonDown(0)) {
                Gui.drawRect((int) 52.0f, y2 - this.offset - 4, width - 52, y2 - this.offset + 20, 1);
            } else if (this.isMouseOverAlt(par1, par2, y2 - this.offset)) {
                Gui.drawRect((int) 52.0f, y2 - this.offset - 4, width - 52, y2 - this.offset + 20, 1);
            }
            this.drawCenteredString(this.fontRendererObj, name, width / 2, y2 - this.offset, -1);
            this.drawCenteredString(this.fontRendererObj, pass, width / 2, y2 - this.offset + 10, 5592405);
            y2 += 26;
        }
        GL11.glDisable(3089);
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
        if (Keyboard.isKeyDown(200)) {
            this.offset -= 26;
            if (this.offset < 0) {
                this.offset = 0;
            }
        } else if (Keyboard.isKeyDown(208)) {
            this.offset += 26;
            if (this.offset < 0) {
                this.offset = 0;
            }
        }
    }

    @Override
    public void initGui() {
    	this.buttonList.add(new GuiButton(0, width / 2 + 4 + 50, height - 24, 100, 20, "Cancel"));
        this.login = new GuiButton(1, width / 2 - 154, height - 48, 100, 20, "Login");
        this.buttonList.add(this.login);
        this.remove = new GuiButton(2, width / 2 - 154, height - 24, 100, 20, "Remove");
        this.buttonList.add(this.remove);
        this.buttonList.add(new GuiButton(3, width / 2 + 4 + 50, height - 48, 100, 20, "Add"));
        this.buttonList.add(new GuiButton(4, width / 2 - 50, height - 48, 100, 20, "Direct Login"));
        this.rename = new GuiButton(6, width / 2 - 50, height - 24, 100, 20, "Edit");
        this.buttonList.add(this.rename);
        this.login.enabled = false;
        this.remove.enabled = false;
        this.rename.enabled = false;
    }

    private boolean isAltInArea(int y2) {
        return y2 - this.offset <= height - 50;
    }

    private boolean isMouseOverAlt(int x2, int y2, int y1) {
        return x2 >= 52 && y2 >= y1 - 4 && x2 <= width - 52 && y2 <= y1 + 20 && y2 >= 33 && x2 <= width && y2 <= height - 50;
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        if (this.offset < 0) {
            this.offset = 0;
        }
        int y2 = 38 - this.offset;
        for (Alt alt2 : AltManager.registry) {
            if (this.isMouseOverAlt(par1, par2, y2)) {
                if (alt2 == this.selectedAlt) {
                    this.actionPerformed(this.buttonList.get(1));
                    return;
                }
                this.selectedAlt = alt2;
            }
            y2 += 26;
        }
        try {
            super.mouseClicked(par1, par2, par3);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareScissorBox(float x2, float y2, float x22, float y22) {
        ScaledResolution scale = new ScaledResolution(this.mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x2 * (float)factor), (int)(((float)scale.getScaledHeight() - y22) * (float)factor), (int)((x22 - x2) * (float)factor), (int)((y22 - y2) * (float)factor));
    }
}

