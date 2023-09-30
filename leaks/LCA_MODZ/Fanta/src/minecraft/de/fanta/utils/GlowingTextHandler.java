package de.fanta.utils;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GlowingTextHandler {
	public static void drawGlow( double x, double y, double x1, double y1, int color ) {
        GlStateManager.disableTexture2D( );
        GlStateManager.enableBlend( );
        GlStateManager.disableAlpha( );
        GlStateManager.tryBlendFuncSeparate( 771, 770, 1, 0 );
        GlStateManager.shadeModel( 7425 );
        drawGradientRect( ( int ) x, ( int ) y, ( int ) x1, ( int ) ( y + ( y1 - y ) / 2f ), 0x0, color );
        drawGradientRect( ( int ) x, ( int ) ( y + ( y1 - y ) / 2f ), ( int ) x1, ( int ) y1, color, 0x0);
        int radius = ( int ) ( ( y1 - y ) / 2f );
        drawPolygonPart( x, ( y + ( y1 - y ) / 2f ), radius, 0, color,0x0);
        drawPolygonPart( x, ( y + ( y1 - y ) / 2f ), radius, 1, color,0x0);
        drawPolygonPart( x1, ( y + ( y1 - y ) / 2f ), radius, 2, color,0x0);
        drawPolygonPart( x1, ( y + ( y1 - y ) / 2f ), radius, 3, color,0x0);
        GlStateManager.shadeModel( 7424 );
        GlStateManager.disableBlend( );
        GlStateManager.enableAlpha( );
        GlStateManager.enableTexture2D( );
    }
 
    public static void drawPolygonPart( double x, double y, int radius, int part, int color, int endcolor ) {
        float alpha = ( float ) ( color >> 24 & 255 ) / 255.0F;
        float red = ( float ) ( color >> 16 & 255 ) / 255.0F;
        float green = ( float ) ( color >> 8 & 255 ) / 255.0F;
        float blue = ( float ) ( color & 255 ) / 255.0F;
        float alpha1 = ( float ) ( endcolor >> 24 & 255 ) / 255.0F;
        float red1 = ( float ) ( endcolor >> 16 & 255 ) / 255.0F;
        float green1 = ( float ) ( endcolor >> 8 & 255 ) / 255.0F;
        float blue1 = ( float ) ( endcolor & 255 ) / 255.0F;
        GlStateManager.disableTexture2D( );
        GlStateManager.enableBlend( );
        GlStateManager.disableAlpha( );
        GlStateManager.tryBlendFuncSeparate( 771, 770, 1, 0);
        GlStateManager.shadeModel( 7425 );
        final Tessellator tessellator = Tessellator.getInstance( );
        final WorldRenderer bufferbuilder = tessellator.getBuffer( );
        bufferbuilder.begin( GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR );
        bufferbuilder.pos( x, y, 0 ).color( red, green, blue, alpha ).endVertex( );
        final double TWICE_PI = Math.PI * 2;
        for ( int i = part * 90; i <= part * 90 + 90; i++ ) {
            double angle = ( TWICE_PI * i / 360 ) + Math.toRadians( 180 );
            bufferbuilder.pos( x + Math.sin( angle ) * radius, y + Math.cos( angle ) * radius, 0 ).color( red1, green1, blue1, alpha1 ).endVertex( );
        }
        tessellator.draw( );
        GlStateManager.shadeModel( 7424 );
        GlStateManager.disableBlend( );
        GlStateManager.enableAlpha( );
        GlStateManager.enableTexture2D( );
    }
    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(771, 770, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, (double) Minecraft.getMinecraft().currentScreen.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, (double) Minecraft.getMinecraft().currentScreen.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, (double) Minecraft.getMinecraft().currentScreen.zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, (double) Minecraft.getMinecraft().currentScreen.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
