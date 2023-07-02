package me.eldodebug.soar.management.events.impl;

import me.eldodebug.soar.management.events.Event;

public class EventPlaySound extends Event {

	private String soundName;
	private float volume;
	private float pitch;
	private float originalVolume;
	private float originalPitch;
	
	public EventPlaySound(String soundName, float volume, float pitch, float originalVolume, float originalPitch) {
		this.soundName = soundName;
		this.volume = volume;
		this.pitch = pitch;
		this.originalVolume = originalVolume;
		this.originalPitch = originalPitch;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getOriginalVolume() {
		return originalVolume;
	}

	public void setOriginalVolume(float originalVolume) {
		this.originalVolume = originalVolume;
	}

	public float getOriginalPitch() {
		return originalPitch;
	}

	public void setOriginalPitch(float originalPitch) {
		this.originalPitch = originalPitch;
	}

	public String getSoundName() {
		return soundName;
	}
}
