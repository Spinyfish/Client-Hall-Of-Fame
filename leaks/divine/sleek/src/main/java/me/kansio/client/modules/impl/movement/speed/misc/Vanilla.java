package me.kansio.client.modules.impl.movement.speed.misc;

import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.player.PlayerUtil;

public class Vanilla extends SpeedMode {

    public Vanilla() {
        super("Vanilla");
    }

    @Override
    public void onUpdate(UpdateEvent event) {

        PlayerUtil.setMotion(getSpeed().getSpeed().getValue());
    }
}
