package me.wavelength.baseclient.overlay;

import java.awt.Color;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.AltManager.GuiAltManager;
import me.wavelength.baseclient.extensions.DiscordRP;
import me.wavelength.baseclient.module.modules.combat.Killaura;
import me.wavelength.baseclient.module.modules.render.SessionInfo;
import me.wavelength.baseclient.utils.ColorUtilL;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.RenderUtils2;
import me.wavelength.baseclient.utils.Time;
import me.wavelength.baseclient.utils.Utils;
import me.wavelength.baseclient.utils.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C18PacketSpectate;

public class GuiIngameHud extends GuiIngame{
	protected Minecraft mc = Minecraft.getMinecraft();

	public GuiIngameHud(Minecraft mcIn) {
		
		super(mcIn);
	}
	
	private void TargetHud() {
		 
	       
	       
	    
	ScaledResolution sr = new ScaledResolution(mc);
	}
	int offset = 3;
	
	
	public void renderGameOverlay(float p_175180_1_){
	     super.renderGameOverlay(p_175180_1_);
	   
	     if(!mc.gameSettings.showDebugInfo) {
		     
	    	    Minecraft mc = Minecraft.getMinecraft();
	    	    
	    	 	//PlayTime
	    	 if(SessionInfo.penis) {
	    		 RenderUtils2.drawRoundedRect(90, 100, 1 + 100 + 150, 20, 10, new Color(0, 0, 0, 100).getRGB());
	 	 	      RenderUtils2.drawRoundedRect(95, 40, 1 + 100 + 140, 45, 2, new Color(0xff4d4c).getRGB());
	 	 	      mc.fontRendererObj.drawStringWithShadow("Statistics", 150, 035, -1);
		    	   mc.fontRendererObj.drawStringWithShadow("Play Time : " + BaseClient.hypixelStatus.getSessionLengthString(), 100, 50, -1);
		    	   mc.fontRendererObj.drawStringWithShadow("Kills : " + BaseClient.hypixelStatus.getSessionKills(), 100, 65, -1);
		    	   mc.fontRendererObj.drawStringWithShadow(" IP : "+ (mc.isSingleplayer() ? "LocalServer" : Minecraft.getMinecraft().getCurrentServerData().serverIP),95, 80, -1);
	    	 }

			    	   
	 	       
	 	       //TargetHud
			 	 
	    	    if(mc.thePlayer.isSwingInProgress == true && Killaura.attacking) {
		 	 	      GuiInventory.drawEntityOnScreen(110, 170,  25, 50, 0, Killaura.hurt);
		 	 	      
	 	      RenderUtils2.drawRoundedRect(90, 180, 1 + 100 + 150, 40 + 60, 10, new Color(0, 0, 0, 60).getRGB());
	 	      mc.fontRendererObj.drawStringWithShadow(Killaura.name, 190, 160, -1);
	 	    mc.fontRendererObj.drawString(Math.round(Killaura.health/20*100)+ "%", 150, 160, -1);
	 	     
	 	      
	 	      
	 	      if(Math.round(Killaura.health) == 20)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 100, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 19)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 95, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 18)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 90, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 17)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 85, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 16)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 80, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 15)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 75, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 14)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 70, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 13)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 65, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 12)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 60, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 11)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 55, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 10)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 50, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 9)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 45, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 8)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 40, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 7)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 35, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 6)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 30, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 5)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 25, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 4)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 20, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 3)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 15, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 2)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 10, 90 + 60, 10, -1);
	 	      
	 	      if(Math.round(Killaura.health) == 1)
	    		  RenderUtils2.drawRoundedRect(140, 130, 1 + 140 + 5, 90 + 60, 10, -1);




	 	      
	 	       }


		 	       //int ColorGREEN = ColorUtilL.GreenFlash(2, 40);
		 	        //Health Warining
		 	        //if(mc.thePlayer.getHealth() == 8) {
		 	        //	mc.fontRendererObj.drawStringWithShadow("Low Health======== WARNING ======== Low Health", 300, 250, ColorGREEN);
		 	        //}if(mc.thePlayer.getHealth() == 7) {
		 	        //	mc.fontRendererObj.drawStringWithShadow("Low Health======== WARNING ======== Low Health", 300, 250, ColorGREEN);
		 	        //}if(mc.thePlayer.getHealth() == 6) {
		 	        //	mc.fontRendererObj.drawStringWithShadow("Low Health======== WARNING ======== Low Health", 300, 250, ColorGREEN);
		 	        //}if(mc.thePlayer.getHealth() == 5) {
		 	        //	mc.fontRendererObj.drawStringWithShadow("Low Health======== WARNING ======== Low Health", 300, 250, ColorGREEN);
		 	        //}if(mc.thePlayer.getHealth() == 4) {
		 	        //	mc.fontRendererObj.drawStringWithShadow("Low Health======== WARNING ======== Low Health", 300, 250, ColorGREEN);
		 	        //}if(mc.thePlayer.getHealth() == 3) {
		 	        //	mc.fontRendererObj.drawStringWithShadow("Low Health======== WARNING ======== Low Health", 300, 250, ColorGREEN);
		 	        //}if(mc.thePlayer.getHealth() == 2) {
		 	        //	mc.fontRendererObj.drawStringWithShadow("Low Health======== WARNING ======== Low Health", 300, 250, ColorGREEN);
		 	        //}if(mc.thePlayer.getHealth() == 1) {
		 	        //	mc.fontRendererObj.drawStringWithShadow("Low Health======== WARNING ======== Low Health", 300, 250, ColorGREEN);
		 	        //}
		 	        
		 	        
 	 	      
	 	       
	 	       //TargetHud

				       

	    	    
	    	    //Hud
	    	    
	    	    
	    	    if(DiscordRP.name.contains("Aiden_jk#1312"))
	    	    	mc.fontRendererObj.drawStringWithShadow("Z" + ChatFormatting.GRAY + "yth " + BaseClient.instance.getClientVersion() + " " + ChatFormatting.GRAY + " [" +  ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "] " + " [" +  ChatFormatting.WHITE + DiscordRP.name + ChatFormatting.GRAY + "] " + " [" + ChatFormatting.WHITE + "0001" + ChatFormatting.GRAY + "] " , 2, 2, new ColorUtilL().astolfoColors(2, 40));
	    	    
	    	    else if(DiscordRP.name.contains("Chazed#7376"))
	    	    	mc.fontRendererObj.drawStringWithShadow("Z" + ChatFormatting.GRAY + "yth " + BaseClient.instance.getClientVersion() + " " + ChatFormatting.GRAY + " [" +  ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "] " + " [" +  ChatFormatting.WHITE + DiscordRP.name + ChatFormatting.GRAY + "] " + " [" + ChatFormatting.WHITE + "0002" + ChatFormatting.GRAY + "] " , 2, 2, new ColorUtilL().astolfoColors(2, 40));

	    	    else if(DiscordRP.name.contains("lrxh#1247"))
	    	    	mc.fontRendererObj.drawStringWithShadow("Z" + ChatFormatting.GRAY + "yth " + BaseClient.instance.getClientVersion() + " " + ChatFormatting.GRAY + " [" +  ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "] " + " [" +  ChatFormatting.WHITE + DiscordRP.name + ChatFormatting.GRAY + "] " + " [" + ChatFormatting.WHITE + "0003" + ChatFormatting.GRAY + "] " , 2, 2, new ColorUtilL().astolfoColors(2, 40));
	    	    
	    	    else if(DiscordRP.name.contains("SuperSkidder#0000"))
	    	    	mc.fontRendererObj.drawStringWithShadow("Z" + ChatFormatting.GRAY + "yth " + BaseClient.instance.getClientVersion() + " " + ChatFormatting.GRAY + " [" +  ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "] " + " [" +  ChatFormatting.WHITE + DiscordRP.name + ChatFormatting.GRAY + "] " + " [" + ChatFormatting.WHITE + "0004" + ChatFormatting.GRAY + "] " , 2, 2, new ColorUtilL().astolfoColors(2, 40));
	    	    
	    	    else if(DiscordRP.name.contains("Aibao#3610"))
	    	    	mc.fontRendererObj.drawStringWithShadow("Z" + ChatFormatting.GRAY + "yth " + BaseClient.instance.getClientVersion() + " " + ChatFormatting.GRAY + " [" +  ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "] " + " [" +  ChatFormatting.WHITE + DiscordRP.name + ChatFormatting.GRAY + "] " + " [" + ChatFormatting.WHITE + "0005" + ChatFormatting.GRAY + "] " , 2, 2, new ColorUtilL().astolfoColors(2, 40));
	    	    
	    	    else if(DiscordRP.name.contains("Dabsio#3087"))
	    	    	mc.fontRendererObj.drawStringWithShadow("Z" + ChatFormatting.GRAY + "yth " + BaseClient.instance.getClientVersion() + " " + ChatFormatting.GRAY + " [" +  ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "] " + " [" +  ChatFormatting.WHITE + DiscordRP.name + ChatFormatting.GRAY + "] " + " [" + ChatFormatting.WHITE + "0006" + ChatFormatting.GRAY + "] " , 2, 2, new ColorUtilL().astolfoColors(2, 40));
	    	    
	    	    else if(DiscordRP.name.contains("Hello_SW#1628"))
	    	    	mc.fontRendererObj.drawStringWithShadow("Z" + ChatFormatting.GRAY + "yth " + BaseClient.instance.getClientVersion() + " " + ChatFormatting.GRAY + " [" +  ChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "] " + " [" +  ChatFormatting.WHITE + DiscordRP.name + ChatFormatting.GRAY + "] " + " [" + ChatFormatting.WHITE + "0006" + ChatFormatting.GRAY + "] " , 2, 2, new ColorUtilL().astolfoColors(2, 40));
	    	    
	    	    else {
    	    			mc.shutdown();
	    	    }
	    	    
	    	    
	            //Hud



	            	//20  = 100%
	            	// 18 =
	             
	             
	             

	             
	             
	        }
		}
	
	
	 int rainbowColor = 0xff0070aa, secondaryColor = 0xff0070aa;
	 
	 int color =  new Color(101, 100, 100, 0).getRGB();
	
	
	}