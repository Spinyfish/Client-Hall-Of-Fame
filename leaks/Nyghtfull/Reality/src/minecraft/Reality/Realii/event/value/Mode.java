/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.event.value;

public class Mode<V extends String>
        extends Value<V> {
    private V[] modes;

    public Mode(String displayName, String name, V[] modes, V value) {
        super(displayName, name);
        this.modes = modes;
        this.setValue(value);
    }

    public Mode(String displayName, String name, V[] modes, V value, boolean visible) {
        super(displayName, name);
        this.modes = modes;
        this.setValue(value);
    }

    public V[] getModes() {
        return this.modes;
    }

    public String getModeAsString() {
        return this.getValue();
    }

    public void setMode(String mode) {
        V[] arrV = this.modes;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            V e = arrV[n2];
            if (e.equalsIgnoreCase(mode)) {
                this.setValue(e);
            }
            ++n2;
        }
    }

    public boolean isValid(String name) {
        V[] arrV = this.modes;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            V e = arrV[n2];
            if (e.equalsIgnoreCase(name)) {
                return true;
            }
            ++n2;
        }
        return false;
    }
}

