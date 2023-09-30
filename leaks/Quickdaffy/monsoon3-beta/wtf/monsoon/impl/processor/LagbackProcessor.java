/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.processor;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.processor.Processor;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.module.hud.NotificationsModule;
import wtf.monsoon.impl.ui.notification.NotificationType;

public class LagbackProcessor
extends Processor {
    private int totalFlags;
    private int indicatorTicks;
    private boolean shouldShowIndicator = false;
    private String flagIndicatorMessage = "";
    @EventLink
    private final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (this.shouldShowIndicator) {
            if (this.indicatorTicks >= 20) {
                this.shouldShowIndicator = false;
                this.flagIndicatorMessage = "";
            }
            ++this.indicatorTicks;
        } else {
            this.indicatorTicks = 0;
        }
    };
    @EventLink
    private final Listener<EventRender2D> eventRender2DListener = e -> {
        FontRenderer fr = this.mc.fontRendererObj;
        ScaledResolution sr = e.getSr();
        if (this.shouldShowIndicator) {
            int l1 = sr.getScaledWidth() / 2 - fr.getStringWidth(this.flagIndicatorMessage) / 2;
            int i1 = sr.getScaledHeight() / 2 + 8;
            fr.drawString(this.flagIndicatorMessage, (float)(l1 + 1), (float)i1, 0);
            fr.drawString(this.flagIndicatorMessage, (float)(l1 - 1), (float)i1, 0);
            fr.drawString(this.flagIndicatorMessage, (float)l1, (float)(i1 + 1), 0);
            fr.drawString(this.flagIndicatorMessage, (float)l1, (float)(i1 - 1), 0);
            fr.drawString(this.flagIndicatorMessage, (float)l1, (float)i1, Color.RED);
        }
    };
    @EventLink
    private final Listener<EventPacket> eventPacketListener = e -> {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || !this.mc.getNetHandler().doneLoadingTerrain) {
            return;
        }
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            Wrapper.getMonsoon().getModuleManager().getModulesToDisableOnFlag().forEach(this::alertFlag);
        }
    };

    private void alertFlag(Module m) {
        if (m.isEnabled()) {
            switch (Wrapper.getModule(NotificationsModule.class).flagAlert.getValue()) {
                case NOTIFICATION: {
                    Wrapper.getNotifManager().notify(NotificationType.WARNING, "Flag Detected!", "Disabled " + m.getName() + " due to a flag.");
                    m.toggle();
                    break;
                }
                case INDICATOR: {
                    this.flagIndicatorMessage = m.getName() + " disabled";
                    this.shouldShowIndicator = true;
                    this.indicatorTicks = 0;
                    m.toggle();
                }
            }
        }
    }

    public int getTotalFlags() {
        return this.totalFlags;
    }

    public int getIndicatorTicks() {
        return this.indicatorTicks;
    }

    public boolean isShouldShowIndicator() {
        return this.shouldShowIndicator;
    }
}

