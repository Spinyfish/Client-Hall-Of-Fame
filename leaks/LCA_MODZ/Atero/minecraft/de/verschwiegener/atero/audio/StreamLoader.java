package de.verschwiegener.atero.audio;

public abstract class StreamLoader {

    private final String DEFAULT_BASE_URL;

    public StreamLoader(String DEFAULT_BASE_URL) {
	this.DEFAULT_BASE_URL = DEFAULT_BASE_URL;
    }

    public abstract void loadStreams();

    public String getDEFAULT_BASE_URL() {
	return DEFAULT_BASE_URL;
    }
}
