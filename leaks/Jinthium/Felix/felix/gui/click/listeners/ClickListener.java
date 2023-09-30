package felix.gui.click.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import felix.Client;
import felix.gui.click.components.GuiButton;
import felix.module.Module;


/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at 11.11.2020. 
 *         Use is only authorized if given credit!
 * 
 */
public class ClickListener implements ActionListener {

	private GuiButton button;

	public ClickListener(GuiButton button) {
		this.button = button;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Module m = Client.INSTANCE.getModuleManager().getModule(button.getText());
		m.toggle();
	}
}
