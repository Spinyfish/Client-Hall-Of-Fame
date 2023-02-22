package me.tecnio.backend.manager.impl.protection;

import me.tecnio.backend.manager.Manager;
import me.tecnio.backend.network.Client;
import packet.Packet;
import packet.impl.client.protection.lllIIllIlIIlIlllIllIlIIIIIlIlIIl;
import packet.impl.server.protection.lIllIIlllIIIIlIllIIIIllIlllllIll;
import util.simpleobjects.TargetObject;

import java.util.List;
import java.util.stream.Collectors;

public class ProtectionManager extends Manager {

    @Override
    public void packet(Packet<?> packet, Client client) {
        if (packet instanceof lllIIllIlIIlIlllIllIlIIIIIlIlIIl) {
            lllIIllIlIIlIlllIllIlIIIIIlIlIIl targetUpdate = ((lllIIllIlIIlIlllIllIlIIIIIlIlIIl) packet);

            List<TargetObject> targets = targetUpdate.getLIlllIIllIIIlIIIIllIIIlIlI().stream()
                    .filter(entity -> entity.lllIIlIIllIlIIlIlllIllIIIIlIlIlI != targetUpdate.getLllIIllIlIlIlIlIlllIlIIIIIlIlIIl())
                    .filter(entity -> {
                        if (entity.llllIlllIIIlIIllIlIIllIIIIlIlIlI && !targetUpdate.isLlllIlIlIlIIlllIlIIIIllIIIlIlIIl()) {
                            return false;
                        }

                        if (entity.llllIlIIllIIIlllIIIlIIllIIlIlIlI && targetUpdate.isLIIIllIIIlIlIIlllIlIlIlIIlllIlIl()) {
                            return false;
                        }

                        if (entity.lllIllIIlIllIlIIllIIIlllIIIlIIlI && targetUpdate.isLIIlllIlIlIlIIllIIIlIlIIIlllIlIl()) {
                            return false;
                        }

                        return true;
                    })
                    .collect(Collectors.toList());

            client.getCommunication().write(new lIllIIlllIIIIlIllIIIIllIlllllIll(targets));
        }
    }
}
