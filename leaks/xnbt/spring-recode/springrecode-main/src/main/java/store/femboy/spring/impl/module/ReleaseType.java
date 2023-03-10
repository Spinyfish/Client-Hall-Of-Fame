package store.femboy.spring.impl.module;

public enum ReleaseType {
    PUBLIC("Public"),
    BETA("Beta"),
    DEV("Dev");

    private static String releaseName;

    ReleaseType(String releaseName){
        ReleaseType.setReleaseName(releaseName);
    }


    public static String getReleaseName() {
        return releaseName;
    }

    public static void setReleaseName(String releaseName) {
        ReleaseType.releaseName = releaseName;
    }
}
