package gay.sukumi.irc.packet.packet.impl.profile;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import gay.sukumi.irc.profile.UserProfile;
import io.netty.buffer.ByteBuf;

@PacketId(4)
public class SProfilePacket extends Packet {

    private UserProfile profile;
    private UserProfile oldProfile;
    private Action action;

    public SProfilePacket() {
    }

    public SProfilePacket(Action action, UserProfile oldProfile, UserProfile profile) {
        this.action = action;
        this.oldProfile = oldProfile;
        this.profile = profile;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        action = Action.valueOf(readString(byteBuf));
        profile = readUserProfile(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(byteBuf, action.name());
        writeUserProfile(byteBuf, profile);
    }

    public UserProfile getProfile() {
        return profile;
    }

    public UserProfile getOldProfile() {
        return oldProfile;
    }

    public Action getAction() {
        return action;
    }

    public enum Action {
        ADD,
        REMOVE,
        CHANGE_NAME,
        CHANGE_SERVER
    }

}
