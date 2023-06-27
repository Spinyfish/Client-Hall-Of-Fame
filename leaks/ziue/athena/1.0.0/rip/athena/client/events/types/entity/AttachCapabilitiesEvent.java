package rip.athena.client.events.types.entity;

import rip.athena.client.events.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import com.google.common.collect.*;
import java.util.*;

public class AttachCapabilitiesEvent extends Event
{
    private Entity entity;
    private Map<ResourceLocation, ICapabilityProvider> caps;
    private Map<ResourceLocation, ICapabilityProvider> view;
    
    public AttachCapabilitiesEvent(final Entity entity) {
        this.caps = (Map<ResourceLocation, ICapabilityProvider>)Maps.newLinkedHashMap();
        this.view = Collections.unmodifiableMap((Map<? extends ResourceLocation, ? extends ICapabilityProvider>)this.caps);
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    public void addCapability(final ResourceLocation key, final ICapabilityProvider cap) {
        if (this.caps.containsKey(key)) {
            throw new IllegalStateException("Duplicate Capability Key: " + key + " " + cap);
        }
        this.caps.put(key, cap);
    }
    
    public Map<ResourceLocation, ICapabilityProvider> getCapabilities() {
        return this.view;
    }
}
