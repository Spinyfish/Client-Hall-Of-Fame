package club.mineman.antigamingchair.util.dummy;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;

import java.util.UUID;

public class DummyPlayer extends EntityPlayer {
    public DummyPlayer(final Entity entity, final String name) {
        super(MinecraftServer.getServer(), (WorldServer) entity.getWorld(), new GameProfile(UUID.randomUUID(), name), new DummyPlayerInteractManager(entity.getWorld()));
    }
}
