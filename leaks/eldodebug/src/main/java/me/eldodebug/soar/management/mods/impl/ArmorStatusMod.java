package me.eldodebug.soar.management.mods.impl;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.gui.GuiEditHUD;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArmorStatusMod extends Mod {
	
	public ArmorStatusMod() {
		super("Armor Status", "Display the armor you are wearing", ModCategory.HUD);
	}
	
	@Override
	public void setup() {
		
		ArrayList<String> options = new ArrayList<>();
		
		options.add("Horizontal");
		options.add("Vertical");
		
		this.addBooleanSetting("Held Item", this, false);
		this.addBooleanSetting("Endurance Value", this, false);
		this.addModeSetting("Mode", this, "Horizontal", options);
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		
    	String mode = Soar.instance.settingsManager.getSettingByName(this, "Mode").getValString();
        ScaledResolution sr = new ScaledResolution(mc);
        boolean horizontal = mode.equals("Horizontal");
        
        int x;
        int y;
        int addHeldItem = Soar.instance.settingsManager.getSettingByName(this, "Held Item").getValBoolean() ? 16 : 0;
        
        ItemStack sword = new ItemStack(Items.diamond_sword);
        
        if(mode.equals("Horizontal")) {
        	x = 65 + addHeldItem;
        	y = 16;
        }else {
        	x = 16;
        	y = 65 + addHeldItem;
        }

        for (int i21 = 0; i21 < mc.thePlayer.inventory.armorInventory.length; ++i21) {
        	
            final ItemStack is = mc.thePlayer.inventory.armorInventory[i21];
            
            if(mc.currentScreen instanceof GuiEditHUD) {
            	this.renderFakeArmorStatus(i21);
            }else {
                this.renderArmorStatus(sr, i21, is);
            }
        }
        
        if(Soar.instance.settingsManager.getSettingByName(this, "Held Item").getValBoolean()) {
            GL11.glPushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            
            if(mc.currentScreen instanceof GuiEditHUD) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(sword, this.getX() + (horizontal ? (-16 * -1 + 48) : 0), this.getY() + (horizontal ? 0 : (-16 * -1 + 48)));
            }else {
                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getHeldItem(), this.getX() + (horizontal ? (-16 * -1 + 48) : 0), this.getY() + (horizontal ? 0 : (-16 * -1 + 48)));
                mc.getRenderItem().renderItemOverlayIntoGUI(fr, mc.thePlayer.getHeldItem(), this.getX() + (horizontal ? (-16 * -1 + 48) : 0), this.getY() + (horizontal ? 0 : (-16 * -1 + 48)), "");
            }
            
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GL11.glPopMatrix();
        }
        
        this.setWidth(x);
        this.setHeight(y);
	}
	
	private void renderFakeArmorStatus(int pos) {
    	
    	String mode = Soar.instance.settingsManager.getSettingByName(this, "Mode").getValString();

        boolean horizontal;
        
        ItemStack helmet = new ItemStack(Items.diamond_helmet);
        ItemStack chestplate = new ItemStack(Items.diamond_chestplate);
        ItemStack leggings = new ItemStack(Items.diamond_leggings);
        ItemStack boots = new ItemStack(Items.diamond_boots);
        
        if(mode.equals("Horizontal")) {
        	horizontal = true;
        }else {
        	horizontal = false;
        }
        
        mc.getRenderItem().renderItemAndEffectIntoGUI(helmet, this.getX() + (horizontal ? -16 * 3 + 48 : 0), this.getY() + (horizontal ? 0 : -16 * 3 + 48));
        mc.getRenderItem().renderItemAndEffectIntoGUI(chestplate, this.getX() + (horizontal ? -16 * 2 + 48 : 0), this.getY() + (horizontal ? 0 : -16 * 2 + 48));
        mc.getRenderItem().renderItemAndEffectIntoGUI(leggings, this.getX() + (horizontal ? -16 * 1 + 48 : 0), this.getY() + (horizontal ? 0 : -16 * 1 + 48));
        mc.getRenderItem().renderItemAndEffectIntoGUI(boots, this.getX() + (horizontal ? -16 * 0 + 48 : 0), this.getY() + (horizontal ? 0 : -16 * 0 + 48));
	}
	
    private void renderArmorStatus(final ScaledResolution sr, final int pos, final ItemStack itemStack) {
    	
    	String mode = Soar.instance.settingsManager.getSettingByName(this, "Mode").getValString();
    	
        if (itemStack == null) {
            return;
        }
        
        int posXAdd;
        int posYAdd;
        RenderItem itemRender = mc.getRenderItem();
        
        float prevZ = itemRender.zLevel;
        
        if(mode.equals("Horizontal")) {
        	posXAdd = -16 * pos + 48;
        	posYAdd = 0;
        }else {
        	posXAdd = 0;
        	posYAdd = -16 * pos + 48;
        }
        
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, this.getX() + posXAdd, this.getY() + posYAdd);

        if(Soar.instance.settingsManager.getSettingByName(this, "Endurance Value").getValBoolean()) {
            GL11.glPushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            
            GlStateManager.translate(0.0F, 0.0F, 32.0F);
            itemRender.zLevel = 0.0F;
            
            mc.getRenderItem().renderItemOverlayIntoGUI(fr, itemStack, this.getX() + posXAdd, this.getY() + posYAdd, "");

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GL11.glPopMatrix();
            itemRender.zLevel = prevZ;
        }
    }
}
