package juul.event;

public class EventReach extends Event<EventReach> {

	public float distance;
	
	public EventReach(float dist) {
		distance = dist;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}
	
}
