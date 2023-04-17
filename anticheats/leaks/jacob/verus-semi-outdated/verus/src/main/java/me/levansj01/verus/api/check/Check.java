package me.levansj01.verus.api.check;

import com.google.common.base.Objects;

public class Check {
    private final String type;

    private final String subType;

    private final String display;

    public Check(String type, String subType) {
        this(type, subType, null);
    }

    public Check(String type, String subType, String display) {
        if (type == null)
            throw new IllegalArgumentException("type cannot be null");
        if (subType == null)
            throw new IllegalArgumentException("subType cacnnot be null");
        this.type = type;
        this.subType = subType;
        this.display = display;
    }

    public String getType() {
        return this.type;
    }

    public String getSubType() {
        return this.subType;
    }

    public String getDisplay() {
        return this.display;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Check check = (Check)o;
        return (this.type.equalsIgnoreCase(check.type) && this.subType.equalsIgnoreCase(check.subType));
    }

    public int hashCode() {
        return Objects.hashCode(this.type.toLowerCase(), this.subType.toLowerCase());
    }
}