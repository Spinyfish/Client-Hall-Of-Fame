package intentions.ui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiError extends GuiScreen {

	private String error, buttonname;
	private GuiScreen parent;
	
	public GuiError(GuiScreen parent, String error, String buttonname) {
		this.error = error;
		this.parent = parent;
		this.buttonname = buttonname;
	}	
	
	@Override
	public void initGui() {
		int width = 100, height = 20, offset = (5 / 2);
		buttonList.add(new GuiButton(0, this.width / 2 - width / 2, this.height / 2, width, height, buttonname));
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException { }
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException{
		if (button.id == 0) {
			mc.displayGuiScreen(parent);
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, this.error, this.width / 2, (float) (this.height / 2.5), -1);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void updateScreen() { }
}
