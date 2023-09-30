package felix.gui.click.components.listeners;

import java.util.ArrayList;

import felix.gui.click.components.GuiComponent;


/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at 11.11.2020. 
 *         Use is only authorized if given credit!
 * 
 */
public abstract class ComponentListener {

	private final ArrayList<GuiComponent> components = new ArrayList<GuiComponent>();

	public ComponentListener() {
	}

	/**
	 * @param component
	 */
	protected void add(GuiComponent component) {
		components.add(component);
	}

	/**
	 *
	 */
	public void clearComponents() {
		components.clear();
	}

	/**
	 * @return
	 */
	public ArrayList<GuiComponent> getComponents() {
		return components;
	}

	/**
	 * 
	 */
	public abstract void addComponents();

}
