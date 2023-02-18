/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.layout.altMgr.kingAlts;

import com.google.gson.annotations.SerializedName;

public class ProfileJson {
    @SerializedName(value="username")
    String username;
    @SerializedName(value="generated")
    int generated;
    @SerializedName(value="generatedToday")
    int generatedToday;
    @SerializedName(value="message")
    String message;

    public String getUsername() {
        return this.username;
    }

    public int getGenerated() {
        return this.generated;
    }

    public int getGeneratedToday() {
        return this.generatedToday;
    }

    public String getMessage() {
        return this.message;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGenerated(int generated) {
        this.generated = generated;
    }

    public void setGeneratedToday(int generatedToday) {
        this.generatedToday = generatedToday;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProfileJson)) {
            return false;
        }
        ProfileJson other = (ProfileJson)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getGenerated() != other.getGenerated()) {
            return false;
        }
        if (this.getGeneratedToday() != other.getGeneratedToday()) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null) {
            if (other$message == null) return true;
        } else if (this$message.equals(other$message)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ProfileJson;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getGenerated();
        result = result * 59 + this.getGeneratedToday();
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "ProfileJson(username=" + this.getUsername() + ", generated=" + this.getGenerated() + ", generatedToday=" + this.getGeneratedToday() + ", message=" + this.getMessage() + ")";
    }
}
