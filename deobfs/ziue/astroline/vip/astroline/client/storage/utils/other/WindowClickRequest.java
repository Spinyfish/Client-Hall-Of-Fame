/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.storage.utils.other;

public abstract class WindowClickRequest {
    private boolean completed;

    public abstract void performRequest();

    public boolean isCompleted() {
        return this.completed;
    }

    public void onCompleted() {
        this.completed = true;
    }
}
