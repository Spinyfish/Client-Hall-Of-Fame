package rip.athena.client.macros;

public class Macro
{
    private String name;
    private String command;
    private boolean enabled;
    private int key;
    
    public Macro(final String name, final String command, final int key, final boolean enabled) {
        this.name = name;
        this.command = command;
        this.key = key;
        this.enabled = enabled;
    }
    
    public Macro(final String name, final String command, final int key) {
        this(name, command, key, true);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public int getKey() {
        return this.key;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setKey(final int key) {
        this.key = key;
    }
}
