package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.value.value.StringValue;
import me.kansio.client.utils.math.MathUtil;
import me.kansio.client.utils.math.Stopwatch;

@ModuleData(
        name = "Spammer",
        description = "Spams a message with a delay",
        category = ModuleCategory.PLAYER
)
public class Spammer extends Module {

    private final NumberValue delay = new NumberValue<>("Delay", this, 3000, 0, 600000, 0.1);

    private final StringValue message = new StringValue("Text", this, "sex");

    private Stopwatch stopwatch = new Stopwatch();

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (stopwatch.timeElapsed(delay.getValue().longValue() + MathUtil.getRandomInRange(1000, 3000))) {
            mc.thePlayer.sendChatMessage(message.getValue());
            stopwatch.resetTime();
        }
    }
}
