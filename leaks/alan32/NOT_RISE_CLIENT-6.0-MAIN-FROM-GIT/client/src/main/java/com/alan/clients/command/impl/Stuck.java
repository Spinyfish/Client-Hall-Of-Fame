package com.alan.clients.command.impl;

import com.alan.clients.api.Rise;
import com.alan.clients.command.Command;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.packet.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Auth
 * @since 3/07/2022
 */
@Rise
public final class Stuck extends Command {

    public Stuck() {
        super("command.stuck.description", "stuck");
    }

    @Override
    public void execute(final String[] args) {
        PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, -1, mc.thePlayer.posZ, false));
        ChatUtil.display("command.stuck.sent");
    }
}