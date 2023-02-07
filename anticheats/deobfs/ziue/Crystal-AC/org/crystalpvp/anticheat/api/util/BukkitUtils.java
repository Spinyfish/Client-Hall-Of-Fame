package org.crystalpvp.anticheat.api.util;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;

public class BukkitUtils {
    @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
    public static int getPotionLevel(Player player, PotionEffectType type) {
        PotionEffect potionEffect;
        ArrayList potionEffectList = new ArrayList(player.getActivePotionEffects());
        Iterator var3 = potionEffectList.iterator();
        do {
            if (var3.hasNext()) continue;
            return 0;
        } while ((potionEffect = (PotionEffect)var3.next()).getType().getId() != type.getId());
        return potionEffect.getAmplifier() + 1;
    }
}
