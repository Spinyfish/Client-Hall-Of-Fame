package felix.gui.menu;

import felix.Client;
import felix.util.font.FontRenderer;
import felix.util.visual.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class TextButton extends GuiButton {
	
	private String buttonText;
	
	private int x, y, widthIn, heightIn;

	public TextButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.buttonText = buttonText;
		this.x = x;
		this.y = y;
		this.widthIn = widthIn;
		this.heightIn = heightIn;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		final FontRenderer fr = Client.INSTANCE.getFontManager().getFont("Display 24", false);
		fr.drawStringWithShadow(buttonText, x, y, -1);
	}
}
