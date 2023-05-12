package markgg.modules.ghost;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Random;

import org.lwjgl.input.Mouse;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.util.Timer;
import net.minecraft.client.settings.KeyBinding;

public class AutoClicker extends Module {

	public NumberSetting speed = new NumberSetting("CPS", this, 12, 1, 24, 1);
	public NumberSetting randAmount = new NumberSetting("Randomness", this, 3, 1, 7, 1);
	public ModeSetting clickingMode = new ModeSetting("Mode", this, "Left", "Left", "Right", "Both");
	public BooleanSetting randomCPS = new BooleanSetting("Randomize", this, true);
	
	public AutoClicker() {
		super("AutoClicker", "Clicks for you" ,0, Category.GHOST);
		addSettings(clickingMode, speed, randAmount, randomCPS);
	}
	
	public Timer timer = new Timer();
	
	public void onEvent(Event e){
		Random rand = new Random();
		int currentSpeed = (int) speed.getValue();
		int newSpeed;

		if (randomCPS.isEnabled()) {
		    int randomNum = rand.nextInt(5);
		    newSpeed = (int) (currentSpeed + randomNum - randAmount.getValue());
		} else {
		    newSpeed = currentSpeed;
		}

		int maxSpeed = (int) speed.getMaximum();
		if (newSpeed > maxSpeed) {
		    newSpeed = maxSpeed;
		} else if (newSpeed < 0) {
		    newSpeed = 0;
		}
		
		newSpeed = newSpeed;
		
		if(e instanceof EventMotion) {
			if(timer.hasTimeElapsed((long) (1000.0D / newSpeed), true) && mc.currentScreen == null){
				try {
					click();
				} catch (AWTException e1) {
					e1.printStackTrace();
				}
			}
		}
	
	}
	
	private void click() throws AWTException {
		Robot bot = new Robot();
		switch (clickingMode.getMode()) {
		case "Right":
			bot.mousePress(InputEvent.BUTTON3_MASK);
			bot.mouseRelease(InputEvent.BUTTON3_MASK);
			break;
		case "Left":
			bot.mousePress(InputEvent.BUTTON1_MASK);
			bot.mouseRelease(InputEvent.BUTTON1_MASK);
			break;
		case "Both":
			bot.mousePress(InputEvent.BUTTON1_MASK);
			bot.mouseRelease(InputEvent.BUTTON1_MASK);
			bot.mousePress(InputEvent.BUTTON3_MASK);
			bot.mouseRelease(InputEvent.BUTTON3_MASK);
			break;
		}
	}

}
