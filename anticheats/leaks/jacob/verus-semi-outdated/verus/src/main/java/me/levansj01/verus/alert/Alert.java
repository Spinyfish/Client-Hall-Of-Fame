package me.levansj01.verus.alert;

import java.util.UUID;
import java.util.function.Supplier;
import me.levansj01.verus.check.Check;

public class Alert {
    private String name;
    private int lag;
    private final long timestamp = System.currentTimeMillis();
    private final Supplier data;
    private int ping;
    private final Check check;
    private final int vl;
    private UUID uuid;

    public void setLag(int n) {
        this.lag = n;
    }

    public void setName(String string) {
        this.name = string;
    }

    public int getPing() {
        return this.ping;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public Check getCheck() {
        return this.check;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getLag() {
        return this.lag;
    }

    public Alert(Check check, Supplier supplier, int n) {
        this.check = check;
        this.data = supplier;
        this.vl = n;
    }

    public void setPing(int n) {
        this.ping = n;
    }

    public Supplier getData() {
        return this.data;
    }

    public int getVl() {
        return this.vl;
    }

    public void setUuid(UUID uUID) {
        this.uuid = uUID;
    }
}