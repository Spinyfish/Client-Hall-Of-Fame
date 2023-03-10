/*
 * Decompiled with CFR 0.152.
 */
package viamcp.protocols;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import viamcp.protocols.ProtocolInfo;
import viamcp.protocols.ProtocolInfoCollection;

public enum ProtocolCollection {
    R1_18(ProtocolVersion.v1_18, ProtocolInfoCollection.R1_18),
    R1_17_1(ProtocolVersion.v1_17_1, ProtocolInfoCollection.R1_17_1),
    R1_17(ProtocolVersion.v1_17, ProtocolInfoCollection.R1_17),
    R1_16_4(ProtocolVersion.v1_16_4, ProtocolInfoCollection.R1_16_4),
    R1_16_3(ProtocolVersion.v1_16_3, ProtocolInfoCollection.R1_16_3),
    R1_16_2(ProtocolVersion.v1_16_2, ProtocolInfoCollection.R1_16_2),
    R1_16_1(ProtocolVersion.v1_16_1, ProtocolInfoCollection.R1_16_1),
    R1_16(ProtocolVersion.v1_16, ProtocolInfoCollection.R1_16),
    R1_15_2(ProtocolVersion.v1_15_2, ProtocolInfoCollection.R1_15_2),
    R1_15_1(ProtocolVersion.v1_15_1, ProtocolInfoCollection.R1_15_1),
    R1_15(ProtocolVersion.v1_15, ProtocolInfoCollection.R1_15),
    R1_14_4(ProtocolVersion.v1_14_4, ProtocolInfoCollection.R1_14_4),
    R1_14_3(ProtocolVersion.v1_14_3, ProtocolInfoCollection.R1_14_3),
    R1_14_2(ProtocolVersion.v1_14_2, ProtocolInfoCollection.R1_14_2),
    R1_14_1(ProtocolVersion.v1_14_1, ProtocolInfoCollection.R1_14_1),
    R1_14(ProtocolVersion.v1_14, ProtocolInfoCollection.R1_14),
    R1_13_2(ProtocolVersion.v1_13_2, ProtocolInfoCollection.R1_13_2),
    R1_13_1(ProtocolVersion.v1_13_1, ProtocolInfoCollection.R1_13_1),
    R1_13(ProtocolVersion.v1_13, ProtocolInfoCollection.R1_13),
    R1_12_2(ProtocolVersion.v1_12_2, ProtocolInfoCollection.R1_12_2),
    R1_12_1(ProtocolVersion.v1_12_1, ProtocolInfoCollection.R1_12_1),
    R1_12(ProtocolVersion.v1_12, ProtocolInfoCollection.R1_12),
    R1_11_1(ProtocolVersion.v1_11_1, ProtocolInfoCollection.R1_11_1),
    R1_11(ProtocolVersion.v1_11, ProtocolInfoCollection.R1_11),
    R1_10(ProtocolVersion.v1_10, ProtocolInfoCollection.R1_10),
    R1_9_3(ProtocolVersion.v1_9_3, ProtocolInfoCollection.R1_9_3),
    R1_9_2(ProtocolVersion.v1_9_2, ProtocolInfoCollection.R1_9_2),
    R1_9_1(ProtocolVersion.v1_9_1, ProtocolInfoCollection.R1_9_1),
    R1_9(ProtocolVersion.v1_9, ProtocolInfoCollection.R1_9),
    R1_8(ProtocolVersion.v1_8, ProtocolInfoCollection.R1_8),
    R1_7_6(ProtocolVersion.v1_7_6, ProtocolInfoCollection.R1_7_6),
    R1_7(ProtocolVersion.v1_7_1, ProtocolInfoCollection.R1_7);

    private final ProtocolVersion version;
    private final ProtocolInfo info;

    private ProtocolCollection(ProtocolVersion version, ProtocolInfo info) {
        this.version = version;
        this.info = info;
    }

    public ProtocolVersion getVersion() {
        return this.version;
    }

    public ProtocolInfo getInfo() {
        return this.info;
    }

    public static ProtocolCollection getProtocolCollectionById(int id) {
        for (ProtocolCollection coll : ProtocolCollection.values()) {
            if (coll.getVersion().getVersion() != id) continue;
            return coll;
        }
        return null;
    }

    public static ProtocolVersion getProtocolById(int id) {
        for (ProtocolCollection coll : ProtocolCollection.values()) {
            if (coll.getVersion().getVersion() != id) continue;
            return coll.getVersion();
        }
        return null;
    }

    public static ProtocolInfo getProtocolInfoById(int id) {
        for (ProtocolCollection coll : ProtocolCollection.values()) {
            if (coll.getVersion().getVersion() != id) continue;
            return coll.getInfo();
        }
        return null;
    }
}

