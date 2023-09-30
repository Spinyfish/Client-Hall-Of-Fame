package de.fanta.module.impl.visual;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex3d;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.gui.font.BasicFontRenderer;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.PlayerUtil;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class ESP extends Module {
	public ESP() {
		super("ESP", 0, Type.Visual, Color.green);
	
		this.settings.add(new Setting("RGBColors", new Slider(1, 20, 0.1, 4)));
		this.settings.add(new Setting("LineWith", new Slider(1, 3.3, 0.1, 1)));
		this.settings.add(new Setting("Rainbow", new CheckBox(false)));
		this.settings.add(new Setting("2DBlur", new CheckBox(false)));
		this.settings.add(new Setting("RGBReversed", new CheckBox(false)));
		this.settings.add(new Setting("ESPModes",
				new DropdownBox("Shader", new String[] { "Shader", "Box", "Outline","NewOutline","Circle", "Real2d" })));

		this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
	}
	 public static ESP INSTANCE;
	float ani = 0;
	boolean max = false;
	private Frustum frustum = new Frustum();
	private final static Minecraft mc = Minecraft.getMinecraft();
	private final static Frustum frustrum = new Frustum();
	private final static IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
	private final static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
	private final static FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
	private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
	private final ArrayList<Entity> collectedEntities = new ArrayList<>();
	public static double RGB;
	public static double LineWith;
	protected ModelBase mainModel;
	public int getColor2() {
		try {
			return ((ColorValue) getSetting("Color").getSetting()).color;
		} catch (Exception e) {
			return Color.white.getRGB();
		}
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof EventRender3D && event.isPre()) {
			for (Object o : mc.theWorld.loadedEntityList) {
				Entity e = (Entity) o;
				if (e instanceof EntityPlayer && e != mc.thePlayer) {
					double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * mc.timer.renderPartialTicks
							- RenderManager.renderPosX;
					double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * mc.timer.renderPartialTicks
							- RenderManager.renderPosY;
					double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * mc.timer.renderPartialTicks
							- RenderManager.renderPosZ;

					BasicFontRenderer font = Minecraft.getMinecraft().fontRendererObj;
					float distance = (float) PlayerUtil.getDistanceToBlock(new BlockPos(x, y, z));
					float scale = Math.min(Math.max(1.2f * (distance * 0.15F), 1.25F), 6F) * ((float) 0.5 / 100);
					
					
					
					
					switch (((DropdownBox) this.getSetting("ESPModes").getSetting()).curOption) {

					case "Shader":
						//RendererGlobal
						break;
						
					case "NewOutline":
						//RendererLivingEntity
						break;
						
						
					case "Box":
						mc.gameSettings.gammaSetting = 10F;
						GlStateManager.pushMatrix();
						GlStateManager.enableBlend();
						GlStateManager.disableDepth();
						GlStateManager.disableLighting();
						GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						GlStateManager.disableTexture2D();
						GlStateManager.translate(x, y + 2, z);
						GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0, 1, 0);
						GlStateManager.scale(-scale, -scale, scale);

						Gui.drawRect(20, -2, 19, 70, getColor2());
						Gui.drawRect(-20, -2, -19, 70, getColor2());
						Gui.drawRect(-20, 70, 20, 71, getColor2());
						Gui.drawRect(-20, -2, 20, -3, getColor2());

						GlStateManager.enableTexture2D();
						if (((EntityPlayer) e).getCurrentEquippedItem() != null) {
							float string_width = font
									.getStringWidth(((EntityPlayer) e).getCurrentEquippedItem().getDisplayName());
							font.drawString(((EntityPlayer) e).getCurrentEquippedItem().getDisplayName(),
									(int) -string_width / 3 - 5, 73, -1);
						}
						if (((EntityPlayer) e).getCurrentEquippedItem() == null) {
							float string_width = font.getStringWidth("Hand");
							font.drawString("Hand", (int) string_width / 8 - 13, 73, -1);
						}
						GlStateManager.enableDepth();
						GlStateManager.disableBlend();
						GlStateManager.color(1, 1, 1, 1);
						GlStateManager.popMatrix();
						break;
					case "Outline":
						break;
					case "Real2d":
						RGB = ((Slider) this.getSetting("RGBColors").getSetting()).curValue;
						LineWith = ((Slider) this.getSetting("LineWith").getSetting()).curValue;
						if (event instanceof EventRender3D && event.isPre()) {
							GL11.glPushMatrix();
							boolean outline = true;
							collectEntities();
							float partialTicks = mc.timer.renderPartialTicks;
							ScaledResolution scaledResolution = new ScaledResolution(mc);
							int scaleFactor = scaledResolution.getScaleFactor();
							double scaling = scaleFactor / Math.pow(scaleFactor, 2.0D);
							GL11.glScaled(scaling, scaling, scaling);
							for (EntityPlayer player : mc.theWorld.playerEntities) {
								if (player != mc.thePlayer || mc.gameSettings.thirdPersonView != 0) {
									if (player.isInvisible())
										continue;
									if (isInViewFrustrum(player)) {
										double x1 = interpolate(player.posX, player.lastTickPosX, partialTicks);
										final double y1 = interpolate(player.posY, player.lastTickPosY, partialTicks);
										final double z1 = interpolate(player.posZ, player.lastTickPosZ, partialTicks);
										final double width = player.width / 1.5;
										final double height = player.height + (player.isSneaking() ? -0.3 : 0.2);

										final AxisAlignedBB aabb = new AxisAlignedBB(x1 - width, y1, z1 - width,
												x1 + width, y1 + height, z1 + width);
										final List vectors = (List) Arrays.asList(
												new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
												new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
												new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
												new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
												new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
												new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
												new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
												new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
										mc.entityRenderer.setupCameraTransform(partialTicks, 0);
										Vector4d position = null;

										for (int i1 = 0, vectorsSize = vectors.size(); i1 < vectorsSize; i1++) {
											Vector3d vector = (Vector3d) vectors.get(i1);
											vector = project2D(scaleFactor, vector.x - mc.getRenderManager().viewerPosX,
													vector.y - mc.getRenderManager().viewerPosY,
													vector.z - mc.getRenderManager().viewerPosZ);

											if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
												if (position == null) {
													position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
												}

												position.x = Math.min(vector.x, position.x);
												position.y = Math.min(vector.y, position.y);
												position.z = Math.max(vector.x, position.z);
												position.w = Math.max(vector.y, position.w);
											}
										}
										if (position != null) {
											mc.entityRenderer.setupOverlayRendering();
											final double posX = position.x;
											final double posY = position.y;
											final double endPosX = position.z;
											final double endPosY = position.w;

											int color = getColor2();
//											int color2 = Gui.dra;

											int black = Color.black.getRGB();
											if (outline) {
												if (((CheckBox) this.getSetting("2DBlur").getSetting()).state) {
												Client.blurHelper.blur2( (float) posX, (float)posY, (float)endPosX, (float)endPosY, (float) 10);
												}
												
												// Black
												Gui.drawRect2(posX - 1, posY, posX + 0.5D, endPosY + .5, black);
												Gui.drawRect2(posX - 1, posY - .5, endPosX + .5, posY + .5 + 0.5D,
														black);
												Gui.drawRect2(endPosX - .5 - 0.5D, posY, endPosX + .5, endPosY + .5,
														black);
												Gui.drawRect2(posX - 1, endPosY - 0.5D - .5, endPosX + .5, endPosY + .5,
														black);

												// Color

												if (((CheckBox) this.getSetting("RGBReversed").getSetting()).state) {
													if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
														Gui.drawRGBLineVertical(posX - .5, posY, (endPosY - posY),
																(float) LineWith, (int) RGB, false);
														// Gui.drawRect2(posX - .5, posY, posX + 0.5D - .5, endPosY,
														// color);

														// Gui.drawRect2(posX, endPosY - 0.5D, endPosX, endPosY, color);
														Gui.drawRGBLineHorizontal(posX, endPosY - 0.5D, endPosX - posX,
																(float) LineWith, (int) RGB, false);

														// Gui.drawRect2(posX - .5, posY, endPosX, posY + 0.5D, color);
														Gui.drawRGBLineHorizontal(posX - .5, posY, endPosX - posX,
																(float) LineWith, (int) RGB, true);

														// Gui.drawRect2(endPosX - 0.5D, posY, endPosX, endPosY, color);
														Gui.drawRGBLineVertical(endPosX - 0.5D, posY, (endPosY - posY),
																(float) LineWith, (int) RGB, true);
//														
													} else {
														Gui.drawRect2(posX - .5, posY, posX + 0.5D - .5, endPosY,
																color);

														Gui.drawRect2(posX, endPosY - 0.5D, endPosX, endPosY, color);

														Gui.drawRect2(posX - .5, posY, endPosX, posY + 0.5D, color);

														Gui.drawRect2(endPosX - 0.5D, posY, endPosX, endPosY, color);
													}
												} else {
													if (((CheckBox) this.getSetting("Rainbow").getSetting()).state) {
														Gui.drawRGBLineVertical(posX - .5, posY, (endPosY - posY),
																(float) LineWith, (int) RGB, true);
														// Gui.drawRect2(posX - .5, posY, posX + 0.5D - .5, endPosY,
														// color);

														// Gui.drawRect2(posX, endPosY - 0.5D, endPosX, endPosY, color);
														Gui.drawRGBLineHorizontal(posX, endPosY - 0.5D, endPosX - posX,
																(float) LineWith, (int) RGB, true);

														// Gui.drawRect2(posX - .5, posY, endPosX, posY + 0.5D, color);
														Gui.drawRGBLineHorizontal(posX - .5, posY, endPosX - posX,
																(float) LineWith, (int) RGB, false);

														// Gui.drawRect2(endPosX - 0.5D, posY, endPosX, endPosY, color);
														Gui.drawRGBLineVertical(endPosX - 0.5D, posY, (endPosY - posY),
																(float) LineWith, (int) RGB, false);
//														
													} else {
														Gui.drawRect2(posX - .5, posY, posX + 0.5D - .5, endPosY,
																color);

														Gui.drawRect2(posX, endPosY - 0.5D, endPosX, endPosY, color);

														Gui.drawRect2(posX - .5, posY, endPosX, posY + 0.5D, color);

														Gui.drawRect2(endPosX - 0.5D, posY, endPosX, endPosY, color);
													}
												}

												// Gui.drawRect2(posX, posY, endPosX, endPosY, new Color(205, 255, 255,
												// 50).getRGB());
												// HEALTH BAR
												float health = player.getHealth();
												float maxHealth = player.getMaxHealth();
//                   	                        new Color(0, 255, 0, 255).getRGB();
												Gui.drawRect2(posX - 1.2,
														endPosY - (health / maxHealth) * (endPosY - posY), posX - 2,
														endPosY, Color.green.getRGB());

											} else {
												Gui.drawRect2(posX + .5, posY, posX - 1,
														posY + (endPosY - posY) / 4 + .5, black);
												Gui.drawRect2(posX - 1, endPosY, posX + .5,
														endPosY - (endPosY - posY) / 4 - .5, black);
												Gui.drawRect2(posX - 1, posY - .5, posX + (endPosX - posX) / 3 + .5,
														posY + 1, black);
												Gui.drawRect2(endPosX - (endPosX - posX) / 3 - .5, posY - .5, endPosX,
														posY + 1, black);
												Gui.drawRect2(endPosX - 1, posY, endPosX + .5,
														posY + (endPosY - posY) / 4 + .5, black);
												Gui.drawRect2(endPosX - 1, endPosY, endPosX + .5,
														endPosY - (endPosY - posY) / 4 - .5, black);
												Gui.drawRect2(posX - 1, endPosY - 1, posX + (endPosX - posX) / 3 + .5,
														endPosY + .5, black);
												Gui.drawRect2(endPosX - (endPosX - posX) / 3 - .5, endPosY - 1,
														endPosX + .5, endPosY + .5, black);
												Gui.drawRect2(posX, posY, posX - .5, posY + (endPosY - posY) / 4,
														color);
												Gui.drawRect2(posX, endPosY, posX - .5, endPosY - (endPosY - posY) / 4,
														color);
												Gui.drawRect2(posX - .5, posY, posX + (endPosX - posX) / 3, posY + .5,
														color);
												Gui.drawRect2(endPosX - (endPosX - posX) / 3, posY, endPosX, posY + .5,
														color);
												Gui.drawRect2(endPosX - .5, posY, endPosX, posY + (endPosY - posY) / 4,
														color);
												Gui.drawRect2(endPosX - .5, endPosY, endPosX,
														endPosY - (endPosY - posY) / 4, color);
												Gui.drawRect2(posX, endPosY - .5, posX + (endPosX - posX) / 3, endPosY,
														color);
												Gui.drawRect2(endPosX - (endPosX - posX) / 3, endPosY - .5,
														endPosX - .5, endPosY, color);

											}
										}

									}
								}
							}

							GL11.glPopMatrix();
						}
						break;
					case "Circle":
						double x2 = 0;
						double z2 = 0;
						double y2 = 0;
						glPushMatrix();
						GlStateManager.enableBlend();
						GlStateManager.disableDepth();
						GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						GlStateManager.color(Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), 100);
						GlStateManager.disableTexture2D();
						glBegin(GL_TRIANGLE_STRIP);
						for (int i = 0; i <= 360; i++) {
							x2 = Math.sin(((i * Math.PI) / 180)) * 1.1;
							z2 = -Math.cos(((i * Math.PI) / 180)) * 1.1;

							y2 = Math.cos(((i * Math.PI) / 180)) * 0.01;

							glVertex3d(x + x2, y + y2 + ani - 0.24, z + z2);
							glVertex3d(x + x2, y + y2 + ani, z + z2);

						}

						glEnd();
						GlStateManager.enableTexture2D();
						GlStateManager.enableDepth();
						GlStateManager.disableBlend();
						// RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z +
						// 1D));
						// glColor4f(50, 150, 50, 255);
						glPopMatrix();

						break;

					}
				}
			}
		}
		if (event instanceof EventTick) {

			if (ani <= 0) {
				max = false;
			}
			if (ani >= mc.thePlayer.getEyeHeight()) {
				max = true;
			}

			if (max == false) {
				ani += 0.04;
			} else if (ani != 0) {
				ani -= 0.04;
			}

		}

	}

	/*
	 * Rainbow ESP übern Gegner GlStateManager.pushMatrix();
	 * GlStateManager.disableDepth(); glEnable(GL_BLEND); glDisable(GL_TEXTURE_2D);
	 * glEnable(GL_LINE_SMOOTH); glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	 * glBegin(GL_LINE_LOOP); glColor4f(50, 50, 50, 100);
	 * 
	 * for (int i = 0; i <= 360; i++) { double x2 = Math.sin(((i * Math.PI) / 180))
	 * * 2;
	 * 
	 * double y2 = Math.cos(((i * Math.PI) / 180)) * 0.4; glVertex3d(x + x2, y + y2,
	 * z + z2); }
	 * 
	 * glEnd(); glDisable(GL_LINE_SMOOTH); glEnable(GL_TEXTURE_2D);
	 * glDisable(GL_BLEND);
	 * 
	 * // RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z +
	 * 1D)); GlStateManager.enableDepth(); GlStateManager.popMatrix();
	 */
	private boolean isInViewFrustrum(Entity entity) {
		return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
	}

	private boolean isInViewFrustrum(AxisAlignedBB bb) {
		Entity current = mc.getRenderViewEntity();
		frustum.setPosition(current.posX, current.posY, current.posZ);
		return frustum.isBoundingBoxInFrustum(bb);
	}

	private double interpolate(double current, double old, double scale) {
		return old + (current - old) * scale;
	}

	private Vector3d project2D(int scaleFactor, double x, double y, double z) {
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

		if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, vector)) {
			return new Vector3d(vector.get(0) / scaleFactor, (Display.getHeight() - vector.get(1)) / scaleFactor,
					vector.get(2));
		}

		return null;
	}

	private void collectEntities() {
		collectedEntities.clear();
		java.util.List<Entity> playerEntities = mc.theWorld.loadedEntityList;
		for (Entity entity : playerEntities) {
			if (entity instanceof EntityPlayer && !(entity instanceof EntityPlayerSP) && !entity.isDead) {
				collectedEntities.add(entity);
			}
		}
	}

	public static void drawRect(double x1, double y1, double x2, double y2, int argbColor) {
		if (x1 < x2) {
			double temp = x1;
			x1 = x2;
			x2 = temp;
		}

		if (y1 < y2) {
			double temp1 = y1;
			y1 = y2;
			y2 = temp1;
		}

		float a = (float) (argbColor >> 24 & 255) / 255.0F;
		float r = (float) (argbColor >> 16 & 255) / 255.0F;
		float g = (float) (argbColor >> 8 & 255) / 255.0F;
		float b = (float) (argbColor & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(r, g, b, a);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos((double) x1, (double) y2, 0.0D).endVertex();
		worldrenderer.pos((double) x2, (double) y2, 0.0D).endVertex();
		worldrenderer.pos((double) x2, (double) y1, 0.0D).endVertex();
		worldrenderer.pos((double) x1, (double) y1, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	  public static void drawChestESP() {
	        List<TileEntity> loadedTileEntityList = mc.getMinecraft().theWorld.loadedTileEntityList;
	        for (int i = 0, loadedTileEntityListSize = loadedTileEntityList.size(); i < loadedTileEntityListSize; i++) {
	            TileEntity tileEntity = loadedTileEntityList.get(i);
	            if (tileEntity instanceof TileEntityChest) {     
	                GlStateManager.disableTexture2D();
	                TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity,
	                        mc.getMinecraft().timer.renderPartialTicks, 1);
	                GlStateManager.enableTexture2D();


	            }
	        }

	    }
}
