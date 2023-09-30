package dev.africa.pandaware.impl.module.combat.antibot.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.combat.antibot.AntiBotModule;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.utils.player.PlayerUtils;
import lombok.AllArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class TabAntiBot extends ModuleMode<AntiBotModule> {
    private final EnumSetting<TabMode> mode = new EnumSetting<>("Mode", TabMode.DUPE);

    public TabAntiBot(String name, AntiBotModule parent) {
        super(name, parent);

        this.registerSettings(this.mode);
    }

    List<EntityPlayer> bots = new ArrayList<>();

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            switch (this.mode.getValue()) {
                case DUPE:
                    /*for (EntityPlayer player : mc.theWorld.playerEntities) {
                        if (player == mc.thePlayer)
                            continue;
                        if (PlayerUtils.getPlayers().contains(player) && player.ticksExisted < 20 && mc.thePlayer.ticksExisted > 20) {
                            bots.add(player);
                        }
                    }*/
                    break;
                case NULL:
                    for (EntityPlayer player : mc.theWorld.playerEntities) {
                        if (player == mc.thePlayer)
                            continue;
                        if (!PlayerUtils.getPlayers().contains(player) && player.ticksExisted >= 20) bots.add(player);
                    }
                    break;
            }
            bots.forEach(bot -> mc.theWorld.removeEntity(bot));
        }
    };

    @AllArgsConstructor
    private enum TabMode {
        DUPE("Duplicate"),
        NULL("Null");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
