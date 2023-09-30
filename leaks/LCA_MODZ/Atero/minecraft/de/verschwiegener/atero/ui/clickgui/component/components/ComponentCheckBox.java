package de.verschwiegener.atero.ui.clickgui.component.components;

import java.awt.Color;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.ui.clickgui.component.Component;
import de.verschwiegener.atero.ui.clickgui.component.PanelExtendet;
import de.verschwiegener.atero.util.render.RenderUtil;

public class ComponentCheckBox extends Component {

    private final Font font;

    public ComponentCheckBox(String name, int y, PanelExtendet pe) {
	super(name, y, pe);
	font = Management.instance.font;
    }

    @Override
    public void drawComponent(int x, int y) {
	super.drawComponent(x, y);
	if (!isParentextendet() && isValid()) {
	    font.drawString(getName(), (getComponentX() + 3), getComponentY() - getPanelExtendet().getPanel().getPanelYOffset(), Color.white.getRGB());
	    if (Management.instance.settingsmgr.getSettingByName(getPanelExtendet().getName()).getItemByName(getName())
		    .isState()) {
		RenderUtil.fillRect(getXPos() - 12, getComponentY() - 5, 9, 9, Management.instance.colorBlue);
	    } else {
		RenderUtil.fillRect(getXPos() - 12, getComponentY() - 5, 9, 9, Management.instance.colorGray);
	    }
	}
    }

    @Override
    public void onMouseClicked(int x, int y, int button) {
	super.onMouseClicked(x, y, button);
	if (isValid()) {
	    if (button == 0) {
		if (isCheckboxHovered(x, y)) {
		    setParentChange(true);
		    Management.instance.settingsmgr.getSettingByName(getPanelExtendet().getName())
			    .getItemByName(getName()).toggleState();
		}
	    }
	}
    }

    public int getXPos() {
	return getComponentX() + getPanelExtendet().getAnimationX();
    }

    private boolean isCheckboxHovered(int mouseX, int mouseY) {
	return mouseX > (getXPos() - 15) && mouseX < (getXPos() - 3) && mouseY > (getComponentY() - 7)
		&& mouseY < (getComponentY() + 4);
    }

}
