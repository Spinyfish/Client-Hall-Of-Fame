package net.optifine.util;

import net.minecraft.client.settings.KeyBinding;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeyUtils {
    public static void fixKeyConflicts(final KeyBinding[] keys, final KeyBinding[] keysPrio) {
        final Set<Integer> set = new HashSet();

        for (int i = 0; i < keysPrio.length; ++i) {
            final KeyBinding keybinding = keysPrio[i];
            set.add(Integer.valueOf(keybinding.getKeyCode()));
        }

        final Set<KeyBinding> set1 = new HashSet(Arrays.asList(keys));
        set1.removeAll(Arrays.asList(keysPrio));

        for (final KeyBinding keybinding1 : set1) {
            final Integer integer = Integer.valueOf(keybinding1.getKeyCode());

            if (set.contains(integer)) {
                keybinding1.setKeyCode(0);
            }
        }
    }
}
