package dev.echo.module.impl.combat;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.listener.event.impl.player.AttackEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.player.MovementUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.util.MovingObjectPosition;


public class AutoClicker extends Module {
    public static final NumberSetting cps2 = new NumberSetting("Min Cps", 8, 20, 1, 1);
    public static final NumberSetting cps3 = new NumberSetting("Max Cps", 8, 20, 1, 1);
    public static final BooleanSetting rightclick = new BooleanSetting("Rick Click", false);
    public static final BooleanSetting leftclick = new BooleanSetting("left CLick", false);

    private final TimerUtil clickStopWatch = new TimerUtil();
    private int ticksDown, attackTicks;
    private long nextSwing;



    public AutoClicker() {
        super("Clicker", Category.COMBAT, "Auto Clicks");
        addSettings(cps2,cps3,leftclick,rightclick);
    }

    @Link
    public Listener<AttackEvent> attackEventListener = e -> {
        this.attackTicks = 0;
    };



    @Link
    private final Listener<TickEvent> tickEventListener = (event) -> {
        this.setSuffix(cps2.getValue().floatValue() + " - " + cps3.getValue().floatValue());
        this.attackTicks++;

            if (clickStopWatch != null && mc != null) {
                if (clickStopWatch.hasTimeElapsed(this.nextSwing) ||
                        (mc.thePlayer != null && mc.thePlayer.hurtTime > 0 && clickStopWatch.hasTimeElapsed(this.nextSwing)) &&
                                mc.currentScreen == null) {
                final long clicks = (long) (Math.round(MathUtils.getRandomInRange(this.cps2.getValue().intValue(), this.cps3.getValue().intValue())) * 1.5);

                if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                    ticksDown++;
                } else {
                    ticksDown = 0;
                }

                this.nextSwing = 1000 / clicks;


                if (rightclick.isEnabled() && mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
                    MovementUtils.sendClick(1, true);

                    if (Math.random() > 0.9) {
                        MovementUtils.sendClick(1, true);
                    }
                }

                if (leftclick.isEnabled() && ticksDown > 1 && (Math.sin(nextSwing) + 1 > Math.random() || Math.random() > 0.25 || clickStopWatch.hasTimeElapsed(4 * 50)) && !mc.gameSettings.keyBindUseItem.isKeyDown() && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)) {
                    MovementUtils.sendClick(0, true);
                }

                this.clickStopWatch.reset();
            }
        }
    };


}
