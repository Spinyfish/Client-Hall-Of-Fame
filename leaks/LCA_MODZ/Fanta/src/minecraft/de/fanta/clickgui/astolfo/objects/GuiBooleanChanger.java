package de.fanta.clickgui.astolfo.objects;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import de.fanta.Client;
import de.fanta.module.Module.Type;
import de.fanta.setting.Setting;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.Minecraft;

public class GuiBooleanChanger extends GuiChanger<Boolean>{

	public GuiBooleanChanger(int x, int y, Setting setting, Type cat) {
		super(x, y, setting, cat);
		this.height = 20;
		this.width = 119;
		clickWaiter = new TimeUtil();
	}
	
	private TimeUtil clickWaiter;
	
	@Override
	public void draw(float mouseX, float mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		if(value == null) value = (Boolean)setting.getSetting().getValue();
		if(value) drawRect(x+3, y, x+3+width, y+20, cat.getColor());
		Client.INSTANCE.unicodeBasicFontRenderer.drawString(setting.getName(), x+6, y+6, Color.white.getRGB());
		if(Mouse.isButtonDown(0) && clickWaiter.hasReached(200)) {
			if(mouseX >= x+3 && mouseX <= x+3+width && mouseY >= y && mouseY <= y+20) {
				this.value = !this.value;
				clickWaiter.reset();
			}
		}
		this.setting.getSetting().setValue(value);
		super.draw(mouseX, mouseY);
	}

}
