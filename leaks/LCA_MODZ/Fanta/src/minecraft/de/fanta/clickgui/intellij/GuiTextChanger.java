package de.fanta.clickgui.intellij;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiTextChanger extends Gui {

	public String accesModifier, accesModifier2, type, variableName, value;
	public float x, y, width, height;
	public Setting setting;
	public String currValue;
	public int lines = 1;

	public GuiTextChanger(String accesModifier, String accesModifier2, String type, String variableName, String value,
			float x, float y, Setting setting) {
		this.accesModifier = accesModifier;
		this.accesModifier2 = accesModifier2;
		this.type = type;
		this.variableName = variableName;
		this.value = value;
		this.x = x;
		this.y = y;
		this.setting = setting;
		this.currValue = value;
		if (setting.getSetting() instanceof ColorValue) {
			try {
				Integer i = Integer.parseInt(setting.getSetting().getValue().toString());
				currValue = "#" + Integer.toHexString(i).substring(0, 6);
			} catch (Exception e) {
				currValue = "#FFFFFF";
			}
		}
	}

	private boolean focused = false;
	private TimeUtil timer = new TimeUtil();
	private boolean showCursor = false;

	public void draw(float mouseX, float mouseY) {
		int widthAdd = 0;
		String space = " ";

		int typeColor = ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR;
		int variableColor = ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR;
		int commentColor = Color.decode("#808080").getRGB();
		String displayValue = currValue;
		String comment = "";
		if (setting.getSetting() instanceof DropdownBox) {
			typeColor = ClickGuiMainPane.TYPE_AS_OBJECT_FONT_COLOR;
			variableColor = Color.decode("#42B678").getRGB();
			displayValue = "\"" + currValue + "\"";
			comment += "/* Allowed Values: {";
			String[] strings = (String[]) setting.getSetting().getMaxValue();
			for (int i = 0; i < strings.length; i++) {
				if (i == strings.length - 1) {
					comment += strings[i];
				} else {
					comment += strings[i] + ", ";
				}
			}
			comment += "}*/";
		} else if (setting.getSetting() instanceof Slider) {
			typeColor = ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR;
			variableColor = ClickGuiMainPane.VALUE_FONT_COLOR;
			displayValue = currValue + "f";
			comment += "/* Max Value: " + setting.getSetting().getMaxValue() + "*/";
		} else if (setting.getSetting() instanceof CheckBox) {
			typeColor = ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR;
			variableColor = ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR;
			comment += "/* Opposite Value: " + !((boolean) setting.getSetting().getValue()) + "*/";
		} else if (setting.getSetting() instanceof ColorValue) {
			typeColor = ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR;
			variableColor = ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR;
			comment += "/* Please enter a Hex Code like this #FF0077*/";
		}
		float valX = 0, valY = 0, valWidth = 0, valHeight = 0;
		ClickGuiMainPane.MENU_FONT.drawString(accesModifier, x + widthAdd, y + 2,
				ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR);
		widthAdd += ClickGuiMainPane.MENU_FONT.getStringWidth(accesModifier + space);
		if (!accesModifier2.trim().isEmpty()) {
			ClickGuiMainPane.MENU_FONT.drawString(accesModifier2, x + widthAdd, y + 2,
					ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR);
			widthAdd += ClickGuiMainPane.MENU_FONT.getStringWidth(accesModifier2 + space);
		}
		ClickGuiMainPane.MENU_FONT.drawString(type, x + widthAdd, y + 2, typeColor);
		widthAdd += ClickGuiMainPane.MENU_FONT.getStringWidth(type + space);
		ClickGuiMainPane.MENU_FONT.drawString(variableName.toLowerCase().replace(" ", "_"), x + widthAdd, y + 2,
				ClickGuiMainPane.VARIABLE_NAME_FONT_COLOR);
		widthAdd += ClickGuiMainPane.MENU_FONT.getStringWidth(variableName.toLowerCase().replace(" ", "_") + space);
		ClickGuiMainPane.MENU_FONT.drawString("=", x + widthAdd, y + 2, ClickGuiMainPane.TYPE_AS_OBJECT_FONT_COLOR);
		widthAdd += ClickGuiMainPane.MENU_FONT.getStringWidth("=" + space);
		valX = x + widthAdd;
		valY = y;
		valWidth = ClickGuiMainPane.MENU_FONT.getStringWidth(displayValue) + 2;
		valHeight = 14;
		ClickGuiMainPane.MENU_FONT.drawString(displayValue, x + widthAdd, y + 2, variableColor);
		widthAdd += ClickGuiMainPane.MENU_FONT.getStringWidth(displayValue);
		if (showCursor && focused)
			ClickGuiMainPane.MENU_FONT
					.drawString("|",
							x + widthAdd
									- (setting.getSetting() instanceof DropdownBox
											|| setting.getSetting() instanceof Slider ? 7.5F : 2),
							y + 2, Color.white.getRGB());
		ClickGuiMainPane.MENU_FONT.drawString(";", x + widthAdd, y + 2, ClickGuiMainPane.MODIFIER_AND_TYPE_FONT_COLOR);
		widthAdd += ClickGuiMainPane.MENU_FONT.getStringWidth(";" + space);
		ClickGuiMainPane.MENU_FONT.drawString(comment, x + widthAdd, y + 2, commentColor);
		widthAdd += ClickGuiMainPane.MENU_FONT.getStringWidth(comment);
		if (setting.getSetting() instanceof ColorValue) {
			try {
				drawRect(x - 30, y + 4, x - 20, y + 14, Color.decode(currValue).getRGB());
			} catch (Exception e) {

			}
		}
		if ((mouseX >= valX && mouseX <= valX + valWidth && mouseY >= y && mouseY <= y + valHeight)
				&& Mouse.isButtonDown(0) && accesModifier2.isEmpty())
			this.focused = true;
		else if (Mouse.isButtonDown(0)
				&& !(mouseX >= valX && mouseX <= valX + valWidth && mouseY >= y && mouseY <= y + valHeight)) {
			this.focused = false;
			save();
		}
//		if(this.focused) {
//			drawHollowRect(valX, valY+1, valWidth, valHeight, 1, Color.CYAN.getRGB());
//		}

		if (timer.hasReached(500))
			showCursor = true;
		if (timer.hasReached(1000)) {
			showCursor = false;
			timer.reset();
		}
	}

	public void keyTyped(char typedChar, int keyCode) {
		if (!this.focused)
			return;
		if (keyCode == 14 && currValue.length() > 0) {
			this.currValue = currValue.substring(0, currValue.length() - 1);
		} else if (keyCode == Keyboard.KEY_RETURN) {
			save();
			this.focused = false;
		} else {
			if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
				writeText(Character.toString(typedChar));
		}
	}

	public void writeText(String s) {
		this.currValue += s;
	}

	public void save() {
		if (setting.getSetting() instanceof CheckBox) {
			currValue = currValue.trim();
			if (currValue.equalsIgnoreCase("true") || currValue.equalsIgnoreCase("false")) {
				setting.getSetting().setValue(Boolean.parseBoolean(currValue.toLowerCase()));
			} else {
				currValue = setting.getSetting().getValue().toString();
			}
		} else if (setting.getSetting() instanceof DropdownBox) {
			boolean allowContinue = false;
			String[] strings = (String[]) setting.getSetting().getMaxValue();
			for (String string : strings) {
				if (string.trim().equalsIgnoreCase(currValue.trim()))
					allowContinue = true;
			}
			if (allowContinue) {
				setting.getSetting().setValue(currValue.trim());
			}
		} else if (setting.getSetting() instanceof ColorValue) {
			if (currValue.length() == 7 && currValue.startsWith("#")) {
				if (currValue.equals("#000000")) {
					setting.getSetting().setValue(Color.black.getRGB());
					return;
				}
				try {
					int color = Color.decode(currValue).getRGB();
					setting.getSetting().setValue(color);
				} catch (Exception e) {
					setting.getSetting().setValue(setting.getSetting().getValue());
				}
			}
		} else if (setting.getSetting() instanceof Slider) {
			String result = currValue.replace(",", ".");
			if (result.startsWith("."))
				result = "0" + result;
			try {
				Double d = Double.parseDouble(result);
				if (d > (Double) setting.getSetting().getMaxValue())
					d = (Double) setting.getSetting().getMaxValue();
				setting.getSetting().setValue(d);
				currValue = "" + d;
			} catch (Exception e) {
				setting.getSetting().setValue(setting.getSetting().getMaxValue());
			}
		}
	}

}
