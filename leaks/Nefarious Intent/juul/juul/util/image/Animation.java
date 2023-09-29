package juul.util.image;

import juul.Juul;
import juul.inject.wrappers.MinecraftWrapper;
import juul.inject.wrappers.TimerWrapper;
import juul.util.Utils;

public class Animation implements Utils {

	public float position;
	public float lastPosition;
	
	public void set(float target){
		lastPosition = position = target;
	}
	
	public void tick(float target, float speed){
		lastPosition = position;
		position += (target-position)/(speed);
	}
	
	public float getPosition(){
		TimerWrapper timer = new TimerWrapper(mc.timer());
		return ((float) (position * timer.renderPartialTicks() + (lastPosition * (1.0f - timer.renderPartialTicks()))));
	}
	
}
