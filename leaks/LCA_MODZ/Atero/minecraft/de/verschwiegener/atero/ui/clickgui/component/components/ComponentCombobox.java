package de.verschwiegener.atero.ui.clickgui.component.components;

import java.awt.Color;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.ui.clickgui.component.Component;
import de.verschwiegener.atero.ui.clickgui.component.PanelExtendet;
import de.verschwiegener.atero.util.render.RenderUtil;

public class ComponentCombobox extends Component {

    private boolean extendet;
    private final Font font;

    public ComponentCombobox(String name, int y, PanelExtendet pe) {
	super(name, y, pe);
	extendet = false;
	font = Management.instance.font;
    }

    @Override
    public void drawComponent(int x, int y) {
		super.drawComponent(x, y);
		if (!isParentextendet()) {
			int textx = (getComponentX() + 1);
			int texty = (getComponentY()) - 6;
			if (Management.instance.modulemgr.getModuleByName("Design").isEnabled()) {
				font.drawString(getName(), (textx) + getPanelExtendet().getWidth() / 2 - font.getStringWidth2(getName()), texty, Color.WHITE.getRGB());
			} else {
				font.drawString(getName(), (textx) + getPanelExtendet().getWidth() / 2 - font.getStringWidth2(getName()), texty, Color.WHITE.getRGB());

			}
			if (Management.instance.modulemgr.getModuleByName("Design").isEnabled()) {
				RenderUtil.fillRect(textx, getComponentY() + 6, getPanelExtendet().getWidth(), 1, Management.instance.colorBlue);
				if (extendet) {
					texty += 2;
					for (String str : getItem().getModes()) {
						texty += 12;
						if (str.equals(getItem().getCurrent())) {
							font.drawString(str, (textx + 2) + getPanelExtendet().getWidth() / 2 - (font.getStringWidth2(str)), texty,
									Management.instance.colorBlue.getRGB());
						} else {
							font.drawString(str, (textx + 2) + getPanelExtendet().getWidth() / 2 - (font.getStringWidth2(str)), texty, Color.WHITE.getRGB());
						}
					}
				}
			}
			if (!Management.instance.modulemgr.getModuleByName("Design").isEnabled()) {
				RenderUtil.fillRect(textx, getComponentY() + 6, getPanelExtendet().getWidth(), 1, Management.instance.colorBlue);
				if (extendet) {
					texty += 2;
					for (String str : getItem().getModes()) {
						texty += 12;
						if (str.equals(getItem().getCurrent())) {
							font.drawString(str, (textx + 2) + getPanelExtendet().getWidth() / 2 - (font.getStringWidth2(str)), texty,
									Management.instance.colorBlue.getRGB());
						} else {
							font.drawString(str, (textx + 2) + getPanelExtendet().getWidth() / 2 - (font.getStringWidth2(str)), texty, Color.WHITE.getRGB());


						}
					}
				}
			}
		}
	}


    @Override
    public void onMouseClicked(int x, int y, int button) {
	super.onMouseClicked(x, y, button);
	if (button == 0) {
	    if (extendet) {
		String value = getComboboxItem(x, y);
		if(value != null) {
		    setParentChange(true);
		    getItem().setCurrent(getComboboxItem(x, y));
		}
	    }
	}else if(button == 1) {
	    if (isComboboxHovered(x, y)) {
		extendet = !extendet;
		if (extendet) {
		    getPanelExtendet().extendPanelByYOffset(12 * getItem().getModes().size(), getName());
		} else {
		    getPanelExtendet().collapsePanelByYOffset(12 * getItem().getModes().size(), getName());
		}
	    }
	}
    }
    public void collapse() {
	if(extendet) {
	    extendet = false;
	    getPanelExtendet().collapsePanelByYOffset(12 * getItem().getModes().size(), getName());
	}
    }

    public void onMouseReleased(int mouseX, int mouseY, int state) {
	super.onMouseReleased(mouseX, mouseY, state);
    }

    public boolean isExtendet() {
	return extendet;
    }

    public boolean isComboboxHovered(int mouseX, int mouseY) {
	return mouseX > (getComponentX())
		&& mouseX < (getComponentX() + 1 + getPanelExtendet().getWidth())
		&& mouseY > (getComponentY() - 6) && mouseY < (getComponentY() + 7);
    }

    private String getComboboxItem(int mouseX, int mouseY) {
	int modeY = 2;
	for (String str : getItem().getModes()) {
	    modeY += 12;
	    if (mouseX > getComponentX() && mouseX < (getComponentX() + getPanelExtendet().getWidth() + 1)) {
		if (mouseY > (getComponentY() + 7) && mouseY < (getComponentY() + 7 + modeY)) {
		    return str;
		}
	    }
	}
	return null;
    }

}
