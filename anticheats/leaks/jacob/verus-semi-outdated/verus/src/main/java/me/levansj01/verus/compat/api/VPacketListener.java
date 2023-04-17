package me.levansj01.verus.compat.api;

import java.util.function.*;
import me.levansj01.verus.data.*;
import me.levansj01.verus.compat.*;

public abstract class VPacketListener implements Consumer
{
    protected final PlayerData data;
    
    protected abstract void process(final Object p0);
    
    public VPacketListener(final PlayerData data) {
        this.data = data;
    }
    
    @Override
    public void accept(final Object o) {
        if (!this.data.isEnabled()) {
            return;
        }
        this.process(o);
    }
    
    protected void handleIn(final Object o, final VPacket vPacket) {
        vPacket.accept(o);
        this.data.preProcess(vPacket);
        vPacket.handle(this.data);
        this.data.handlePacketListeners(vPacket);
        this.data.postProcess(vPacket);
    }
    
    protected void handleOut(final Object o, final VPacket vPacket) {
        vPacket.accept(o);
        vPacket.handle(this.data);
    }
}
