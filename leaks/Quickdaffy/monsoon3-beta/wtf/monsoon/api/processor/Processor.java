/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.processor;

import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.obj.MonsoonPlayerObject;

public abstract class Processor {
    public MonsoonPlayerObject player = Wrapper.getMonsoon().getPlayer();
    public Minecraft mc = Wrapper.getMinecraft();
}

