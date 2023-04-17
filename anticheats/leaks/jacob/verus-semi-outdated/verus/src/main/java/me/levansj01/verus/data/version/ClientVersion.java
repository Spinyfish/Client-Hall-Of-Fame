package me.levansj01.verus.data.version;

public enum ClientVersion {
    V1_18("V1_18"),
    V1_17("V1_17"),
    V1_16("V1_16"),
    V1_15("V1_15"),
    V1_14("V1_14"),
    V1_13("V1_13"),
    V1_12("V1_12"),
    V1_11("V1_11"),
    V1_10("V1_10"),
    V1_9("V1_9"),
    V1_8("V1_8"),
    V1_7("V1_7"),
    NONE("NONE"),
    VERSION_UNSUPPORTED("VERSION_UNSUPPORTED");

    private final String name;

    ClientVersion(String name) {
        this.name = name;
    }

    public boolean after(ClientVersion clientVersion) {
        return this.ordinal() > clientVersion.ordinal();
    }

    public boolean before(ClientVersion clientVersion) {
        return this.ordinal() < clientVersion.ordinal();
    }

    public ClientVersion next() {
        ClientVersion[] clientVersions = values();
        int ordinal = this.ordinal();
        ordinal += 1;
        return clientVersions[Math.min(ordinal, clientVersions.length - 1)];
    }

    public String getName() {
        return this.name;
    }

    public boolean afterEq(ClientVersion clientVersion) {
        return this.ordinal() >= clientVersion.ordinal();
    }

}
