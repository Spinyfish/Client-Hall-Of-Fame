package de.verschwiegener.atero.audio;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.verschwiegener.atero.Management;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class Streamer {
    
    private final ScheduledExecutorService EXECUTOR_SERVICE;
    private final BasicPlayer basicPlayer;
    private double currentVolume;

    public Streamer() {
	basicPlayer = new BasicPlayer();
	setVolume(0.1D);
	EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
	startUpdateTask();
    }
    
    private void startUpdateTask() {
	System.err.println("Schedule");
	EXECUTOR_SERVICE.scheduleAtFixedRate(this::updateStreams, 5L, 20L, TimeUnit.SECONDS);
    }

    public float getVolume() {
	return (float) currentVolume;
    }

    public boolean isPlaying() {
	return basicPlayer.getStatus() == 0;
    }

    public synchronized void play() {
	try {
	    basicPlayer.resume();
	    basicPlayer.setGain(currentVolume);
	} catch (final BasicPlayerException e) {
	    e.printStackTrace();
	}
    }
    
    private void updateStreams() {
	Management.instance.streamManager.updateStreams();	
    }
    
    public void updateStream() {
	play(Management.instance.currentStream);
    }

    public synchronized void play(final Stream stream) {
	try {
	    basicPlayer.stop();
	    basicPlayer.open(new URL(stream.getChannelURL()).openStream());
	    basicPlayer.play();
	    basicPlayer.setGain(currentVolume);
	} catch (BasicPlayerException | java.io.IOException e) {
	    e.printStackTrace();
	    System.out.println("StreamURL: " + stream.getChannelURL());
	}
    }

    public void setVolume(final double gain) {
	try {
	    currentVolume = gain;
	    basicPlayer.setGain(gain);
	} catch (final BasicPlayerException e) {
	    e.printStackTrace();
	}
    }

    public synchronized void stop() {
	try {
	    basicPlayer.pause();
	} catch (final BasicPlayerException e) {
	    e.printStackTrace();
	}
    }
    public synchronized void resume() {
	try {
	    basicPlayer.resume();
	}catch(BasicPlayerException ex) {
	}
    }

    public void toggle() {
	try {
	    if (basicPlayer.getStatus() == 1 || basicPlayer.getStatus() == 2) {
		basicPlayer.resume();
	    } else if (basicPlayer.getStatus() == 0) {
		basicPlayer.pause();
	    }
	} catch (final BasicPlayerException e) {
	    e.printStackTrace();
	}
    }
}
