package de.fanta.clickgui.astolfo.objects;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import de.fanta.Client;
import de.fanta.module.Module.Type;
import de.fanta.setting.Setting;
import net.minecraft.client.Minecraft;

public class GuiNumberChanger extends GuiChanger<Double>{

	private float xAdd = 0;
	private boolean init = false;
	
	public GuiNumberChanger(int x, int y, Setting setting, Type cat) {
		super(x, y, setting, cat);
		this.height = 20;
		this.width = 119;
	}

	@Override
	public void draw(float mouseX, float mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		if(this.value == null) {
			this.value = (Double) this.setting.getSetting().getValue();
		}
		if(!init) {
			this.value = calculateValue();
			float percentWidth = (float) ((width/(Double)setting.getSetting().getMaxValue())*(Double)setting.getSetting().getValue());
			this.xAdd = (int) percentWidth;
			init = true;
		}
		drawRect(x+3, y +1, x+3+xAdd, y+20, cat.getColor());
		Client.INSTANCE.unicodeBasicFontRenderer.drawString(setting.getName(), x+6, y+4, Color.white.getRGB());
		int valueWidth = Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value.toString().replace(".", ","));
		Client.INSTANCE.unicodeBasicFontRenderer.drawString(value.toString().replace(".", ","), x+width-valueWidth, y + 4, Color.white.getRGB());
		if(Mouse.isButtonDown(0)) {
			if(mouseX >= x+3 && mouseX <= x+3+width && mouseY >= y && mouseY <= y+20) {
				xAdd = (float) (((float)mouseX-x-3F));
			}
		}
		this.value = calculateValue();
		if(this.value != null) this.setting.getSetting().setValue(this.value);
	}
	
	public double calculateValue() {
		float percentWidth = (((float)1/width)*xAdd)*100;
		
		double d = (float)((Double)setting.getSetting().getMaxValue()*(percentWidth/100));
		return (double)(Math.round(d*100)/100.0);
	}
	
}
