/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_8to1_9.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.storage.EntityTracker;
import de.gerrygames.viarewind.utils.PacketUtil;
import de.gerrygames.viarewind.utils.Tickable;

public class Levitation
extends StoredObject
implements Tickable {
    private int amplifier;
    private volatile boolean active = false;

    public Levitation(UserConnection user) {
        super(user);
    }

    @Override
    public void tick() {
        if (!this.active) {
            return;
        }
        int vY = (this.amplifier + 1) * 360;
        PacketWrapper packet = PacketWrapper.create(18, null, this.getUser());
        packet.write(Type.VAR_INT, this.getUser().get(EntityTracker.class).getPlayerId());
        packet.write(Type.SHORT, (short)0);
        packet.write(Type.SHORT, (short)vY);
        packet.write(Type.SHORT, (short)0);
        PacketUtil.sendPacket(packet, Protocol1_8TO1_9.class);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }
}

