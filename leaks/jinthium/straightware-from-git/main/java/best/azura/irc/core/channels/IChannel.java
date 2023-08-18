package best.azura.irc.core.channels;

public interface IChannel {

    int getId();

    String getName();

    String getDescription();

    boolean isLocked();

}
