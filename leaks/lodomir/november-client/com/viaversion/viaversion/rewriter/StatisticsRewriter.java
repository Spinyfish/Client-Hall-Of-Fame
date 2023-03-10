/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.rewriter.IdRewriteFunction;
import org.checkerframework.checker.nullness.qual.Nullable;

public class StatisticsRewriter {
    private final Protocol protocol;
    private final int customStatsCategory = 8;

    public StatisticsRewriter(Protocol protocol) {
        this.protocol = protocol;
    }

    public void register(ClientboundPacketType packetType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int size;
                    int newSize = size = wrapper.passthrough(Type.VAR_INT).intValue();
                    for (int i = 0; i < size; ++i) {
                        int categoryId = wrapper.read(Type.VAR_INT);
                        int statisticId = wrapper.read(Type.VAR_INT);
                        int value = wrapper.read(Type.VAR_INT);
                        if (categoryId == 8 && StatisticsRewriter.this.protocol.getMappingData().getStatisticsMappings() != null) {
                            statisticId = StatisticsRewriter.this.protocol.getMappingData().getStatisticsMappings().getNewId(statisticId);
                            if (statisticId == -1) {
                                --newSize;
                                continue;
                            }
                        } else {
                            IdRewriteFunction statisticsRewriter;
                            RegistryType type = StatisticsRewriter.this.getRegistryTypeForStatistic(categoryId);
                            if (type != null && (statisticsRewriter = StatisticsRewriter.this.getRewriter(type)) != null) {
                                statisticId = statisticsRewriter.rewrite(statisticId);
                            }
                        }
                        wrapper.write(Type.VAR_INT, categoryId);
                        wrapper.write(Type.VAR_INT, statisticId);
                        wrapper.write(Type.VAR_INT, value);
                    }
                    if (newSize != size) {
                        wrapper.set(Type.VAR_INT, 0, newSize);
                    }
                });
            }
        });
    }

    protected @Nullable IdRewriteFunction getRewriter(RegistryType type) {
        switch (type) {
            case BLOCK: {
                return this.protocol.getMappingData().getBlockMappings() != null ? id -> this.protocol.getMappingData().getNewBlockId(id) : null;
            }
            case ITEM: {
                return this.protocol.getMappingData().getItemMappings() != null ? id -> this.protocol.getMappingData().getNewItemId(id) : null;
            }
            case ENTITY: {
                return this.protocol.getEntityRewriter() != null ? id -> this.protocol.getEntityRewriter().newEntityId(id) : null;
            }
        }
        throw new IllegalArgumentException("Unknown registry type in statistics packet: " + (Object)((Object)type));
    }

    public @Nullable RegistryType getRegistryTypeForStatistic(int statisticsId) {
        switch (statisticsId) {
            case 0: {
                return RegistryType.BLOCK;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                return RegistryType.ITEM;
            }
            case 6: 
            case 7: {
                return RegistryType.ENTITY;
            }
        }
        return null;
    }
}

