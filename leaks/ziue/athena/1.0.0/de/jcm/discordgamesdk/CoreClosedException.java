package de.jcm.discordgamesdk;

public class CoreClosedException extends IllegalStateException
{
    public CoreClosedException() {
        super("Core is closed");
    }
}
