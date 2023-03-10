/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

public class NoGuiClose
extends Module {
    public NoGuiClose() {
        super("NoGuiClose", 0, Category.OTHER);
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (event.getPacket() instanceof S2EPacketCloseWindow && NoGuiClose.mc.currentScreen instanceof GuiChat) {
            event.setCancelled(true);
        }
    }
}

