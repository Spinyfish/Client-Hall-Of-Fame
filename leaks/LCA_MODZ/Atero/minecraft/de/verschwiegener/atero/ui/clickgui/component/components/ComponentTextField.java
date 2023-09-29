package de.verschwiegener.atero.ui.clickgui.component.components;

import java.awt.Color;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.ui.clickgui.component.Component;
import de.verschwiegener.atero.ui.clickgui.component.PanelExtendet;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.util.ChatAllowedCharacters;

public class ComponentTextField extends Component {

    private final Font font;
    private String currentText;
    private int textFieldEditPosition;
    private boolean textFieldSelected;
    private int count;
    private boolean draw;

    public ComponentTextField(final String name, final int y, final PanelExtendet pe) {
	super(name, y, pe);
	font = Management.instance.font;
	currentText = getItem().getCurrent();
    }

    @Override
    public void drawComponent(final int x, final int y) {
	super.drawComponent(x, y);
	if (!isParentextendet()) {
	    final int textX = (getComponentX() + 3);
	    if (textFieldSelected) {
		try {
		    final String split1 = currentText.substring(0, textFieldEditPosition);
		    final String split2 = currentText.substring(textFieldEditPosition, currentText.length());

		    font.drawString(split1, textX,
			    getComponentY() - getPanelExtendet().getPanel().getPanelYOffset(),
			    Color.BLACK.getRGB());
		    font.drawString(split2, textX + font.getStringWidth2(split1) * 2 + 3,
			    getComponentY() - getPanelExtendet().getPanel().getPanelYOffset(),
			    Color.BLACK.getRGB());

		    drawSelected(getComponentX() + 3 + font.getStringWidth2(split1));
		} catch (final Exception e) {
		}
	    } else {
		font.drawString(getItem().getCurrent(), textX,
			getComponentY() - getPanelExtendet().getPanel().getPanelYOffset(), Color.BLACK.getRGB());
	    }
	}
    }

    private void drawSelected(final int x) {
	count++;
	if (count % 200 == 0) {
	    draw = !draw;
	    count = 0;
	}
	if (draw) {
	    RenderUtil.fillRect(x + 1, getComponentY() - font.getBaseStringHeight() + 5, 0.5,
		    font.getBaseStringHeight() - 1, Color.WHITE);
	}
    }

    private boolean isTextFieldHovered(final int mouseX, final int mouseY) {
	return mouseX > getComponentX() + 3 && mouseX < getComponentX() + 3 + getPanelExtendet().getWidth()
		&& mouseY > getComponentY() - 7 && mouseY < getComponentY() + 3;
    }

    @Override
    public void onKeyTyped(final char typedChar, final int keyCode) {
	super.onKeyTyped(typedChar, keyCode);
	if (textFieldSelected) {
	    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
		final String beforadd = currentText.substring(0, textFieldEditPosition);
		final String afteradd = currentText.substring(textFieldEditPosition, currentText.length());
		currentText = beforadd + String.valueOf(typedChar) + afteradd;
		textFieldEditPosition += 1;
	    }
	    switch (keyCode) {
	    case 203:
		if (textFieldEditPosition != 0 && textFieldEditPosition > 0) {
		    textFieldEditPosition -= 1;
		}
		break;
	    case 205:
		if (textFieldEditPosition != currentText.length() && textFieldEditPosition < currentText.length()) {
		    textFieldEditPosition += 1;
		}
		break;
	    case 14:
		if (textFieldEditPosition - 1 > -1) {
		    try {
			final String beforRemove = currentText.substring(0, textFieldEditPosition - 1);
			final String afterRemove = currentText.substring(textFieldEditPosition, currentText.length());
			textFieldEditPosition -= 1;
			currentText = beforRemove + afterRemove;
		    } catch (final Exception e) {
		    }

		}
		break;
	    case 28:
		getItem().setCurrent(currentText);
		textFieldSelected = false;
		break;
	    }
	}
    }

    @Override
    public void onMouseClicked(final int mouseX, final int mouseY, final int button) {
	super.onMouseClicked(mouseX, mouseY, button);
	textFieldSelected = false;
	if (button == 0 && isTextFieldHovered(mouseX, mouseY)) {
	    textFieldSelected = true;
	    draw = true;
	    if (mouseX > getComponentX() + 3 + font.getStringWidth(currentText)) {
		textFieldEditPosition = currentText.length();
	    }
	}
    }

}
