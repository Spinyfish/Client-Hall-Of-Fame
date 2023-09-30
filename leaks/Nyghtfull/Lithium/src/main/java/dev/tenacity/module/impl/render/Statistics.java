package dev.tenacity.module.impl.render;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.objects.GradientColorWheel;
import dev.tenacity.utils.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

public class Statistics extends Module {

    public static int gamesPlayed, killCount, deathCount;
    public static long startTime = System.currentTimeMillis(), endTime = -1;
    public static final String[] KILL_TRIGGERS = {"by *", "para *", "fue destrozado a manos de *"};
    private final Map<String, Double> statistics = new LinkedHashMap<>();
    private final BooleanSetting motionGraph = new BooleanSetting("Show Speed Graph", true);
    private final BooleanSetting seprateMotionGraph = new BooleanSetting("Separate Graph", true);

    private final Dragging dragging = Tenacity.INSTANCE.createDrag(this, "sessionstats", 5, 150);
    private final Dragging motionDragging = Tenacity.INSTANCE.createDrag(this, "motionGraph", 5, 200);

    private final GradientColorWheel colorWheel = new GradientColorWheel();


    public Statistics() {
        super("Statistics", Category.RENDER, "Displays statistics about your session");
        seprateMotionGraph.addParent(motionGraph, ParentAttribute.BOOLEAN_CONDITION);
        addSettings(colorWheel.createModeSetting("Color Mode"), colorWheel.getColorSetting(), motionGraph, seprateMotionGraph);
    }

    private float width, height;

    @Override
    public void onShaderEvent(ShaderEvent e) {
        float x = this.dragging.getX(), y = this.dragging.getY();
        boolean seperated = motionGraph.isEnabled() && seprateMotionGraph.isEnabled();
        if (e.getBloomOptions().getSetting("Statistics").isEnabled()) {
            RoundedUtil.drawGradientRound(x, y, width, height, 6, colorWheel.getColor1(), colorWheel.getColor4(), colorWheel.getColor2(), colorWheel.getColor3());

            if (seperated) {
                RoundedUtil.drawGradientRound(motionDragging.getX(), motionDragging.getY(),
                        motionDragging.getWidth(), motionDragging.getHeight(), 6, colorWheel.getColor1(),
                        colorWheel.getColor4(), colorWheel.getColor2(), colorWheel.getColor3());
            }

        } else {
            RoundedUtil.drawRound(x, y, width, height, 6, Color.BLACK);

            if (seperated) {
                RoundedUtil.drawRound(motionDragging.getX(), motionDragging.getY(),
                        motionDragging.getWidth(), motionDragging.getHeight(), 6, Color.BLACK);
            }
        }

    }

    private final ShaderUtil circleShader = new ShaderUtil("Tenacity/Shaders/circle-arc.frag");

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        float x = this.dragging.getX(), y = this.dragging.getY();
        boolean moreHeight = motionGraph.isEnabled() && !seprateMotionGraph.isEnabled();
        boolean seperated = motionGraph.isEnabled() && seprateMotionGraph.isEnabled();


        motionDragging.setWidth(seperated ? width : 0);
        motionDragging.setHeight(seperated ? 75 : 0);


        width = 145;
        float orginalHeight = statistics.size() * (lithiumBoldFont18.getHeight() + 6) + 26;
        height = orginalHeight + (moreHeight ? 75 : 0);

        dragging.setHeight(height);
        dragging.setWidth(width);

        colorWheel.setColorsForMode("Dark", ColorUtil.brighter(new Color(30, 30, 30), .65f));
        colorWheel.setColors();

        float alpha = colorWheel.getColorMode().is("Dark") ? 1 : .85f;
        RoundedUtil.drawGradientRound(x, y, width, height, 6,
                ColorUtil.applyOpacity(colorWheel.getColor1(), alpha),
                ColorUtil.applyOpacity(colorWheel.getColor4(), alpha),
                ColorUtil.applyOpacity(colorWheel.getColor2(), alpha),
                ColorUtil.applyOpacity(colorWheel.getColor3(), alpha));


        lithiumBoldFont22.drawString("Statistics", x + 5, y + 2, -1);

        float underlineWidth = lithiumBoldFont22.getStringWidth("Statistics");

        RoundedUtil.drawRound(x + 5, y + 2 + lithiumBoldFont22.getHeight() + 1, underlineWidth - .5f, 1f, .5f, Color.white);

        statistics.put("Games Played", (double) gamesPlayed);
        statistics.put("K/D", deathCount == 0 ? killCount : MathUtils.round((double) killCount / deathCount, 2));
        statistics.put("Kills", (double) killCount);

        int count = 0;
        for (Map.Entry<String, Double> entry : statistics.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            int offset = count * (lithiumBoldFont18.getHeight() + 7);
            lithiumBoldFont18.drawString(key + ": ", x + 5, y + offset + 21, -1);
            lithiumFont18.drawString(key.equals("K/D") ? String.valueOf(value.doubleValue()) : String.valueOf(value.intValue()),
                    x + 5 + lithiumBoldFont18.getStringWidth(key + ": "), y + offset + 21, -1);

            count++;
        }


        float radius = 40;

        float playtimeX = x + width - (lithiumBoldFont20.getStringWidth("Play Time") + 6);
        lithiumBoldFont20.drawString("Play Time", x + width - (lithiumBoldFont20.getStringWidth("Play Time") + 5), y + 4, -1);
        float playUnderlineWidth = lithiumBoldFont20.getStringWidth("Play Time");

        RoundedUtil.drawRound(x + width - (lithiumBoldFont20.getStringWidth("Play Time") + 5),
                y + 4 + lithiumBoldFont22.getHeight(), playUnderlineWidth - .5f, 1, .5f, Color.white);

        int[] playTime = getPlayTime();

        float circleY = y + (4) + lithiumBoldFont22.getHeight() + 2;

        drawCircle(playtimeX - 1.5f, circleY, radius, -2f, 1,
                ColorUtil.applyOpacity(Color.BLACK, .5f), 1);

        int[] playTimeActual = getPlayTime();
        boolean change = playTime[0] % 2 == 0;

        float percentage = (playTime[1] + (playTime[2] / 60f)) / 60f;

        drawCircle(playtimeX - 1.5f, circleY, radius, 1 - percentage, change ? 1 : -1,
                Color.WHITE, .05f);


        drawAnimatedPlaytime(playtimeX, circleY + ((radius + 10) / 2f - lithiumFont16.getHeight() / 2f), (radius + 10), playTimeActual);


        if (motionGraph.isEnabled()) {
            if (seperated) {
                RoundedUtil.drawGradientRound(motionDragging.getX(), motionDragging.getY(),
                        motionDragging.getWidth(), motionDragging.getHeight(), 6,
                        ColorUtil.applyOpacity(colorWheel.getColor1(), alpha),
                        ColorUtil.applyOpacity(colorWheel.getColor4(), alpha),
                        ColorUtil.applyOpacity(colorWheel.getColor2(), alpha),
                        ColorUtil.applyOpacity(colorWheel.getColor3(), alpha));
                drawMotionGraph(motionDragging.getX(), motionDragging.getY(), motionDragging.getWidth(), motionDragging.getHeight());
            } else {
                drawMotionGraph(x, y + height - 75, width, 75);
            }
        }
    }

    private final List<Float> speeds = new ArrayList<>();

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (event.isPre()) {
            if ((speeds.size() - 1) >= 100) {
                speeds.remove(0);
            }
            speeds.add(getPlayerSpeed());

        }
    }

    private void drawMotionGraph(float x, float y, float width, float height) {
        float textX = x + 5;
        lithiumBoldFont20.drawString("Speed", textX, y + 3, -1);
        float underlineWidth = lithiumBoldFont20.getStringWidth("Speed");
        //  RoundedUtil.drawRound(textX, y + 3 + lithiumBoldFont20.getHeight() + 2, underlineWidth - .5f, .9f, .25f, Color.white);

        double average = speeds.stream().collect(Collectors.averagingDouble(value -> value.doubleValue() * 50));
        average = Math.round(average * 100) / 100.0;

        String text = "Average: " + average + " BPS";

        lithiumFont18.drawString(text, x + width - (lithiumFont18.getStringWidth(text) + 5),
                y + 3.5f, -1);


        float lineHeight = height - (lithiumBoldFont20.getHeight() + 16);
        float lineWidth = width - 10;
        float lineX = x + 5;
        float lineY = y + height - 5;
        float distance = 8 + lithiumBoldFont20.getHeight();

        RoundedUtil.drawRound(lineX - 3, y + distance, lineWidth + 6, height - (distance + 2),
                5, ColorUtil.applyOpacity(Color.BLACK, .25f));

        glPushMatrix();
        GLUtil.setup2DRendering();
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
        glBegin(GL_LINES);

        int count = 0;
        if (speeds.size() > 3) {
            for (float speed : speeds) {
                if (count >= speeds.size() - 1) continue;
                RenderUtil.color(-1);
                float speedY = speed * lineHeight;
                float nextSpeedY = speeds.get(count + 1) * lineHeight;
                float length = lineWidth / (speeds.size() - 1);

                glVertex2f(lineX + (count * length), lineY - Math.min(speedY, lineHeight));
                glVertex2f(lineX + ((count + 1) * length), lineY - Math.min(nextSpeedY, lineHeight));
                count++;
            }
        }

        glEnd();


        glDisable(GL_LINE_SMOOTH);
        GLUtil.end2DRendering();
        glPopMatrix();

    }


    private void drawAnimatedPlaytime(float circleX, float y, float circleWidth, int[] playTime) {
        String seconds = ((playTime[2] < 10) ? "0" : "") + playTime[2];
        String minutes = ((playTime[1] < 10) ? "0" : "") + playTime[1];

        StringBuilder sb = new StringBuilder(seconds);
        if ((playTime[1] > 0) || playTime[0] > 0) {
            sb.insert(0, minutes + ":");
        }
        if (playTime[0] > 0) {
            sb.insert(0, playTime[0] + ":");
        }

        lithiumFont16.drawCenteredString(sb.toString(), (circleX - 1.5f) + (circleWidth / 2f), y, -1);
    }

    private void drawCircle(float x, float y, float radius, float progress, int change, Color color, float smoothness) {
        GLUtil.startBlend();
        float borderThickness = 1;
        circleShader.init();
        circleShader.setUniformf("radialSmoothness", smoothness);
        circleShader.setUniformf("radius", radius);
        circleShader.setUniformf("borderThickness", borderThickness);
        circleShader.setUniformf("progress", progress);
        circleShader.setUniformi("change", change);
        circleShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        float wh = radius + 10;
        ScaledResolution sr = new ScaledResolution(mc);
        circleShader.setUniformf("pos", (x + ((wh / 2f) - ((radius + borderThickness) / 2f))) * sr.getScaleFactor(),
                (Minecraft.getMinecraft().displayHeight - ((radius + borderThickness) * sr.getScaleFactor())) - ((y + ((wh / 2f) - ((radius + borderThickness) / 2f))) * sr.getScaleFactor()));
        ShaderUtil.drawQuads(x, y, wh, wh);
        circleShader.unload();
        GLUtil.endBlend();
    }

    public static int[] getPlayTime() {
        long diff = getTimeDiff();
        long diffSeconds = 0, diffMinutes = 0, diffHours = 0;
        if (diff > 0) {
            diffSeconds = diff / 1000 % 60;
            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
        }
       /* String str = (int) diffSeconds + "s";
        if (diffMinutes > 0) str = (int) diffMinutes + "m " + str;
        if (diffHours > 0) str = (int) diffHours + "h " + str;*/
        return new int[]{(int) diffHours, (int) diffMinutes, (int) diffSeconds};
    }

    public static long getTimeDiff() {
        return (endTime == -1 ? System.currentTimeMillis() : endTime) - startTime;
    }

    public static void reset() {
        startTime = System.currentTimeMillis();
        endTime = -1;
        gamesPlayed = 0;
        killCount = 0;
    }

    private float getPlayerSpeed() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        return (float) bps / 50;
    }

    @Override
    public void onEnable() {
        speeds.clear();
        super.onEnable();
    }
}
