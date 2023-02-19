package net.minecraft.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import alos.stella.ui.altmanager.GuiAltManager;
import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.HoveredUtils;
import alos.stella.utils.gl.GLSL;
import alos.stella.utils.render.DrawUtils;
import alos.stella.utils.render.GLUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GuiMainMenu extends GuiScreen {
    public static int counter = 1;
    private final long initTime = System.currentTimeMillis();
    public static int bg = 0;
    private GLSL backgroundShader;
    float offset;
    private List<String> buttons = new ArrayList();
    private List<String> changes = new ArrayList();
    private String selectedButton = "";
    private GuiSelectWorld guiWorldSelection;
    private GuiMultiplayer guiMultiplayer;
    private GuiOptions guiOptions;
    public GuiMainMenu() {
    }
    public void initGui() {
        try {
            this.backgroundShader = new GLSL("/assets/noise.fsh");
        }
        catch (IOException var2) {
            throw new IllegalStateException("Failed to load backgound shader", var2);
        }
        this.buttons.clear();
        this.buttons.addAll(Arrays.asList("SinglePlayer", "MultiPlayer", "Settings", "Alts", "Quit"));
    }
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.changes.clear();
        this.changes.add(EnumChatFormatting.GREEN + " + " + EnumChatFormatting.DARK_PURPLE + "test");
        this.changes.add(EnumChatFormatting.GREEN + " + " + EnumChatFormatting.DARK_PURPLE + "test");
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.backgroundShader.useShader(sr.getScaledWidth(), sr.getScaledHeight(), mouseX, mouseY, (float)(System.currentTimeMillis() - this.initTime) / 1500.0f);
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)-1.0f, (float)-1.0f);
        GL11.glVertex2f((float)-1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)-1.0f);
        GL11.glEnd();
        GL20.glUseProgram((int)0);
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        DrawUtils.drawRound((float)(sr.getScaledWidth() / 2 - 70), (float)(sr.getScaledHeight() / 2 - 40), 140.0F, 140.0F, 5.0F, new Color(0, 0, 0, 110));
        GLUtils.glBlur(() ->  DrawUtils.drawRound((float)(sr.getScaledWidth() / 2 - 70), (float)(sr.getScaledHeight() / 2 - 40), 140.0F, 140.0F, 5.0F, new Color(0, 0, 0, 110)), 15);
        Fonts.fontSFUI40.drawCenteredString("Stella", (float)(sr.getScaledWidth() / 2), (float)(sr.getScaledHeight() / 2 - 30), -1);
        this.offset = 0.0F;
        for(Iterator var5 = this.buttons.iterator(); var5.hasNext(); this.offset += 20.0F) {
            String string = (String)var5.next();
            Fonts.fontSFUI40.drawCenteredString(string, (float)(sr.getScaledWidth() / 2), (float)(sr.getScaledHeight() / 2 - 40 + 34 + 5) + this.offset, -1);
            DrawUtils.drawRound((float)(sr.getScaledWidth() / 2 - 55), (float)(sr.getScaledHeight() / 2 - 40 + 34) + this.offset, 110.0F, 15.0F, 5.0F,  HoveredUtils.isHovered(mouseX, mouseY, (double)(sr.getScaledWidth() / 2 - 55), (double)((float)(sr.getScaledHeight() / 2 - 40 + 34) + this.offset), 110.0, 15.0) ? new Color(255, 255, 255, 18) : new Color(255, 255, 255, 10));
        }
        for(Iterator i = this.changes.iterator(); i.hasNext(); this.offset += 10.0F) {
            String string = (String)i.next();
            Fonts.fontSFUI40.drawCenteredString(string, (sr.getScaledWidth() / 2 - 450), (sr.getScaledHeight() / 2 - 320) + this.offset, -1);DrawUtils.drawRound((float)(sr.getScaledWidth() / 2 - 55), (float)(sr.getScaledHeight() / 2 - 40 + 34) + this.offset, 110.0F, 15.0F, 5.0F,  HoveredUtils.isHovered(mouseX, mouseY, (double)(sr.getScaledWidth() / 2 - 55), (double)((float)(sr.getScaledHeight() / 2 - 40 + 34) + this.offset), 110.0, 15.0) ? new Color(255, 255, 255, 18) : new Color(255, 255, 255, 10));

        }
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.offset = 0.0F;
        for(Iterator var5 = this.buttons.iterator(); var5.hasNext(); this.offset += 20.0F) {
            String string = (String)var5.next();
            if (HoveredUtils.isHovered(mouseX, mouseY, (double)(sr.getScaledWidth() / 2 - 55), (double)((float)(sr.getScaledHeight() / 2 - 40 + 34) + this.offset), 110.0, 15.0)) {
                this.selectedButton = string;
            }
        }
        switch (this.selectedButton) {
            case "SinglePlayer":
                this.guiWorldSelection = new GuiSelectWorld(this.guiWorldSelection);
                this.mc.displayGuiScreen(this.guiWorldSelection);
                this.selectedButton = "";
                break;
            case "MultiPlayer":
                this.guiMultiplayer = new GuiMultiplayer(this.guiMultiplayer);
                this.mc.displayGuiScreen(this.guiMultiplayer);
                this.selectedButton = "";
                break;
            case "Settings":
                this.guiOptions = new GuiOptions(this.guiOptions, this.mc.gameSettings);
                this.mc.displayGuiScreen(this.guiOptions);
                this.selectedButton = "";
                break;
            case "Alts":
                GuiScreen changeUser = new GuiAltManager();
                this.mc.displayGuiScreen(changeUser);
                this.selectedButton = "";
                break;
            case "Quit":
                mc.shutdown();
        }
    }
}
