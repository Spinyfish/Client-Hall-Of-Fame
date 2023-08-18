package me.jinthium.straight.impl.modules.ghost.autoclicker;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.game.PlayerAttackEvent;
import me.jinthium.straight.impl.event.game.TickEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.ghost.AutoClicker;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.util.MovingObjectPosition;

@ModeInfo(name = "Normal", parent = AutoClicker.class)
public class NormalClicker extends ModuleMode<AutoClicker> {

    private final NumberSetting minCPS = new NumberSetting("Min CPS", 8, 1, 20,  1);
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", 14, 1, 20,  1);
    private final BooleanSetting rightClick = new BooleanSetting("Right Click", false);
    private final BooleanSetting leftClick = new BooleanSetting("Left Click", true);
    private final BooleanSetting hitSelect = new BooleanSetting("Hit Select", false);
    private final TimerUtil clickStopWatch = new TimerUtil();
    private int ticksDown, attackTicks;
    private long nextSwing;

    public NormalClicker(){

        this.registerSettings(minCPS, maxCPS, rightClick, leftClick, hitSelect);
    }

    @Callback
    final EventCallback<TickEvent> tickEventEventCallback = event -> {
            this.attackTicks++;

            if (clickStopWatch.hasTimeElapsed(this.nextSwing, true) && (!hitSelect.isEnabled() || ((hitSelect.isEnabled() && attackTicks >= 10) ||
                    (mc.thePlayer.hurtTime > 0 && clickStopWatch.hasTimeElapsed(this.nextSwing, true)))) && mc.currentScreen == null) {
                final long clicks = (long) (Math.round(MathUtils.getRandomInRange(this.minCPS.getValue().intValue(), this.maxCPS.getValue().intValue())) * 1.5);

                if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                    ticksDown++;
                } else {
                    ticksDown = 0;
                }

                this.nextSwing = 1000 / clicks;

                if (rightClick.isEnabled() && mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
                    PlayerUtil.sendClick(1, true);

                    if (Math.random() > 0.9) {
                        PlayerUtil.sendClick(1, true);
                    }
                }

                if (leftClick.isEnabled() && ticksDown > 1 && (Math.sin(nextSwing) + 1 > Math.random() || Math.random() > 0.25 || clickStopWatch.hasTimeElapsed(4 * 50))
                        && !mc.gameSettings.keyBindUseItem.isKeyDown() && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)) {
                    PlayerUtil.sendClick(0, true);
                }
            }
    };

    @Callback
    final EventCallback<PlayerAttackEvent> playerAttackEventEventCallback = event -> {
        attackTicks = 0;
    };
}
