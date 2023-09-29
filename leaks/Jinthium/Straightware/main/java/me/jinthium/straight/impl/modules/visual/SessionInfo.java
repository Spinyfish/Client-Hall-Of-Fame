package me.jinthium.straight.impl.modules.visual;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.dragging.Dragging;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.ServerJoinEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import org.lwjglx.Sys;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class SessionInfo extends Module {

    private final Dragging sessionDrag = Client.INSTANCE.createDrag(this, "sessioninfo", 5, 100);
    private String time = "0 seconds";
    private long startTime = System.currentTimeMillis();

    public SessionInfo(){
        super("SessionInfo", Category.VISUALS);
        this.addSettings();
    }

    @Callback
    final EventCallback<ServerJoinEvent> serverJoinEventEventCallback = event -> {
        startTime = System.currentTimeMillis();
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        if(event.isPre()){
            if (mc.thePlayer.ticksExisted % 10 == 0) {
                long elapsed = System.currentTimeMillis() - startTime;
                long hours = TimeUnit.MILLISECONDS.toHours(elapsed);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed) % 60;

                this.time = String.format("%sh, %sm, %ss", hours, minutes, seconds);
            }
        }
    };

    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        sessionDrag.setHeight(30);


//        boolean change = System.currentTimeMillis() % 2 == 0;
//        RenderUtil.drawCircle(100F, 100F, 30F, (float) (1f - MathUtils.calculatePercentage(startTime, System.currentTimeMillis())), change ? 1 : -1, Color.white, 1f);

        RoundedUtil.drawRound(sessionDrag.getX(), sessionDrag.getY(), sessionDrag.getWidth(), sessionDrag.getHeight(), 4, new Color(0, 0, 0, 150));
        normalFont22.drawStringWithShadow("Time Played:", (sessionDrag.getX() + sessionDrag.getWidth() - normalFont22.getStringWidth("Time Played:") / 2f)
                , sessionDrag.getY() + normalFont22.getHeight(), -1);
        normalFont22.drawStringWithShadow(time, sessionDrag.getX() + (sessionDrag.getWidth() / 1.2f - normalFont22.getStringWidth(time)), sessionDrag.getY() + sessionDrag.getHeight() - normalFont22.getHeight(), -1);
    };
}
