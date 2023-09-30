package de.verschwiegener.atero.audio;

import java.util.ArrayList;
import java.util.function.Predicate;

import de.verschwiegener.atero.audio.ilovemusik.IloveMusikStreamLoader;
import de.verschwiegener.atero.command.Command;

public class StreamManager {

    private ArrayList<Stream> streams = new ArrayList<Stream>();
    private ArrayList<StreamLoader> streamloader = new ArrayList<StreamLoader>();

    public StreamManager() {
	streamloader.add(new IloveMusikStreamLoader());
    }

    public Stream getStreamByName(final String name) {
	return streams.stream().filter(new Predicate<Stream>() {
	    @Override
	    public boolean test(Stream module) {
		return module.getChannelName().equalsIgnoreCase(name);
	    }
	}).findFirst().orElse(null);
    }
    
    public Stream getStreamByFullName(final String name) {
	return streams.stream().filter(new Predicate<Stream>() {
	    @Override
	    public boolean test(Stream module) {
		return module.getFullChannelName().equalsIgnoreCase(name);
	    }
	}).findFirst().orElse(null);
    }

    public void updateStreams() {
	for (StreamLoader streamloader : streamloader) {
	    streamloader.loadStreams();
	}
    }

    public ArrayList<Stream> getStreams() {
	return streams;
    }
}
