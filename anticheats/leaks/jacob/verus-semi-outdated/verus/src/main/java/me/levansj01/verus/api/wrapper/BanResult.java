package me.levansj01.verus.api.wrapper;

public enum BanResult {
    CANCEL, ANNOUNCE, SILENT;

    public boolean isCancel() {
        return this == CANCEL;
    }

    public boolean isBan() {
        return this == SILENT;
    }

    public boolean isAnnounce() {
        return this == ANNOUNCE;
    }

}
