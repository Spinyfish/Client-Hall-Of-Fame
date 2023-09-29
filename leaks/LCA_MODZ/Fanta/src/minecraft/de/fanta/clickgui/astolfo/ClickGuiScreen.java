package de.fanta.clickgui.astolfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import de.fanta.Client;
import de.fanta.module.Module.Type;
import de.fanta.utils.FileManager;
import de.fanta.utils.JsonUtil;
import net.minecraft.client.gui.GuiScreen;

public class ClickGuiScreen extends GuiScreen{


	private List<ClickGuiPane> panes = new ArrayList<ClickGuiPane>();
	public ClickGuiPane selectedPane = null;
	
	@Override
	public void initGui() {
		panes.clear();
		int i = 20;
		for(Type Type : Type.values()) {
			File f = new File(FileManager.getDirectory("astolfoclickgui"), Type.name().toLowerCase()+".json");
			ClickGuiPane pane = JsonUtil.getObject(ClickGuiPane.class, f);
			ClickGuiPane pane2 = new ClickGuiPane(Type, i, 20, this);
			if(pane != null) {
				pane2.x = pane.x;
				pane2.y = pane.y;
				if(pane.extended) {
					pane2.extended = true;
				}else {
					pane2.extended = false;
				}
				pane2.type = pane.type;
			}
			panes.add(pane2);
			i+=120;
		}
	}
	

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, width, height, Integer.MIN_VALUE);
		for(ClickGuiPane pane : this.panes) {
			pane.draw(mouseX, mouseY);
		}
		if(this.selectedPane != null && Mouse.isButtonDown(0)) this.selectedPane.mouseDragged(mouseX, mouseY);
		int mouseD = Mouse.getDWheel();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for(ClickGuiPane pane : this.panes) {
			pane.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		for(ClickGuiPane pane : this.panes) {
			if(this.selectedPane == null || this.selectedPane == pane) {
				if(pane.isHovered(mouseX, mouseY)) this.selectedPane = pane;
			}
		}
		if(this.selectedPane != null && clickedMouseButton == 0) this.selectedPane.mouseDragged(mouseX, mouseY);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		this.selectedPane = null;
	}

	@Override
	public void onGuiClosed() {
		for(ClickGuiPane pane : this.panes) {
			pane.onGuiClosed();
		}
		savePanes();
	}
	
	public void savePanes() {
		FileManager.createDirectory("astolfoclickgui");
		for(ClickGuiPane pane : this.panes) {
			JsonUtil.writeObjectToFile(pane, new File(FileManager.getDirectory("astolfoclickgui"), pane.type.name().toLowerCase()+".json"));
		}
	}
	
}
