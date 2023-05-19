package club.marsh.bloom.impl.events;

public class Render2DEvent extends Event {
	public float partialticks;
	public Render2DEvent(float partialticks) {
		this.partialticks = partialticks;
	}
}
