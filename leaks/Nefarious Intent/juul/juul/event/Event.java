package juul.event;

import juul.Juul;

public class Event<T> {

	public boolean cancelled;
	public Type type = Type.PRE;
	public Direction direction = Direction.SEND;
	
	public enum Type {
		PRE,
		POST
	}
	
	public enum Direction {
		SEND,
		RECEIVE
	}
	
	public boolean isPre() {
		return type == Type.PRE;
	}
	
	public boolean isPost() {
		return type == Type.POST;
	}
	
	public boolean isSending() {
		return direction == Direction.SEND;
	}
	
	public boolean isReceiving() {
		return direction == Direction.RECEIVE;
	}
	
	public T pre() {
		type = Type.PRE;
		return (T) this;
	}
	
	public T post(){
		type = Type.POST;
		return (T)this;
	}
	
	public T send(){
		direction = Direction.SEND;
		return (T)this;
	}
	
	public T receive(){
		direction = Direction.RECEIVE;
		return (T)this;
	}
	
	public T fire(){
		if(Juul.INSTANCE != null)
			Juul.INSTANCE.onEvent(this);
		
		//System.out.println(this);
		
		return (T)this;
	}
	
}
