package de.jcm.discordgamesdk.voice;

import java.nio.charset.*;
import java.util.*;

public class VoiceInputMode
{
    private InputModeType type;
    private String shortcut;
    
    VoiceInputMode(final int type, final String shortcut) {
        this(javaValue(type), shortcut);
    }
    
    public VoiceInputMode(final InputModeType type, final String shortcut) {
        this.type = type;
        this.shortcut = shortcut;
    }
    
    public InputModeType getType() {
        return this.type;
    }
    
    public void setType(final InputModeType type) {
        this.type = type;
    }
    
    public String getShortcut() {
        return this.shortcut;
    }
    
    public void setShortcut(final String shortcut) {
        if (shortcut.getBytes(StandardCharsets.UTF_8).length >= 256) {
            throw new IllegalArgumentException("max shortcut length is 255");
        }
        this.shortcut = shortcut;
    }
    
    @Override
    public String toString() {
        return "VoiceInputMode{type=" + this.type + ", shortcut='" + this.shortcut + '\'' + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final VoiceInputMode that = (VoiceInputMode)o;
        return this.type == that.type && Objects.equals(this.shortcut, that.shortcut);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.shortcut);
    }
    
    int getNativeType() {
        return this.getType().nativeValue();
    }
    
    public enum InputModeType
    {
        VOICE_ACTIVITY, 
        PUSH_TO_TALK;
        
        private static final int OFFSET = 0;
        
        private int nativeValue() {
            return this.ordinal() + 0;
        }
        
        private static InputModeType javaValue(final int nativeValue) {
            return values()[nativeValue - 0];
        }
    }
}
