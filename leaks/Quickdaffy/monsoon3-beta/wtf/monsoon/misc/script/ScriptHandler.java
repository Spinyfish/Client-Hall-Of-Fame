/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.misc.script;

import wtf.monsoon.impl.event.EventPacket;

public interface ScriptHandler {
    public void onEnable();

    public void onDisable();

    public void onUpdate();

    public void onRender2D();

    public void onPacket(EventPacket var1);
}

