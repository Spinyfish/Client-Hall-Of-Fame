package cc.swift.module.impl.render.hud.impl.data.components;

import cc.swift.Swift;
import cc.swift.events.RenderOverlayEvent;
import cc.swift.module.impl.render.hud.HudFont;
import cc.swift.module.impl.render.hud.impl.data.DataComponent;
import cc.swift.util.ChatUtil;
import net.minecraft.network.play.server.S31PacketWindowProperty;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.WHITE;

public class TimeComponent extends DataComponent {

    // WHY??????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public void render(HudFont font, float posX, float posY) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String text = GRAY + "(" + WHITE + dtf.format(now) + GRAY + ")";
        font.drawStringWithShadow(text, (int) (Swift.INSTANCE.sr.getScaledWidth() -  font.getStringWidth(text) - posX), (int) (Swift.INSTANCE.sr.getScaledHeight() - posY), Color.white.getRGB());

        width = (float) font.getStringWidth(text) + 2;
        lines = (float) 1;
    }
}
