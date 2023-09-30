package best.azura.irc.core.channels;

import java.util.Collection;
import java.util.HashMap;

public class ChannelManager {

    private final HashMap<Integer, IChannel> REGISTRY = new HashMap<>();

    public ChannelManager() {
       // addChannel();
    }

    public IChannel getChannelById(int Id) {
        if (REGISTRY.containsKey(Id)) {
            return REGISTRY.get(Id);
        }

        return null;
    }

    public void addChannel(int id, IChannel channel) {
        if (!REGISTRY.containsKey(id)) {
            REGISTRY.put(id, channel);
        }
    }

    public Collection<IChannel> getChannels() {
        return REGISTRY.values();
    }

    public HashMap<Integer, IChannel> getREGISTRY() {
        return REGISTRY;
    }
}
