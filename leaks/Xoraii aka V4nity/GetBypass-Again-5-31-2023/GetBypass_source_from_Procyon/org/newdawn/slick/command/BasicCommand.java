// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.command;

public class BasicCommand implements Command
{
    private String name;
    
    public BasicCommand(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public boolean equals(final Object other) {
        return other instanceof BasicCommand && ((BasicCommand)other).name.equals(this.name);
    }
    
    public String toString() {
        return "[Command=" + this.name + "]";
    }
}
