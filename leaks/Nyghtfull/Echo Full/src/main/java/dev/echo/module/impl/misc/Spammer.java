package dev.echo.module.impl.misc;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.MultipleBoolSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.module.settings.impl.StringSetting;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.time.TimerUtil;

public final class Spammer extends Module {

    private final StringSetting text = new StringSetting("Text");
    private final NumberSetting delay = new NumberSetting("Delay", 100, 1000, 100, 1);
    private final MultipleBoolSetting settings = new MultipleBoolSetting("Settings",
            new BooleanSetting("AntiSpam", false),
            new BooleanSetting("Bypass", false));
    private final TimerUtil delayTimer = new TimerUtil();

    @Link
    public Listener<MotionEvent> motionEventListener = event -> {
        String spammerText = text.getString();

        if (spammerText != null && delayTimer.hasTimeElapsed(settings.getSetting("Bypass").isEnabled() ? 2000 : delay.getValue().longValue())) {

            if (settings.getSetting("AntiSpam").isEnabled()) {
                spammerText += " " + MathUtils.getRandomInRange(10, 100000);
            }

            mc.thePlayer.sendChatMessage(spammerText);
            delayTimer.reset();
        }
    };

    public Spammer() {
        super("Spammer", Category.MISC, "Spams in chat");
        this.addSettings(text, delay, settings);
    }

}
