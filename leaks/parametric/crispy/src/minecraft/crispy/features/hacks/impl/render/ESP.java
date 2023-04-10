package crispy.features.hacks.impl.render;

import crispy.features.event.Event;
import crispy.features.event.impl.render.Event3D;
import crispy.features.event.impl.render.EventUpdateModel;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.render.GLUtils;
import crispy.util.render.RenderUtils;
import crispy.util.render.WorldToScreen;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Timer;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.NumberValue;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


@HackInfo(name = "ESP", category = Category.RENDER)
public class ESP extends Hack {

    public static float h;
    private final BooleanValue twoD = new BooleanValue("2D", true);
    public static final BooleanValue chams = new BooleanValue("Chams", false);
    private final BooleanValue skeleton = new BooleanValue("Skeleton", false);
    private final Map<EntityPlayer, float[][]> playerRotationMap = new WeakHashMap<>();

    public static BooleanValue Rainbow = new BooleanValue("Rainbow", true, ESP.chams::getObject);
    public static NumberValue<Integer> Red = new NumberValue<Integer>("Red", 0, 0, 255, ESP.chams::getObject);
    public static NumberValue<Integer> Green = new NumberValue<Integer>("Green", 0, 0, 255, ESP.chams::getObject);
    public static NumberValue<Integer> Blue = new NumberValue<Integer>("Blue", 0, 0, 255, ESP.chams::getObject);

    @Override
    public void onEvent(Event e) {
        if (e instanceof Event3D) {
            if (twoD.getObject()) {
                val mvMatrix = WorldToScreen.getMatrix(GL11.GL_MODELVIEW_MATRIX);
                val projectionMatrix = WorldToScreen.getMatrix(GL11.GL_PROJECTION_MATRIX);
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glPushMatrix();
                GL11.glLoadIdentity();
                GL11.glOrtho(0.0, mc.displayWidth, mc.displayHeight, 0.0, -1.0, 1.0);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glPushMatrix();
                GL11.glLoadIdentity();
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.enableTexture2D();
                GL11.glDepthMask(true);
                GL11.glLineWidth(1.0f);
                for (Object o : Minecraft.theWorld.loadedEntityList) {
                    if (o instanceof EntityPlayer && o != mc.thePlayer) {
                        Entity entityLiving = (Entity) o;
                        RenderManager renderManager = mc.getRenderManager();
                        Timer timer = mc.timer;
                        AxisAlignedBB bb = entityLiving.getEntityBoundingBox()
                                .offset(-entityLiving.posX, -entityLiving.posY, -entityLiving.posZ)
                                .offset(entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * timer.renderPartialTicks,
                                        entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * timer.renderPartialTicks,
                                        entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * timer.renderPartialTicks)
                                .offset(-renderManager.renderPosX, -renderManager.renderPosY, -renderManager.renderPosZ);
                        List<List<Double>> boxVertices = Arrays.asList(Arrays.asList(bb.minX, bb.minY, bb.minZ), Arrays.asList(bb.minX, bb.maxY, bb.minZ), Arrays.asList(bb.maxX, bb.maxY, bb.minZ), Arrays.asList(bb.maxX, bb.minY, bb.minZ), Arrays.asList(bb.minX, bb.minY, bb.maxZ), Arrays.asList(bb.minX, bb.maxY, bb.maxZ), Arrays.asList(bb.maxX, bb.maxY, bb.maxZ), Arrays.asList(bb.maxX, bb.minY, bb.maxZ));
                        float minX = Float.MAX_VALUE;
                        float minY = Float.MAX_VALUE;
                        float maxX = -1f;
                        float maxY = -1f;
                        for (List<Double> boxVertex : boxVertices) {
                            Vector2f screenPos = WorldToScreen.worldToScreen(new Vector3f(boxVertex.get(0).floatValue(), boxVertex.get(1).floatValue(), boxVertex.get(2).floatValue()), mvMatrix, projectionMatrix, mc.displayWidth, mc.displayHeight);
                            if (screenPos == null) continue;
                            minX = Math.min(screenPos.x, minX);
                            minY = Math.min(screenPos.y, minY);
                            maxX = Math.max(screenPos.x, maxX);
                            maxY = Math.max(screenPos.y, maxY);
                        }
                        if (minX > 0 || minY > 0 || maxX <= mc.displayWidth || maxY <= mc.displayWidth) {
                            GL11.glColor4f(255, 255, 255, 255);
                            GL11.glBegin(GL11.GL_LINE_LOOP);
                            GL11.glVertex2f(minX, minY);
                            GL11.glVertex2f(minX, maxY);
                            GL11.glVertex2f(maxX, maxY);
                            GL11.glVertex2f(maxX, minY);
                            GL11.glEnd();
                        }
                    }
                }
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glPopMatrix();
                GL11.glPopAttrib();
            }
            if (chams.getObject()) {

                if (h > 255) {
                    h = 0;
                }

                h += 0.1;


            }

        }
        if (skeleton.getObject()) {
            if (e instanceof Event3D) {
                for (Object theObject : Minecraft.theWorld.loadedEntityList) {
                    if (!(theObject instanceof EntityLivingBase)) {
                        continue;
                    }
                    EntityLivingBase entityLivingBase = (EntityLivingBase) theObject;
                    if (entityLivingBase instanceof EntityPlayer) {
                        if (entityLivingBase != mc.thePlayer) {

                            onRender((Event3D) e);
                        }


                    }
                }
            } else if (e instanceof EventUpdateModel) {
                EventUpdateModel event = (EventUpdateModel) e;
                ModelPlayer model = ((EventUpdateModel) e).getModel();
                this.playerRotationMap.put(event.getPlayer(), new float[][]{{model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ}, {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ}, {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ}, {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ}, {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}});
            }
        }

    }

    public void onRender(Event3D event) {

        this.setUp(true);
        GL11.glEnable(2903);
        GL11.glDisable(2848);

        Map playerRotationMap = this.playerRotationMap;

        List<? extends Object> worldPlayers = Minecraft.theWorld.playerEntities;
        this.playerRotationMap.keySet().removeIf(e -> contain((EntityPlayer) e));
        Object[] players = playerRotationMap.keySet().toArray();
        int i = 0;

        for (int playersLength = players.length; i < playersLength; ++i) {
            EntityPlayer player = (EntityPlayer) players[i];
            float[][] entPos = (float[][]) playerRotationMap.get(player);
            if (entPos != null && player.getEntityId() != -1488 && player.isEntityAlive() && !player.isDead && player != mc.thePlayer && !player.isPlayerSleeping() && !player.isInvisible()) {
                GL11.glPushMatrix();
                float[][] modelRotations = (float[][]) playerRotationMap.get(player);
                GL11.glLineWidth(1.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                double x = GLUtils.interpolate(player.posX, player.lastTickPosX, event.getPartialTicks()) - RenderManager.renderPosX;
                double y = GLUtils.interpolate(player.posY, player.lastTickPosY, event.getPartialTicks()) - RenderManager.renderPosY;
                double z = GLUtils.interpolate(player.posZ, player.lastTickPosZ, event.getPartialTicks()) - RenderManager.renderPosZ;
                GL11.glTranslated(x, y, z);
                float bodyYawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * mc.timer.renderPartialTicks;
                GL11.glRotatef(-bodyYawOffset, 0.0F, 1.0F, 0.0F);
                GL11.glTranslated(0.0D, 0.0D, player.isSneaking() ? -0.235D : 0.0D);
                float legHeight = player.isSneaking() ? 0.6F : 0.75F;
                float rad = 57.29578F;
                GL11.glPushMatrix();
                GL11.glTranslated(-0.125D, legHeight, 0.0D);
                if (modelRotations[3][0] != 0.0F) {
                    GL11.glRotatef(modelRotations[3][0] * 57.29578F, 1.0F, 0.0F, 0.0F);
                }

                if (modelRotations[3][1] != 0.0F) {
                    GL11.glRotatef(modelRotations[3][1] * 57.29578F, 0.0F, 1.0F, 0.0F);
                }

                if (modelRotations[3][2] != 0.0F) {
                    GL11.glRotatef(modelRotations[3][2] * 57.29578F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, -legHeight, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.125D, legHeight, 0.0D);
                if (modelRotations[4][0] != 0.0F) {
                    GL11.glRotatef(modelRotations[4][0] * 57.29578F, 1.0F, 0.0F, 0.0F);
                }

                if (modelRotations[4][1] != 0.0F) {
                    GL11.glRotatef(modelRotations[4][1] * 57.29578F, 0.0F, 1.0F, 0.0F);
                }

                if (modelRotations[4][2] != 0.0F) {
                    GL11.glRotatef(modelRotations[4][2] * 57.29578F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, -legHeight, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glTranslated(0.0D, 0.0D, player.isSneaking() ? 0.25D : 0.0D);
                GL11.glPushMatrix();
                GL11.glTranslated(0.0D, player.isSneaking() ? -0.05D : 0.0D, player.isSneaking() ? -0.01725D : 0.0D);
                GL11.glPushMatrix();
                GL11.glTranslated(-0.375D, (double) legHeight + 0.55D, 0.0D);
                if (modelRotations[1][0] != 0.0F) {
                    GL11.glRotatef(modelRotations[1][0] * 57.29578F, 1.0F, 0.0F, 0.0F);
                }

                if (modelRotations[1][1] != 0.0F) {
                    GL11.glRotatef(modelRotations[1][1] * 57.29578F, 0.0F, 1.0F, 0.0F);
                }

                if (modelRotations[1][2] != 0.0F) {
                    GL11.glRotatef(-modelRotations[1][2] * 57.29578F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, -0.5D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.375D, (double) legHeight + 0.55D, 0.0D);
                if (modelRotations[2][0] != 0.0F) {
                    GL11.glRotatef(modelRotations[2][0] * 57.29578F, 1.0F, 0.0F, 0.0F);
                }

                if (modelRotations[2][1] != 0.0F) {
                    GL11.glRotatef(modelRotations[2][1] * 57.29578F, 0.0F, 1.0F, 0.0F);
                }

                if (modelRotations[2][2] != 0.0F) {
                    GL11.glRotatef(-modelRotations[2][2] * 57.29578F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, -0.5D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glRotatef(bodyYawOffset - player.rotationYawHead, 0.0F, 1.0F, 0.0F);
                GL11.glPushMatrix();
                GL11.glTranslated(0.0D, (double) legHeight + 0.55D, 0.0D);
                if (modelRotations[0][0] != 0.0F) {
                    GL11.glRotatef(modelRotations[0][0] * 57.29578F, 1.0F, 0.0F, 0.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, 0.3D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glRotatef(player.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslated(0.0D, player.isSneaking() ? -0.16175D : 0.0D, player.isSneaking() ? -0.48025D : 0.0D);
                GL11.glPushMatrix();
                GL11.glTranslated(0.0D, legHeight, 0.0D);
                GL11.glBegin(3);
                GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
                GL11.glVertex3d(0.125D, 0.0D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.0D, legHeight, 0.0D);
                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, 0.55D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.0D, (double) legHeight + 0.55D, 0.0D);
                GL11.glBegin(3);
                GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
                GL11.glVertex3d(0.375D, 0.0D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
        }

        this.setUp(false);
    }

    public void player(EntityLivingBase entity) {
        float red = 0F;
        float green = 102F;
        float blue = 0F;

        double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - RenderManager.renderPosX;
        double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks) - RenderManager.renderPosY;
        double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - RenderManager.renderPosZ;

        render(red, green, blue, xPos, yPos, zPos, (float) (entity.width - .1), entity.height);
    }


    public void render(float red, float green, float blue, double x, double y, double z, float width, float height) {
        RenderUtils.drawEntityESP(x, y, z, width, height, red, green, blue, 0.45F, 0F, 0F, 0F, 1F, .1F);
    }

    public boolean contain(EntityPlayer var0) {
        return !Minecraft.theWorld.playerEntities.contains(var0);
    }

    private void setUp(boolean start) {
        boolean smooth = false;
        if (start) {
            if (smooth) {
                GLUtils.startSmooth();
            } else {
                GL11.glDisable(2848);
            }

            GL11.glDisable(2929);
            GL11.glDisable(3553);
        } else {
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            if (smooth) {
                GLUtils.endSmooth();
            }
        }

        GL11.glDepthMask(!start);
    }

}
