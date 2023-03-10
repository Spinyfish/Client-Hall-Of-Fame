/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.protocol.version;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.protocol.version.VersionRange;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ProtocolVersion {
    private static final Int2ObjectMap<ProtocolVersion> VERSIONS = new Int2ObjectOpenHashMap<ProtocolVersion>();
    private static final List<ProtocolVersion> VERSION_LIST = new ArrayList<ProtocolVersion>();
    public static final ProtocolVersion v1_4_6 = ProtocolVersion.register(51, "1.4.6/7", new VersionRange("1.4", 6, 7));
    public static final ProtocolVersion v1_5_1 = ProtocolVersion.register(60, "1.5.1");
    public static final ProtocolVersion v1_5_2 = ProtocolVersion.register(61, "1.5.2");
    public static final ProtocolVersion v_1_6_1 = ProtocolVersion.register(73, "1.6.1");
    public static final ProtocolVersion v_1_6_2 = ProtocolVersion.register(74, "1.6.2");
    public static final ProtocolVersion v_1_6_3 = ProtocolVersion.register(77, "1.6.3");
    public static final ProtocolVersion v_1_6_4 = ProtocolVersion.register(78, "1.6.4");
    public static final ProtocolVersion v1_7_1 = ProtocolVersion.register(4, "1.7-1.7.5", new VersionRange("1.7", 0, 5));
    public static final ProtocolVersion v1_7_6 = ProtocolVersion.register(5, "1.7.6-1.7.10", new VersionRange("1.7", 6, 10));
    public static final ProtocolVersion v1_8 = ProtocolVersion.register(47, "1.8.x");
    public static final ProtocolVersion v1_9 = ProtocolVersion.register(107, "1.9");
    public static final ProtocolVersion v1_9_1 = ProtocolVersion.register(108, "1.9.1");
    public static final ProtocolVersion v1_9_2 = ProtocolVersion.register(109, "1.9.2");
    public static final ProtocolVersion v1_9_3 = ProtocolVersion.register(110, "1.9.3/4", new VersionRange("1.9", 3, 4));
    public static final ProtocolVersion v1_10 = ProtocolVersion.register(210, "1.10.x");
    public static final ProtocolVersion v1_11 = ProtocolVersion.register(315, "1.11");
    public static final ProtocolVersion v1_11_1 = ProtocolVersion.register(316, "1.11.1/2", new VersionRange("1.11", 1, 2));
    public static final ProtocolVersion v1_12 = ProtocolVersion.register(335, "1.12");
    public static final ProtocolVersion v1_12_1 = ProtocolVersion.register(338, "1.12.1");
    public static final ProtocolVersion v1_12_2 = ProtocolVersion.register(340, "1.12.2");
    public static final ProtocolVersion v1_13 = ProtocolVersion.register(393, "1.13");
    public static final ProtocolVersion v1_13_1 = ProtocolVersion.register(401, "1.13.1");
    public static final ProtocolVersion v1_13_2 = ProtocolVersion.register(404, "1.13.2");
    public static final ProtocolVersion v1_14 = ProtocolVersion.register(477, "1.14");
    public static final ProtocolVersion v1_14_1 = ProtocolVersion.register(480, "1.14.1");
    public static final ProtocolVersion v1_14_2 = ProtocolVersion.register(485, "1.14.2");
    public static final ProtocolVersion v1_14_3 = ProtocolVersion.register(490, "1.14.3");
    public static final ProtocolVersion v1_14_4 = ProtocolVersion.register(498, "1.14.4");
    public static final ProtocolVersion v1_15 = ProtocolVersion.register(573, "1.15");
    public static final ProtocolVersion v1_15_1 = ProtocolVersion.register(575, "1.15.1");
    public static final ProtocolVersion v1_15_2 = ProtocolVersion.register(578, "1.15.2");
    public static final ProtocolVersion v1_16 = ProtocolVersion.register(735, "1.16");
    public static final ProtocolVersion v1_16_1 = ProtocolVersion.register(736, "1.16.1");
    public static final ProtocolVersion v1_16_2 = ProtocolVersion.register(751, "1.16.2");
    public static final ProtocolVersion v1_16_3 = ProtocolVersion.register(753, "1.16.3");
    public static final ProtocolVersion v1_16_4 = ProtocolVersion.register(754, "1.16.4/5", new VersionRange("1.16", 4, 5));
    public static final ProtocolVersion v1_17 = ProtocolVersion.register(755, "1.17");
    public static final ProtocolVersion v1_17_1 = ProtocolVersion.register(756, "1.17.1");
    public static final ProtocolVersion v1_18 = ProtocolVersion.register(757, "1.18");
    public static final ProtocolVersion unknown = ProtocolVersion.register(-1, "UNKNOWN");
    private final int version;
    private final int snapshotVersion;
    private final String name;
    private final boolean versionWildcard;
    private final Set<String> includedVersions;

    public static ProtocolVersion register(int version, String name) {
        return ProtocolVersion.register(version, -1, name);
    }

    public static ProtocolVersion register(int version, int snapshotVersion, String name) {
        return ProtocolVersion.register(version, snapshotVersion, name, null);
    }

    public static ProtocolVersion register(int version, String name, @Nullable VersionRange versionRange) {
        return ProtocolVersion.register(version, -1, name, versionRange);
    }

    public static ProtocolVersion register(int version, int snapshotVersion, String name, @Nullable VersionRange versionRange) {
        ProtocolVersion protocol = new ProtocolVersion(version, snapshotVersion, name, versionRange);
        VERSION_LIST.add(protocol);
        VERSIONS.put(protocol.getVersion(), protocol);
        if (protocol.isSnapshot()) {
            VERSIONS.put(protocol.getFullSnapshotVersion(), protocol);
        }
        return protocol;
    }

    public static boolean isRegistered(int version) {
        return VERSIONS.containsKey(version);
    }

    public static @NonNull ProtocolVersion getProtocol(int version) {
        ProtocolVersion protocolVersion = (ProtocolVersion)VERSIONS.get(version);
        if (protocolVersion != null) {
            return protocolVersion;
        }
        return new ProtocolVersion(version, "Unknown (" + version + ")");
    }

    public static int getIndex(ProtocolVersion version) {
        return VERSION_LIST.indexOf(version);
    }

    public static List<ProtocolVersion> getProtocols() {
        return Collections.unmodifiableList(VERSION_LIST);
    }

    public static @Nullable ProtocolVersion getClosest(String protocol) {
        for (ProtocolVersion version : VERSIONS.values()) {
            String majorVersion;
            String name = version.getName();
            if (name.equals(protocol)) {
                return version;
            }
            if (!(version.isVersionWildcard() ? (majorVersion = name.substring(0, name.length() - 2)).equals(protocol) || protocol.startsWith(name.substring(0, name.length() - 1)) : version.isRange() && version.getIncludedVersions().contains(protocol))) continue;
            return version;
        }
        return null;
    }

    public ProtocolVersion(int version, String name) {
        this(version, -1, name, null);
    }

    public ProtocolVersion(int version, int snapshotVersion, String name, @Nullable VersionRange versionRange) {
        this.version = version;
        this.snapshotVersion = snapshotVersion;
        this.name = name;
        this.versionWildcard = name.endsWith(".x");
        Preconditions.checkArgument((!this.versionWildcard || versionRange == null ? 1 : 0) != 0, (Object)"A version cannot be a wildcard and a range at the same time!");
        if (versionRange != null) {
            this.includedVersions = new LinkedHashSet<String>();
            for (int i = versionRange.rangeFrom(); i <= versionRange.rangeTo(); ++i) {
                if (i == 0) {
                    this.includedVersions.add(versionRange.baseVersion());
                }
                this.includedVersions.add(versionRange.baseVersion() + "." + i);
            }
        } else {
            this.includedVersions = Collections.singleton(name);
        }
    }

    public int getVersion() {
        return this.version;
    }

    public int getSnapshotVersion() {
        Preconditions.checkArgument((boolean)this.isSnapshot());
        return this.snapshotVersion;
    }

    public int getFullSnapshotVersion() {
        Preconditions.checkArgument((boolean)this.isSnapshot());
        return 0x40000000 | this.snapshotVersion;
    }

    public int getOriginalVersion() {
        return this.snapshotVersion == -1 ? this.version : 0x40000000 | this.snapshotVersion;
    }

    public boolean isKnown() {
        return this.version != -1;
    }

    public boolean isRange() {
        return this.includedVersions.size() != 1;
    }

    public Set<String> getIncludedVersions() {
        return Collections.unmodifiableSet(this.includedVersions);
    }

    public boolean isVersionWildcard() {
        return this.versionWildcard;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSnapshot() {
        return this.snapshotVersion != -1;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProtocolVersion that = (ProtocolVersion)o;
        return this.version == that.version;
    }

    public int hashCode() {
        return this.version;
    }

    public String toString() {
        return String.format("%s (%d)", this.name, this.version);
    }
}

