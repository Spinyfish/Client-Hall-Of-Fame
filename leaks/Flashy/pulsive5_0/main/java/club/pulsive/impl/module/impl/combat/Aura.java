package club.pulsive.impl.module.impl.combat;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.*;
import club.pulsive.impl.event.render.Render3DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.misc.ClientSettings;
import club.pulsive.impl.module.impl.movement.Speed;
import club.pulsive.impl.module.impl.player.Scaffold;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.client.BlockPosUtil;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.entity.EntityValidator;
import club.pulsive.impl.util.entity.impl.*;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.MovementUtil;
import club.pulsive.impl.util.player.PlayerUtil;
import club.pulsive.impl.util.player.RotationUtil;
import club.pulsive.impl.util.render.RenderUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "KillAura", renderName = "KillAura", description = "Automatically attack entities.", aliases = "Aura", category = Category.COMBAT)
public final class Aura extends Module {
    static double[] y1 = {0.104080378093037, 0.105454222033912, 0.102888018147468, 0.099634532004642};

    private final EnumProperty<KillAuraMode> modeProperty = new EnumProperty<>("Mode", KillAuraMode.SINGLE);

    private final DoubleProperty switchDelayProperty = new DoubleProperty("Switch Delay", 50, 1, 1000, 20, Property.Representation.INT, () -> modeProperty.getValue() == KillAuraMode.SWITCH);
    private final EnumProperty<SortMode> sortProperty = new EnumProperty<>("Sort by", SortMode.HEALTH, () -> modeProperty.getValue() == KillAuraMode.SINGLE);
    public int waitDelay, groundTicks, crits;
    private final DoubleProperty minAPSProperty = new DoubleProperty("Min APS", 11, 1, 20, 1, Property.Representation.INT);
    private final DoubleProperty maxAPSProperty = new DoubleProperty("Max APS", 14, 1, 20, 1, Property.Representation.INT);

    private final EnumProperty<AttackMode> attackModeProperty = new EnumProperty<>("Attack In", AttackMode.PRE);
    private final EnumProperty<BlockMode> blockModeProperty = new EnumProperty<>("Block Mode", BlockMode.WATCHDOG);
    private final EnumProperty<RotationsMode> rotationsProperty = new EnumProperty<>("Rotations", RotationsMode.UNDETECTABLE);
    private final Property<Boolean> onlyAttackWhenLookingAtEntityLongNameMethod = new Property<>("Attack When Looking", false, () -> rotationsProperty.getValue() != RotationsMode.UNDETECTABLE);
    private final EnumProperty<RotationUtil.RotationsPoint> rotationsPointProperty = new EnumProperty<>("Rotations Point", RotationUtil.RotationsPoint.CLOSEST);

    private final DoubleProperty rangeProperty = new DoubleProperty("Range", 4.2, 1, 7, 0.1);
    private final DoubleProperty maxTargets = new DoubleProperty("Max Targets", 4, 1, 10, 1, () -> modeProperty.getValue() == KillAuraMode.MULTI);
    private final DoubleProperty blockRangeProperty = new DoubleProperty("Block Range", 4.2, 1, 10, 0.1);
    private final DoubleProperty fovRangeProperty = new DoubleProperty("Fov Range", 180, 1, 180, 10, Property.Representation.INT);

    private final MultiSelectEnumProperty<PlayerUtil.TARGETS> targetsProperty = new MultiSelectEnumProperty<>("Targets", Lists.newArrayList(PlayerUtil.TARGETS.PLAYERS), PlayerUtil.TARGETS.values());

    private final Property<Boolean> rayTraceProperty = new Property<>("Ray Trace", false);
    private final Property<Boolean> lockViewProperty = new Property<>("Lock View", false);
    private final Property<Boolean> autoBlockProperty = new Property<>("Autoblock", true);
    private final Property<Boolean> noSwingProperty = new Property<>("No Swing", false);
    private final Property<Boolean> keepSprintProperty = new Property<>("Keep Sprint", true);
   private final TimerUtil switchTimer = new TimerUtil();
    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil blockTimer = new TimerUtil();
    BlockPosUtil blockPosUtil = new BlockPosUtil();
    private float[] rotationStore;
    public boolean block, blockAnimation, changeTarget;
    private boolean rotating, hasNotThing;
    private int targetIndex, ticks;

    private final List<EntityLivingBase> targets = new ArrayList<>(), multiTargets = new ArrayList<>();
    public EntityValidator entityValidator = new EntityValidator(), blockValidator = new EntityValidator();
    private EntityLivingBase target;

    

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        if(mc.thePlayer.ticksExisted <= 5 || event.getEventState() != PacketEvent.EventState.RECEIVING) return;
        switch (blockModeProperty.getValue()) {
            case WATCHDOG: {
//                if (event.getPacket() instanceof S30PacketWindowItems && block && blockModeProperty.getValue() == BlockMode.HYPIXEL && mc.thePlayer.ticksExisted % 5 == 2) {
////                    event.setCancelled(true);
//                    PacketUtil.sendPacketNoEvent(new C0);
//                    block = false;
//                    Logger.print("unblock");
//                }
                break;
            }
        }
    };

    @EventHandler
    private final Listener<PlayerJumpEvent> playerJumpEventListener = event -> {
        if(isBypassRotations() && rotating){
            event.setYaw(this.rotationStore[0]);
        }
    };

    @EventHandler
    private final Listener<PlayerStrafeEvent> playerStrafeEventListener = event -> {
        if(isBypassRotations() && rotating){
            event.setYaw(this.rotationStore[0]);
        }
    };

    private boolean doesRotations() {
        return this.rotationsProperty.getValue() != RotationsMode.OFF;
    }

    private boolean isBypassRotations() {
        return this.rotationsProperty.getValue() == RotationsMode.UNDETECTABLE;
    }

    @Override
    public void init() {
        this.addValueChangeListener(this.modeProperty);
        super.init();
    }

    @EventHandler
    private final Listener<Render3DEvent> render3DEventListener = event -> {
        if(mc.thePlayer.ticksExisted <= 5) return;
        if (target != null && mc.theWorld != null) {
            drawCircle(target, 0.66, true);
        }
    };
    
    @Override
    public void onEnable() {
        switchTimer.reset();
        attackTimer.reset();
        blockTimer.reset();
        blockAnimation = false;
        targets.clear();
        multiTargets.clear();
        hasNotThing = false;
        changeTarget = false;
        ticks = 0;
        block = false;
        this.rotating = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        targetIndex = 0;
        blockAnimation = false;
        target = null;
        if(!mc.isSingleplayer()) {
            if(mc.getCurrentServerData().serverIP.contains("hypixel.net") && block) {
                PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, blockPosUtil.hypixelBlockPos(), EnumFacing.DOWN));
                block = false;
            }   
        }
        targets.clear();

    }

    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if(Pulsive.INSTANCE.getModuleManager().getModule(Scaffold.class).isToggled()) {
            return;
        }
        entityValidator = new EntityValidator();
        blockValidator = new EntityValidator();
        final AliveCheck aliveCheck = new AliveCheck();
        final EntityCheck entityCheck = new EntityCheck(targetsProperty.isSelected(PlayerUtil.TARGETS.PLAYERS), targetsProperty.isSelected(PlayerUtil.TARGETS.ANIMALS), targetsProperty.isSelected(PlayerUtil.TARGETS.MOBS), targetsProperty.isSelected(PlayerUtil.TARGETS.INVISIBLE));
        final TeamsCheck teamsCheck = new TeamsCheck(targetsProperty.isSelected(PlayerUtil.TARGETS.TEAMS));
        entityValidator.add(aliveCheck);
        entityValidator.add(new ConstantDistanceCheck(blockRangeProperty.getValue().floatValue()));
        entityValidator.add(entityCheck);
        entityValidator.add(teamsCheck);
        blockValidator.add(aliveCheck);
        blockValidator.add(new ConstantDistanceCheck(blockRangeProperty.getValue().floatValue()));
        blockValidator.add(entityCheck);
        blockValidator.add(teamsCheck);
        if(mc.thePlayer.ticksExisted <= 5){
            if(this.isToggled()){
                this.toggle();
            }
            return;
        }

        if(event.isUpdate()) return;;

        updateTargets();
        if(event.isPre()){
            if (target == null) {
                blockAnimation = false;
                rotating = false;
                multiTargets.clear();
            }

            if (block) {
                unblock();
            }
        }


        if(modeProperty.getValue() != KillAuraMode.MULTI) {
            target = getTarget();
            if (event.getState() == PlayerMotionEvent.EventState.PRE) {
                if (isEntityNearby()) {
                    final Vec3 hitOrigin = RotationUtil.getHitOrigin(mc.thePlayer);
                    final Vec3 attackHitVec = this.getAttackHitVec(hitOrigin, target);
                    if (doesRotations() && attackHitVec != null) {
                        final float[] rotations = RotationUtil.getRotations(
                                new float[]{lockViewProperty.getValue() ? mc.thePlayer.rotationYaw : event.getPrevYaw(),
                                        lockViewProperty.getValue() ? mc.thePlayer.rotationPitch : event.getPrevPitch()},
                                rotationsProperty.getValue() == RotationsMode.SNAP ? 0.0F : 17.5f,
                                hitOrigin,
                                attackHitVec);
                        if (!isBypassRotations()) rotationStore = new float[]{event.getPrevYaw(), event.getPrevPitch()};
                        setServerSideRotations(isBypassRotations() ? rotationStore : rotations, event, lockViewProperty.getValue());
                    }
                }

            } else {
                // if (PlayerUtil.isValid(target, targetsProperty)) {
                final Vec3 origin = RotationUtil.getHitOrigin(this.mc.thePlayer);
                // Calculate where your hit vector will be on the entity
                final MovingObjectPosition intercept = RotationUtil.calculateIntercept(
                        RotationUtil.getHittableBoundingBox(target, 0.1),
                        origin,
                        event.getYaw(),
                        event.getPitch(),
                        rangeProperty.getValue().floatValue());
                if (isEntityNearbyAttack() || (intercept != null && onlyAttackWhenLookingAtEntityLongNameMethod.getValue() && isEntityNearbyAttack())) {
                    if (attackModeProperty.getValue() == AttackMode.POST || (attackModeProperty.getValue() == AttackMode.HVH && mc.thePlayer.ticksExisted % 10 == 0)) {
                        attack(target);
                    }
                }
                //}
            }
        }else{
            final Vec3 hitOrigin = RotationUtil.getHitOrigin(mc.thePlayer);
            final boolean doRots = this.doesRotations();
            final List<float[]> rotationsToEntities = new ArrayList<>();
            if(!isEntityNearby())
                target = null;


            targets.stream().limit(maxTargets.getValue().intValue()).forEach(entity -> {
                target = entity;
                final Vec3 attackHitVec = this.getAttackHitVec(hitOrigin, target);

                if (attackHitVec != null && isEntityNearby() && event.isPre()) {
                    if (doRots) {
                        // Calculate rotations (with max yaw/pitch change & GCD) to best attack hit vec
                        final float[] rotations = RotationUtil.getRotations(
                                new float[]{lockViewProperty.getValue() ? mc.thePlayer.rotationYaw : event.getPrevYaw(),
                                        lockViewProperty.getValue() ? mc.thePlayer.rotationPitch : event.getPrevPitch()},
                                rotationsProperty.getValue() == RotationsMode.SNAP ? 0.0F : 17.5f,
                                hitOrigin,
                                attackHitVec);

                        rotationsToEntities.add(rotations);
                        multiTargets.add(target);
                    }
                }
            });
            if(event.isPre()){
                if(this.doesRotations() && isEntityNearby()){
                    final float[] avgRotations = RotationUtil.calculateAverageRotations(rotationsToEntities);
                    this.setServerSideRotations(avgRotations, event, lockViewProperty.getValue());
                }
            }
        }

        if(isEntityNearby()) {
            final Vec3 origin = RotationUtil.getHitOrigin(this.mc.thePlayer);
            final MovingObjectPosition intercept = RotationUtil.calculateIntercept(
                    RotationUtil.getHittableBoundingBox(target, 0.1),
                    origin,
                    event.getYaw(),
                    event.getPitch(),
                    rangeProperty.getValue().floatValue());

            if (event.isPre()) {
                if (Pulsive.INSTANCE.getModuleManager().getModule(Criticals.class).isToggled()
                        && target != null && mc.thePlayer.onGround && !Pulsive.INSTANCE.getModuleManager().getModule(Speed.class).isToggled()
                        && (isEntityNearbyAttack() && target.hurtResistantTime != 20) && !PlayerUtil.isInLiquid() && !MovementUtil.isInsideBlock()) {
                    event.setPosY(event.getPosY() + 0.003);
                    if (blockTimer.hasElapsed(750)) {
                        Logger.print("crit");
                        event.setPosY(event.getPosY() + 0.001);
                        blockTimer.reset();
                    }
                    event.setGround(false);
                }

                if (isEntityNearbyAttack() && ((intercept != null && onlyAttackWhenLookingAtEntityLongNameMethod.getValue()) || !onlyAttackWhenLookingAtEntityLongNameMethod.getValue())) {
                    if (attackModeProperty.getValue() == AttackMode.PRE || (attackModeProperty.getValue() == AttackMode.HVH && mc.thePlayer.ticksExisted % 10 == 0)) {
                        attack(target);
                    }
                }
            } else if (event.isPost()) {
                if (isEntityNearbyAttack() && ((intercept != null && onlyAttackWhenLookingAtEntityLongNameMethod.getValue()) || !onlyAttackWhenLookingAtEntityLongNameMethod.getValue())) {
                    if (attackModeProperty.getValue() == AttackMode.POST || (attackModeProperty.getValue() == AttackMode.HVH && mc.thePlayer.ticksExisted % 10 == 0)) {
                        attack(target);
                    }
                }

                boolean canBlock = autoBlockProperty.getValue() && mc.thePlayer.getHeldItem() != null;
                if (canBlock && !block && blockModeProperty.getValue() == BlockMode.WATCHDOG) {
                    block();
                }
                if (canBlock && !block && blockModeProperty.getValue() == BlockMode.VERUS) {
                    block();
                }
            }
        }

    };

    @EventHandler
    private final Listener<UpdateLookEvent> onUpdateLook = event -> {
        if (this.isBypassRotations()) {
            // Get the eye-pos of the player
            final Vec3 hitOrigin = RotationUtil.getHitOrigin(this.mc.thePlayer);
            // If was not rotating previously
            if (!this.rotating) {
                // Set rotationStore to the current player angles
                this.rotationStore[0] = this.mc.thePlayer.rotationYaw;
                this.rotationStore[1] = this.mc.thePlayer.rotationPitch;
            }
            // Reset rotating state
            this.rotating = false;
            // Get the current targeted entity
            final EntityLivingBase target = this.getTarget();
            if (target == null) return;
            // Find the optimal attack hit vec to aim at
            final Vec3 attackHitVec = RotationUtil.getCenterPointOnBB(target.getEntityBoundingBox(),
                    0.5 + ApacheMath.random() * 0.1);
            // Calculate the yaw/pitch deltas to target
            float[] rotations = RotationUtil.getRotations(hitOrigin, attackHitVec);
            // Apply GCD fix
            RotationUtil.applyGCD(rotations, this.rotationStore);
            RotationUtil.applySmoothing(this.rotationStore, 15.5f, rotations);
            // Update rotations store
            this.rotationStore[0] = rotations[0];
            this.rotationStore[1] = rotations[1];
            // Is now rotating
            this.rotating = true;
        }
    };

    private Vec3 getAttackHitVec(final Vec3 hitOrigin, final EntityLivingBase entity) {
        final AxisAlignedBB boundingBox = RotationUtil.getHittableBoundingBox(entity, .1f);
        // Get optimal attack hit vec
        return RotationUtil.getAttackHitVec(mc, hitOrigin, boundingBox,
                this.rotationsPointProperty.getValue().getHitVec(hitOrigin, boundingBox),
                true, 5);
    }

    public boolean isMulti(){
        return !multiTargets.isEmpty() && isToggled();
    }

    public final EntityLivingBase getTarget() {
        if (targets.isEmpty()) {
            return null;
        }

        if (modeProperty.getValue() == KillAuraMode.SINGLE) {
            return targets.get(0);
        }

        final int size = targets.size();

        if (size >= targetIndex && changeTarget) {
            targetIndex++;
            changeTarget = false;
        }


        if(switchTimer.hasElapsed(switchDelayProperty.getValue().longValue())){
            changeTarget = true;
            switchTimer.reset();
        }

        if (size <= targetIndex) {
            targetIndex = 0;
        }

        return targets.get(targetIndex);
    }

    private void updateTargets() {
        targets.clear();

        final List<Entity> entities = mc.theWorld.loadedEntityList;

        for (int i = 0, entitiesSize = entities.size(); i < entitiesSize; i++) {
            final Entity entity = entities.get(i);
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityValidator.validate(entityLivingBase) && mc.theWorld.loadedEntityList.contains(entityLivingBase)) {
                    this.targets.add(entityLivingBase);
                }
            }
        }
    }

    public boolean isEntityNearby() {
        final List<Entity> loadedEntityList = mc.theWorld.loadedEntityList;
        for (int i = 0, loadedEntityListSize = loadedEntityList.size(); i < loadedEntityListSize; i++) {
            final Entity entity = loadedEntityList.get(i);
            if (blockValidator.validate(entity)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEntityNearbyAttack(){
        return mc.thePlayer.getDistanceToEntity(getTarget()) <= rangeProperty.getValue();
    }

    private void setServerSideRotations(float[] rotations,
                                        final PlayerMotionEvent event,
                                        final boolean lockView) {
        // When using undetectable rotations set to stored
        if (this.isBypassRotations()) {
            rotations = this.rotationStore;
        }

        if(rotations == null) return;

        if(rotations[1] < 0.1 && rotations[1] > 0){
            rotations[1] = -1;
        }

        // Set event yaw (rotations yaw)
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);

        // Do lock view aim
        if (lockView) {
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1];
        }
        rotating = true;
    }
    
    private void attack(EntityLivingBase entity) {
        int min = minAPSProperty.getValue().intValue();
        int max = maxAPSProperty.getValue().intValue();
        int cps;

        if (min == max) cps = min;
        else cps = MathUtil.randomInt(min, max + 1);
        if (attackTimer.hasElapsed(1000 / cps)) {
           // mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0001, mc.thePlayer.posZ);
            if(ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
                if (noSwingProperty.getValue()) {
                    PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                } else mc.thePlayer.swingItem();
            }


            if (keepSprintProperty.getValue()) {
                PacketUtil.sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            } else {
                mc.playerController.attackEntity(mc.thePlayer, entity);
            }


            if(ViaMCP.getInstance().getVersion() > ProtocolCollection.getProtocolById(47).getVersion()){
                if (noSwingProperty.getValue()) {
                    PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                } else mc.thePlayer.swingItem();
            }
            attackTimer.reset();
            // blockTimer.reset();
            ticks++;
        }
    }

    private void block() {
        switch (blockModeProperty.getValue()) {
            case WATCHDOG:
            case VERUS: {
                if(isEntityNearbyAttack() && PlayerUtil.isHoldingSword()) {
                    if (mc.thePlayer.swingProgressInt == -1) {
                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    } else if (mc.thePlayer.swingProgressInt < 0.5) {
                        PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0081284124F, 0.00004921712F, 0.0081248912F));
                    }
                }
                break;
            }
        }
        block = true;
        blockAnimation = true;
    }

    private void unblock(){
        switch(blockModeProperty.getValue()){
            case VERUS:{
                mc.playerController.syncCurrentPlayItem();
                PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                break;
            }
            case WATCHDOG:{
//                Logger.print("" + mc.thePlayer.swingProgressInt);
//                if(mc.thePlayer.swingProgressInt == -1) {
//                    mc.playerController.syncCurrentPlayItem();
//                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//                }
                break;
            }
        }
        //blockAnimation = false;
        block = false;
    }

//    private boolean distanceCheck(EntityLivingBase entityLivingBase, boolean block) {
//        return mc.thePlayer != null && entityLivingBase != null && mc.thePlayer.getDistanceToEntity(entityLivingBase) < (block ? ApacheMath.max(rangeProperty.getValue(), blockRangeProperty.getValue()) : rangeProperty.getValue());
//    }

    public float getRange(){
        return rangeProperty.getValue().floatValue();
    }

    private void drawCircle(Entity entity, double rad, boolean shade) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glDepthMask(false);
        GlStateManager.alphaFunc(GL_GREATER, 0);
        if (shade) glShadeModel(GL_SMOOTH);
        GlStateManager.disableCull();
        glBegin(GL_TRIANGLE_STRIP);

        final double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, mc.timer.renderPartialTicks);
        final double y = (RenderUtil.interpolate(entity.posY, entity.lastTickPosY, mc.timer.renderPartialTicks)) + ApacheMath.sin(System.currentTimeMillis() / 4E+2) + 1;
        final double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, mc.timer.renderPartialTicks);
        //double x1 = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, mc.timer.renderPartialTicks);
        
        
        Color c;
        for (float i = 0; i < ApacheMath.PI * 2; i += ApacheMath.PI * 2 / 64) {
            final double vecX = x + rad * ApacheMath.cos(i);
            final double vecZ = z + rad * ApacheMath.sin(i);
            c = ClientSettings.mainColor.getValue();

            if (shade) {
                glColor4f(c.getRed() / 255F,
                        c.getGreen() / 255F,
                        c.getBlue() / 255F,
                        0
                );
                glVertex3d(vecX, y - ApacheMath.cos(System.currentTimeMillis() / 4E+2) / 2, vecZ);
                glColor4f(c.getRed() / 255F,
                        c.getGreen() / 255F,
                        c.getBlue() / 255F,
                        0.85F
                );
            }
            glVertex3d(vecX, y, vecZ);
        }
        glEnd();
        if (shade) glShadeModel(GL_FLAT);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.alphaFunc(GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
        glColor3f(255, 255, 255);
    }

    @AllArgsConstructor
    private enum SortMode {
        HEALTH("Health", new HealthSorter()),
        ARMOR("Armor", new ArmorSorter()),
        FOV("FOV", new FovSorter()),
        HURTTIME("Hurttime", new HurtTimeSorter()),
        DISTANCE("Distance", new DistanceSorter());

        private final String modeName;
        private final Comparator<EntityLivingBase> sorter;

        @Override
        public String toString() {return modeName;}
    }

    @AllArgsConstructor
    private enum KillAuraMode {
        SINGLE("Single"),
        SWITCH("Switch"),
        MULTI("Multi");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }

    @AllArgsConstructor
    private enum AttackMode {
        PRE("Pre"),
        POST("Post"),
        HVH("HvH");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }

    @AllArgsConstructor
    private enum BlockMode {
        WATCHDOG("Watchdog"),
        VERUS("Verus");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }


    private final static class HealthSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(PlayerUtil.getEffectiveHealth(o1), PlayerUtil.getEffectiveHealth(o2));
        }
    }

    private final static class ArmorSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(o1.getTotalArmorValue(), o2.getTotalArmorValue());
        }
    }

    private final static class FovSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(o1.getPositionVector().subtract(mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks)).angle(mc.thePlayer.getLookVec()), o2.getPositionVector().subtract(mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks)).angle(mc.thePlayer.getLookVec()));
        }
    }

    private final static class HurtTimeSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(20 - o1.hurtResistantTime, 20 - o2.hurtResistantTime);
        }
    }

    private final static class DistanceSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return -Double.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2));
        }
    }
    public MultiSelectEnumProperty<PlayerUtil.TARGETS> targetsProperty() {
        return targetsProperty;
    }

    @AllArgsConstructor
    public enum RotationsMode{
        OFF("Off"),
        SNAP("Snap"),
        UNDETECTABLE("Undetectable"),
        SMOOTH("Smooth");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }
}
