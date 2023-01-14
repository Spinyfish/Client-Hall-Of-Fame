package com.srt.settings;

public class Setting<T> {
	private String name;
	private T value;
	private boolean hidden;

	public Setting(String name, T value) {
		this.name = name;
		this.value = value;
		this.hidden = false;
	}

	public Boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
