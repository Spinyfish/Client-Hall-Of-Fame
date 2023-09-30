package de.fanta.clickgui.astolfo.objects;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import de.fanta.Client;
import de.fanta.module.Module.Type;
import de.fanta.setting.Setting;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.Minecraft;

public class GuiStringSelector extends GuiChanger<String>{

	private String[] values;
	
	public GuiStringSelector(int x, int y, Setting setting, Type cat) {
		super(x, y, setting, cat);
		this.height = 20;
		this.width = 119;
		this.lastMS = System.currentTimeMillis();
	}
		
	private long lastMS = System.currentTimeMillis();
	
	@Override
	public void draw(float mouseX, float mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		if(value == null) this.value = (String)setting.getSetting().getValue();
		if(this.values == null) this.values = (String[])setting.getSetting().getMaxValue();
		Client.INSTANCE.unicodeBasicFontRenderer.drawString(setting.getName(), x+6, y+6, Color.white.getRGB());
		int valueWidth =Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(value);
		Client.INSTANCE.unicodeBasicFontRenderer.drawString(value, x+width-valueWidth, y+6, Color.white.getRGB());
		if(Mouse.isButtonDown(0) && System.currentTimeMillis()-lastMS >= 200) {
			if(mouseX >= x+3 && mouseX <= x+3+width && mouseY >= y && mouseY <= y+20) {
				int index = 0;
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals(value)) index = i;
				}
				if(index+1 < values.length) {
					value = values[index+1];
					lastMS = System.currentTimeMillis();
				}else {
					value = values[0];
					lastMS = System.currentTimeMillis();
				}
			}
		}
		if(!Mouse.isButtonDown(0)) {
			lastMS = System.currentTimeMillis()-250;
		}
		this.setting.getSetting().setValue(value);
		super.draw(mouseX, mouseY);
	}

}
