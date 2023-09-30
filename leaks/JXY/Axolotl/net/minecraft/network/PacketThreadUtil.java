package net.minecraft.network;

import axolotl.Axolotl;
import axolotl.cheats.events.EventPacket;
import axolotl.cheats.events.EventType;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil
{
    private static final String __OBFID = "CL_00002306";

    public static void func_180031_a(final Packet p_180031_0_, final INetHandler p_180031_1_, IThreadListener p_180031_2_)
    {
        if (!p_180031_2_.isCallingFromMinecraftThread())
        {
            p_180031_2_.addScheduledTask(new Runnable()
            {
                private static final String __OBFID = "CL_00002305";
                public void run()
                {
                    EventPacket event = new EventPacket(p_180031_0_, EventType.PRE, 2);
                    Axolotl.INSTANCE.moduleManager.onEvent(event);
                    if(event.cancelled) {
                        return;
                    }
                    p_180031_0_.processPacket(p_180031_1_);
                    event.eventType = EventType.POST;
                    Axolotl.INSTANCE.moduleManager.onEvent(event);
                }
            });
            throw ThreadQuickExitException.field_179886_a;
        }
    }
}
