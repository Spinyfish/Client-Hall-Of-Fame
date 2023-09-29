package de.verschwiegener.atero.ui.multiplayer;

import java.awt.Color;
import java.io.IOException;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.util.ReconnectUtil;
import de.verschwiegener.atero.util.TimeUtils;
import de.verschwiegener.atero.util.Util;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class GuiReconnect {
    
    private final Font font;
    private int x;
    private int y;
    private String currentIPv4;
    private String currentIPv6;
    private String newIPv4;
    private String newIPv6;
    private String reconnectState;
    private Color reconnectColor;
    private TimeUtils timer = new TimeUtils();
    
    public GuiReconnect() {
	font = Management.instance.font;
    }
    
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
	final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	x = sr.getScaledWidth() / 2 - 100;
	y = sr.getScaledHeight() / 2 - 50;
	
	RenderUtil.drawRectRound(x, y, 200, 100, 5, Management.instance.colorBlack);
	font.drawString("Reconnect", (x + 100) - (font.getStringWidth2("Reconnect")), y, Color.WHITE.getRGB());
	
	font.drawString("IPv4:", (x) + 4, (y + 15), Color.WHITE.getRGB());
	font.drawString(currentIPv4, (x) + 30 + font.getStringWidth("IPv4:"), (y + 15), Color.WHITE.getRGB());
	
	font.drawString("IPv6:", (x) + 4,  (y + 27), Color.WHITE.getRGB());
	font.drawString(currentIPv6, (x) + 30 + font.getStringWidth("IPv6:"), (y + 27), Color.WHITE.getRGB());
	
	font.drawString("New IPv4:", (x) + 4, (y + 42), Color.WHITE.getRGB());
	font.drawString(newIPv4, (x) + 30 + font.getStringWidth("New IPv4:"), (y + 42), Color.WHITE.getRGB());
	
	font.drawString("New IPv6:", (x) + 4,  (y + 54), Color.WHITE.getRGB());
	font.drawString(newIPv6, (x) + 30 + font.getStringWidth("New IPv6:"), (y + 54), Color.WHITE.getRGB());
	
	RenderUtil.fillRect(x + 50, y + 80, 100, 16, Management.instance.colorGray);
	font.drawString("Reconnect", (x + 100) - (font.getStringWidth2("Reconnect")), (y + 81), Color.WHITE.getRGB());
	
	font.drawString(reconnectState, (x + 100) - (font.getStringWidth2(reconnectState)), (y + 66), reconnectColor.getRGB());
	
	if(reconnectState == "Reconnecting") {
	    if(timer.hasReached(1000)) {
		timer.reset();
		try {
		    newIPv4 = Util.getIpv4();
		    newIPv6 = Util.getIpv6();
		    if(newIPv4 == currentIPv4) {
			newIPv4 = "";
		    }
		    if(newIPv6 == currentIPv6) {
			newIPv6 = "";
		    }
		    if(newIPv4 != "" && newIPv6 != "") {
			reconnectState = "Reconnected";
		    	reconnectColor = Color.GREEN;
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		    newIPv4 = "";
		    newIPv6 = "";
		}
	    }
	}
    }
    
    private boolean isButtonHovered(int mouseX, int mouseY) {
	return mouseX > (x + 50) && mouseX < (x + 150) && mouseY > (y + 80) && mouseY < (y + 96);
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
	if(mouseButton == 0) {
	    if(isButtonHovered(mouseX, mouseY)) {
		System.out.println("Pressed");
		reconnectState = ReconnectUtil.sendReconnect();
		if(reconnectState == "Reconnecting") {
		    reconnectColor = Management.instance.colorBlue;
		}else if(reconnectState == "Failed") {
		    reconnectColor = Color.RED;
		}
	    }
	}
    }
    
    public void handleKeyboardInput(final char typedChar, final int keycode) {
	
    }
    
    public void updateIP() {
	try {
	    currentIPv4 = Util.getIpv4();
	    currentIPv6 = Util.getIpv6();
	} catch (IOException e) {
	    e.printStackTrace();
	    currentIPv4 = "None";
	}
    }
    public void setReconnectState(String reconnectState) {
	this.reconnectState = reconnectState;
	newIPv4 = "";
	newIPv6 = "";
    }
    public void setReconnectColor(Color reconnectColor) {
	this.reconnectColor = reconnectColor;
    }

}
