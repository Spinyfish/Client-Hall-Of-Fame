package me.levansj01.verus.type;

public enum VerusType {

    REGULAR,
    CUSTOM,
    ENTERPRISE,
    PREMIUM,
    DEV;

    public boolean after(VerusType type) {
        return this.ordinal() > type.ordinal();
    }

    public boolean afterOrEq(VerusType type) {
        return this.ordinal() >= type.ordinal();
    }

    public boolean before(VerusType type) {
        return this.ordinal() < type.ordinal();
    }
}