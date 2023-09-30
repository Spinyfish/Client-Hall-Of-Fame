/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Scoreboard;
import wtf.monsoon.api.event.Event;

public class EventRenderScoreboard
extends Event {
    @NonNull
    private Scoreboard scoreboard;
    @NonNull
    private ScaledResolution sr;
    @NonNull
    private int y;

    public EventRenderScoreboard(Scoreboard scoreboard, ScaledResolution sr, int y) {
        this.scoreboard = scoreboard;
        this.sr = sr;
        this.y = y;
    }

    @NonNull
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @NonNull
    public ScaledResolution getSr() {
        return this.sr;
    }

    @NonNull
    public int getY() {
        return this.y;
    }

    public void setScoreboard(@NonNull Scoreboard scoreboard) {
        if (scoreboard == null) {
            throw new NullPointerException("scoreboard is marked non-null but is null");
        }
        this.scoreboard = scoreboard;
    }

    public void setSr(@NonNull ScaledResolution sr) {
        if (sr == null) {
            throw new NullPointerException("sr is marked non-null but is null");
        }
        this.sr = sr;
    }

    public void setY(@NonNull int y) {
        this.y = y;
    }
}

