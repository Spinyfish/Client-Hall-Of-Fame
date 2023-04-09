package tech.dort.dortware.impl.utils.player;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.util.Util;
import tech.dort.dortware.impl.events.UpdateEvent;

public class PlayerUtil implements Util {
    private static double lastDist;

    static {
        Client.INSTANCE.getEventBus().post(new PlayerSP());
    }

    public static double getLastDist() {
        return lastDist;
    }

    public static class PlayerSP {


        @Subscribe
        public void sp(UpdateEvent event) {
            if (event.isPre()) {
                double xDif = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
                double zDif = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
                lastDist = StrictMath.sqrt(xDif * xDif + zDif * zDif);
            }
        }
    }
}
