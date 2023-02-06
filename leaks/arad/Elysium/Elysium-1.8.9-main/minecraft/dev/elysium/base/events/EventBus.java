package dev.elysium.base.events;

import java.lang.reflect.Method;
import java.util.HashMap;

import dev.elysium.base.events.types.Event;
import dev.elysium.base.events.types.RegisteredListener;

public class EventBus {

	/*
	 * 
	 * EventBus by Body Alhoha
	 * 
	 */
	
	
	private static HashMap<Object, RegisteredListener> objects = new HashMap<Object, RegisteredListener>();
	
	/*
	 * Register an object to the POST event.
	 * @param Object to register
	 */
	public static void register(Object obj) {
		synchronized (objects) {
			objects.put(obj, new RegisteredListener(obj));
		}
		
	}
	
	/*
	 * Unregister an object to the POST event.
	 * @param Object to unregister
	 */
	public static void unregister(Object obj) {
		synchronized (objects) {
			objects.remove(obj);
		}
	
	}
	/*
	 * Post an event to the Event Bus
	 * @param Event to post
	 */
	public static void post(Event event) {
		if(event == null)return; //Check if the event is null
		synchronized (objects) {
			
			for(int i = 0; i < objects.size(); i++) {
			
				RegisteredListener listener = (RegisteredListener) objects.values().toArray()[i];
				Object obj = listener.object;
				
				Class current = obj.getClass();
				
				for(Method meth : listener.methods) {
					 //System.out.println("ok " + meth.getName() + " objects : " + objects.size() + " index  " + i);
					if(meth.getParameterTypes()[0] != event.getClass())continue;
					try {
						meth.invoke(obj, event);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
}