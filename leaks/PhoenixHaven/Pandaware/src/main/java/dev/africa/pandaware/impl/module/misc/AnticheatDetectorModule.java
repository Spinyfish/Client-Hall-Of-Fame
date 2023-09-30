package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.ServerJoinEvent;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.math.TimeHelper;
import lombok.var;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S01PacketJoinGame;

@ModuleInfo(name = "Anticheat Detector", description = "Detects various anticheats using packets")
public class AnticheatDetectorModule extends Module {
    private final TimeHelper timer = new TimeHelper();
    private boolean joined;
    private boolean firstTransaction;
    private boolean secondTransaction;

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S01PacketJoinGame) joined = true;

        if (event.getPacket() instanceof C0FPacketConfirmTransaction && joined) {
            var packet = (C0FPacketConfirmTransaction) event.getPacket();

            if (packet.getUid() == -30767) {
                firstTransaction = true;
            }
            if (packet.getUid() == -30766 && firstTransaction) {
                secondTransaction = true;
            }
            if (packet.getUid() == -25767 && secondTransaction) {
                Printer.chat("Vulcan Anticheat Detected");
                this.toggle(false);
            } else if (packet.getUid() == -30765 && secondTransaction) {
                Printer.chat("Old Verus Anticheat Detected");
                this.toggle(false);
            }
        }
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event ->  {
        if (timer.reach(5000) && joined) {
            Printer.chat("Detector timed out");
            this.toggle(false);
        }
        if (!joined) timer.reset();
    };

    @Override
    public void onEnable() {
        Printer.chat("Please rejoin the server or join a game in order for all the checks to work");
        joined = false;
        timer.reset();
    }

    @Override
    public void onDisable() {
        firstTransaction = false;
    }
}
