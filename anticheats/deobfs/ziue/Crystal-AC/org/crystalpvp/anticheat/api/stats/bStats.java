package org.crystalpvp.anticheat.api.stats;

import org.crystalpvp.anticheat.CrystalAC;
import org.bstats.bukkit.Metrics;

public class bStats {


    public static void enableMetrics() {

        Metrics metrics = new Metrics(CrystalAC.getPlugin());

    }
}
