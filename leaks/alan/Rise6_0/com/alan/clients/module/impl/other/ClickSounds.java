package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.ClickEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;
import com.alan.clients.util.sound.SoundUtil;
import com.alan.clients.value.impl.SubMode;
import org.apache.commons.lang3.RandomUtils;

@Rise
@ModuleInfo(name = "Click Sounds", description = "module.other.clicksounds.description", category = Category.OTHER)
public final class ClickSounds extends Module {

    private final ModeValue sound = new ModeValue("Sound", this)
            .add(new SubMode("Standard"))
            .add(new SubMode("Double"))
            .add(new SubMode("Alan"))
            .setDefault("Standard");

    private final NumberValue volume = new NumberValue("Volume", this, 0.5, 0.1, 2, 0.1);
    private final NumberValue variation = new NumberValue("Variation", this, 5, 0, 100, 1);

    @EventLink()
    public final Listener<ClickEvent> onClick = event -> {
        String soundName = "rise.click.standard";

        switch (sound.getValue().getName()) {
            case "Double": {
                soundName = "rise.click.double";
                break;
            }

            case "Alan": {
                soundName = "rise.click.alan";
                break;
            }
        }

        SoundUtil.playSound(soundName, volume.getValue().floatValue(), RandomUtils.nextFloat(1.0F, 1 + variation.getValue().floatValue() / 100f));
    };
}