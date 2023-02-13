package juul.inject.wrappers;

import juul.Juul;
import juul.inject.Wrapper;

public class MovingObjectPositionWrapper extends Wrapper {

	public MovingObjectPositionWrapper(Object instance) {
		super("net.minecraft.util.MovingObjectPosition", instance);
	}
	
	public Object entityHit() { return getField("entityHit"); }
	
}
