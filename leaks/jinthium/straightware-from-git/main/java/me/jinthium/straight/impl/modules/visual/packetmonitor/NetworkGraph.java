package me.jinthium.straight.impl.modules.visual.packetmonitor;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.dragging.Dragging;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;
import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;

public class NetworkGraph extends Module {

    private final Dragging graphDraggable = Client.INSTANCE.createDrag(this, "network-graph", 120, 25);
    private final PacketMonitor incomingPackets = new PacketMonitor();
    private final PacketMonitor outgoingPackets = new PacketMonitor();
    private final PacketMonitor sentC03Packets = new PacketMonitor();

    public NetworkGraph(){
        super("Network Graph", Category.VISUALS);
    }

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        switch (event.getPacketState()){
            case SENDING -> {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    this.sentC03Packets.increment();
                }

                this.outgoingPackets.increment();
            }
            case RECEIVING -> this.incomingPackets.increment();
        }
    };

    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        final int samples = 15;
        final int width = samples * 6;
        graphDraggable.setWidth(width + 50 + width);
        graphDraggable.setHeight(25);

        this.incomingPackets.update(samples + 1);
        this.outgoingPackets.update(samples + 1);
        this.sentC03Packets.update(samples + 1);

        glScaled(0.5, 0.5, 1);
        this.mc.fontRendererObj.drawStringWithShadow("Incoming packets", graphDraggable.getX() * 2 + 40, graphDraggable.getY() * 2 - 7, 0xFFFFFFFF);

        this.mc.fontRendererObj.drawStringWithShadow("Outgoing packets", (graphDraggable.getX() + width + 30) * 2 + 40, graphDraggable.getY() * 2 - 7, 0xFFFFFFFF);
        glScaled(2.0, 2.0, 1);

        this.drawNetGraph(graphDraggable.getX(), graphDraggable.getY() + 2, width, graphDraggable.getHeight(), samples,
                this.incomingPackets.getPacketRecord());
        this.drawNetGraph(graphDraggable.getX() + width + 30, graphDraggable.getY() + 2, width, graphDraggable.getHeight(), samples,
                this.outgoingPackets.getPacketRecord());
    };

    private void drawNetGraph(final float x,
                              final float y,
                              final float width,
                              final float height,
                              final int nSamplesDisplayed,
                              final Collection<Integer> packetSeconds) {
        RoundedUtil.drawRound(x, y, width, height, 0, new Color(0x40000000));

        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Enable line anti-aliasing
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        // Enable blending (required for anti-aliasing)
        glEnable(GL_BLEND);
        // Set width to 2px
        glLineWidth(1.0F);
        // Translate matrix to top right of graph
        glTranslated(x + width, y, 0);

        final double sampleWidth = (double) width / nSamplesDisplayed;
        double lineAlignment = 0;

        RenderUtil.color(0xFFFFFFFF);

        final int maxPackets = packetSeconds.stream()
                .max(Integer::compareTo)
                .orElse(1);

        final int packetDenominator = (int) (maxPackets * 1.25);

        int totalPackets = 0;

        RenderUtil.color(0xFFFFFFFF);

        // Start drawing lines
        glBegin(GL_LINE_STRIP);
        {
            for (final int packets : packetSeconds) {
                final double percentage = Math.min(1.0, (double) packets / packetDenominator);
                final double yPosition = height * percentage;

                glVertex2d(-lineAlignment, height - yPosition);

                lineAlignment += sampleWidth;

                totalPackets += packets;
            }
        }
        // Draw the line graph
        glEnd();

        final int avgPackets = totalPackets / nSamplesDisplayed;
        final double avgPercent = Math.min(1.0, (double) avgPackets / packetDenominator);
        final double avgPosition = height * avgPercent;

        RenderUtil.color(0xFF00FF00);

        // Draw average packets line
        glBegin(GL_LINES);
        {
            glVertex2d(-width, height - avgPosition);
            glVertex2d(0, height - avgPosition);
        }
        glEnd();

        // Enable texture drawing
        glEnable(GL_TEXTURE_2D);
        // Disable line anti-aliasing
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        // Disable blending
        glDisable(GL_BLEND);

        glScaled(0.5, 0.5, 1);
        this.mc.fontRendererObj.drawStringWithShadow(avgPackets + " avg", 2, (float) ((height - avgPosition) * 2) - 9 / 2.0F, 0xFFFFFFFF);
        glScaled(2.0, 2.0, 1);

        // Restore matrix
        glTranslated(-x - width, -y, 0);

        RenderUtil.outlinedRect(x, y, width, height, 0.5F, 0xFFFFFFFF);
    }

    @Override
    public void onEnable() {
        this.outgoingPackets.reset();
        this.sentC03Packets.reset();
        this.incomingPackets.reset();
        super.onEnable();
    }
}
