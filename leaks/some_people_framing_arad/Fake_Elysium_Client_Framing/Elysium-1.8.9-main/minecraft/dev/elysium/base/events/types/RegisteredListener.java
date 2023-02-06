package dev.elysium.base.events.types;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RegisteredListener {

	public Object object;
	public List<Method> methods = new ArrayList<Method>();
	
	public RegisteredListener(Object object) {
		this.object = object;
		Class current = object.getClass();
		while(!current.getName().equals("java.lang.Object")) {
			
			for(Method method : current.getDeclaredMethods()) { //foreach method in the class of that object
				if(method.getParameterTypes().length != 1)continue; //check if there is one parameter
				if(!method.isAnnotationPresent(EventTarget.class))continue; //check if there is the EventTarget annotation
				methods.add(method); //check if the method is listening for another event
			}
			
			current = current.getSuperclass();
		}
		
	}
	
}
