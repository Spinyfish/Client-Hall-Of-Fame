/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Range
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.protocol;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.ProtocolManager;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.ProtocolPathKey;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.VersionedPacketTransformer;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectSortedMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntBidirectionalIterator;
import com.viaversion.viaversion.protocol.ProtocolPathEntryImpl;
import com.viaversion.viaversion.protocol.ProtocolPathKeyImpl;
import com.viaversion.viaversion.protocol.ServerProtocolVersionSingleton;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.protocol.packet.VersionedPacketTransformerImpl;
import com.viaversion.viaversion.protocols.base.BaseProtocol;
import com.viaversion.viaversion.protocols.base.BaseProtocol1_16;
import com.viaversion.viaversion.protocols.base.BaseProtocol1_7;
import com.viaversion.viaversion.protocols.protocol1_10to1_9_3.Protocol1_10To1_9_3_4;
import com.viaversion.viaversion.protocols.protocol1_11_1to1_11.Protocol1_11_1To1_11;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.Protocol1_11To1_10;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.Protocol1_12_1To1_12;
import com.viaversion.viaversion.protocols.protocol1_12_2to1_12_1.Protocol1_12_2To1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import com.viaversion.viaversion.protocols.protocol1_13_2to1_13_1.Protocol1_13_2To1_13_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_14_1to1_14.Protocol1_14_1To1_14;
import com.viaversion.viaversion.protocols.protocol1_14_2to1_14_1.Protocol1_14_2To1_14_1;
import com.viaversion.viaversion.protocols.protocol1_14_3to1_14_2.Protocol1_14_3To1_14_2;
import com.viaversion.viaversion.protocols.protocol1_14_4to1_14_3.Protocol1_14_4To1_14_3;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_15_1to1_15.Protocol1_15_1To1_15;
import com.viaversion.viaversion.protocols.protocol1_15_2to1_15_1.Protocol1_15_2To1_15_1;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import com.viaversion.viaversion.protocols.protocol1_16_1to1_16.Protocol1_16_1To1_16;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import com.viaversion.viaversion.protocols.protocol1_16_3to1_16_2.Protocol1_16_3To1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_4to1_16_3.Protocol1_16_4To1_16_3;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.Protocol1_17_1To1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.Protocol1_18To1_17_1;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.Protocol1_9_1_2To1_9_3_4;
import com.viaversion.viaversion.protocols.protocol1_9_1to1_9.Protocol1_9_1To1_9;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.Protocol1_9_3To1_9_1_2;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_9_1.Protocol1_9To1_9_1;
import com.viaversion.viaversion.util.Pair;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;

public class ProtocolManagerImpl
implements ProtocolManager {
    private static final Protocol BASE_PROTOCOL = new BaseProtocol();
    private final Int2ObjectMap<Int2ObjectMap<Protocol>> registryMap = new Int2ObjectOpenHashMap<Int2ObjectMap<Protocol>>(32);
    private final Map<Class<? extends Protocol>, Protocol> protocols = new HashMap<Class<? extends Protocol>, Protocol>();
    private final Map<ProtocolPathKey, List<ProtocolPathEntry>> pathCache = new ConcurrentHashMap<ProtocolPathKey, List<ProtocolPathEntry>>();
    private final Set<Integer> supportedVersions = new HashSet<Integer>();
    private final List<Pair<Range<Integer>, Protocol>> baseProtocols = Lists.newCopyOnWriteArrayList();
    private final List<Protocol> registerList = new ArrayList<Protocol>();
    private final ReadWriteLock mappingLoaderLock = new ReentrantReadWriteLock();
    private Map<Class<? extends Protocol>, CompletableFuture<Void>> mappingLoaderFutures = new HashMap<Class<? extends Protocol>, CompletableFuture<Void>>();
    private ThreadPoolExecutor mappingLoaderExecutor;
    private boolean mappingsLoaded;
    private ServerProtocolVersion serverProtocolVersion = new ServerProtocolVersionSingleton(-1);
    private boolean onlyCheckLoweringPathEntries = true;
    private int maxProtocolPathSize = 50;

    public ProtocolManagerImpl() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Via-Mappingloader-%d").build();
        this.mappingLoaderExecutor = new ThreadPoolExecutor(5, 16, 45L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
        this.mappingLoaderExecutor.allowCoreThreadTimeOut(true);
    }

    public void registerProtocols() {
        this.registerBaseProtocol(BASE_PROTOCOL, (Range<Integer>)Range.lessThan((Comparable)Integer.valueOf(Integer.MIN_VALUE)));
        this.registerBaseProtocol(new BaseProtocol1_7(), (Range<Integer>)Range.lessThan((Comparable)Integer.valueOf(ProtocolVersion.v1_16.getVersion())));
        this.registerBaseProtocol(new BaseProtocol1_16(), (Range<Integer>)Range.atLeast((Comparable)Integer.valueOf(ProtocolVersion.v1_16.getVersion())));
        this.registerProtocol((Protocol)new Protocol1_9To1_8(), ProtocolVersion.v1_9, ProtocolVersion.v1_8);
        this.registerProtocol((Protocol)new Protocol1_9_1To1_9(), Arrays.asList(ProtocolVersion.v1_9_1.getVersion(), ProtocolVersion.v1_9_2.getVersion()), ProtocolVersion.v1_9.getVersion());
        this.registerProtocol((Protocol)new Protocol1_9_3To1_9_1_2(), ProtocolVersion.v1_9_3, ProtocolVersion.v1_9_2);
        this.registerProtocol((Protocol)new Protocol1_9To1_9_1(), ProtocolVersion.v1_9, ProtocolVersion.v1_9_1);
        this.registerProtocol((Protocol)new Protocol1_9_1_2To1_9_3_4(), Arrays.asList(ProtocolVersion.v1_9_1.getVersion(), ProtocolVersion.v1_9_2.getVersion()), ProtocolVersion.v1_9_3.getVersion());
        this.registerProtocol((Protocol)new Protocol1_10To1_9_3_4(), ProtocolVersion.v1_10, ProtocolVersion.v1_9_3);
        this.registerProtocol((Protocol)new Protocol1_11To1_10(), ProtocolVersion.v1_11, ProtocolVersion.v1_10);
        this.registerProtocol((Protocol)new Protocol1_11_1To1_11(), ProtocolVersion.v1_11_1, ProtocolVersion.v1_11);
        this.registerProtocol((Protocol)new Protocol1_12To1_11_1(), ProtocolVersion.v1_12, ProtocolVersion.v1_11_1);
        this.registerProtocol((Protocol)new Protocol1_12_1To1_12(), ProtocolVersion.v1_12_1, ProtocolVersion.v1_12);
        this.registerProtocol((Protocol)new Protocol1_12_2To1_12_1(), ProtocolVersion.v1_12_2, ProtocolVersion.v1_12_1);
        this.registerProtocol((Protocol)new Protocol1_13To1_12_2(), ProtocolVersion.v1_13, ProtocolVersion.v1_12_2);
        this.registerProtocol((Protocol)new Protocol1_13_1To1_13(), ProtocolVersion.v1_13_1, ProtocolVersion.v1_13);
        this.registerProtocol((Protocol)new Protocol1_13_2To1_13_1(), ProtocolVersion.v1_13_2, ProtocolVersion.v1_13_1);
        this.registerProtocol((Protocol)new Protocol1_14To1_13_2(), ProtocolVersion.v1_14, ProtocolVersion.v1_13_2);
        this.registerProtocol((Protocol)new Protocol1_14_1To1_14(), ProtocolVersion.v1_14_1, ProtocolVersion.v1_14);
        this.registerProtocol((Protocol)new Protocol1_14_2To1_14_1(), ProtocolVersion.v1_14_2, ProtocolVersion.v1_14_1);
        this.registerProtocol((Protocol)new Protocol1_14_3To1_14_2(), ProtocolVersion.v1_14_3, ProtocolVersion.v1_14_2);
        this.registerProtocol((Protocol)new Protocol1_14_4To1_14_3(), ProtocolVersion.v1_14_4, ProtocolVersion.v1_14_3);
        this.registerProtocol((Protocol)new Protocol1_15To1_14_4(), ProtocolVersion.v1_15, ProtocolVersion.v1_14_4);
        this.registerProtocol((Protocol)new Protocol1_15_1To1_15(), ProtocolVersion.v1_15_1, ProtocolVersion.v1_15);
        this.registerProtocol((Protocol)new Protocol1_15_2To1_15_1(), ProtocolVersion.v1_15_2, ProtocolVersion.v1_15_1);
        this.registerProtocol((Protocol)new Protocol1_16To1_15_2(), ProtocolVersion.v1_16, ProtocolVersion.v1_15_2);
        this.registerProtocol((Protocol)new Protocol1_16_1To1_16(), ProtocolVersion.v1_16_1, ProtocolVersion.v1_16);
        this.registerProtocol((Protocol)new Protocol1_16_2To1_16_1(), ProtocolVersion.v1_16_2, ProtocolVersion.v1_16_1);
        this.registerProtocol((Protocol)new Protocol1_16_3To1_16_2(), ProtocolVersion.v1_16_3, ProtocolVersion.v1_16_2);
        this.registerProtocol((Protocol)new Protocol1_16_4To1_16_3(), ProtocolVersion.v1_16_4, ProtocolVersion.v1_16_3);
        this.registerProtocol((Protocol)new Protocol1_17To1_16_4(), ProtocolVersion.v1_17, ProtocolVersion.v1_16_4);
        this.registerProtocol((Protocol)new Protocol1_17_1To1_17(), ProtocolVersion.v1_17_1, ProtocolVersion.v1_17);
        this.registerProtocol((Protocol)new Protocol1_18To1_17_1(), ProtocolVersion.v1_18, ProtocolVersion.v1_17_1);
    }

    @Override
    public void registerProtocol(Protocol protocol, ProtocolVersion clientVersion, ProtocolVersion serverVersion) {
        this.registerProtocol(protocol, Collections.singletonList(clientVersion.getVersion()), serverVersion.getVersion());
    }

    @Override
    public void registerProtocol(Protocol protocol, List<Integer> supportedClientVersion, int serverVersion) {
        protocol.initialize();
        if (!this.pathCache.isEmpty()) {
            this.pathCache.clear();
        }
        this.protocols.put(protocol.getClass(), protocol);
        for (int clientVersion : supportedClientVersion) {
            Preconditions.checkArgument((clientVersion != serverVersion ? 1 : 0) != 0);
            Int2ObjectMap protocolMap = this.registryMap.computeIfAbsent(clientVersion, s -> new Int2ObjectOpenHashMap(2));
            protocolMap.put(serverVersion, protocol);
        }
        if (Via.getPlatform().isPluginEnabled()) {
            protocol.register(Via.getManager().getProviders());
            this.refreshVersions();
        } else {
            this.registerList.add(protocol);
        }
        if (protocol.hasMappingDataToLoad()) {
            if (this.mappingLoaderExecutor != null) {
                this.addMappingLoaderFuture(protocol.getClass(), protocol::loadMappingData);
            } else {
                protocol.loadMappingData();
            }
        }
    }

    @Override
    public void registerBaseProtocol(Protocol baseProtocol, Range<Integer> supportedProtocols) {
        Preconditions.checkArgument((boolean)baseProtocol.isBaseProtocol(), (Object)"Protocol is not a base protocol");
        baseProtocol.initialize();
        this.baseProtocols.add(new Pair<Range<Integer>, Protocol>(supportedProtocols, baseProtocol));
        if (Via.getPlatform().isPluginEnabled()) {
            baseProtocol.register(Via.getManager().getProviders());
            this.refreshVersions();
        } else {
            this.registerList.add(baseProtocol);
        }
    }

    public void refreshVersions() {
        this.supportedVersions.clear();
        this.supportedVersions.add(this.serverProtocolVersion.lowestSupportedVersion());
        for (ProtocolVersion version : ProtocolVersion.getProtocols()) {
            List<ProtocolPathEntry> protocolPath = this.getProtocolPath(version.getVersion(), this.serverProtocolVersion.lowestSupportedVersion());
            if (protocolPath == null) continue;
            this.supportedVersions.add(version.getVersion());
            for (ProtocolPathEntry pathEntry : protocolPath) {
                this.supportedVersions.add(pathEntry.outputProtocolVersion());
            }
        }
    }

    @Override
    public @Nullable List<ProtocolPathEntry> getProtocolPath(int clientVersion, int serverVersion) {
        if (clientVersion == serverVersion) {
            return null;
        }
        ProtocolPathKeyImpl protocolKey = new ProtocolPathKeyImpl(clientVersion, serverVersion);
        List<ProtocolPathEntry> protocolList = this.pathCache.get(protocolKey);
        if (protocolList != null) {
            return protocolList;
        }
        Int2ObjectSortedMap<Protocol> outputPath = this.getProtocolPath(new Int2ObjectLinkedOpenHashMap<Protocol>(), clientVersion, serverVersion);
        if (outputPath == null) {
            return null;
        }
        ArrayList<ProtocolPathEntry> path = new ArrayList<ProtocolPathEntry>(outputPath.size());
        for (Int2ObjectMap.Entry entry : outputPath.int2ObjectEntrySet()) {
            path.add(new ProtocolPathEntryImpl(entry.getIntKey(), (Protocol)entry.getValue()));
        }
        this.pathCache.put(protocolKey, path);
        return path;
    }

    @Override
    public <C extends ClientboundPacketType, S extends ServerboundPacketType> VersionedPacketTransformer<C, S> createPacketTransformer(ProtocolVersion inputVersion, @Nullable Class<C> clientboundPacketsClass, @Nullable Class<S> serverboundPacketsClass) {
        Preconditions.checkArgument((clientboundPacketsClass != ClientboundPacketType.class && serverboundPacketsClass != ServerboundPacketType.class ? 1 : 0) != 0);
        return new VersionedPacketTransformerImpl<C, S>(inputVersion, clientboundPacketsClass, serverboundPacketsClass);
    }

    private @Nullable Int2ObjectSortedMap<Protocol> getProtocolPath(Int2ObjectSortedMap<Protocol> current, int clientVersion, int serverVersion) {
        if (current.size() > this.maxProtocolPathSize) {
            return null;
        }
        Int2ObjectMap toServerProtocolMap = (Int2ObjectMap)this.registryMap.get(clientVersion);
        if (toServerProtocolMap == null) {
            return null;
        }
        Protocol protocol = (Protocol)toServerProtocolMap.get(serverVersion);
        if (protocol != null) {
            current.put(serverVersion, protocol);
            return current;
        }
        Int2ObjectSortedMap<Protocol> shortest = null;
        for (Int2ObjectMap.Entry entry : toServerProtocolMap.int2ObjectEntrySet()) {
            int translatedToVersion = entry.getIntKey();
            if (current.containsKey(translatedToVersion) || this.onlyCheckLoweringPathEntries && Math.abs(serverVersion - translatedToVersion) > Math.abs(serverVersion - clientVersion)) continue;
            Int2ObjectSortedMap<Protocol> newCurrent = new Int2ObjectLinkedOpenHashMap<Protocol>((Int2ObjectMap<Protocol>)current);
            newCurrent.put(translatedToVersion, (Protocol)entry.getValue());
            if ((newCurrent = this.getProtocolPath(newCurrent, translatedToVersion, serverVersion)) == null || shortest != null && newCurrent.size() >= shortest.size()) continue;
            shortest = newCurrent;
        }
        return shortest;
    }

    @Override
    public <T extends Protocol> @Nullable T getProtocol(Class<T> protocolClass) {
        return (T)this.protocols.get(protocolClass);
    }

    @Override
    public @Nullable Protocol getProtocol(int clientVersion, int serverVersion) {
        Int2ObjectMap map = (Int2ObjectMap)this.registryMap.get(clientVersion);
        return map != null ? (Protocol)map.get(serverVersion) : null;
    }

    @Override
    public Protocol getBaseProtocol(int serverVersion) {
        for (Pair rangeProtocol : Lists.reverse(this.baseProtocols)) {
            if (!((Range)rangeProtocol.key()).contains((Comparable)Integer.valueOf(serverVersion))) continue;
            return (Protocol)rangeProtocol.value();
        }
        throw new IllegalStateException("No Base Protocol for " + serverVersion);
    }

    @Override
    public ServerProtocolVersion getServerProtocolVersion() {
        return this.serverProtocolVersion;
    }

    public void setServerProtocol(ServerProtocolVersion serverProtocolVersion) {
        this.serverProtocolVersion = serverProtocolVersion;
        ProtocolRegistry.SERVER_PROTOCOL = serverProtocolVersion.lowestSupportedVersion();
    }

    @Override
    public boolean isWorkingPipe() {
        for (Int2ObjectMap map : this.registryMap.values()) {
            IntBidirectionalIterator intBidirectionalIterator = this.serverProtocolVersion.supportedVersions().iterator();
            while (intBidirectionalIterator.hasNext()) {
                int protocolVersion = (Integer)intBidirectionalIterator.next();
                if (!map.containsKey(protocolVersion)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public SortedSet<Integer> getSupportedVersions() {
        return Collections.unmodifiableSortedSet(new TreeSet<Integer>(this.supportedVersions));
    }

    @Override
    public void setOnlyCheckLoweringPathEntries(boolean onlyCheckLoweringPathEntries) {
        this.onlyCheckLoweringPathEntries = onlyCheckLoweringPathEntries;
    }

    @Override
    public boolean onlyCheckLoweringPathEntries() {
        return this.onlyCheckLoweringPathEntries;
    }

    @Override
    public int getMaxProtocolPathSize() {
        return this.maxProtocolPathSize;
    }

    @Override
    public void setMaxProtocolPathSize(int maxProtocolPathSize) {
        this.maxProtocolPathSize = maxProtocolPathSize;
    }

    @Override
    public Protocol getBaseProtocol() {
        return BASE_PROTOCOL;
    }

    @Override
    public void completeMappingDataLoading(Class<? extends Protocol> protocolClass) throws Exception {
        if (this.mappingsLoaded) {
            return;
        }
        CompletableFuture<Void> future = this.getMappingLoaderFuture(protocolClass);
        if (future != null) {
            future.get();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean checkForMappingCompletion() {
        this.mappingLoaderLock.readLock().lock();
        try {
            if (this.mappingsLoaded) {
                boolean bl = false;
                return bl;
            }
            for (CompletableFuture<Void> future : this.mappingLoaderFutures.values()) {
                if (future.isDone()) continue;
                boolean bl = false;
                return bl;
            }
            this.shutdownLoaderExecutor();
            boolean bl = true;
            return bl;
        }
        finally {
            this.mappingLoaderLock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMappingLoaderFuture(Class<? extends Protocol> protocolClass, Runnable runnable) {
        CompletionStage future = CompletableFuture.runAsync(runnable, this.mappingLoaderExecutor).exceptionally((Function)this.mappingLoaderThrowable(protocolClass));
        this.mappingLoaderLock.writeLock().lock();
        try {
            this.mappingLoaderFutures.put(protocolClass, (CompletableFuture<Void>)future);
        }
        finally {
            this.mappingLoaderLock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMappingLoaderFuture(Class<? extends Protocol> protocolClass, Class<? extends Protocol> dependsOn, Runnable runnable) {
        CompletionStage future = ((CompletableFuture)this.getMappingLoaderFuture(dependsOn).whenCompleteAsync((v, throwable) -> runnable.run(), (Executor)this.mappingLoaderExecutor)).exceptionally(this.mappingLoaderThrowable(protocolClass));
        this.mappingLoaderLock.writeLock().lock();
        try {
            this.mappingLoaderFutures.put(protocolClass, (CompletableFuture<Void>)future);
        }
        finally {
            this.mappingLoaderLock.writeLock().unlock();
        }
    }

    @Override
    public @Nullable CompletableFuture<Void> getMappingLoaderFuture(Class<? extends Protocol> protocolClass) {
        this.mappingLoaderLock.readLock().lock();
        try {
            CompletableFuture<Void> completableFuture = this.mappingsLoaded ? null : this.mappingLoaderFutures.get(protocolClass);
            return completableFuture;
        }
        finally {
            this.mappingLoaderLock.readLock().unlock();
        }
    }

    @Override
    public PacketWrapper createPacketWrapper(@Nullable PacketType packetType, @Nullable ByteBuf buf, UserConnection connection) {
        return new PacketWrapperImpl(packetType, buf, connection);
    }

    @Override
    @Deprecated
    public PacketWrapper createPacketWrapper(int packetId, @Nullable ByteBuf buf, UserConnection connection) {
        return new PacketWrapperImpl(packetId, buf, connection);
    }

    public void onServerLoaded() {
        for (Protocol protocol : this.registerList) {
            protocol.register(Via.getManager().getProviders());
        }
        this.registerList.clear();
    }

    private void shutdownLoaderExecutor() {
        Preconditions.checkArgument((!this.mappingsLoaded ? 1 : 0) != 0);
        Via.getPlatform().getLogger().info("Finished mapping loading, shutting down loader executor!");
        this.mappingsLoaded = true;
        this.mappingLoaderExecutor.shutdown();
        this.mappingLoaderExecutor = null;
        this.mappingLoaderFutures.clear();
        this.mappingLoaderFutures = null;
        if (MappingDataLoader.isCacheJsonMappings()) {
            MappingDataLoader.getMappingsCache().clear();
        }
    }

    private Function<Throwable, Void> mappingLoaderThrowable(Class<? extends Protocol> protocolClass) {
        return throwable -> {
            Via.getPlatform().getLogger().severe("Error during mapping loading of " + protocolClass.getSimpleName());
            throwable.printStackTrace();
            return null;
        };
    }
}

