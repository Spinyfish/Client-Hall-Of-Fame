package de.jcm.discordgamesdk;

import java.util.*;
import java.util.stream.*;
import de.jcm.discordgamesdk.user.*;
import de.jcm.discordgamesdk.lobby.*;
import java.util.function.*;

public class LobbyManager
{
    private final long pointer;
    private final Core core;
    
    LobbyManager(final long pointer, final Core core) {
        this.pointer = pointer;
        this.core = core;
    }
    
    public LobbyTransaction getLobbyCreateTransaction() {
        final Object ret = this.core.execute(() -> this.getLobbyCreateTransaction(this.pointer));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (LobbyTransaction)ret;
    }
    
    public LobbyTransaction getLobbyUpdateTransaction(final long lobbyId) {
        final Object ret = this.core.execute(() -> this.getLobbyUpdateTransaction(this.pointer, lobbyId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (LobbyTransaction)ret;
    }
    
    public LobbyTransaction getLobbyUpdateTransaction(final Lobby lobby) {
        return this.getLobbyUpdateTransaction(lobby.getId());
    }
    
    public LobbyMemberTransaction getMemberUpdateTransaction(final long lobbyId, final long userId) {
        final Object ret = this.core.execute(() -> this.getMemberUpdateTransaction(this.pointer, lobbyId, userId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (LobbyMemberTransaction)ret;
    }
    
    public LobbyMemberTransaction getMemberUpdateTransaction(final Lobby lobby, final long userId) {
        return this.getMemberUpdateTransaction(lobby.getId(), userId);
    }
    
    public void createLobby(final LobbyTransaction transaction, final BiConsumer<Result, Lobby> callback) {
        this.core.execute(() -> this.createLobby(this.pointer, transaction.getPointer(), Objects.requireNonNull(callback)));
    }
    
    public void createLobby(final LobbyTransaction transaction, final Consumer<Lobby> callback) {
        this.createLobby(transaction, (result, lobby) -> {
            Core.DEFAULT_CALLBACK.accept(result);
            callback.accept(lobby);
        });
    }
    
    public void updateLobby(final long lobbyId, final LobbyTransaction transaction, final Consumer<Result> callback) {
        this.core.execute(() -> this.updateLobby(this.pointer, lobbyId, transaction.getPointer(), Objects.requireNonNull(callback)));
    }
    
    public void updateLobby(final long lobbyId, final LobbyTransaction transaction) {
        this.updateLobby(lobbyId, transaction, Core.DEFAULT_CALLBACK);
    }
    
    public void updateLobby(final Lobby lobby, final LobbyTransaction transaction, final Consumer<Result> callback) {
        this.updateLobby(lobby.getId(), transaction, callback);
    }
    
    public void updateLobby(final Lobby lobby, final LobbyTransaction transaction) {
        this.updateLobby(lobby, transaction, Core.DEFAULT_CALLBACK);
    }
    
    public void deleteLobby(final long lobbyId, final Consumer<Result> callback) {
        this.core.execute(() -> this.deleteLobby(this.pointer, lobbyId, Objects.requireNonNull(callback)));
    }
    
    public void deleteLobby(final long lobbyId) {
        this.deleteLobby(lobbyId, Core.DEFAULT_CALLBACK);
    }
    
    public void deleteLobby(final Lobby lobby, final Consumer<Result> callback) {
        this.deleteLobby(lobby.getId(), callback);
    }
    
    public void deleteLobby(final Lobby lobby) {
        this.deleteLobby(lobby, Core.DEFAULT_CALLBACK);
    }
    
    public void connectLobby(final long lobbyId, final String secret, final BiConsumer<Result, Lobby> callback) {
        if (secret.getBytes().length >= 128) {
            throw new IllegalArgumentException("max secret length is 127");
        }
        this.core.execute(() -> this.connectLobby(this.pointer, lobbyId, secret, Objects.requireNonNull(callback)));
    }
    
    public void connectLobby(final long lobbyId, final String secret, final Consumer<Lobby> callback) {
        this.connectLobby(lobbyId, secret, (result, lobby) -> {
            Core.DEFAULT_CALLBACK.accept(result);
            callback.accept(lobby);
        });
    }
    
    public void connectLobby(final Lobby lobby, final BiConsumer<Result, Lobby> callback) {
        this.connectLobby(lobby.getId(), lobby.getSecret(), callback);
    }
    
    public void connectLobbyWithActivitySecret(final String activitySecret, final BiConsumer<Result, Lobby> callback) {
        if (activitySecret.getBytes().length >= 128) {
            throw new IllegalArgumentException("max activity secret length is 127");
        }
        this.core.execute(() -> this.connectLobbyWithActivitySecret(this.pointer, activitySecret, Objects.requireNonNull(callback)));
    }
    
    public void connectLobbyWithActivitySecret(final String activitySecret, final Consumer<Lobby> callback) {
        this.connectLobbyWithActivitySecret(activitySecret, (result, lobby) -> {
            Core.DEFAULT_CALLBACK.accept(result);
            callback.accept(lobby);
        });
    }
    
    public void disconnectLobby(final long lobbyId, final Consumer<Result> callback) {
        this.core.execute(() -> this.disconnectLobby(this.pointer, lobbyId, Objects.requireNonNull(callback)));
    }
    
    public void disconnectLobby(final long lobbyId) {
        this.disconnectLobby(lobbyId, Core.DEFAULT_CALLBACK);
    }
    
    public void disconnectLobby(final Lobby lobby, final Consumer<Result> callback) {
        this.disconnectLobby(lobby.getId(), callback);
    }
    
    public void disconnectLobby(final Lobby lobby) {
        this.disconnectLobby(lobby, Core.DEFAULT_CALLBACK);
    }
    
    public Lobby getLobby(final long lobbyId) {
        final Object ret = this.core.execute(() -> this.getLobby(this.pointer, lobbyId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (Lobby)ret;
    }
    
    public String getLobbyActivitySecret(final long lobbyId) {
        final Object ret = this.core.execute(() -> this.getLobbyActivitySecret(this.pointer, lobbyId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (String)ret;
    }
    
    public String getLobbyActivitySecret(final Lobby lobby) {
        return this.getLobbyActivitySecret(lobby.getId());
    }
    
    public String getLobbyMetadataValue(final long lobbyId, final String key) {
        if (key.getBytes().length >= 256) {
            throw new IllegalArgumentException("max key length is 255");
        }
        final Object ret = this.core.execute(() -> this.getLobbyMetadataValue(this.pointer, lobbyId, key));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (String)ret;
    }
    
    public String getLobbyMetadataValue(final Lobby lobby, final String key) {
        return this.getLobbyMetadataValue(lobby.getId(), key);
    }
    
    public String getLobbyMetadataKey(final long lobbyId, final int index) {
        final Object ret = this.core.execute(() -> this.getLobbyMetadataKey(this.pointer, lobbyId, index));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (String)ret;
    }
    
    public String getLobbyMetadataKey(final Lobby lobby, final int index) {
        return this.getLobbyMetadataKey(lobby.getId(), index);
    }
    
    public int lobbyMetadataCount(final long lobbyId) {
        final Object ret = this.core.execute(() -> this.lobbyMetadataCount(this.pointer, lobbyId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (int)ret;
    }
    
    public int lobbyMetadataCount(final Lobby lobby) {
        return this.lobbyMetadataCount(lobby.getId());
    }
    
    public Map<String, String> getLobbyMetadata(final long lobbyId) {
        final int count = this.lobbyMetadataCount(lobbyId);
        final HashMap<String, String> map = new HashMap<String, String>(count);
        for (int i = 0; i < count; ++i) {
            final String key = this.getLobbyMetadataKey(lobbyId, i);
            final String value = this.getLobbyMetadataValue(lobbyId, key);
            map.put(key, value);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)map);
    }
    
    public Map<String, String> getLobbyMetadata(final Lobby lobby) {
        return this.getLobbyMetadata(lobby.getId());
    }
    
    public int memberCount(final long lobbyId) {
        final Object ret = this.core.execute(() -> this.memberCount(this.pointer, lobbyId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (int)ret;
    }
    
    public int memberCount(final Lobby lobby) {
        return this.memberCount(lobby.getId());
    }
    
    public long getMemberUserId(final long lobbyId, final int index) {
        final Object ret = this.core.execute(() -> this.getMemberUserId(this.pointer, lobbyId, index));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (long)ret;
    }
    
    public long getMemberUserId(final Lobby lobby, final int index) {
        return this.getMemberUserId(lobby.getId(), index);
    }
    
    public List<Long> getMemberUserIds(final long lobbyId) {
        final List<Long> list = IntStream.range(0, this.memberCount(lobbyId)).mapToLong(i -> this.getMemberUserId(lobbyId, i)).boxed().collect((Collector<? super Long, ?, List<Long>>)Collectors.toList());
        return Collections.unmodifiableList((List<? extends Long>)list);
    }
    
    public List<Long> getMemberUserIds(final Lobby lobby) {
        return this.getMemberUserIds(lobby.getId());
    }
    
    public DiscordUser getMemberUser(final long lobbyId, final long userId) {
        final Object ret = this.core.execute(() -> this.getMemberUser(this.pointer, lobbyId, userId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (DiscordUser)ret;
    }
    
    public DiscordUser getMemberUser(final Lobby lobby, final long userId) {
        return this.getMemberUser(lobby.getId(), userId);
    }
    
    public List<DiscordUser> getMemberUsers(final long lobbyId) {
        final List<DiscordUser> list = this.getMemberUserIds(lobbyId).stream().map(l -> this.getMemberUser(lobbyId, l)).collect((Collector<? super Object, ?, List<DiscordUser>>)Collectors.toList());
        return Collections.unmodifiableList((List<? extends DiscordUser>)list);
    }
    
    public List<DiscordUser> getMemberUsers(final Lobby lobby) {
        return this.getMemberUsers(lobby.getId());
    }
    
    public String getMemberMetadataValue(final long lobbyId, final long userId, final String key) {
        if (key.getBytes().length >= 256) {
            throw new IllegalArgumentException("max key length is 255");
        }
        final Object ret = this.core.execute(() -> this.getMemberMetadataValue(this.pointer, lobbyId, userId, key));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (String)ret;
    }
    
    public String getMemberMetadataValue(final Lobby lobby, final long userId, final String key) {
        return this.getMemberMetadataValue(lobby.getId(), userId, key);
    }
    
    public String getMemberMetadataKey(final long lobbyId, final long userId, final int index) {
        final Object ret = this.core.execute(() -> this.getMemberMetadataKey(this.pointer, lobbyId, userId, index));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (String)ret;
    }
    
    public String getMemberMetadataKey(final Lobby lobby, final long userId, final int index) {
        return this.getMemberMetadataKey(lobby.getId(), userId, index);
    }
    
    public int memberMetadataCount(final long lobbyId, final long userId) {
        final Object ret = this.core.execute(() -> this.memberMetadataCount(this.pointer, lobbyId, userId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (int)ret;
    }
    
    public int memberMetadataCount(final Lobby lobby, final long userId) {
        return this.memberMetadataCount(lobby.getId(), userId);
    }
    
    public Map<String, String> getMemberMetadata(final long lobbyId, final long userId) {
        final int count = this.memberMetadataCount(lobbyId, userId);
        final HashMap<String, String> map = new HashMap<String, String>(count);
        for (int i = 0; i < count; ++i) {
            final String key = this.getMemberMetadataKey(lobbyId, userId, i);
            final String value = this.getMemberMetadataValue(lobbyId, userId, key);
            map.put(key, value);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)map);
    }
    
    public Map<String, String> getMemberMetadata(final Lobby lobby, final long userId) {
        return this.getMemberMetadata(lobby.getId(), userId);
    }
    
    public void updateMember(final long lobbyId, final long userId, final LobbyMemberTransaction transaction, final Consumer<Result> callback) {
        this.core.execute(() -> this.updateMember(this.pointer, lobbyId, userId, transaction.getPointer(), Objects.requireNonNull(callback)));
    }
    
    public void updateMember(final long lobbyId, final long userId, final LobbyMemberTransaction transaction) {
        this.updateMember(lobbyId, userId, transaction, Core.DEFAULT_CALLBACK);
    }
    
    public void updateMember(final Lobby lobby, final long userId, final LobbyMemberTransaction transaction, final Consumer<Result> callback) {
        this.updateMember(lobby.getId(), userId, transaction, callback);
    }
    
    public void updateMember(final Lobby lobby, final long userId, final LobbyMemberTransaction transaction) {
        this.updateMember(lobby, userId, transaction, Core.DEFAULT_CALLBACK);
    }
    
    public void sendLobbyMessage(final long lobbyId, final byte[] data, final Consumer<Result> callback) {
        this.core.execute(() -> this.sendLobbyMessage(this.pointer, lobbyId, data, 0, data.length, Objects.requireNonNull(callback)));
    }
    
    public void sendLobbyMessage(final long lobbyId, final byte[] data) {
        this.sendLobbyMessage(lobbyId, data, Core.DEFAULT_CALLBACK);
    }
    
    public void sendLobbyMessage(final Lobby lobby, final byte[] data, final Consumer<Result> callback) {
        this.sendLobbyMessage(lobby.getId(), data, callback);
    }
    
    public void sendLobbyMessage(final Lobby lobby, final byte[] data) {
        this.sendLobbyMessage(lobby, data, Core.DEFAULT_CALLBACK);
    }
    
    public LobbySearchQuery getSearchQuery() {
        final Object ret = this.core.execute(() -> this.getSearchQuery(this.pointer));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (LobbySearchQuery)ret;
    }
    
    public void search(final LobbySearchQuery query, final Consumer<Result> callback) {
        this.core.execute(() -> this.search(this.pointer, query.getPointer(), Objects.requireNonNull(callback)));
    }
    
    public void search(final LobbySearchQuery query) {
        this.search(query, Core.DEFAULT_CALLBACK);
    }
    
    public int lobbyCount() {
        return this.core.execute(() -> this.lobbyCount(this.pointer));
    }
    
    public long getLobbyId(final int index) {
        final Object ret = this.core.execute(() -> this.getLobbyId(this.pointer, index));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (long)ret;
    }
    
    public List<Long> getLobbyIds() {
        final List<Long> list = IntStream.range(0, this.lobbyCount()).mapToLong(this::getLobbyId).boxed().collect((Collector<? super Long, ?, List<Long>>)Collectors.toList());
        return Collections.unmodifiableList((List<? extends Long>)list);
    }
    
    public List<Lobby> getLobbies() {
        final List<Lobby> list = this.getLobbyIds().stream().map((Function<? super Object, ?>)this::getLobby).collect((Collector<? super Object, ?, List<Lobby>>)Collectors.toList());
        return Collections.unmodifiableList((List<? extends Lobby>)list);
    }
    
    public void connectVoice(final long lobbyId, final Consumer<Result> callback) {
        this.core.execute(() -> this.connectVoice(this.pointer, lobbyId, Objects.requireNonNull(callback)));
    }
    
    public void connectVoice(final long lobbyId) {
        this.connectVoice(lobbyId, Core.DEFAULT_CALLBACK);
    }
    
    public void connectVoice(final Lobby lobby, final Consumer<Result> callback) {
        this.connectVoice(lobby.getId(), callback);
    }
    
    public void connectVoice(final Lobby lobby) {
        this.connectVoice(lobby, Core.DEFAULT_CALLBACK);
    }
    
    public void disconnectVoice(final long lobbyId, final Consumer<Result> callback) {
        this.core.execute(() -> this.disconnectVoice(this.pointer, lobbyId, Objects.requireNonNull(callback)));
    }
    
    public void disconnectVoice(final long lobbyId) {
        this.disconnectVoice(lobbyId, Core.DEFAULT_CALLBACK);
    }
    
    public void disconnectVoice(final Lobby lobby, final Consumer<Result> callback) {
        this.disconnectVoice(lobby.getId(), callback);
    }
    
    public void disconnectVoice(final Lobby lobby) {
        this.disconnectVoice(lobby, Core.DEFAULT_CALLBACK);
    }
    
    public void connectNetwork(final long lobbyId) {
        final Result result = this.core.execute(() -> this.connectNetwork(this.pointer, lobbyId));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void connectNetwork(final Lobby lobby) {
        this.connectNetwork(lobby.getId());
    }
    
    public void disconnectNetwork(final long lobbyId) {
        final Result result = this.core.execute(() -> this.disconnectNetwork(this.pointer, lobbyId));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void disconnectNetwork(final Lobby lobby) {
        this.disconnectNetwork(lobby.getId());
    }
    
    public void flushNetwork() {
        final Result result = this.core.execute(() -> this.flushNetwork(this.pointer));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void openNetworkChannel(final long lobbyId, final byte channelId, final boolean reliable) {
        final Result result = this.core.execute(() -> this.openNetworkChannel(this.pointer, lobbyId, channelId, reliable));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void openNetworkChannel(final Lobby lobby, final byte channelId, final boolean reliable) {
        this.openNetworkChannel(lobby.getId(), channelId, reliable);
    }
    
    public void sendNetworkMessage(final long lobbyId, final long userId, final byte channelId, final byte[] data) {
        final Result result = this.core.execute(() -> this.sendNetworkMessage(this.pointer, lobbyId, userId, channelId, data, 0, data.length));
        if (result != Result.OK) {
            throw new GameSDKException(result);
        }
    }
    
    public void sendNetworkMessage(final Lobby lobby, final long userId, final byte channelId, final byte[] data) {
        this.sendNetworkMessage(lobby.getId(), userId, channelId, data);
    }
    
    private native Object getLobbyCreateTransaction(final long p0);
    
    private native Object getLobbyUpdateTransaction(final long p0, final long p1);
    
    private native Object getMemberUpdateTransaction(final long p0, final long p1, final long p2);
    
    private native void createLobby(final long p0, final long p1, final BiConsumer<Result, Lobby> p2);
    
    private native void updateLobby(final long p0, final long p1, final long p2, final Consumer<Result> p3);
    
    private native void deleteLobby(final long p0, final long p1, final Consumer<Result> p2);
    
    private native void connectLobby(final long p0, final long p1, final String p2, final BiConsumer<Result, Lobby> p3);
    
    private native void connectLobbyWithActivitySecret(final long p0, final String p1, final BiConsumer<Result, Lobby> p2);
    
    private native void disconnectLobby(final long p0, final long p1, final Consumer<Result> p2);
    
    private native Object getLobby(final long p0, final long p1);
    
    private native Object getLobbyActivitySecret(final long p0, final long p1);
    
    private native Object getLobbyMetadataValue(final long p0, final long p1, final String p2);
    
    private native Object getLobbyMetadataKey(final long p0, final long p1, final int p2);
    
    private native Object lobbyMetadataCount(final long p0, final long p1);
    
    private native Object memberCount(final long p0, final long p1);
    
    private native Object getMemberUserId(final long p0, final long p1, final int p2);
    
    private native Object getMemberUser(final long p0, final long p1, final long p2);
    
    private native Object getMemberMetadataValue(final long p0, final long p1, final long p2, final String p3);
    
    private native Object getMemberMetadataKey(final long p0, final long p1, final long p2, final int p3);
    
    private native Object memberMetadataCount(final long p0, final long p1, final long p2);
    
    private native void updateMember(final long p0, final long p1, final long p2, final long p3, final Consumer<Result> p4);
    
    private native void sendLobbyMessage(final long p0, final long p1, final byte[] p2, final int p3, final int p4, final Consumer<Result> p5);
    
    private native Object getSearchQuery(final long p0);
    
    private native void search(final long p0, final long p1, final Consumer<Result> p2);
    
    private native int lobbyCount(final long p0);
    
    private native Object getLobbyId(final long p0, final int p1);
    
    private native void connectVoice(final long p0, final long p1, final Consumer<Result> p2);
    
    private native void disconnectVoice(final long p0, final long p1, final Consumer<Result> p2);
    
    private native Result connectNetwork(final long p0, final long p1);
    
    private native Result disconnectNetwork(final long p0, final long p1);
    
    private native Result flushNetwork(final long p0);
    
    private native Result openNetworkChannel(final long p0, final long p1, final byte p2, final boolean p3);
    
    private native Result sendNetworkMessage(final long p0, final long p1, final long p2, final byte p3, final byte[] p4, final int p5, final int p6);
}
