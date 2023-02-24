package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import me.kansio.client.Client;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.player.hackerdetect.checks.Check;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.utils.chat.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.HashMap;

@ModuleData(
        name = "Hacker Detect",
        category = ModuleCategory.PLAYER,
        description = "Detects Cheaters Useing Client Side AC"
)
public class HackerDetect extends Module {

    public ModeValue theme = new ModeValue("Theme", this, "Sleek", "Verus", "AGC", "Ghostly");

    @Getter
    private static HackerDetect instance;

    @Getter @Setter
    private double cageYValue;

    @Getter
    private HashMap<EntityPlayer, Integer> violations = new HashMap<>();

    public HackerDetect() {
        instance = this;
    }

    public void onEnable() {
        switch (theme.getValue()) {
            case "Sleek":
                ChatUtil.logNoPrefix("§7[§b§lSleekAC§7] §bAlerts Enabled");
                break;
            case "Verus":
                ChatUtil.logNoPrefix("§9You are now viewing alerts");
                break;
            case "AGC":
                ChatUtil.logNoPrefix("§9You are now viewing alerts");
                break;
            case "Ghostly":
                ChatUtil.logNoPrefix("§9You are now viewing alerts");
                break;
        }
    }

    public void onDisable() {
        switch (theme.getValue()) {
            case "Sleek":
                ChatUtil.logNoPrefix("§7[§b§lSleekAC§7] §bAlerts Disabled");
                break;
            case "Verus":
                ChatUtil.logNoPrefix("§9You are no longer viewing alerts");
                break;
            case "AGC":
                ChatUtil.logNoPrefix("§9You are no longer viewing alerts");
                break;
            case "Ghostly":
                ChatUtil.logNoPrefix("§9You are no longer viewing alerts");
                break;
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.ticksExisted < 5)
            violations.clear();

        for (Check c : Client.getInstance().getCheckManager().getChecks()) {
            c.onUpdate();
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        for (Check c : Client.getInstance().getCheckManager().getChecks()) {
            c.onPacket(event);
        }

        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = event.getPacket();
            String msg = packet.getChatComponent().getFormattedText();

            if (msg.contains("Cages open in:")) {
                for (Check c : Client.getInstance().getCheckManager().getChecks()) {
                    c.onBlocksMCGameStartTick();
                }
            }
        }
    }

}
