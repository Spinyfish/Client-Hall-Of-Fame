package club.pulsive.impl.util.render;


import club.pulsive.impl.util.math.apache.ApacheMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;


import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;


public class RoundedUtil {

    final static Minecraft mc = Minecraft.getMinecraft();
    final static FontRenderer fr = mc.fontRendererObj;
    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    /*
     *
     * NORMAL
     *
     */

    /**
     * @param x : X pos
     * @param y : Y pos
     * @param x1 : X2 pos
     * @param y1 : Y2 pos
     * @param radius : round of edges;
     * @param color : color;
     */

    public static void drawSmoothRoundedRect(float x, float y, float x1, float y1, float radius, int color) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        glEnable(GL_LINE_SMOOTH);
        setColor(color);
        glEnable(2848);
        glBegin(GL_POLYGON);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        glEnd();
        glBegin(GL_LINE_LOOP);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        glEnd();
        glEnable(3553);

        glDisable(2848);

        glDisable(GL_LINE_SMOOTH);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
    }
    public static void drawRoundedRect(float x, float y, float x1, float y1, float radius, int color) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        glEnable(GL_LINE_SMOOTH);
        setColor(color);
        glEnable(2848);
        glBegin(GL_POLYGON);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        glEnd();
        glEnable(3553);
        //glDisable(3042);
        glDisable(2848);
        // glDisable(3042);
        glDisable(GL_LINE_SMOOTH);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
        RenderUtil.color(-1);
    }
    
    public static float getOffset(int amount) {
        long ms = (long) (3.3 * 1000L);
        long currentMillis = -1;
        currentMillis = System.currentTimeMillis();
        return (currentMillis + (3 * 2 / (amount + 1) * 50)) % ms / (ms / 2.0F);
    }
    public static void drawGradientRoundedRect(float x, float y, float x1, float y1, float radius, int color, int color2) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        glEnable(GL_LINE_SMOOTH);

        glEnable(2848);
        glBegin(GL_POLYGON);
        int i;

        for (i = 0; i <= 90; i += 3) {
            long ms = (long) (1.3 * 1000L);



            long currentMillis = -1;


            currentMillis = System.currentTimeMillis();


            final float offset = (currentMillis + (3 * 2 / (10 * 2 + 1) * 50)) % ms / (ms / 2.0F);
            setColor(fadeBetween(color,color2,offset));
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);

        }
        for (i = 90; i <= 180; i += 3) {
            long ms = (long) (1.3 * 1000L);



            long currentMillis = -1;


            currentMillis = System.currentTimeMillis();


            final float offset = (currentMillis + (3 * 2 / (10 + 1) * 50)) % ms / (ms / 2.0F);
            setColor(fadeBetween(color2,color,offset));
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);

        }
        for (i = 0; i <= 90; i += 3) {
            long ms = (long) (1.3 * 1000L);



            long currentMillis = -1;


            currentMillis = System.currentTimeMillis();


            final float offset = (currentMillis + (3 * 2 / (10 * 2 + 1) * 50)) % ms / (ms / 2.0F);
            setColor(fadeBetween(color,color2,offset));
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);

        }
        for (i = 90; i <= 180; i += 3) {
            long ms = (long) (1.3 * 1000L);



            long currentMillis = -1;


            currentMillis = System.currentTimeMillis();


            final float offset = (currentMillis + (3 * 2 / (10 + 1) * 50)) % ms / (ms / 2.0F);
            setColor(fadeBetween(color2,color,offset));
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);

        }
        glEnd();
        glEnable(3553);
        //glDisable(3042);
        glDisable(2848);
        // glDisable(3042);
        glDisable(GL_LINE_SMOOTH);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
    }
    /**
     * @param x : X pos
     * @param y : Y pos
     * @param x1 : X2 pos
     * @param y1 : Y2 pos
     * @param radius : round of edges;
     * @param lineWidth : width of outline line;
     * @param color : color;
     */

    public static void drawRoundedOutline(float x, float y, float x1, float y1, float radius,float lineWidth, int color) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        setColor(color);
        glEnable(2848);
        glLineWidth(lineWidth);
        glBegin(GL_LINE_LOOP);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        glEnd();
        glEnable(3553);
        //glDisable(3042);
        glDisable(2848);
        //  glDisable(3042);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
    }

    /*
     *
     * SELECTED EDGES
     *
     */

    /**
     * @param x : X pos
     * @param y : Y pos
     * @param x1 : X2 pos
     * @param y1 : Y2 pos
     * @param radius1 : round of left top edges;
     * @param radius2 : round of right top edges;
     * @param radius3 : round of left bottom edges;
     * @param radius4 : round of right bottom edges;
     * @param color : color;
     */

    public static void drawSelectRoundedRect(float x, float y, float x1, float y1, float radius1,float radius2,float radius3,float radius4, int color) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        setColor(color);
        glEnable(2848);
        glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius1 + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius1 * -1.0D, y + radius1 + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius1 * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius2 + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius2 * -1.0D, y1 - radius2 + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius2 * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius3 + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius3, y1 - radius3 + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius3);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius4 + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius4, y + radius4 + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius4);
        glEnd();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glDisable(3042);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
    }

    /**
     * @param x : X pos
     * @param y : Y pos
     * @param x1 : X2 pos
     * @param y1 : Y2 pos
     * @param radius1 : round of left top edges;
     * @param radius2 : round of right top edges;
     * @param radius3 : round of left bottom edges;
     * @param radius4 : round of right bottom edges;
     * @param lineWidth : width of outline line;
     * @param color : color;
     */

    public static void drawSelectRoundedOutline(float x, float y, float x1, float y1, float radius1,float radius2,float radius3,float radius4,float lineWidth, int color) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        setColor(color);
        glEnable(2848);
        glLineWidth(lineWidth);
        glBegin(GL_LINE_LOOP);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius1 + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius1 * -1.0D, y + radius1 + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius1 * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius2 + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius2 * -1.0D, y1 - radius2 + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius2 * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius3 + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius3, y1 - radius3 + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius3);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius4 + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius4, y + radius4 + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius4);
        glEnd();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glDisable(3042);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
    }
    public static void setColor(int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        glColor4f(r, g, b, a);
    }

    /*
     *
     * GRADIENT
     *
     */


    public static int fadeBetween(int color1, int color2, float offset) {
        if (offset > 1)
            offset = 1 - offset % 1;

        double invert = 1 - offset;
        int r = (int) ((color1 >> 16 & 0xFF) * invert +
                (color2 >> 16 & 0xFF) * offset);
        int g = (int) ((color1 >> 8 & 0xFF) * invert +
                (color2 >> 8 & 0xFF) * offset);
        int b = (int) ((color1 & 0xFF) * invert +
                (color2 & 0xFF) * offset);
        int a = (int) ((color1 >> 24 & 0xFF) * invert +
                (color2 >> 24 & 0xFF) * offset);
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF);
    }
    public static void drawRoundedGradientRectCorner(float x, float y, float x1, float y1, float radius, int color, int color2) {

        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);

        glEnable(2848);
        glLineWidth(3);
        glBegin(GL_LINE_LOOP);
        int i;
        setColor(color2);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);

        setColor(color);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        setColor(color2);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        setColor(color);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);

        glEnd();
        glEnable(3553);
        // glDisable(3042);
        glDisable(2848);
        //  glDisable(3042);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        setColor(-1);
        glPopAttrib();

    }
    public static void drawRoundedGradientRectCorner2(float x, float y, float x1, float y1, float radius, int color, int color2) {

        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        long ms = (long) (1.3 * 1000L);



        long currentMillis = -1;


        currentMillis = System.currentTimeMillis();




        glEnable(2848);
        glLineWidth(3);
        glBegin(GL_LINE_LOOP);
        int i;


        for (i = 0; i <= 90; i += 3) {
            float offset = (currentMillis + (3 * 2 / (i + 1) * 100)) % ms / (ms / 2.0F);
            setColor(fadeBetween(color,color2,offset));
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);

        }


        for (i = 90; i <= 180; i += 3) {
            float offset = (currentMillis + (3 * 2 / (i + 1) * 100)) % ms / (ms / 2.0F);
            setColor(fadeBetween(color,color2,offset));
            glVertex2d(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D);
        }


        for (i = 0; i <= 90; i += 3) {
            float offset = (currentMillis + (3 * 2 / (i + 1) * 100)) % ms / (ms / 2.0F);
            setColor(fadeBetween(color,color2,offset));
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);
        }


        for (i = 90; i <= 180; i += 3) {
            float offset = (currentMillis + (3 * 2 / (i + 1) * 100)) % ms / (ms / 2.0F);
            setColor(fadeBetween(color,color2,offset));
            glVertex2d(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius);

        }
        glEnd();
        glEnable(3553);
        // glDisable(3042);
        glDisable(2848);
        //  glDisable(3042);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        setColor(-1);
        glPopAttrib();

    }
    /*NOT WORKING ATM*/
    /*
     *
     * IMAGE
     *
     */

  
    public static void drawRoundedImageRect(float x, float y, float x1, float y1, float radius) {
        enableGL2D();
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glEnable(GL_TEXTURE_2D);
        glDisable(3553);
        glEnable(2848);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        int i;
        for (i = 0; i <= 90; i += 3)
            worldrenderer.pos(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D,0)
                    .tex(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D)
                    .endVertex();
        for (i = 90; i <= 180; i += 3)
            worldrenderer.pos(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D,0)
                    .tex(x + radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius * -1.0D, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius * -1.0D)
                    .endVertex();
        for (i = 0; i <= 90; i += 3)
            worldrenderer.pos(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius,0)
                    .tex(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y1 - radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius)
                    .endVertex();
        for (i = 90; i <= 180; i += 3)
            worldrenderer.pos(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius,0)
                    .tex(x1 - radius + ApacheMath.sin(i * ApacheMath.PI / 180.0D) * radius, y + radius + ApacheMath.cos(i * ApacheMath.PI / 180.0D) * radius)
                    .endVertex();
        tessellator.draw();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glDisable(3042);
        glDisable(GL_TEXTURE_2D);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
        disableGL2D();
    }
}
