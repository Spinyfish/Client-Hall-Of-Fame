package me.eldodebug.soar.management.events.impl;

import me.eldodebug.soar.management.events.Event;

public class EventText extends Event{

	private String text;
	private String outputText;
	
	public EventText(String text) {
		this.text = text;
		this.outputText = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getOutputText() {
		return this.outputText;
	}
	
	public String replace(String src, String target) {
		this.outputText = text.replace(src, target);
		return this.outputText;
	}
}
