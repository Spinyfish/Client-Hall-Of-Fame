/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.service.module.impl.player;

import com.google.gson.annotations.SerializedName;

class BanQuantityListJSON {
    @SerializedName(value="success")
    boolean success;
    @SerializedName(value="watchdog_lastMinute")
    int watchdogLastMinute;
    @SerializedName(value="staff_rollingDaily")
    int staffRollingDaily;
    @SerializedName(value="watchdog_total")
    int watchdogTotal;
    @SerializedName(value="watchdog_rollingDaily")
    int watchdogRollingDaily;
    @SerializedName(value="staff_total")
    int staffTotal;

    public boolean isSuccess() {
        return this.success;
    }

    public int getWatchdogLastMinute() {
        return this.watchdogLastMinute;
    }

    public int getStaffRollingDaily() {
        return this.staffRollingDaily;
    }

    public int getWatchdogTotal() {
        return this.watchdogTotal;
    }

    public int getWatchdogRollingDaily() {
        return this.watchdogRollingDaily;
    }

    public int getStaffTotal() {
        return this.staffTotal;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setWatchdogLastMinute(int watchdogLastMinute) {
        this.watchdogLastMinute = watchdogLastMinute;
    }

    public void setStaffRollingDaily(int staffRollingDaily) {
        this.staffRollingDaily = staffRollingDaily;
    }

    public void setWatchdogTotal(int watchdogTotal) {
        this.watchdogTotal = watchdogTotal;
    }

    public void setWatchdogRollingDaily(int watchdogRollingDaily) {
        this.watchdogRollingDaily = watchdogRollingDaily;
    }

    public void setStaffTotal(int staffTotal) {
        this.staffTotal = staffTotal;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BanQuantityListJSON)) {
            return false;
        }
        BanQuantityListJSON other = (BanQuantityListJSON)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        if (this.getWatchdogLastMinute() != other.getWatchdogLastMinute()) {
            return false;
        }
        if (this.getStaffRollingDaily() != other.getStaffRollingDaily()) {
            return false;
        }
        if (this.getWatchdogTotal() != other.getWatchdogTotal()) {
            return false;
        }
        if (this.getWatchdogRollingDaily() != other.getWatchdogRollingDaily()) {
            return false;
        }
        if (this.getStaffTotal() == other.getStaffTotal()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof BanQuantityListJSON;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        result = result * 59 + this.getWatchdogLastMinute();
        result = result * 59 + this.getStaffRollingDaily();
        result = result * 59 + this.getWatchdogTotal();
        result = result * 59 + this.getWatchdogRollingDaily();
        result = result * 59 + this.getStaffTotal();
        return result;
    }

    public String toString() {
        return "BanQuantityListJSON(success=" + this.isSuccess() + ", watchdogLastMinute=" + this.getWatchdogLastMinute() + ", staffRollingDaily=" + this.getStaffRollingDaily() + ", watchdogTotal=" + this.getWatchdogTotal() + ", watchdogRollingDaily=" + this.getWatchdogRollingDaily() + ", staffTotal=" + this.getStaffTotal() + ")";
    }
}
