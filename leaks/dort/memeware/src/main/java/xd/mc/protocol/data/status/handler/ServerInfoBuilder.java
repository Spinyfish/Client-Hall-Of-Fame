package niggerlib.mc.protocol.data.status.handler;

import niggerlib.mc.protocol.data.status.ServerStatusInfo;
import niggerlib.packetlib.Session;

public interface ServerInfoBuilder {
    public ServerStatusInfo buildInfo(Session session);
}
