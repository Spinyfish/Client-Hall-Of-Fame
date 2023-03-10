/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 */
package lodomir.dev.ui.menu;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.alt.GuiAltManager;
import lodomir.dev.alt.microsoft.GuiLoginMicrosoft;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.ui.menu.buttons.MenuButton;
import lodomir.dev.ui.menu.shader.GLSLSandboxShader;
import lodomir.dev.utils.render.ColorUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class MainMenu
extends GuiScreen {
    private GLSLSandboxShader shader;
    private long initTime = System.currentTimeMillis();

    public MainMenu() {
        try {
            this.shader = new GLSLSandboxShader("assets/minecraft/noise.fsh");
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to load backgound shader", e);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        this.shader.useShader(width * 2, height * 2, mouseX, mouseY, (float)(System.currentTimeMillis() - this.initTime) / 1000.0f);
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)-1.0f, (float)-1.0f);
        GL11.glVertex2f((float)-1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)-1.0f);
        GL11.glEnd();
        GL20.glUseProgram((int)0);
        November.INSTANCE.discordRP.update("", "");
        ScaledResolution sr = new ScaledResolution(this.mc);
        TTFFontRenderer fr = November.INSTANCE.fm.getFont("SFUI 20");
        fr.drawStringWithShadow("Playing on " + November.INSTANCE.release + " " + November.INSTANCE.build.toLowerCase() + " release (latest)", 1.0f, sr.getScaledHeight() - 21, new Color(255, 255, 255, 180).getRGB());
        fr.drawStringWithShadow("Made with \u00a79 <3 \u00a7f by Lodomir & nullready (and thank you xthepwer)", 1.0f, sr.getScaledHeight() - 11, new Color(255, 255, 255, 180).getRGB());
        fr.drawStringWithShadow("Welcome, \u00a79" + November.INSTANCE.user, sr.getScaledWidth() - fr.getStringWidth("Welcome, " + November.INSTANCE.user), sr.getScaledHeight() - 11, new Color(255, 255, 255, 180).getRGB());
        ArrayList<String> changes = new ArrayList<String>();
        int offset = 0;
        changes.add(ChatFormatting.BLUE + "0.2 Update:");
        changes.add("+ better file managment");
        changes.add("+ more imersive colors");
        changes.add("+ improved autoclicker");
        changes.add("+ lightning detector");
        changes.add("+ improved mainmenu");
        changes.add("+ verus longjump");
        changes.add("+ custom font");
        changes.add("+ color blend");
        changes.add("+ hypixel fly");
        changes.add("+ float novoid");
        changes.add("+ config system");
        changes.add("+ 2d esp");
        for (int i = 0; i < changes.size(); ++i) {
            fr.drawStringWithShadow((String)changes.get(i), 5.0f, 5 + i * 12, new Color(220, 220, 220, 220).getRGB());
            ++offset;
        }
        November.INSTANCE.fm.getFont("NOTO BOLD 72").drawCenteredString(November.INSTANCE.name, width / 2, height / 2 - 90, ColorUtils.fade(new Color(0, 40, 192, 150), 100, 100).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new MenuButton(0, width / 2 - 80, height / 2 - 44, "Singleplayer"));
        this.buttonList.add(new MenuButton(1, width / 2 - 80, height / 2 - 22, "Multiplayer"));
        this.buttonList.add(new MenuButton(2, width / 2 - 80, height / 2, "Alt Manager"));
        this.buttonList.add(new MenuButton(3, width / 2 - 80, height / 2 + 22, "Settings"));
        this.buttonList.add(new MenuButton(4, width / 2 - 80, height / 2 + 44, "Exit"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                this.mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            }
            case 2: {
                this.mc.displayGuiScreen(new GuiLoginMicrosoft(new GuiAltManager()));
                break;
            }
            case 3: {
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            }
            case 4: {
                this.mc.shutdown();
            }
        }
    }
}

