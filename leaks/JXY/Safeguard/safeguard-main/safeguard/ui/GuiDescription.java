package intentions.ui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiDescription extends GuiScreen
{

	private String description, name;
	
	public GuiDescription(String description, String name)
	{
		this.description = description;
		this.name = name;
	}	
	
	@Override
	public void initGui()
	{
		int width = 100, height = 20, offset = (5 / 2);
		buttonList.add(new GuiButton(0, this.width / 2 - width / 2, this.height / 2, width, height, "Close"));
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{

	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.id == 0)
		{
			
			mc.displayGuiScreen(null);
			
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawCenteredString(this.fontRendererObj, this.description, this.width / 2, (float) (this.height / 2.5), -1);
		this.drawCenteredString(this.fontRendererObj, this.name, this.width / 2,  this.height / 2, -1);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void updateScreen()
	{
		
	}

}
