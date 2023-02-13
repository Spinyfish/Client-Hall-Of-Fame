package juul.module.combat;

import juul.event.Event;
import juul.event.EventClickMouse;
import juul.event.EventHUD;
import juul.event.EventTick;
import juul.inject.wrappers.TimerWrapper;
import juul.module.Module;
import juul.module.settings.BooleanSetting;
import juul.module.settings.NumberSetting;
import juul.util.objects.Timer;
import juul.util.value.AdaptiveValue;
import juul.util.value.Random;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;
import java.util.Queue;

public class AutoClicker extends Module {

	private final Queue<Long> leftClicks = new LinkedList<>();

	public BooleanSetting debug;

	public NumberSetting condition, increment;

    private float flClicks = 0f;
    private float flDropChance = 0f;
    private float flSpikeChance = 0f;

    private final AdaptiveValue adaptiveClicks = new AdaptiveValue(12);
    private final AdaptiveValue adaptiveHits = new AdaptiveValue(0);

    private final Timer tOperations = new Timer();
    private final Timer tClicks = new Timer();

    @Override
    public void onEnable() {
        adaptiveClicks.reset();
        adaptiveHits.reset();
    }

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks when you hold down the attack button", Keyboard.KEY_Y, Category.COMBAT);
		addSetting(debug = new BooleanSetting("Draw Debug", "info", false));
		addSetting(condition = new NumberSetting("Condition", "sens", 5, 1, 10, 1));
		addSetting(increment = new NumberSetting("Increment", "sens", 1, 1, 5, 1));
    }

    public void onEvent(Event e) {
        if (e instanceof EventTick && mc.getGameSettings().keyBindAttack.getIsKeyPressed()) {

            if (mc.player() == null || constants.world() == null) return;


            if (thePlayer.isUsingItem()) return;

			tOperations.reset();

			TimerWrapper timer = new TimerWrapper(constants.getTimer());

			flDropChance = Random.flRandom(0, 100);
			flSpikeChance = Random.flRandom(0, 100);

			if (adaptiveHits.getValue() > condition.getValue()) {
				adaptiveClicks.decreaseValue(increment.getValueFloat());
				adaptiveHits.reset();

				if (adaptiveClicks.getValue() < 1) adaptiveClicks.setValue(1);
			}

			if (adaptiveHits.getValue() < -condition.getValue()) {
				adaptiveClicks.increaseValue(increment.getValueFloat());
				adaptiveHits.reset();

				if (adaptiveClicks.getValue() < 20) adaptiveClicks.setValue(20);
			}

			flClicks = adaptiveClicks.getRandomizedValue(1);

			if (flDropChance <= 25 && flDropChance != 0)
				flClicks -= Random.flRandom(1, 2);

			if (flSpikeChance <= 10 && flSpikeChance != 0)
				flClicks += Random.flRandom(1, 2);

			final float tickOffset = 20 * timer.getTimerSpeed();

			final long sleep = (long) ((1000 / flClicks) - tOperations.getTime() - tickOffset);

			if (tClicks.hasTimeElapsed(sleep, true))
				mc.clickMouse();
        }
		else if (e instanceof EventClickMouse)
		{
			leftClicks.add(System.currentTimeMillis() + 1000L);

			EventClickMouse ev = (EventClickMouse) e;



			if (ev.contact && ev.entity != null)
				adaptiveHits.increaseValue(increment.getValueFloat());
			else if (!ev.contact && mc.objectMouseOver() != null && thePlayerSP.getHurtTime() > 0 && thePlayerSP.getHurtTime() < 2)
				adaptiveHits.decreaseValue(increment.getValueFloat());

		}
		else if(e instanceof EventHUD && debug.enabled)
		{
			// later make this draggable add blur/bloom bg ect
			constants.drawString(getLeftCPS() + " cps", 20, 20, 0, 0xffdddddd);
			constants.drawString(adaptiveClicks.getValue() + " adc", 20, 30, 0, 0xffdddddd);
			constants.drawString(adaptiveHits.getValue() + " adh", 20, 40, 0, 0xffdddddd);
		}
    }
	protected int getLeftCPS()
	{
		long time = System.currentTimeMillis();

		while (!leftClicks.isEmpty() && leftClicks.peek() < time) leftClicks.remove();

		return leftClicks.size();
	}

}
