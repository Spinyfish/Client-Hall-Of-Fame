package dev.elysium.client.utils.render;

import java.awt.*;

public class ColorAnimation {

    public static int getColor(int startColor, int endColor, float prog) {
        float progress = prog;
        /**if(progress > 1) {
            throw new IndexOutOfBoundsException("progress is greater than 1");
        } else if(progress < 0) {
            throw new IndexOutOfBoundsException("progress is less than 0");
        }**/ //Why
        // oh shit sorry xd

        progress = Math.min(Math.max(progress,0),1); //:pray:

        BColor color = new BColor(-1);

        int alphaStart = startColor >> 24 & 255;
        int redStart = startColor >> 16 & 255;
        int greenStart = startColor >> 8 & 255;
        int blueStart = startColor & 255;

        int alphaEnd = endColor >> 24 & 255;
        int redEnd = endColor >> 16 & 255;
        int greenEnd = endColor >> 8 & 255;
        int blueEnd = endColor & 255;

        int[] start = {redStart, greenStart, blueStart, alphaStart};
        int[] end = {redEnd, greenEnd, blueEnd, alphaEnd};

        int[] fin = {0, 0, 0, 0};

        int[] diffs = {redStart - redEnd, greenStart - greenEnd, blueStart - blueEnd, alphaStart - alphaEnd};

        for(int i = 0; i <= 3; i++) {
            double floor = Math.abs(Math.floor(diffs[i] * progress));
            if(start[i] > end[i]) {
                fin[i] = end[i] + (int) floor;
            } else {
                fin[i] = start[i] + (int) floor;
            }
        }

        return new Color(fin[0], fin[1], fin[2], fin[3]).getRGB();
    }
}
