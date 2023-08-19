package cc.swift.module.impl.render.hud.impl.data.components;

import cc.swift.Swift;
import cc.swift.events.RenderOverlayEvent;
import cc.swift.module.impl.render.hud.HudFont;
import cc.swift.module.impl.render.hud.impl.data.DataComponent;
import cc.swift.util.player.MovementUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;


public class BPSDataComponent extends DataComponent {

    @Override
    public void render(HudFont font, float posX, float posY) {
        float bpsVal = Math.round(MovementUtil.getBlocksPerSecond() * 100) / 100f;
        String text = EnumChatFormatting.RED + "BPS: " + EnumChatFormatting.GRAY + bpsVal;
        ScaledResolution sr = Swift.INSTANCE.sr;
        font.drawStringWithShadow(text, (int) (sr.getScaledWidth() - font.getStringWidth(text) - posX), (int) (sr.getScaledHeight() - posY), 0xFFFFFF);
        this.lines = 1;
    }

}
