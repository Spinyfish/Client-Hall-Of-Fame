package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class S38PacketPlayerListItem implements Packet
{
    private S38PacketPlayerListItem.Action action;
    private final List players = Lists.newArrayList();

    public S38PacketPlayerListItem() {}

    public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, EntityPlayerMP ... players)
    {
        this.action = actionIn;
        EntityPlayerMP[] var3 = players;
        int var4 = players.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            EntityPlayerMP var6 = var3[var5];
            this.players.add(new S38PacketPlayerListItem.AddPlayerData(var6.getGameProfile(), var6.ping, var6.theItemInWorldManager.getGameType(), var6.getTabListDisplayName()));
        }
    }

    public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, Iterable players)
    {
        this.action = actionIn;
        Iterator var3 = players.iterator();

        while (var3.hasNext())
        {
            EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
            this.players.add(new S38PacketPlayerListItem.AddPlayerData(var4.getGameProfile(), var4.ping, var4.theItemInWorldManager.getGameType(), var4.getTabListDisplayName()));
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.action = (S38PacketPlayerListItem.Action)buf.readEnumValue(S38PacketPlayerListItem.Action.class);
        int var2 = buf.readVarIntFromBuffer();

        for (int var3 = 0; var3 < var2; ++var3)
        {
            GameProfile var4 = null;
            int var5 = 0;
            WorldSettings.GameType var6 = null;
            IChatComponent var7 = null;

            switch (S38PacketPlayerListItem.SwitchAction.LOOKUP[this.action.ordinal()])
            {
                case 1:
                    var4 = new GameProfile(buf.readUuid(), buf.readStringFromBuffer(16));
                    int var8 = buf.readVarIntFromBuffer();

                    for (int var9 = 0; var9 < var8; ++var9)
                    {
                        String var10 = buf.readStringFromBuffer(32767);
                        String var11 = buf.readStringFromBuffer(32767);

                        if (buf.readBoolean())
                        {
                            var4.getProperties().put(var10, new Property(var10, var11, buf.readStringFromBuffer(32767)));
                        }
                        else
                        {
                            var4.getProperties().put(var10, new Property(var10, var11));
                        }
                    }

                    var6 = WorldSettings.GameType.getByID(buf.readVarIntFromBuffer());
                    var5 = buf.readVarIntFromBuffer();

                    if (buf.readBoolean())
                    {
                        var7 = buf.readChatComponent();
                    }

                    break;

                case 2:
                    var4 = new GameProfile(buf.readUuid(), (String)null);
                    var6 = WorldSettings.GameType.getByID(buf.readVarIntFromBuffer());
                    break;

                case 3:
                    var4 = new GameProfile(buf.readUuid(), (String)null);
                    var5 = buf.readVarIntFromBuffer();
                    break;

                case 4:
                    var4 = new GameProfile(buf.readUuid(), (String)null);

                    if (buf.readBoolean())
                    {
                        var7 = buf.readChatComponent();
                    }

                    break;

                case 5:
                    var4 = new GameProfile(buf.readUuid(), (String)null);
            }

            this.players.add(new S38PacketPlayerListItem.AddPlayerData(var4, var5, var6, var7));
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.action);
        buf.writeVarIntToBuffer(this.players.size());
        Iterator var2 = this.players.iterator();

        while (var2.hasNext())
        {
            S38PacketPlayerListItem.AddPlayerData var3 = (S38PacketPlayerListItem.AddPlayerData)var2.next();

            switch (S38PacketPlayerListItem.SwitchAction.LOOKUP[this.action.ordinal()])
            {
                case 1:
                    buf.writeUuid(var3.getProfile().getId());
                    buf.writeString(var3.getProfile().getName());
                    buf.writeVarIntToBuffer(var3.getProfile().getProperties().size());
                    Iterator var4 = var3.getProfile().getProperties().values().iterator();

                    while (var4.hasNext())
                    {
                        Property var5 = (Property)var4.next();
                        buf.writeString(var5.getName());
                        buf.writeString(var5.getValue());

                        if (var5.hasSignature())
                        {
                            buf.writeBoolean(true);
                            buf.writeString(var5.getSignature());
                        }
                        else
                        {
                            buf.writeBoolean(false);
                        }
                    }

                    buf.writeVarIntToBuffer(var3.getGameMode().getID());
                    buf.writeVarIntToBuffer(var3.getPing());

                    if (var3.getDisplayName() == null)
                    {
                        buf.writeBoolean(false);
                    }
                    else
                    {
                        buf.writeBoolean(true);
                        buf.writeChatComponent(var3.getDisplayName());
                    }

                    break;

                case 2:
                    buf.writeUuid(var3.getProfile().getId());
                    buf.writeVarIntToBuffer(var3.getGameMode().getID());
                    break;

                case 3:
                    buf.writeUuid(var3.getProfile().getId());
                    buf.writeVarIntToBuffer(var3.getPing());
                    break;

                case 4:
                    buf.writeUuid(var3.getProfile().getId());

                    if (var3.getDisplayName() == null)
                    {
                        buf.writeBoolean(false);
                    }
                    else
                    {
                        buf.writeBoolean(true);
                        buf.writeChatComponent(var3.getDisplayName());
                    }

                    break;

                case 5:
                    buf.writeUuid(var3.getProfile().getId());
            }
        }
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handlePlayerListItem(this);
    }

    public List func_179767_a()
    {
        return this.players;
    }

    public S38PacketPlayerListItem.Action func_179768_b()
    {
        return this.action;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    public static enum Action
    {
        ADD_PLAYER("ADD_PLAYER", 0),
        UPDATE_GAME_MODE("UPDATE_GAME_MODE", 1),
        UPDATE_LATENCY("UPDATE_LATENCY", 2),
        UPDATE_DISPLAY_NAME("UPDATE_DISPLAY_NAME", 3),
        REMOVE_PLAYER("REMOVE_PLAYER", 4);

        private Action(String p_i45966_1_, int p_i45966_2_) {}
    }

    public class AddPlayerData
    {
        private final int ping;
        private final WorldSettings.GameType gamemode;
        private final GameProfile profile;
        private final IChatComponent displayName;

        public AddPlayerData(GameProfile profile, int pingIn, WorldSettings.GameType gamemodeIn, IChatComponent displayNameIn)
        {
            this.profile = profile;
            this.ping = pingIn;
            this.gamemode = gamemodeIn;
            this.displayName = displayNameIn;
        }

        public GameProfile getProfile()
        {
            return this.profile;
        }

        public int getPing()
        {
            return this.ping;
        }

        public WorldSettings.GameType getGameMode()
        {
            return this.gamemode;
        }

        public IChatComponent getDisplayName()
        {
            return this.displayName;
        }
    }

    static final class SwitchAction
    {
        static final int[] LOOKUP = new int[S38PacketPlayerListItem.Action.values().length];

        static
        {
            try
            {
                LOOKUP[S38PacketPlayerListItem.Action.ADD_PLAYER.ordinal()] = 1;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try
            {
                LOOKUP[S38PacketPlayerListItem.Action.UPDATE_GAME_MODE.ordinal()] = 2;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                LOOKUP[S38PacketPlayerListItem.Action.UPDATE_LATENCY.ordinal()] = 3;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                LOOKUP[S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME.ordinal()] = 4;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                LOOKUP[S38PacketPlayerListItem.Action.REMOVE_PLAYER.ordinal()] = 5;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
