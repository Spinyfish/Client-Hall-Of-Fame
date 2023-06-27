package net.minecraft.client.gui.spectator;

import java.util.*;
import com.google.common.collect.*;
import net.minecraft.client.gui.spectator.categories.*;
import net.minecraft.util.*;

public class BaseSpectatorGroup implements ISpectatorMenuView
{
    private final List<ISpectatorMenuObject> field_178671_a;
    
    public BaseSpectatorGroup() {
        (this.field_178671_a = (List<ISpectatorMenuObject>)Lists.newArrayList()).add(new TeleportToPlayer());
        this.field_178671_a.add(new TeleportToTeam());
    }
    
    @Override
    public List<ISpectatorMenuObject> func_178669_a() {
        return this.field_178671_a;
    }
    
    @Override
    public IChatComponent func_178670_b() {
        return new ChatComponentText("Press a key to select a command, and again to use it.");
    }
}
