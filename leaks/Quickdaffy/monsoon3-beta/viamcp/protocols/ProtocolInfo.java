/*
 * Decompiled with CFR 0.152.
 */
package viamcp.protocols;

public class ProtocolInfo {
    private final String name;
    private final String description;
    private final String releaseDate;

    public ProtocolInfo(String name, String description, String releaseDate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }
}

