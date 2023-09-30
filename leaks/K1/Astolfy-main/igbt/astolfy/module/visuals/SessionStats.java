package igbt.astolfy.module.visuals;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.events.listeners.EventRender2D;
import igbt.astolfy.events.listeners.EventUpdate;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.ui.inGame.GuiElement.GuiElement;
import igbt.astolfy.utils.TimerUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.server.S01PacketJoinGame;
import org.apache.commons.lang3.time.DateUtils;

public class SessionStats extends ModuleBase {

    public int kills = 0;
    public int wins = 0;
    public boolean inGame = false;
    public GuiElement guiElement = new GuiElement("SessionStats", this, 5, 50, 100,100);
    public TimerUtils gameTimer = new TimerUtils();
    public static TimerUtils totalTimer = new TimerUtils();
    public SessionStats() {
        super("Stats", 0, Category.VISUALS);
    }

    public void onSkipEvent(Event e) {
        if(e instanceof EventPacket){
            EventPacket p = (EventPacket)e;
            if(p.getPacket() instanceof S01PacketJoinGame){
                reset();
            }
            if(p.getPacket() instanceof S00PacketDisconnect){
                inGame = false;
            }
            if(p.getPacket() instanceof C00Handshake){
                inGame = false;
            }
        }
    }

    private void reset() {
        kills = 0;
        wins = 0;
        gameTimer.reset();
    }

    private String getTimeString(long secs){
        long totalTimeSeconds = secs;
        long hours = totalTimeSeconds / 3600;
        long minutes = (totalTimeSeconds % 3600) / 60;
        long seconds = totalTimeSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void onEvent(Event e){
        if(e instanceof EventRender2D){
            guiElement.renderStart();
            Gui.drawRect(0, 0, 200, 100, 0x80000000);
            mc.customFont.drawString("Session Stats", 5, 5, -1);
            mc.customFont.drawString("Total PlayTime: " + (getTimeString(totalTimer.getTime() / 1000)), 5, 20, -1);
            mc.customFont.drawString("Game PlayTime: " + (getTimeString(gameTimer.getTime() / 1000)), 5, 30, -1);
            guiElement.renderEnd();
            guiElement.setWidth(200);
            if(!inGame){
                totalTimer.reset();
                inGame = true;
            }
        }

        if(e instanceof EventPacket){
            EventPacket p = (EventPacket)e;
            if(p.getPacket() instanceof S01PacketJoinGame){
                reset();
            }
        }
    }
}
