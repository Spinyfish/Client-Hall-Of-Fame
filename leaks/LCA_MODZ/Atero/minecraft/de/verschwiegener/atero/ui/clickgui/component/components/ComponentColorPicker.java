package de.verschwiegener.atero.ui.clickgui.component.components;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.ui.clickgui.component.Component;
import de.verschwiegener.atero.ui.clickgui.component.PanelExtendet;
import de.verschwiegener.atero.util.ColorPicker;
import de.verschwiegener.atero.util.ColorPicker2;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParseException;

public class ComponentColorPicker extends Component {
    final ColorPicker colorPicker = new ColorPicker(ColorPicker.Type.QUAD);
	final ColorPicker2 colorPicker2 = new ColorPicker2(ColorPicker2.Type.QUAD);

    public ComponentColorPicker(String name, int y, PanelExtendet pe) {
	super(name, y, pe);
    }

    @Override
    public void drawComponent(int x, int y) {
	super.drawComponent(x, y);
	colorPicker.draw(getComponentX() + 5, getComponentY(), 70, 50, x, y, getItem().getColor());
	if (x > getComponentX() + 5 && x < getComponentX() + 75 && y > getComponentY() && y < getComponentY() + 70) {
	    if (Mouse.isButtonDown(0)) {
		getItem().setColor(colorPicker.getHoverColor());
	    }
	}
		//colorPicker2.draw(getComponentX() + 5, getComponentY() + 85, 70, 50, x, y, getItem().getColor());
		//if (x > getComponentX() + 5 && x < getComponentX() + 75 && y > getComponentY() + 85 && y < getComponentY() + 70 + 85) {
		//	if (Mouse.isButtonDown(0)) {
			//	getItem().setColor(colorPicker2.getHoverColor());
		//	}
		//}
    }

}
