package niggerlib.mc.protocol.data.status.handler;

import niggerlib.packetlib.Session;

public interface ServerPingTimeHandler {
    public void handle(Session session, long pingTime);
}
