package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.math.MathUtil;
import me.kansio.client.utils.math.Stopwatch;
import net.minecraft.entity.player.EntityPlayer;

@ModuleData(
        name = "Auto Report",
        description = "Creates a lot of false reports on hypixel :troll:",
        category = ModuleCategory.PLAYER
)
public class AutoReport extends Module {

    private NumberValue delay = new NumberValue("Delay (ms)", this, 100, 0, 100000, 1);

    private Stopwatch timer = new Stopwatch();

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (timer.timeElapsed(delay.getValue().longValue())) {
            reportPlayer();
            timer.resetTime();
        }
    }

    private void reportPlayer() {
        EntityPlayer toReport = mc.theWorld.playerEntities.get(MathUtil.getRandomInRange(0, mc.theWorld.playerEntities.size() - 1));

        if (toReport.getName().contains("§"))
            return;

        mc.thePlayer.sendChatMessage("/report " + toReport.getName() + " bhop ka");
    }

}
