package me.kansio.client.modules.impl.player.hackerdetect.checks.movement;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.player.hackerdetect.checks.Check;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.math.BPSUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class FlightA extends Check {

    private Minecraft mc = Minecraft.getMinecraft();

    private HashMap<EntityPlayer, Integer> airTicks = new HashMap<>();

    @Override
    public String name() {
        return "Flight (Check A)";
    }

    @Override
    public void onUpdate() {
        for (EntityPlayer ent : mc.theWorld.playerEntities) {
            if (ent == mc.thePlayer) {
                return;
            }

            double yDiff = ent.posY - ent.prevPosY;

            if (ent.onGround) {
                airTicks.put(ent, 0);
                return;
            }

            if (yDiff < -0.45) {
                airTicks.put(ent, 0);
                return;
            }

            int ticks = airTicks.getOrDefault(ent, 0);
            airTicks.put(ent, ticks + 1);

            if (ticks > 35) {
                if (!Client.getInstance().getTargetManager().isTarget(ent)) {
                    flag(ent);
                    Client.getInstance().getTargetManager().getTarget().add(ent.getName());
                }
            }
        }
    }
}
