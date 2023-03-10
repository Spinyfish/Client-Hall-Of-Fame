/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayOutputStream;
import lodomir.dev.event.impl.network.EventSendPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class LunarSpoofer
extends Module {
    public LunarSpoofer() {
        super("LunarSpoofer", 0, Category.OTHER);
    }

    @Override
    @Subscribe
    public void onSendPacket(EventSendPacket event) {
        C17PacketCustomPayload pay;
        if (event.getPacket() instanceof C17PacketCustomPayload && (pay = (C17PacketCustomPayload)event.getPacket()).getChannelName().equalsIgnoreCase("MC|Brand")) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ByteBuf message = Unpooled.buffer();
            message.writeBytes("Lunar-Client".getBytes());
            this.sendPacket(new C17PacketCustomPayload("REGISTER", new PacketBuffer(message)));
        }
    }
}

