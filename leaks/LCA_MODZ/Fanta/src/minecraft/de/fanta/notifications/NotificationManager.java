package de.fanta.notifications;

import java.util.ArrayList;
import java.util.List;



public class NotificationManager {

	private static List<Notification> notis = new ArrayList<Notification>();
	private static Notification current = null;
	
	public NotificationManager() {
	}
	
	public static void addNotificationToQueue(Notification noti) {
		notis.add(noti);
	}
	
	public static void removeFirst() {
		if(notis.isEmpty()) return;
		notis.remove(0);
		current = null;
	}
	
	private static void update() {
		if(!notis.isEmpty()) {
			if(current == null) {
				current = notis.get(0);
			}
			if(current != null && !current.isShowing() && !current.isStarted()) {
				current.setShowing(true);
				current.setStarted(true);
			}
			if(current != null && !current.isShowing() && current.isStarted()) {
				removeFirst();
			}
		}
	}
	
	public static void render() {
		update();
		if(!notis.isEmpty() && getCurrent() != null) getCurrent().draw();
	}
	
	public static Notification getCurrent() {
		return current;
	}
	
}
