package felix.gui.click.components;

import felix.gui.click.Panel;
import felix.util.visual.RenderUtil;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at
 *         11.11.2020. Use is only authorized if given credit!
 * 
 */
public class GuiLabel implements GuiComponent {
	private String text;

	public GuiLabel(String text) {
		this.text = text;
	}

	@Override
	public void render(int posX, int posY, int width, int mouseX, int mouseY, int wheelY) {
		Panel.fR.drawStringWithShadow(text, posX + width / 2 - Panel.fR.getWidth(text) / 2, posY + 2, Panel.fontColor);
		
		RenderUtil.drawRect(posX, posY - 1, width, 1, Panel.black195);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
	}

	@Override
	public void keyTyped(int keyCode, char typedChar) {
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return Panel.fR.FONT_HEIGHT + 2;
	}
	
	@Override
	public boolean allowScroll() {
			return false;
	}
}
