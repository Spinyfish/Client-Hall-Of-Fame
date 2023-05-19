package club.marsh.bloom.impl.events;

public class Render3DEvent extends Event {
	public float partialticks;
	public boolean post;
	public Render3DEvent(float partialticks, boolean post) {
		this.partialticks = partialticks;
		this.post = post;
	}
}
