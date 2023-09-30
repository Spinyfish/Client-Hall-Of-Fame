package felix.gui.click.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import felix.Client;
import felix.gui.click.ClickGui;
import felix.gui.click.Panel;
import felix.gui.click.components.listeners.ComponentListener;
import felix.gui.click.util.ClickUtil;
import felix.module.Module;
import felix.util.font.FontRenderer;
import felix.util.other.MathUtils;
import felix.util.other.TimeHelper;
import felix.util.visual.RenderUtil;
import net.minecraft.client.gui.VanillaFontRenderer;


/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at 11.11.2020. 
 *         Use is only authorized if given credit!
 * 
 */
public class GuiButton implements GuiComponent {
	public static int expandedID = -1;
	private int id;

	private String text;

	private ArrayList<ActionListener> clickListeners = new ArrayList<ActionListener>();
	private ArrayList<GuiComponent> guiComponents = new ArrayList<GuiComponent>();

	private int width, textWidth, posX, posY;
	
	private double animatedWidth;

	/**
	 * 
	 */
	public GuiButton(String text) {
		this.text = text;
		textWidth = (int) Panel.fR.getWidth(text);
		id = ClickGui.compID += 1;
	}

	@Override
	public void render(int posX, int posY, int width, int mouseX, int mouseY, int wheelY) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;

		final int height = getHeight();
		renderGUI(posX, posY, width, height);
		
	}

	/**
	 * Renders button for theme Caesium
	 */
	private void renderGUI(int posX, int posY, int width, int height) {
		
		final TimeHelper enableTimer = new TimeHelper();
		
		
		
		animatedWidth = RenderUtil.animate(width, animatedWidth, 0.09);
		
		RenderUtil.drawRect(posX, posY, width -1, height, Panel.black100);
		
		if (Client.INSTANCE.getModuleManager().getModule(getText()).isEnabled()) {
			RenderUtil.drawRect(posX, posY, animatedWidth -1, height, Panel.color);
		}
		
		
		final FontRenderer fr = Client.INSTANCE.getFontManager().getFont("Display 16", true);
				
		fr.drawStringWithShadow(getText(), posX + (width / 2) - fr.getWidth(getText()) / 2 + 1, posY + 2, Panel.fontColor);
		
		RenderUtil.drawRect(posX, posY, width - 99, height, Panel.black195);
		RenderUtil.drawRect(posX +98, posY, width - 99, height, Panel.black195);
		
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (GuiFrame.dragID == -1 && ClickUtil.isHovered(posX, posY, width, getHeight(), mouseX, mouseY)) {
			if (mouseButton == 1) {
				if (expandedID != id) {
					expandedID = id;
				} else {
					expandedID = -1;
				}
			} else if (mouseButton == 0) {
				for (ActionListener listener : clickListeners) {
					listener.actionPerformed(new ActionEvent(this, id, "click", System.currentTimeMillis(), 0));
				}
			}
		}

		if (expandedID == id) {
			for (GuiComponent component : guiComponents) {
				component.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}

	}

	@Override
	public void keyTyped(int keyCode, char typedChar) {
		if (expandedID == id) {
			for (GuiComponent component : guiComponents) {
				component.keyTyped(keyCode, typedChar);
			}
		}

	}

	@Override
	public int getWidth() {
		return 5 + textWidth;
	}

	@Override
	public int getHeight() {
		return Panel.fR.FONT_HEIGHT + 3;
	}
	
	@Override
	public boolean allowScroll() {
		return false;
	}

	public String getText() {
		return text;
	}

	public int getButtonID() {
		return id;
	}

	public ArrayList<GuiComponent> getComponents() {
		return guiComponents;
	}

	public void addClickListener(ActionListener actionlistener) {
		clickListeners.add(actionlistener);
	}

	public void addExtendListener(ComponentListener listener) {
		listener.addComponents();
		guiComponents.addAll(listener.getComponents());
	}

}
