/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.layout.altMgr;

public class Alt {
    public String email;
    public String name;
    public String password;
    public boolean cracked;
    private boolean unchecked;
    public boolean starred;
    String message = "Null";

    public Alt(String email, String password, String name, String message) {
        this(email, password, name, false);
        this.message = message;
    }

    public Alt(String email, String password, String name) {
        this(email, password, name, false);
    }

    public Alt(String email, String password, String name, boolean starred) {
        this.email = email;
        this.starred = starred;
        if (password == null || password.isEmpty()) {
            this.cracked = true;
            this.unchecked = false;
            this.name = email;
            this.password = null;
        } else {
            this.cracked = false;
            this.unchecked = name == null || name.isEmpty();
            this.name = name;
            this.password = password;
        }
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getNameOrEmail() {
        return this.unchecked ? this.email : this.name;
    }

    public String getPassword() {
        if (this.password != null) {
            if (!this.password.isEmpty()) return this.password;
        }
        this.cracked = true;
        return "";
    }

    public boolean isCracked() {
        return this.cracked;
    }

    public boolean isStarred() {
        return this.starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public boolean isUnchecked() {
        return this.unchecked;
    }

    public void setChecked(String name) {
        this.name = name;
        this.unchecked = false;
    }
}
