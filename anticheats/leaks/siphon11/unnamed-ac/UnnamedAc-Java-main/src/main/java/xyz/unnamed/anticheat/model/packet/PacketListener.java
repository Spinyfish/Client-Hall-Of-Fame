package xyz.unnamed.anticheat.model.packet;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.model.packet.nms.PacketLookup;
import xyz.unnamed.anticheat.service.CheckService;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Getter
@RequiredArgsConstructor
public class PacketListener extends ChannelDuplexHandler {

    private final PlayerData playerData;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object nmsPacket) throws Exception {
        super.channelRead(ctx, nmsPacket);

        UPacket packet = PacketLookup.readNmsPacket(nmsPacket);

        if (packet == null)
            return;

        CheckService.EXECUTOR.submit(() -> this.playerData.getCheckManager().handleIncoming(packet));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object nmsPacket, ChannelPromise promise) throws Exception {
        UPacket packet = PacketLookup.readNmsPacket(nmsPacket);

        if (packet == null) {
            super.write(ctx, nmsPacket, promise);
            return;
        }

        CheckService.EXECUTOR.submit(() -> this.playerData.getCheckManager().handleOutgoing(packet));

        super.write(ctx, nmsPacket, promise);
    }
}
