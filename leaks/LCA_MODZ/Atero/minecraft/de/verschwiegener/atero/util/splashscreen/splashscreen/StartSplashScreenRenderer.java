//package de.verschwiegener.atero.util.splashscreen.splashscreen;
//
//import java.awt.Color;
//import java.awt.Point;
//
//import org.lwjgl.LWJGLException;
//import org.lwjgl.opengl.ContextCapabilities;
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GLContext;
//import org.lwjgl.opengl.OpenGLException;
//import org.lwjgl.opengl.SharedDrawable;
//
//import de.verschwiegener.atero.util.TimeUtils;
//import de.verschwiegener.atero.util.render.ShaderRenderer;
//import de.verschwiegener.atero.util.splashscreen.LineRenderer;
//
//import org.lwjgl.opengl.Drawable;
//
//import org.lwjgl.opengl.Display;
//
//import net.minecraft.client.LoadingScreenRenderer;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiIngame;
//import net.minecraft.client.gui.GuiMainMenu;
//import net.minecraft.client.multiplayer.GuiConnecting;
//import net.minecraft.client.gui.GuiIngame;
//import net.minecraft.client.gui.ScaledResolution;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.shader.Framebuffer;
//
//public class StartSplashScreenRenderer {
//	static Point[] points = { new Point(20, 20), new Point(30, 30), new Point(20, 50), new Point(60, 50) };
//	static SharedDrawable sharedD;
//	static TimeUtils timer = new TimeUtils();
//	static SplashScreenRenderUtil renderer;
//	private static String vertex = "uniform vec2 uViewPort; //Width and Height of the viewportvarying vec2 vLineCenter;void main(void){vec4 pp = gl_ModelViewProjectionMatrix * gl_Vertex;gl_Position = pp;vec2 vp = uViewPort;vLineCenter = 0.5*(pp.xy + vec2(1, 1))*vp;};";
//	private static String fragment = "uniform float uLineWidth;uniform vec4 uColor;uniform float uBlendFactor; //1.5..2.5varying vec2 vLineCenter;void main(void) {vec4 col = uColor;        double d = length(vLineCenter-gl_FragCoord.xy);double w = uLineWidth;if (d>w){col.w = 0;}else{col.w *= pow(float((w-d)/w), uBlendFactor); gl_FragColor = col;}};";
//	
//	public static void draw(final Minecraft mc, final Drawable d) {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					//sharedD = new SharedDrawable(d);
//					//sharedD.makeCurrent();
//					// Brauche ich wahrscheinlich nicht
//					// GLContext.loadOpenGLLibrary();
//					// GLContext.useContext(sharedD.getContext());
//					// GLContext.getCapabilities();
//					// GLContext.loadOpenGLLibrary();
//					
//
//					ScaledResolution scaledresolution = new ScaledResolution(mc);
//					int i = scaledresolution.getScaleFactor();
//					Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i,
//							scaledresolution.getScaledHeight() * i, true);
//					framebuffer.bindFramebuffer(false);
//
//					GlStateManager.matrixMode(5889);
//					GlStateManager.loadIdentity();
//					GlStateManager.ortho(0.0D, (double) scaledresolution.getScaledWidth(),(double) scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
//					GlStateManager.matrixMode(5888);
//					GlStateManager.loadIdentity();
//					GlStateManager.translate(0.0F, 0.0F, -2000.0F);
//					GlStateManager.disableLighting();
//					GlStateManager.disableFog();
//					GlStateManager.disableDepth();
//
//					renderer = new SplashScreenRenderUtil();
//					//ShaderRenderer sr = new ShaderRenderer(fragment, vertex);
//					
//					while (!renderer.finish) {
//						if (timer.hasReached(20)) {
//							timer.reset();
//							renderer.draw();
//							//sr.render();
//							Display.update();
//						}
//					}
//					framebuffer.unbindFramebuffer();
//					framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i,scaledresolution.getScaledHeight() * i);
//					mc.updateDisplay();
//				//} catch (LWJGLException ex) {
//				//	ex.printStackTrace();
//			//	}
//				Display.update();
//				try {
//					sharedD.releaseContext();
//				} catch (LWJGLException e) {
//					e.printStackTrace();
//				}
//				
//				//Das wird nur benötigt, wenn man den ganzen Text rendert, dann diesen code auf in der Minecraft Klasse ausklammern
//				/*mc.ingameGUI = new GuiIngame(mc);
//
//				if (mc.serverName != null) {
//					mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, mc.serverName, mc.serverPort));
//				} else {
//					mc.displayGuiScreen(new GuiMainMenu());
//				}
//
//				mc.renderEngine.deleteTexture(mc.mojangLogo);
//				mc.mojangLogo = null;
//				mc.loadingScreen = new LoadingScreenRenderer(mc);
//
//				if (mc.gameSettings.fullScreen && !mc.fullscreen) {
//					mc.toggleFullscreen();
//				}
//
//				try {
//					Display.setVSyncEnabled(mc.gameSettings.enableVsync);
//				} catch (OpenGLException var2) {
//					mc.gameSettings.enableVsync = false;
//					mc.gameSettings.saveOptions();
//				}
//
//				mc.renderGlobal.makeEntityOutlineShader();*/
//				Thread.currentThread().interrupt();
//
//		//	}
//		}).start();
//
//	}
//
//	public static SplashScreenRenderUtil getRenderer() {
//		return renderer;
//	}
//
//	public static double normalize(double in) {
//		if (in < 0) {
//			return in / -1;
//		}
//		return in;
//	}
//
//}
