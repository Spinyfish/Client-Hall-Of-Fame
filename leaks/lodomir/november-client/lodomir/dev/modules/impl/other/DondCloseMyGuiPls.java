/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.EventGetPackets;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

public class DondCloseMyGuiPls
extends Module {
    public DondCloseMyGuiPls() {
        super("NoGuiClose", 0, Category.OTHER);
    }

    @Subscribe
    public void onGetPackets(EventGetPackets event) {
        if (event.getPackets() instanceof S2EPacketCloseWindow && DondCloseMyGuiPls.mc.currentScreen instanceof GuiChat) {
            event.setCancelled(true);
        }
    }
}

