package felix.gui.click.components;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at
 *         10.11.2020. Use is only authorized if given credit!
 * 
 */
public interface Frame {

	void initialize();

	void render(int mouseX, int mouseY);

	void mouseClicked(int mouseX, int mouseY, int mouseButton);

	void keyTyped(int keyCode, char typedChar);

}
