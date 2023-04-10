package crispy.util.render.particle;

import arithmo.gui.altmanager.Colors;
import arithmo.gui.altmanager.RenderUtil;
import crispy.util.animation.Translate;


public class Particle {
    private final float posX;
    private final float posY;
    private boolean yes;

    private int opacity;
    private Translate translate;
    private int offset;

    public Particle(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        opacity = randomNumber(255, 0);
        offset += randomNumber(9, 2);

    }

    public static int randomNumber(int max, int min) {
        return (int) Math.round(min + Math.random() * ((max - min)));
    }

    public void drawParticle() {

        if (yes) {
            opacity += offset;
        }
        if (!yes) {
            opacity -= offset;

        }

        if (opacity < 0) {
            yes = true;
            opacity = 0;
        }
        if (opacity > 255) {
            yes = false;
            opacity = 255;
        }


        RenderUtil.drawFullCircle(posX, posY, 0.5f, Colors.getColor(255, 255, 255, opacity));

    }
}
