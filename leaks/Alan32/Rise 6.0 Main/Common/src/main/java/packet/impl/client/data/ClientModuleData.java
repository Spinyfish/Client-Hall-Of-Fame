package packet.impl.client.data;

import lombok.AllArgsConstructor;
import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ClientModuleData implements ClientPacket {
    public boolean banned;
    public List<String> modules;
    public String ip;

    @Override
    public void process(IClientPacketHandler handler) {
        handler.handle(this);
    }
}
