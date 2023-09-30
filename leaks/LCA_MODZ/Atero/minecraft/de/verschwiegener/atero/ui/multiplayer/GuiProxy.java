package de.verschwiegener.atero.ui.multiplayer;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.proxy.Proxy;
import de.verschwiegener.atero.proxy.ProxyType;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiProxy {

    private final Font font;
    private ProxyType currentType;

    private int x;
    private int y;
    private int selectetTextField;
    private String field1Text = "";
    private String field2Text = "";
    private boolean hasFirstConnected, proxyCheckFinish;
    private String proxyState;
    private Color messageColor;

    public GuiProxy() {
	font = Management.instance.font;
	currentType = ProxyType.SOCKS4;
	selectetTextField = 0;
    }

    private void drawCombobox(final int x, final int y) {
	RenderUtil.fillRect(x + 50, y + 59, 100, 16, Management.instance.colorGray);
	font.drawString(currentType.toString(),
		(x + 100) - font.getStringWidth2(currentType.toString()), (y + 60),
		Color.WHITE.getRGB());
	RenderUtil.drawRect(x + 60, y + 59, 1, 16, Management.instance.colorBlack, 2);
	RenderUtil.drawRect(x + 140, y + 59, 1, 16, Management.instance.colorBlack, 2);

	RenderUtil.drawLine(x + 52, y + 67, x + 58, y + 62, 2, Management.instance.colorBlack);
	RenderUtil.drawLine(x + 52, y + 67, x + 58, y + 72, 2, Management.instance.colorBlack);

	RenderUtil.drawLine(x + 143, y + 62, x + 149, y + 67, 2, Management.instance.colorBlack);
	RenderUtil.drawLine(x + 143, y + 72, x + 149, y + 67, 2, Management.instance.colorBlack);
    }

    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
	final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	x = sr.getScaledWidth() / 2 - 100;
	y = sr.getScaledHeight() / 2 - 50;

	RenderUtil.drawRectRound(x, y, 200, 115, 5, Management.instance.colorBlack);

	font.drawString("Proxy", sr.getScaledWidth() / 2 - font.getStringWidth2("Proxy"),
		(sr.getScaledHeight() / 2 - 50) + font.getBaseStringHeight() / 2 - 2, Color.WHITE.getRGB());

	font.drawString("IP:", x + 3, (y + 20), Color.WHITE.getRGB());
	RenderUtil.fillRect(x + 25 + 3, y + 19, 150, 16, Management.instance.colorGray);
	font.drawString(field1Text, (x + 25 + 3), (y + 20), Color.WHITE.getRGB());

	font.drawString("Port: ", x + 3, (y + 40) , Color.WHITE.getRGB());
	RenderUtil.fillRect(x + 25 + 3, y + 39, 150, 16, Management.instance.colorGray);
	font.drawString(field2Text, (x + 25 + 3), (y + 40), Color.WHITE.getRGB());

	RenderUtil.fillRect(x + 50, y + 95, 100, 16, Management.instance.colorGray);
	font.drawString("Connect", (x + 100) - font.getStringWidth2("Connect"), (y + 95) + 2,
		Color.WHITE.getRGB());
	
	if(hasFirstConnected) {
	    font.drawString(proxyState, (x + 100) * 2 - font.getStringWidth2("Valid Proxy"), (y + 78) * 2, messageColor.getRGB());
	}

	drawCombobox(x, y);
    }
    
    public void setProxyState(String proxyState) {
	this.proxyState = proxyState;
    }
    public void setMessageColor(Color messageColor) {
	this.messageColor = messageColor;
    }

    public void handleKeyboardInput(final char typedChar, final int keycode) {
	String current;
	if(selectetTextField != 0) {
	    hasFirstConnected = false;
	}
	if (selectetTextField == 1) {
	    current = field1Text;
	} else {
	    current = field2Text;
	}

	if (GuiScreen.isKeyComboCtrlV(keycode)) {
	    if(selectetTextField == 2) {
		if(isInt(GuiScreen.getClipboardString())) {
			current += GuiScreen.getClipboardString();
		}
	    }else {
		current += GuiScreen.getClipboardString();
	    }
	}

	switch (keycode) {
	case 14:
	    if (current.length() > 0) {
		current = current.substring(0, current.length() - 1);
	    }
	    break;
	default:
	    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
		current += typedChar;
		if(selectetTextField == 2) {
			if(!isInt(current)) {
			    if (current.length() > 0) {
				current = current.substring(0, current.length() - 1);
			    }
			}
		    }
	    }
	    break;
	}
	if (selectetTextField == 1) {
	    field1Text = current;
	} else {
	    field2Text = current;
	}
    }

    private boolean isComboboxLeftHovered(final int mouseX, final int mouseY) {
	return mouseX > x + 50 && mouseX < x + 60 && mouseY > y + 59 && mouseY < y + 75;
    }

    private boolean isComboboxRightHovered(final int mouseX, final int mouseY) {
	return mouseX > x + 140 && mouseX < x + 150 && mouseY > y + 59 && mouseY < y + 75;
    }

    private boolean isTextField1Hovered(final int mouseX, final int mouseY) {
	return mouseX > x + 28 && mouseX < x + 178 && mouseY > y + 19 && mouseY < y + 35;
    }
    private boolean isConnectButtonHovered(final int mouseX, final int mouseY) {
   	return mouseX > x + 50 && mouseX < x + 150 && mouseY > y + 95 && mouseY < y + 111;
    }

    private boolean isTextField2Hovered(final int mouseX, final int mouseY) {
	return mouseX > x + 28 && mouseX < x + 178 && mouseY > y + 39 && mouseY < y + 55;
    }

    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
	if (mouseButton == 0) {
	    if (isComboboxLeftHovered(mouseX, mouseY)) {
		currentType = moveType(-1);
	    } else if (isComboboxRightHovered(mouseX, mouseY)) {
		currentType = moveType(1);
	    }

	    if (isTextField1Hovered(mouseX, mouseY)) {
		System.out.println("1");
		selectetTextField = 1;
	    } else if (isTextField2Hovered(mouseX, mouseY)) {
		System.out.println("2");
		selectetTextField = 2;
	    } else {
		selectetTextField = 0;
	    }
	    if (isConnectButtonHovered(mouseX, mouseY)) {
		if(!(field1Text != null && field1Text.isEmpty() && !(field2Text != null && field2Text.isEmpty()))) {
		//if(!field1Text.isBlank() && !field2Text.isBlank()) {
		    hasFirstConnected = true;
		    proxyState = "Pinging...";
		    messageColor = Color.GREEN;
		    Management.instance.proxymgr.setCurrentProxy(new Proxy(field1Text, Integer.valueOf(field2Text), currentType));
		}
	    }

	}
    }

    private ProxyType moveType(final int amount) {
	final List<ProxyType> types = Arrays.asList(ProxyType.values());
	final int currentIndex = types.indexOf(currentType);
	int index = currentIndex + amount;
	if (index > types.size() - 1) {
	    index = 0;
	} else if (index < 0) {
	    index = types.size() - 1;
	}
	return types.get(index);
    }
    
    private boolean isInt(String str) {
	try {
	    int x = Integer.parseInt(str);
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}

    }

}
