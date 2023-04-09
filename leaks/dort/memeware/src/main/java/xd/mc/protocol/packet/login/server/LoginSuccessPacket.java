package niggerlib.mc.protocol.packet.login.server;

import niggerlib.mc.auth.data.GameProfile;
import niggerlib.mc.protocol.packet.MinecraftPacket;
import niggerlib.packetlib.io.NetInput;
import niggerlib.packetlib.io.NetOutput;

import java.io.IOException;

public class LoginSuccessPacket extends MinecraftPacket {
    private GameProfile profile;

    @SuppressWarnings("unused")
    private LoginSuccessPacket() {
    }

    public LoginSuccessPacket(GameProfile profile) {
        this.profile = profile;
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.profile = new GameProfile(in.readString(), in.readString());
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeString(this.profile.getIdAsString());
        out.writeString(this.profile.getName());
    }

    @Override
    public boolean isPriority() {
        return true;
    }
}
