/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.layout.altMgr.kingAlts;

import com.google.gson.annotations.SerializedName;

public class AltJson {
    @SerializedName(value="email")
    String email;
    @SerializedName(value="password")
    String password;
    @SerializedName(value="message")
    String message;

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getMessage() {
        return this.message;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AltJson)) {
            return false;
        }
        AltJson other = (AltJson)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$password = this.getPassword();
        String other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
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
        return other instanceof AltJson;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "AltJson(email=" + this.getEmail() + ", password=" + this.getPassword() + ", message=" + this.getMessage() + ")";
    }
}
