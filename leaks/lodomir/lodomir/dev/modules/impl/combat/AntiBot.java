/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.KillAura;
import lodomir.dev.settings.impl.ModeSetting;
import net.minecraft.entity.Entity;

public class AntiBot
extends Module {
    public static List<Entity> bot = new ArrayList<Entity>();
    public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel", "Matrix");

    public AntiBot() {
        super("AntiBot", 0, Category.COMBAT);
        this.addSetting(this.mode);
    }

    @Override
    public void onEnable() {
        bot.clear();
        for (Entity e : AntiBot.mc.theWorld.playerEntities) {
            e.bot = true;
        }
    }

    @Override
    public void onDisable() {
        bot.clear();
        for (Entity e : AntiBot.mc.theWorld.playerEntities) {
            e.bot = false;
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        for (Entity target : AntiBot.mc.theWorld.loadedEntityList) {
            if (target != AntiBot.mc.thePlayer) {
                switch (this.mode.getMode()) {
                    case "Hypixel": {
                        String string = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
                        String name = target.getName();
                        for (int i = 0; i < name.length(); ++i) {
                            String normalName = String.valueOf(name.charAt(i));
                            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_".contains(normalName)) continue;
                            target.bot = true;
                            break;
                        }
                        if (target.ticksExisted >= 20 || (int)target.posX != (int)AntiBot.mc.thePlayer.posX || (int)target.posZ != (int)AntiBot.mc.thePlayer.posZ || !target.isInvisible()) break;
                        target.bot = true;
                        break;
                    }
                    case "Matrix": {
                        if (KillAura.target.getName() != target.getName() || target.motionY != 0.0) break;
                        target.bot = true;
                    }
                }
            }
            if (!target.bot) continue;
            bot.add(target);
        }
        this.setSuffix(this.mode.getMode());
    }
}

