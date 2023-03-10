/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.CommandBlockStorage;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import java.util.Optional;

public class CommandBlockProvider
implements Provider {
    public void addOrUpdateBlock(UserConnection user, Position position, CompoundTag tag) throws Exception {
        this.checkPermission(user);
        if (this.isEnabled()) {
            this.getStorage(user).addOrUpdateBlock(position, tag);
        }
    }

    public Optional<CompoundTag> get(UserConnection user, Position position) throws Exception {
        this.checkPermission(user);
        if (this.isEnabled()) {
            return this.getStorage(user).getCommandBlock(position);
        }
        return Optional.empty();
    }

    public void unloadChunk(UserConnection user, int x, int z) throws Exception {
        this.checkPermission(user);
        if (this.isEnabled()) {
            this.getStorage(user).unloadChunk(x, z);
        }
    }

    private CommandBlockStorage getStorage(UserConnection connection) {
        return connection.get(CommandBlockStorage.class);
    }

    public void sendPermission(UserConnection user) throws Exception {
        if (!this.isEnabled()) {
            return;
        }
        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.ENTITY_STATUS, null, user);
        EntityTracker1_9 tracker = (EntityTracker1_9)user.getEntityTracker(Protocol1_9To1_8.class);
        wrapper.write(Type.INT, tracker.getProvidedEntityId());
        wrapper.write(Type.BYTE, (byte)26);
        wrapper.scheduleSend(Protocol1_9To1_8.class);
        user.get(CommandBlockStorage.class).setPermissions(true);
    }

    private void checkPermission(UserConnection user) throws Exception {
        if (!this.isEnabled()) {
            return;
        }
        CommandBlockStorage storage = this.getStorage(user);
        if (!storage.isPermissions()) {
            this.sendPermission(user);
        }
    }

    public boolean isEnabled() {
        return true;
    }

    public void unloadChunks(UserConnection userConnection) {
        if (this.isEnabled()) {
            this.getStorage(userConnection).unloadChunks();
        }
    }
}

