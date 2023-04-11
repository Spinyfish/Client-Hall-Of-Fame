//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package byron.Mono.utils;

import com.google.gson.JsonSyntaxException;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
    private static final Frustum frustrum = new Frustum();
    private static int lastWidth;
    private static ICamera camera = new Frustum();
    private static ShaderGroup shaderGroup;
    private static int lastFactor;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ArrayList depth = new ArrayList();
    private static int lastHeight;
    private static Framebuffer framebuffer;
    private static final Map<Integer, Boolean> glCapMap = new HashMap();

    private static void glColor(int var0) {
        float var1 = (float)(var0 >> ((12 | 5) << 2 << 2 << 1 ^ 440) & ((60 | 28) >>> 4 ^ 252)) / 255.0F;
        float var2 = (float)(var0 >> (((2 ^ 0) >>> 3 | 366171201) ^ 366171217) & ((239 & 176) >> 3 & 7 ^ 251)) / 255.0F;
        float var3 = (float)(var0 >> ((2 >>> 3 & 1129680141 ^ 1140027025) >>> 3 ^ 142503386) & ((210 >> 4 & 5) >>> 1 >> 1 ^ 254)) / 255.0F;
        float var4 = (float)(var0 & ((9 | 8 | 4) << 4 ^ 47)) / 255.0F;
        GlStateManager.color(var2, var3, var4, var1);
    }



    public static void drawHLine(double var0, double var2, float var4, int var5) {
        if (var2 < var0) {
            float var6 = (float)var0;
            var0 = var2;
            var2 = (double)var6;
        }

        Gui.drawRect(var0, (double)var4, var2 + 1.0D, (double)(var4 + 1.0F), var5);
    }

    public static void pre() {
        if (depth.isEmpty()) {
            GL11.glClearDepth(1.0D);
            GL11.glClear(213 >>> 1 << 1 << 3 ^ 1952);
        }

    }



  
    public static void drawOutlinedBoundingBox(AxisAlignedBB var0) {
        int[] var10000 = new int[(1 ^ 0 ^ 0) & 0 ^ 529703311 ^ 529703307];
        var10000[((706003200 | 100473489 | 611972843) ^ 23627664) << 1 >>> 3 ^ 195386906] = (8 & 6) >>> 2 >>> 2 ^ 10;
        var10000[(0 ^ 1954981331) >> 1 ^ 977490664] = (743637616 & 33329016) >> 1 >> 4 ^ 163859;
        var10000[((1 & 0) >>> 3 & 1281738649) << 3 ^ 2] = 23 & 10 & 1 ^ 30;
        var10000[2 << 1 >>> 1 << 3 >>> 1 ^ 11] = 7 >> 3 >> 4 >>> 2 ^ 15;
        int[] var1 = var10000;
        Tessellator var2 = Tessellator.getInstance();
        WorldRenderer var3 = var2.getWorldRenderer();
        var3.begin((0 | 70007069) & 13311308 ^ 27784 ^ 554375, DefaultVertexFormats.POSITION_COLOR);
        var3.pos(var0.minX, var0.minY, var0.minZ).color(var1[((467432096 & 398322048) << 2 | 218325737) << 3 ^ 2074023752], var1[(0 >> 4 | 291524830) ^ 291524831], var1[((0 >>> 1 ^ 1348859624) & 1128205930) >> 4 ^ 67260516], var1[((1 ^ 0) >> 2 & 113579329 ^ 1421985660 | 1132431193) ^ 1476380542]).endVertex();
        var3.pos(var0.maxX, var0.minY, var0.minZ).color(var1[1702603999 >>> 1 << 1 >>> 1 ^ 784656112 ^ 477704351], var1[((0 ^ 2118352784) & 111585787 & 86532341 | 44904261) ^ 112013268], var1[(0 << 1 | 1353513297) ^ 1353513299], var1[2 << 4 >> 3 << 1 >>> 4 ^ 3]).endVertex();
        var3.pos(var0.maxX, var0.minY, var0.maxZ).color(var1[((2147197890 | 1262448226) >>> 1 | 1023763122) ^ 1073739763], var1[(0 & 351496307 | 2116932112) << 4 ^ -488824575], var1[1 >>> 4 ^ 1744459036 ^ 1744459038], var1[(2 ^ 0 ^ 0) >> 2 ^ 3]).endVertex();
        var3.pos(var0.minX, var0.minY, var0.maxZ).color(var1[2077592873 & 1491711287 ^ 731692847 ^ 1935489550], var1[((0 & 1602166334) >> 4 | 542496399) ^ 542496398], var1[0 >> 2 >> 3 >>> 1 ^ 2], var1[(((0 | 412690685) >> 1 ^ 72715162) & 45313432) >>> 1 ^ 558787]).endVertex();
        var3.pos(var0.minX, var0.minY, var0.minZ).color(var1[(593181464 & 552707652 ^ 61250885 ^ 138345993) >> 4 ^ 36325520 ^ 9886852], var1[(0 >>> 2 >>> 2 ^ 2127959704) >>> 3 ^ 265994962], var1[((0 ^ 180043666) >>> 1 ^ 12146644) >>> 4 ^ 6179971], var1[(0 ^ 714580071) << 2 ^ -1436647009]).endVertex();
        var2.draw();
        var3.begin(0 << 2 << 2 ^ 3, DefaultVertexFormats.POSITION_COLOR);
        var3.pos(var0.minX, var0.maxY, var0.minZ).color(var1[(2099395840 << 3 ^ 305398816) << 4 ^ -1302412800], var1[((0 | 341476497) ^ 142124800) >> 1 ^ 235999689], var1[0 >> 3 & 1996342891 ^ 2], var1[(0 & 1441860879) >> 1 << 1 << 2 ^ 3]).endVertex();
        var3.pos(var0.maxX, var0.maxY, var0.minZ).color(var1[(375405382 | 104255594) << 4 >>> 1 ^ 867687280], var1[0 >> 2 << 1 << 4 ^ 1], var1[(1 ^ 0 | 0) ^ 0 ^ 3], var1[((2 ^ 1) & 0) >> 2 ^ 3]).endVertex();
        var3.pos(var0.maxX, var0.maxY, var0.maxZ).color(var1[2029980337 << 2 & 1048809032 ^ 578816576], var1[(0 & 219492164) >> 1 >> 4 ^ 1], var1[(0 ^ 918396818 | 619836928) ^ 922615696], var1[(1 >>> 3 & 1343662206) >> 1 ^ 3]).endVertex();
        var3.pos(var0.minX, var0.maxY, var0.maxZ).color(var1[(1836191443 ^ 1836114244 | 168015) & 105796 ^ '鵄'], var1[(0 & 691425711) >>> 2 >>> 4 ^ 1], var1[((1 ^ 0) & 0) >>> 3 ^ 2], var1[(1 | 0) & 0 ^ 3]).endVertex();
        var3.pos(var0.minX, var0.maxY, var0.minZ).color(var1[((2145616641 & 1169192337 & 553230065) >> 4 | 536509) ^ 667581], var1[0 << 4 & 1206553746 ^ 1], var1[0 << 4 ^ 2015827064 ^ 654588003 ^ 1596140569], var1[(0 | 1996274413) & 142229211 ^ 7879370]).endVertex();
        var2.draw();
        var3.begin(0 >> 3 << 3 << 4 ^ 1, DefaultVertexFormats.POSITION_COLOR);
        var3.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var3.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var3.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var3.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var3.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var3.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var3.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var3.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.draw();
    }




    public static void drawFilledBox(AxisAlignedBB var0) {
        Tessellator var1 = Tessellator.getInstance();
        WorldRenderer var2 = var1.getWorldRenderer();
        var2.begin((3 & 2 | 1) >> 2 << 4 ^ 7, DefaultVertexFormats.POSITION);
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var1.draw();
    }


    public static Block getBlock(BlockPos var0) {
        return mc.theWorld.getBlockState(var0).getBlock();
    }


    public static void drawRectSized(float var0, float var1, float var2, float var3, int var4) {
        drawRect((double)var0, (double)var1, (double)(var0 + var2), (double)(var1 + var3), var4);
    }

  

    public static void setGlCap(int var0, boolean var1) {
        glCapMap.put(var0, GL11.glGetBoolean(var0));
        setGlState(var0, var1);
    }

    private static boolean isInViewFrustrum(AxisAlignedBB var0) {
        Entity var1 = Minecraft.getMinecraft().getRenderViewEntity();
        frustrum.setPosition(var1.posX, var1.posY, var1.posZ);
        return frustrum.isBoundingBoxInFrustum(var0);
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB var0) {
        Tessellator var1 = Tessellator.getInstance();
        WorldRenderer var2 = var1.getWorldRenderer();
        var2.begin(0 >>> 4 & 1966881745 ^ 3, DefaultVertexFormats.POSITION);
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var1.draw();
    }

    public static void setupLineSmooth() {
        GL11.glEnable(((1846 << 2 | 4857) & 7235) >> 1 ^ 1474);
        GL11.glDisable(638 << 1 >>> 1 << 1 ^ 4012);
        GL11.glDisable((1083 | 25) >> 1 << 3 & 3868 ^ 4 ^ 2941);
        GL11.glEnable(2639 << 2 << 2 >>> 2 >> 1 ^ 8126);
        GL11.glDisable((2726 >> 3 << 1 | 323) >> 4 ^ 13 ^ 3538);
        GL11.glHint((1283 | 855) ^ 432 ^ 2741, (4161 >>> 3 ^ 44) >>> 3 << 1 ^ 4490);
        GL11.glBlendFunc((497 & 431 & 75) >> 1 ^ 770, 424 << 2 >> 2 ^ 683);
        GL11.glEnable(26957 >>> 4 >> 2 << 2 ^ '蘉');
        GL11.glEnable(((16898 & 8885 | 300) & 464) >> 4 ^ '肎');
        GL11.glShadeModel((3509 ^ 1383) & 387 & 35 ^ 7427);
    }


    public static void drawTracerLine(double var0, double var2, double var4, float var6, float var7, float var8, float var9, float var10) {
        GL11.glPushMatrix();
        GL11.glEnable(((1821 ^ 389) & 499) >>> 3 ^ 3056);
        GL11.glEnable((1191 & 629 & 25 | 0) ^ 2849);
        GL11.glDisable(109 >>> 4 ^ 4 ^ 2931);
        GL11.glDisable(1514 << 1 >>> 1 >>> 1 ^ 3860);
        GL11.glBlendFunc((355 ^ 308) << 2 & 347 & 177 ^ 786, ((50 & 32) << 4 | 22) >> 4 ^ 802);
        GL11.glEnable((347 >> 1 ^ 114 | 11 | 122) ^ 2845);
        GL11.glLineWidth(var10);
        GL11.glColor4f(var6, var7, var8, var9);
        GL11.glBegin((1 | 0 | 0 | 0) ^ 3);
        GL11.glVertex3d(0.0D, 0.0D + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight() - 0.2D, 0.0D);
        GL11.glVertex3d(var0, var2, var4);
        GL11.glEnd();
        GL11.glDisable((1584 | 970) >> 2 & 59 ^ 3032);
        GL11.glEnable((3051 & 175) >> 2 >>> 2 << 2 ^ 3529);
        GL11.glEnable((181 & 178 | 49) ^ 3008);
        GL11.glDisable(486 << 2 >>> 3 & 221 ^ 19 ^ 3042);
        GL11.glDisable(1771 >> 3 << 2 ^ 2198);
        GL11.glPopMatrix();
    }

    public static double lerp(double var0, double var2, double var4) {
        return (1.0D - var4) * var0 + var4 * var2;
    }




   



    public static void endSmooth() {
        GL11.glDisable((526 & 28 & 6) >> 1 >> 1 ^ 2849);
        GL11.glDisable(2527 >> 3 ^ 225 ^ 2715);
        GL11.glEnable((2189 >>> 1 | 129) ^ 39 ^ 4080);
    }

    public static void drawRect(double var0, double var2, double var4, double var6, int var8) {
        double var9;
        if (var0 < var4) {
            var9 = var0;
            var0 = var4;
            var4 = var9;
        }

        if (var2 < var6) {
            var9 = var2;
            var2 = var6;
            var6 = var9;
        }

        float var15 = (float)(var8 >> ((16 & 13) >> 1 ^ 2108907842 ^ 2108907866) & (249 << 1 >> 2 ^ 131)) / 255.0F;
        float var10 = (float)(var8 >> ((2 ^ 0) >> 4 ^ 16) & ((59 | 40) << 2 << 1 ^ 295)) / 255.0F;
        float var11 = (float)(var8 >> ((3 ^ 0 | 2) << 2 ^ 4) & ((((35 | 17) >>> 1 | 0) & 16) >>> 1 ^ 247)) / 255.0F;
        float var12 = (float)(var8 & (56 >> 3 >> 1 >>> 2 ^ 255)) / 255.0F;
        Tessellator var13 = Tessellator.getInstance();
        WorldRenderer var14 = var13.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((265 >> 3 ^ 29) << 1 ^ 890, (451 >> 4 >> 3 ^ 0 | 2) ^ 768, (0 ^ 538195867) >>> 4 ^ 33637240, (2075181006 & 1477714238 ^ 891035542) << 4 ^ -792467072);
        GlStateManager.color(var10, var11, var12, var15);
        var14.begin((3 >>> 2 & 1071982344) << 1 ^ 7, DefaultVertexFormats.POSITION);
        var14.pos(var0, var6, 0.0D).endVertex();
        var14.pos(var4, var6, 0.0D).endVertex();
        var14.pos(var4, var2, 0.0D).endVertex();
        var14.pos(var0, var2, 0.0D).endVertex();
        var13.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


    public static int reAlpha(int var0, float var1) {
        Color var2 = new Color(var0);
        float var3 = 0.003921569F * (float)var2.getRed();
        float var4 = 0.003921569F * (float)var2.getGreen();
        float var5 = 0.003921569F * (float)var2.getBlue();
        return (new Color(var3, var4, var5, var1)).getRGB();
    }

    public RenderUtil() {
    }

    public static void disableGL2D() {
        GL11.glEnable(((2845 | 2088) & 238) >> 3 >> 1 ^ 3555);
        GL11.glDisable((1451 | 1394) >> 3 << 1 >>> 2 ^ 3005);
        GL11.glDisable(790 << 3 & 5848 ^ 7088);
        GL11.glHint((2123 << 2 | 6287 | 1542) ^ 13309, (4009 | 670) << 2 >> 3 & 1273 ^ 5593);
        GL11.glHint((1617 ^ 54) >>> 4 ^ 3125, (2143 & 1896 & 14 | 4) >>> 3 ^ 4353);
    }


    public static void drawBoundingBox(AxisAlignedBB var0) {
        Tessellator var1 = Tessellator.getInstance();
        WorldRenderer var2 = var1.getWorldRenderer();
        var2.begin(((0 | 1505987361) ^ 601868223) & 5964319 ^ 1502566 ^ 978303, DefaultVertexFormats.POSITION_COLOR);
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var1.draw();
        var2.begin((1 << 3 & 7) >>> 3 ^ 7, DefaultVertexFormats.POSITION_COLOR);
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var1.draw();
        var2.begin(0 >> 2 << 1 >>> 1 << 4 ^ 7, DefaultVertexFormats.POSITION_COLOR);
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var1.draw();
        var2.begin((3 | 1) & 0 ^ 7, DefaultVertexFormats.POSITION_COLOR);
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ);
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var1.draw();
        var2.begin((6 | 2) & 4 ^ 3, DefaultVertexFormats.POSITION_COLOR);
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var1.draw();
        var2.begin((3 >>> 3 | 838543589) >> 3 >>> 4 ^ 6551126, DefaultVertexFormats.POSITION_COLOR);
        var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
        var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.minZ);
        var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
        var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
        var1.draw();
    }

    public static void drawBorderedRect(float var0, float var1, float var2, float var3, float var4, int var5, int var6) {
        drawRect((double)var0, (double)var1, (double)var2, (double)var3, var6);
        float var7 = (float)(var5 >> ((13 >>> 3 & 0) >> 4 ^ 24) & ((27 << 1 >>> 4 & 0) << 1 ^ 255)) / 255.0F;
        float var8 = (float)(var5 >> ((9 & 4 | 943720509) ^ 943720493) & ((211 | 136) << 1 ^ 329)) / 255.0F;
        float var9 = (float)(var5 >> ((2 & 1) << 3 >> 2 ^ 8) & ((28 << 1 ^ 17) >>> 2 ^ 245)) / 255.0F;
        float var10 = (float)(var5 & (190 >>> 4 >> 4 ^ 255)) / 255.0F;
        GL11.glEnable((1444 >> 2 & 111 ^ 87) & 9 ^ 3050);
        GL11.glDisable((2985 >>> 1 ^ 1113) >>> 2 ^ 3458);
        GL11.glBlendFunc(225 >>> 3 >> 1 << 4 ^ 994, (522 ^ 219) >> 1 & 237 ^ 875);
        GL11.glEnable((716 | 710) >>> 2 >> 3 & 21 ^ 2868);
        GL11.glPushMatrix();
        GL11.glColor4f(var8, var9, var10, var7);
        GL11.glLineWidth(var4);
        GL11.glBegin(0 << 1 << 4 ^ 1);
        GL11.glVertex2d((double)var0, (double)var1);
        GL11.glVertex2d((double)var0, (double)var3);
        GL11.glVertex2d((double)var2, (double)var3);
        GL11.glVertex2d((double)var2, (double)var1);
        GL11.glVertex2d((double)var0, (double)var1);
        GL11.glVertex2d((double)var2, (double)var1);
        GL11.glVertex2d((double)var0, (double)var3);
        GL11.glVertex2d((double)var2, (double)var3);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(2063 << 3 ^ 12943 ^ 32534);
        GL11.glDisable(((1954 ^ 142) & 1708 & 569 | 363) ^ 2185);
        GL11.glDisable((1660 | 121 | 955) >>> 4 ^ 2911);
    }

    public static void drawVLine(double var0, float var2, float var3, int var4) {
        if (var3 < var2) {
            float var5 = var2;
            var2 = var3;
            var3 = var5;
        }

        Gui.drawRect(var0, (double)(var2 + 1.0F), var0 + 1.0D, (double)var3, var4);
    }


    public static void drawImage(ResourceLocation var0, int var1, int var2, int var3, int var4) {
        OpenGlHelper.glBlendFunc((336 >> 1 ^ 159 | 48) ^ 821, (17 & 5 & 0 | 990294739) ^ 990294480, 0 & 713412248 & 193964923 ^ 1, (419888364 >> 3 >> 1 ^ 20541609) >> 1 >> 4 ^ 346363);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(var0);
        Gui.drawModalRectWithCustomSizedTexture(var1, var2, 0.0F, 0.0F, var3, var4, (float)var3, (float)var4);
    }

    public static double interpolate(double var0, double var2, double var4) {
        return var2 + (var0 - var2) * var4;
    }

 
  
    public static void post() {
        GL11.glDepthFunc((Integer)depth.get((336819775 ^ 284789625 ^ 34660095) >> 2 ^ 29264110));
        depth.remove((940550832 ^ 211926798 | 437301621) >>> 3 & 71224023 ^ 68600471);
    }

    public static void setGlState(int var0, boolean var1) {
        if (var1) {
            GL11.glEnable(var0);
        } else {
            GL11.glDisable(var0);
        }

    }

    public static void startSmooth() {
        GL11.glEnable(1314 >>> 1 >> 2 << 4 >> 4 ^ 65 ^ 3013);
        GL11.glEnable(2478 >> 4 & 35 ^ 1 ^ 2882);
        GL11.glEnable((2340 >>> 3 & 0) << 4 >>> 2 >>> 1 ^ 2832);
        GL11.glEnable(((1961 | 888) ^ 361 | 119) >>> 1 & 711 ^ 2465);
        GL11.glBlendFunc((39 >>> 3 >>> 1 ^ 1) & 0 ^ 770, ((606 ^ 486) & 444 | 101) ^ 766);
        GL11.glHint((1569 >>> 3 | 41) & 158 ^ 3294, (781 | 372) << 2 ^ 7414);
        GL11.glHint(((2682 | 1390) & 478) >>> 2 ^ 3076, ((279 ^ 258) & 0) << 3 << 4 ^ 4354);
        GL11.glHint(((1240 >>> 2 ^ 268) & 2) >>> 3 ^ 3153, (1887 & 1644) >> 2 >> 2 ^ 4454);
    }

    public static void glColor(int var0, int var1, int var2, int var3) {
        GlStateManager.color((float)var0 / 255.0F, (float)var1 / 255.0F, (float)var2 / 255.0F, (float)var3 / 255.0F);
    }

    public static void glColor(Color var0) {
        float var1 = (float)var0.getRed() / 255.0F;
        float var2 = (float)var0.getGreen() / 255.0F;
        float var3 = (float)var0.getBlue() / 255.0F;
        float var4 = (float)var0.getAlpha() / 255.0F;
        GlStateManager.color(var1, var2, var3, var4);
    }
    
    public void push() {
        GL11.glPushMatrix();
    }

    public void pop() {
        GL11.glPopMatrix();
    }

    public static void enable(final int glTarget) {
        GL11.glEnable(glTarget);
    }

    public static void disable(final int glTarget) {
        GL11.glDisable(glTarget);
    }
    
    public static void start() {
        enable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        disable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_CULL_FACE);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        enable(GL11.GL_CULL_FACE);
        enable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_BLEND);
        glColor(Color.white);
    }

    public void startSmooth2() {
        enable(GL11.GL_POLYGON_SMOOTH);
        enable(GL11.GL_LINE_SMOOTH);
        enable(GL11.GL_POINT_SMOOTH);
    }

    public void endSmooth2() {
        disable(GL11.GL_POINT_SMOOTH);
        disable(GL11.GL_LINE_SMOOTH);
        disable(GL11.GL_POLYGON_SMOOTH);
    }
    
    public static void rect(final double x, final double y, final double width, final double height, final boolean filled, final Color color) {
        start();
        if (color != null)
            glColor(color);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINES);

        {
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width, y + height);
            vertex(x, y + height);
            if (!filled) {
                vertex(x, y);
                vertex(x, y + height);
                vertex(x + width, y);
                vertex(x + width, y + height);
            }
        }
        end();
        stop();
    }

    public void rect(final double x, final double y, final double width, final double height, final boolean filled) {
        rect(x, y, width, height, filled, null);
    }

    public static void rect(final double x, final double y, final double width, final double height, final Color color) {
        rect(x, y, width, height, true, color);
    }

    public void rect(final double x, final double y, final double width, final double height) {
        rect(x, y, width, height, true, null);
    }

    

    public static void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }

    public void translate(final double x, final double y) {
        GL11.glTranslated(x, y, 0);
    }

    public void scale(final double x, final double y) {
        GL11.glScaled(x, y, 1);
    }

    public void rotate(final double x, final double y, final double z, final double angle) {
        GL11.glRotated(angle, x, y, z);
    }

    public static void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public static void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public void color(Color color, final int alpha) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
    }

    public static void lineWidth(final double width) {
        GL11.glLineWidth((float) width);
    }
    
    public static void lineNoGl(final double firstX, final double firstY, final double secondX, final double secondY, final Color color) {

        start();
        if (color != null)
            color(color);
        RenderUtil.lineWidth(1);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);
        {
            vertex(firstX, firstY);
            vertex(secondX, secondY);
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }
    
    

}
