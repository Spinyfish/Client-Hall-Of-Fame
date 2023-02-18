/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.service.command;

public abstract class Command {
    private final String[] names;
    private final String description;

    public Command(String[] names, String description) {
        this.names = names;
        this.description = description;
    }

    public String getName() {
        return this.names[0];
    }

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract String executeCommand(String var1, String[] var2);
}
