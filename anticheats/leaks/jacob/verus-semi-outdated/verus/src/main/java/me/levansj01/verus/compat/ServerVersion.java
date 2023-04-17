package me.levansj01.verus.compat;

public enum ServerVersion {
    
    NONE("NONE", 13), 
    v1_16_R3("v1_16_R3", 9), 
    v1_12_R1("v1_12_R1", 3), 
    v1_14_R1("v1_14_R1", 5), 
    v1_16_R2("v1_16_R2", 8), 
    v1_8_R3("v1_8_R3", 1), 
    v1_15_R1("v1_15_R1", 6), 
    v1_7_R4("v1_7_R4", 0), 
    v1_13_R2("v1_13_R2", 4), 
    v1_16_R1("v1_16_R1", 7), 
    v1_11_R1("v1_11_R1", 2), 
    v1_17_R1("v1_17_R1", 10), 
    v1_18_R1("v1_18_R1", 11), 
    v1_18_R2("v1_18_R2", 12);
    
    private ServerVersion(final String s, final int n) {
    }
    
    public boolean before(final ServerVersion serverVersion) {
        return serverVersion.ordinal() > this.ordinal();
    }
    
    public boolean after(final ServerVersion serverVersion) {
        return this.ordinal() > serverVersion.ordinal();
    }
    
    public boolean beforeEq(final ServerVersion serverVersion) {
        return serverVersion.ordinal() >= this.ordinal();
    }
    
    public boolean afterEq(final ServerVersion serverVersion) {
        return this.ordinal() >= serverVersion.ordinal();
    }
    
    private static ServerVersion[] $values() {
        return new ServerVersion[] { ServerVersion.v1_7_R4, ServerVersion.v1_8_R3, ServerVersion.v1_11_R1, ServerVersion.v1_12_R1, ServerVersion.v1_13_R2, ServerVersion.v1_14_R1, ServerVersion.v1_15_R1, ServerVersion.v1_16_R1, ServerVersion.v1_16_R2, ServerVersion.v1_16_R3, ServerVersion.v1_17_R1, ServerVersion.v1_18_R1, ServerVersion.v1_18_R2, ServerVersion.NONE };
    }
}
