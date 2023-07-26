package com.craftworks.pearclient.event.impl;

import com.craftworks.pearclient.event.Event;

public class KeyEvent extends Event {
	private int key;
	
	public KeyEvent(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}

}
