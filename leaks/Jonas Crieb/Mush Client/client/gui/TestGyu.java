package client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import client.account.GuiAltManager;
import client.util.gl.GLSL;
import client.util.visual.DrawUtils;
import client.util.visual.GLUtils;
import client.util.visual.HoveredUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.reflect.Reflector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class TestGyu extends GuiScreen {
    public static int counter = 1;
    private final long initTime = System.currentTimeMillis();
    public static int bg = 0;
    private GLSL backgroundShader;
    float offset;
    private List<String> buttons = new ArrayList();
    private List<String> changes = new ArrayList();
    public TestGyu() {
    }
    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_)
    {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer", new Object[0])));

        if (Reflector.GuiModList_Constructor.exists())
        {

        }
        else
        {
            this.buttonList.add(new GuiButton(15, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, "Account Manager"));
        }
    }
    public void initGui() {
        try {
            this.backgroundShader = new GLSL("/assets/novoline_alt.fsh");
        }
        catch (IOException var2) {
            throw new IllegalStateException("Failed to load backgound shader", var2);
        }
        int j = this.height / 4 + 48;
        this.addSingleplayerMultiplayerButtons(j, 24);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit", new Object[0])));
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12));

    }
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
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

        this.offset = 0.0F;

        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (button.id == 5)
        {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (button.id == 2)
        {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }



        if (button.id == 4)
        {
            this.mc.shutdown();
        }

        if (button.id == 6 && Reflector.GuiModList_Constructor.exists())
        {
            this.mc.displayGuiScreen((GuiScreen)Reflector.newInstance(Reflector.GuiModList_Constructor, new Object[] {this}));
        }

        if (button.id == 11)
        {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        if (button.id == 12)
        {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

            if (worldinfo != null)
            {
                GuiYesNo guiyesno = GuiSelectWorld.makeDeleteWorldYesNo(this, worldinfo.getWorldName(), 12);
                this.mc.displayGuiScreen(guiyesno);
            }
        }

        if (button.id == 15)
        {
            this.mc.displayGuiScreen(new GuiAltManager());
        }
    }

}
