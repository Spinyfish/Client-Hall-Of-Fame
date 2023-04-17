package club.mineman.antigamingchair.client;

public enum ClientType {
    COSMIC_CLIENT("COSMIC_CLIENT", 0, false, "CosmicClient"),
    CHEAT_BREAKER("CHEAT_BREAKER", 1, false, "CheatBreaker"),
    VANILLA("VANILLA", 2, false, "Vanilla"),
    FORGE("FORGE", 3, false, "Forge"),
    OC_MC("OC_MC", 4, false, "OCMC"),
    HACKED_CLIENT_A("HACKED_CLIENT_A", 5, true, "Hacked Client A"),
    HACKED_CLIENT_B("HACKED_CLIENT_B", 6, true, "Hacked Client B"),
    HACKED_CLIENT_C("HACKED_CLIENT_C", 7, true, "Hacked Client C"),
    HACKED_CLIENT_D("HACKED_CLIENT_D", 8, true, "Hacked Client D"),
    HACKED_CLIENT_E("HACKED_CLIENT_E", 9, true, "Hacked Client E"),
    HACKED_CLIENT_F("HACKED_CLIENT_F", 10, true, "Hacked Client F");

    private final boolean hacked;
    private final String name;

    ClientType(final String s, final int n, final boolean hacked, final String name) {
        this.hacked = hacked;
        this.name = name;
    }

    public boolean isHacked() {
        return this.hacked;
    }

    public String getName() {
        return this.name;
    }
}
