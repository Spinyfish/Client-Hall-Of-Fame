package me.eldodebug.soar.management.mods.impl;

import java.util.Arrays;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventReceivePacket;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.ClientUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;
import me.eldodebug.soar.utils.server.ServerUtils;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.StringUtils;

public class SessionInfoMod extends Mod{

	private int win;
	private int killCount;
    private  String[] KILL_TRIGGERS = {"by *", "para *", "fue destrozado a manos de *"};
    
	public SessionInfoMod() {
		super("Session Info", "Display session information", ModCategory.HUD);
	}

	@Override
	public void setup() {
		win = 0;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		if(!ServerUtils.isHypixel()) {
			ClientUtils.showNotification("Warning", "This mod is recommended for use with Hypixel");
		}
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		
		String time;
		
		if(mc.isSingleplayer()) {
			time = "SinglePlayer";
		}else {
            long durationInMillis = System.currentTimeMillis() - Soar.instance.getPlayTime();
            long second = (durationInMillis / 1000) % 60;
            long minute = (durationInMillis / (1000 * 60)) % 60;
            long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
            time = String.format("%02d:%02d:%02d", hour, minute, second);
		}
		
		this.drawBackground(this.getX(), this.getY(), 140, 65);
        RoundedUtils.drawRound(this.getX(), this.getY() + 17, 140, 1, 0, this.getFontColor());
        
        FontUtils.regular_bold24.drawCenteredString("Session Info", this.getX() + (140 / 2), this.getY() + 4.5F, this.getFontColor().getRGB());
        
        FontUtils.regular_bold20.drawString("Play Time: " + time, this.getX() + 4.5F, this.getY() + 23.5F, this.getFontColor().getRGB());
        FontUtils.regular_bold20.drawString("Wins: " + win, this.getX() + 4.5F, this.getY() + 23.5 + 14, this.getFontColor().getRGB());
        FontUtils.regular_bold20.drawString("Kills: " + killCount, this.getX() + 4.5F, this.getY() + 23.5 + 28, this.getFontColor().getRGB());
        
        this.setWidth(140);
        this.setHeight(64);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		this.drawShadow(this.getX(), this.getY(), this.getWidth() - this.getX(), this.getHeight() - this.getY());
	}
	
    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
        if (event.getPacket() instanceof S45PacketTitle) {
        	S45PacketTitle title = (S45PacketTitle) event.getPacket();

            if(title.getMessage() != null) {
                String title2 = title.getMessage().getUnformattedText();
                if(title2.contains("VICTORY!")) {
                	win++;
                }
            }
        }
        
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat chatPacket = (S02PacketChat) event.getPacket();
            String chatMessage = chatPacket.getChatComponent().getUnformattedText();
            
            if (mc.thePlayer == null) {
            	return;
            }
            
            String message = StringUtils.stripControlCodes(chatMessage);
            if (!message.contains(":") && Arrays.stream(KILL_TRIGGERS).anyMatch(message.replace(mc.thePlayer.getName(), "*")::contains)) {
                killCount++;
            }
        }
    }
}
