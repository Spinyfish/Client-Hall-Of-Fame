package net.minecraft.client.settings;

import net.minecraft.util.*;
import java.util.*;
import net.minecraft.client.resources.*;
import com.google.common.collect.*;

public class KeyBinding implements Comparable<KeyBinding>
{
    private static final List<KeyBinding> keybindArray;
    private static final IntHashMap<KeyBinding> hash;
    private static final Set<String> keybindSet;
    private final String keyDescription;
    private final int keyCodeDefault;
    private final String keyCategory;
    private int keyCode;
    private boolean pressed;
    private int pressTime;
    
    public static void onTick(final int keyCode) {
        if (keyCode != 0) {
            final KeyBinding keybinding = KeyBinding.hash.lookup(keyCode);
            if (keybinding != null) {
                final KeyBinding keyBinding = keybinding;
                ++keyBinding.pressTime;
            }
        }
    }
    
    public static void setKeyBindState(final int keyCode, final boolean pressed) {
        if (keyCode != 0) {
            final KeyBinding keybinding = KeyBinding.hash.lookup(keyCode);
            if (keybinding != null) {
                keybinding.pressed = pressed;
            }
        }
    }
    
    public static void unPressAllKeys() {
        for (final KeyBinding keybinding : KeyBinding.keybindArray) {
            keybinding.unpressKey();
        }
    }
    
    public static void resetKeyBindingArrayAndHash() {
        KeyBinding.hash.clearMap();
        for (final KeyBinding keybinding : KeyBinding.keybindArray) {
            KeyBinding.hash.addKey(keybinding.keyCode, keybinding);
        }
    }
    
    public static Set<String> getKeybinds() {
        return KeyBinding.keybindSet;
    }
    
    public KeyBinding(final String description, final int keyCode, final String category) {
        this.keyDescription = description;
        this.keyCode = keyCode;
        this.keyCodeDefault = keyCode;
        this.keyCategory = category;
        KeyBinding.keybindArray.add(this);
        KeyBinding.hash.addKey(keyCode, this);
        KeyBinding.keybindSet.add(category);
    }
    
    public boolean isKeyDown() {
        return this.pressed;
    }
    
    public String getKeyCategory() {
        return this.keyCategory;
    }
    
    public boolean isPressed() {
        if (this.pressTime == 0) {
            return false;
        }
        --this.pressTime;
        return true;
    }
    
    private void unpressKey() {
        this.pressTime = 0;
        this.pressed = false;
    }
    
    public String getKeyDescription() {
        return this.keyDescription;
    }
    
    public int getKeyCodeDefault() {
        return this.keyCodeDefault;
    }
    
    public int getKeyCode() {
        return this.keyCode;
    }
    
    public void setKeyCode(final int keyCode) {
        this.keyCode = keyCode;
    }
    
    @Override
    public int compareTo(final KeyBinding p_compareTo_1_) {
        int i = I18n.format(this.keyCategory, new Object[0]).compareTo(I18n.format(p_compareTo_1_.keyCategory, new Object[0]));
        if (i == 0) {
            i = I18n.format(this.keyDescription, new Object[0]).compareTo(I18n.format(p_compareTo_1_.keyDescription, new Object[0]));
        }
        return i;
    }
    
    static {
        keybindArray = Lists.newArrayList();
        hash = new IntHashMap<KeyBinding>();
        keybindSet = Sets.newHashSet();
    }
}
