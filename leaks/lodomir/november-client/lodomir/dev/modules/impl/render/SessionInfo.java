/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import lodomir.dev.November;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.KillAura;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.render.RenderUtils;
import lodomir.dev.utils.world.ServerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

public class SessionInfo
extends Module {
    public TimeUtils timer = new TimeUtils();
    private int kills;
    private int wins;
    double width;
    double height;
    private EntityPlayer target;
    private TTFFontRenderer fr;

    public SessionInfo() {
        super("SessionInfo", 0, Category.RENDER);
    }

    @Override
    public void onGetPacket(EventGetPacket event) {
        String message;
        Packet packet = event.getPacket();
        if (packet instanceof S02PacketChat && (message = ((S02PacketChat)packet).getChatComponent().getUnformattedText()).contains("You Won! Want to play again?")) {
            ++this.wins;
        }
        if (packet instanceof S02PacketChat) {
            String chat = ((S02PacketChat)packet).getChatComponent().getUnformattedText();
            String look = "was killed by " + SessionInfo.mc.thePlayer.getName();
            String look2 = "was slain by " + SessionInfo.mc.thePlayer.getName();
            String look3 = "was thrown to the void by " + SessionInfo.mc.thePlayer.getName();
            String look4 = "was killed with magic while fighting " + SessionInfo.mc.thePlayer.getName();
            String look5 = "couldn't fly while escaping " + SessionInfo.mc.thePlayer.getName();
            String look6 = "fell to their death while escaping " + SessionInfo.mc.thePlayer.getName();
            if (chat.contains(look) || chat.contains(look2) || chat.contains(look3) || chat.contains(look4) || chat.contains(look5) || chat.contains(look6)) {
                ++this.kills;
            }
        }
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        Entity entity = KillAura.target;
        if (entity instanceof EntityPlayer) {
            this.target = (EntityPlayer)entity;
        }
    }

    @Override
    @Subscribe
    public void on2D(EventRender2D event) {
        this.fr = !Interface.mode.isMode("November") ? November.INSTANCE.fm.getFont("PRODUCT SANS 20") : November.INSTANCE.fm.getFont("SFR 20");
        this.width = (float)SessionInfo.mc.displayWidth / (float)(SessionInfo.mc.gameSettings.guiScale * 2) + 180.0f;
        this.height = (float)SessionInfo.mc.displayHeight / (float)(SessionInfo.mc.gameSettings.guiScale * 2);
        RenderUtils.drawRoundedRectOutline(3.0, 21.0, SessionInfo.mc.thePlayer.getName().length() <= 8 ? 110.0 : (double)(62 + this.fr.getStringWidth(SessionInfo.mc.thePlayer.getName())), 56.0, 5.0, Interface.getColor());
        RenderUtils.drawRoundedRect(3.0, 21.0, SessionInfo.mc.thePlayer.getName().length() <= 8 ? 110.0 : (double)(62 + this.fr.getStringWidth(SessionInfo.mc.thePlayer.getName())), 56.0, 5.0, new Color(0, 0, 0, 40).getRGB());
        this.fr.drawStringWithShadow("Username: " + SessionInfo.mc.thePlayer.getName(), 5.0f, 25.0f, -1);
        this.fr.drawStringWithShadow("Play Time: " + ServerUtils.getSessionLengthString(), 5.0, 37.7, -1);
        this.fr.drawStringWithShadow("Kills: " + this.kills, 5.0, 50.400000000000006, -1);
        this.fr.drawStringWithShadow("Wins: " + this.wins, 5.0, 63.099999999999994, -1);
    }
}

