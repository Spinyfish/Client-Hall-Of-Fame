package me.levansj01.verus.util;

import org.bukkit.entity.*;
import com.google.common.collect.*;
import org.bukkit.enchantments.*;
import java.util.function.*;
import org.bukkit.inventory.*;
import me.levansj01.verus.data.state.*;
import org.bukkit.potion.*;
import java.util.*;
import java.util.stream.*;
import org.bukkit.*;
import org.bukkit.command.*;
import me.levansj01.verus.util.location.*;
import me.levansj01.launcher.*;
import org.bukkit.plugin.*;
import org.bukkit.metadata.*;
import me.levansj01.verus.gui.impl.*;

public class BukkitUtil
{
    public static boolean hasEnchantment(final Player player, final int n) {
        final HashSet hashSet = Sets.newHashSet();
        final ItemStack[] armorContents = player.getInventory().getArmorContents();
        while (0 < armorContents.length) {
            final ItemStack itemStack = armorContents[0];
            if (itemStack != null) {
                hashSet.addAll(itemStack.getEnchantments().keySet());
            }
            int n2 = 0;
            ++n2;
        }
        return hashSet.stream().map(Enchantment::getId).anyMatch(BukkitUtil::lambda$hasEnchantment$4);
    }
    
    public static boolean hasPermissionMeta(final Player player, final String s) {
        return player.hasMetadata(s) && player.hasPermission(s);
    }
    
    public static int getPotionLevel(final State state, final PotionEffectType potionEffectType) {
        return getPotionLevel(state.get(), potionEffectType.getId());
    }
    
    private static boolean lambda$getEnchantment$2(final Enchantment enchantment, final Map.Entry entry) {
        return enchantment.equals(entry.getKey());
    }
    
    public static int getPotionLevel(final PotionEffect[] array, final int n) {
        while (0 < array.length) {
            final PotionEffect potionEffect = array[0];
            if (potionEffect.getType().getId() == n) {
                return potionEffect.getAmplifier() + 1;
            }
            int n2 = 0;
            ++n2;
        }
        return 0;
    }
    
    private static boolean lambda$hasEffect$1(final int n, final Integer n2) {
        return n == n2;
    }
    
    private static boolean lambda$hasEnchantment$4(final int n, final Integer n2) {
        return n == n2;
    }
    
    public static int getPotionLevel(final Collection collection, final PotionEffectType potionEffectType) {
        return getPotionLevel(collection, potionEffectType.getId());
    }
    
    private static boolean lambda$getPotionLevel$0(final int n, final PotionEffect potionEffect) {
        return potionEffect.getType().getId() == n;
    }
    
    public static int getPotionLevel(final PotionEffect[] array, final PotionEffectType potionEffectType) {
        return getPotionLevel(array, potionEffectType.getId());
    }
    
    public static boolean hasEnchantment(final ItemStack itemStack, final String s) {
        if (itemStack == null) {
            return false;
        }
        final Map enchantments = itemStack.getEnchantments();
        if (enchantments.isEmpty()) {
            return false;
        }
        final Enchantment byName = Enchantment.getByName(s.toUpperCase());
        if (byName == null) {
            return false;
        }
        final Stream stream = enchantments.keySet().stream();
        final Enchantment enchantment = byName;
        Objects.requireNonNull(enchantment);
        return stream.anyMatch(enchantment::equals);
    }
    
    public static boolean hasEffect(final State state, final int n) {
        return hasEffect(state.get(), n);
    }
    
    public static int getPotionLevel(final Collection collection, final int n) {
        return collection.stream().filter(BukkitUtil::lambda$getPotionLevel$0).map((Function<? super Object, ? extends Integer>)PotionEffect::getAmplifier).findAny().orElse(-1) + 1;
    }
    
    public static PlayerLocation fromPlayer(final Player player) {
        final Location location = player.getLocation();
        return new PlayerLocation(System.currentTimeMillis(), 0, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), player.isOnGround(), false);
    }
    
    @Deprecated
    public static boolean hasEnchantment(final ItemStack itemStack, final int n) {
        return itemStack != null && itemStack.getEnchantments().keySet().stream().map(Enchantment::getId).anyMatch(BukkitUtil::lambda$hasEnchantment$3);
    }
    
    private static boolean lambda$hasEnchantment$3(final int n, final Integer n2) {
        return n == n2;
    }
    
   public static boolean hasPermission(CommandSender var0, String var1) {
      Player var10000;
      if (var0 instanceof Player) {
         var10000 = (Player)var0;
         if (var1 != false) {
            boolean var10001 = false;
            return (boolean)var10000;
         }
      }

      var10000 = true;
      return (boolean)var10000;
   }

    public static boolean hasEffect(final Collection collection, final int n) {
        return collection.stream().map(PotionEffect::getType).map(PotionEffectType::getId).anyMatch(BukkitUtil::lambda$hasEffect$1);
    }
    
    public static PacketLocation fromPlayer2(final Player player) {
        final Location location = player.getLocation();
        return new PacketLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), player.isOnGround(), true, true, false);
    }
    
    public static void setMeta(final Metadatable metadatable, final String s, final boolean b) {
        if (b) {
            metadatable.setMetadata(s, (MetadataValue)new FixedMetadataValue((Plugin)VerusLauncher.getPlugin(), (Object)true));
        }
        else {
            metadatable.removeMetadata(s, (Plugin)VerusLauncher.getPlugin());
        }
    }
    
    public static int getEnchantment(final ItemStack itemStack, final String s) {
        if (itemStack == null) {
            return 0;
        }
        final Map enchantments = itemStack.getEnchantments();
        if (enchantments.isEmpty()) {
            return 0;
        }
        final Enchantment byName = Enchantment.getByName(s.toUpperCase());
        if (byName == null) {
            return 0;
        }
        return (int)enchantments.entrySet().stream().filter(BukkitUtil::lambda$getEnchantment$2).map(Map.Entry::getValue).findAny().orElse(0);
    }
    
    public static boolean isDev(final Player player) {
        return MainGUI.ALLOWED_UUIDS.contains(player.getUniqueId());
    }
    
    public static boolean hasEffect(final PotionEffect[] array, final int n) {
        while (0 < array.length) {
            if (array[0].getType().getId() == n) {
                return true;
            }
            int n2 = 0;
            ++n2;
        }
        return false;
    }
    
    public static boolean hasEffect(final State state, final PotionEffectType potionEffectType) {
        return hasEffect(state.get(), potionEffectType.getId());
    }
    
    public static int getPotionLevel(final State state, final int n) {
        return getPotionLevel(state.get(), n);
    }
}
