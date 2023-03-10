/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.MathUtils;
import lodomir.dev.utils.math.TimeUtils;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class Spammer
extends Module {
    public static String text = "get november get good! discord.io/november";
    public NumberSetting delay = new NumberSetting("Delay", 0.0, 5000.0, 1000.0, 50.0);
    public BooleanSetting random = new BooleanSetting("Random", false);
    public NumberSetting length = new NumberSetting("Random length", 1.0, 16.0, 8.0, 1.0);
    public TimeUtils timer = new TimeUtils();

    public Spammer() {
        super("Spammer", 0, Category.OTHER);
        this.addSetting(this.delay);
        this.addSetting(this.random);
        this.addSetting(this.length);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        StringBuilder builder = new StringBuilder();
        String message = text;
        if (!this.random.isEnabled()) {
            this.length.setVisible(false);
        } else {
            this.length.setVisible(true);
        }
        if (this.timer.hasReached((long)this.delay.getValueFloat())) {
            if (!this.random.isEnabled()) {
                for (int i = 0; i < message.length(); ++i) {
                    builder.append(message.charAt(i));
                }
            } else if (this.random.isEnabled()) {
                builder.append(message).append(" ");
                int i = 0;
                while ((double)i < this.length.getValue()) {
                    String alphabet = "qwertyuiopasdfghjklzxcvbnm1234567890";
                    builder.append("qwertyuiopasdfghjklzxcvbnm1234567890".charAt((int)MathUtils.getRandom(0.0, "qwertyuiopasdfghjklzxcvbnm1234567890".length())));
                    ++i;
                }
            }
            this.sendPacket(new C01PacketChatMessage(builder.toString()));
            this.timer.reset();
        }
    }
}

