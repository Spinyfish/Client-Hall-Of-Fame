package co.gongzh.procbridge;

public final class Versions
{
    private static final byte[] V1_0;
    private static final byte[] V1_1;
    static final byte[] CURRENT;
    
    public static byte[] getCurrent() {
        return Versions.CURRENT.clone();
    }
    
    private Versions() {
    }
    
    static {
        V1_0 = new byte[] { 1, 0 };
        V1_1 = new byte[] { 1, 1 };
        CURRENT = Versions.V1_1;
    }
}
