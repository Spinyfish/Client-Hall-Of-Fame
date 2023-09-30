package felix.gui.click.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import felix.Client;
import felix.gui.click.components.GuiButton;
import felix.gui.click.components.GuiColor;
import felix.gui.click.components.GuiComboBox;
import felix.gui.click.components.GuiGetKey;
import felix.gui.click.components.GuiHidden;
import felix.gui.click.components.GuiLabel;
import felix.gui.click.components.GuiSlider;
import felix.gui.click.components.GuiToggleButton;
import felix.gui.click.components.listeners.ComboListener;
import felix.gui.click.components.listeners.ComponentListener;
import felix.gui.click.components.listeners.KeyListener;
import felix.gui.click.components.listeners.ValueListener;
import felix.module.Module;
import felix.util.other.MathUtils;
import felix.value.Value;
import felix.value.impl.*;


/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at
 *         11.11.2020. Use is only authorized if given credit!
 * 
 */
public class ComponentsListener extends ComponentListener {
	private GuiButton button;

	public ComponentsListener(GuiButton button) {
		this.button = button;
	}

	@Override
	public void addComponents() {
		final Module m = Client.INSTANCE.getModuleManager().getModule(button.getText());
		add(new GuiLabel(m.getName() + " Settings"));
		for (Value set : m.getValues()) {
				switch (set.getValueType()) {
				case Boolean:
					GuiToggleButton toggleButton = new GuiToggleButton(set.getLabel());
					toggleButton.setToggled(((BooleanValue) set).isEnabled());
					toggleButton.addClickListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							set.setValue(toggleButton.isToggled());
						}
					});
					add(toggleButton);
					break;
				case Number:
					final NumberValue setting = (NumberValue) set;
					GuiSlider slider = null;
					slider = new GuiSlider(setting.getLabel(), setting.getMinimum().floatValue(), setting.getMaximum().floatValue(), setting.getValue().floatValue(), setting.getValue() instanceof Integer ? 0 : 1);
					slider.addValueListener(new ValueListener() {

						@Override
						public void valueUpdated(Number value) {
							if (setting.getValue() instanceof Integer) {
								set.setValue(value.intValue());
							}
							if (setting.getValue() instanceof Float) {
								set.setValue(value.floatValue());
							}
							if (setting.getValue() instanceof Double) {
								set.setValue(value.doubleValue());
							}
							if (setting.getValue() instanceof Long) {
								set.setValue(value.longValue());
							}
							if (setting.getValue() instanceof Byte) {
								set.setValue(value.byteValue());
							}
							if (setting.getValue() instanceof Short) {
								set.setValue(value.shortValue());
							}
						}

						@Override
						public void valueChanged(Number value) {
							if (setting.getValue() instanceof Integer) {
								set.setValue(value.intValue());
							}
							if (setting.getValue() instanceof Float) {
								set.setValue(value.floatValue());
							}
							if (setting.getValue() instanceof Double) {
								set.setValue(value.doubleValue());
							}
							if (setting.getValue() instanceof Long) {
								set.setValue(value.longValue());
							}
							if (setting.getValue() instanceof Byte) {
								set.setValue(value.byteValue());
							}
							if (setting.getValue() instanceof Short) {
								set.setValue(value.shortValue());
							}
						}
					});
					add(slider);
					break;
				case Enum:
					GuiComboBox comboBox = new GuiComboBox((EnumValue) set);
					comboBox.addComboListener(new ComboListener() {

						@Override
						public void comboChanged(String combo) {
						}
					});
					add(comboBox);
					break;
				case Color:
					GuiColor color = new GuiColor((ColorValue) set);
					add(color);
					break;
			}
		}
		GuiGetKey ggk = new GuiGetKey("KeyBind", m.getKey());
		ggk.addKeyListener(new KeyListener() {

			@Override
			public void keyChanged(int key) {
				m.setKeyBind(key);
			}
		});
		add(ggk);
	}
}
