/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.misc.script;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.impl.event.EventUpdate;

public class ScriptController
extends Module {
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> this.setEnabled(false);

    public ScriptController() {
        super("ScriptOptions", "Control Scripts", Category.SCRIPT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.setEnabled(false);
    }
}

