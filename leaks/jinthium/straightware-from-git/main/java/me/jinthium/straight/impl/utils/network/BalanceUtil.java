package me.jinthium.straight.impl.utils.network;

import me.jinthium.straight.api.util.MinecraftInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;

public enum BalanceUtil implements MinecraftInstance {
    INSTANCE;

    private int balance;
    private long lastPacket;

    public void onGlobalPacket(Packet<?> packetIn) {
        if (packetIn instanceof S01PacketJoinGame) {
            this.lastPacket = System.currentTimeMillis();
            this.balance = 0;
        }

        if (packetIn instanceof C03PacketPlayer) {
            if (mc.thePlayer.ticksExisted > 1) {
                long difference = System.currentTimeMillis() - this.lastPacket;

                long fixedBalance = difference - 50L;

                this.balance -= fixedBalance;
            } else {
                this.balance = 0;
            }

            this.lastPacket = System.currentTimeMillis();
        }
    }

    public int getBalance() {
        return balance;
    }
}