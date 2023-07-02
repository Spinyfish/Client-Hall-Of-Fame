package me.eldodebug.soar.management.mods.impl;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.HudMod;

public class CPSDisplayMod extends HudMod{

    private List<Long> clicksLMB = new ArrayList<Long>();
    
    private List<Long> clicksRMB = new ArrayList<Long>();
    
    private boolean wasPressedLMB;
    private long lastPressedLMB;
    
    private boolean wasPressedRMB;
    private long lastPressedRMB;
    
	public CPSDisplayMod() {
		super("CPS Display", "Display click per secound");
	}

	@Override
	public void setup() {
		this.addBooleanSetting("Show Right Click", this, true);
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		super.onRender2D();
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		super.onRenderShadow();
	}
	
	@Override
	public String getText() {
		
		boolean rightClick = Soar.instance.settingsManager.getSettingByName(this, "Show Right Click").getValBoolean();
        boolean pressedLMB = Mouse.isButtonDown(0);
        
        if(pressedLMB != this.wasPressedLMB) {
            this.lastPressedLMB = System.currentTimeMillis();
            this.wasPressedLMB = pressedLMB;
            if(pressedLMB) {
                this.clicksLMB.add(this.lastPressedLMB);
            }
        }
        
        boolean pressedRMB = Mouse.isButtonDown(1);
        
        if(pressedRMB != this.wasPressedRMB) {
            this.lastPressedRMB = System.currentTimeMillis();
            this.wasPressedRMB = pressedRMB;
            if(pressedRMB) {
                this.clicksRMB.add(this.lastPressedRMB);
            }
        }
        
		return (rightClick ? this.getLMB() + " | " + this.getRMB() : this.getLMB()) + " CPS";
	}
	
    public int getLMB() {
        final long time = System.currentTimeMillis();
        this.clicksLMB.removeIf(aLong -> aLong + 1000 < time);
        return this.clicksLMB.size();
    }
    
    public int getRMB() {
        final long time = System.currentTimeMillis();
        this.clicksRMB.removeIf(aLong -> aLong + 1000 < time);
        return this.clicksRMB.size();
    }
}
