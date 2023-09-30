package de.verschwiegener.atero.module.modules.player;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;

public class NoFriends extends Module {
    TimeUtils timeUtils;

    public NoFriends() {
        super("NoFriends", "Sprint", Keyboard.KEY_NONE, Category.Player);
    }

    public void onEnable() {
    }

    public void onDisable() {
        super.onDisable();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
setExtraTag("True");
        }
    }

}
