package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Rise
@ModuleInfo(name = "Insults", description = "module.other.insults.description", category = Category.OTHER)
public final class Insults extends Module {

    public final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Default"))
            .add(new SubMode("Watchdog"))
            .setDefault("Default");

    public final Map<String, List<String>> map = new HashMap<>();

    private final NumberValue delay = new NumberValue("Delay", this, 0, 0, 50, 1);

    private final String[] insults = {
            "Wow! My combo is Rise'n!",
            "Why would someone as bad as you not use Rise 6.0?",
            "Here's your ticket to spectator from Rise 6.0!",
            "I see you're a pay to lose player, huh?",
            "Do you need some PvP advice? Well Rise 6.0 is all you need.",
            "Hey! Wise up, don't waste another day without Rise.",
            "You didn't even stand a chance against Rise.",
            "We regret to inform you that your free trial of life has unfortunately expired.",
            "RISE against other cheaters by getting Rise!",
            "You can pay for that loss by getting Rise.",
            "Remember to use hand sanitizer to get rid of bacteria like you!",
            "Hey, try not to drown in your own salt.",
            "Having problems with forgetting to left click? Rise 6.0 can fix it!",
            "Come on, is that all you have against Rise 6.0?",
            "Rise up today by getting Rise 6.0!",
            "Get Rise, you need it."
    };

    private EntityPlayer target;
    private int ticks;

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (target != null && !mc.theWorld.playerEntities.contains(target) && mc.thePlayer.ticksExisted > 20) {
            if (ticks >= delay.getValue().intValue()) {
                String insult = insults[RandomUtils.nextInt(0, insults.length)];

                if (mode.getValue().getName().equals("Watchdog")) {
                    insult = "[STAFF] [WATCHDOG] %s reeled in.";
                }

                mc.thePlayer.sendChatMessage(String.format(insult, PlayerUtil.name(target)));
                target = null;
            }

            ticks++;
        } else {
            target = null;
        }
    };

    @EventLink()
    public final Listener<AttackEvent> onAttack = event -> {

        final Entity target = event.getTarget();

        if (target instanceof EntityPlayer) {
            this.target = (EntityPlayer) target;
            ticks = 0;
        }
    };

    @EventLink()
    public final Listener<WorldChangeEvent> onWorldChange = event -> {

        target = null;
        ticks = 0;
    };
}