package com.craftworks.pearclient.gui.mainmenu;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.gui.mainmenu.button.IButton;
import com.craftworks.pearclient.util.animation.SimpleAnimation;
import com.craftworks.pearclient.util.draw.DrawUtil;
import com.craftworks.pearclient.util.draw.GLDraw;
import com.craftworks.pearclient.util.font.GlyphPageFontRenderer;
import com.craftworks.pearclient.util.scroll.ScrollHelper;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class MainMenu extends GuiScreen {

	private GlyphPageFontRenderer renderera;
	
	private GlyphPageFontRenderer rendererb;
	
    private final ArrayList<IButton> PP = new ArrayList<>();
    
    private final ArrayList<GuiButton> P = new ArrayList<>();
	
	ScrollHelper scrollHelper = new ScrollHelper();
    
    private static final ResourceLocation background = new ResourceLocation("pearclient/background/PearBackground.png");

    public MainMenu() {
    	PP.add(new IButton("pearclient/icon/Settings.png", width / 2, height / 2 + 100));
    }

    @Override
    public void initGui() {
        super.initGui();
        GlStateManager.pushMatrix();
        
		GlStateManager.translate(width/5F, height/5F, 0);
		
		this.buttonList.add(new GuiButton(3, this.width / 2 - 2, this.height / 2 + 30, 72, 20,"Exit"));
		
        this.buttonList.add(new GuiButton(2, this.width / 2 - 80, this.height / 2 + 30, 72, 20,"Options"));
        
        this.buttonList.add(new GuiButton(1, this.width / 2 - 80, this.height / 2 + 5, 150, 20,"Multiplayer"));
        
        this.buttonList.add(new GuiButton(0, this.width / 2 - 80, this.height / 2 - 20, 150, 20,"Singleplayer"));
        
        GlStateManager.popMatrix();
        
        PearClient.instance.discordRP.update("Idle", "");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	this.drawDefaultBackground();
    	renderera = GlyphPageFontRenderer.create("Arial Rounded MT Bold", 37, true, false, false);
    	
    	rendererb = GlyphPageFontRenderer.create("Roboto", 14, true, false, false);
    	
        ScaledResolution sr = new ScaledResolution(mc);
        
        mc.getTextureManager().bindTexture(background);
        
        this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
        
        renderera.drawString("Pear Client", width / 2f - fontRendererObj.getStringWidth("Pear Client"), height / 2 - 45, -1, false);
        
        DrawUtil.drawPicture(width / 2 - 40, height / 2 - 105, 70, 70, Color.WHITE.getRGB(), "pearclient/icon/logo.png");
        
        GLDraw.drawRoundedRect(width * 1.001f - 120, height * 1.002f - 70 ,width * 1.001f - 10, height * 1.001f - 10, 4.0f, new Color(0, 0, 0).getRGB());
        
        if(mouseX >= width * 1.001f - 120 && mouseX <= width * 1.001f - 10 && mouseY >= height * 1.002f - 70 && mouseY <= height * 1.001f - 10) {
        	GLDraw.drawRoundedOutline(width * 1.001f - 120, height * 1.002f - 70, width * 1.001f - 10, height * 1.001f - 10, 4.0f, 2.0f, new Color(60, 232, 118, 200).getRGB());
        } else {
        	GLDraw.drawRoundedOutline(width * 1.001f - 120, height * 1.002f - 70, width * 1.001f - 10, height * 1.001f - 10, 4.0f, 2.0f, new Color(60, 232, 118, 100).getRGB());	
        }
        
        for (IButton ibtn : PP) {
            if (ibtn.getIcon().equals("pearclient/icon/Settings.png")) {
                ibtn.renderButton(width / 2 + 10, height * 1 - 25, mouseX, mouseY);
            }
        }
        
        rendererb.drawString("OFFICIAL PARTNERED WITH", width * 1.001f - 112, height * 1.002f - 60, -1, false);
        
        rendererb.drawString("Tick Hosting", width * 1.001f - 85, height * 1.002f - 50, -1, false);
        
        rendererb.drawString("tickhosting.com", width * 1.001f - 90, height * 1.002f - 35, new Color(60, 232, 118).getRGB(), false);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0) {
			mc.displayGuiScreen(new GuiSelectWorld(this));
		}
		
		if(button.id == 1) {
			mc.displayGuiScreen(new GuiMultiplayer(this));
		}
		
		if(button.id == 2) {
			mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
		}
		
		if(button.id == 3) {
			mc.shutdown();
		}
		super.actionPerformed(button);
	}
}