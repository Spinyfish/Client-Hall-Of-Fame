package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.player.*;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.DevelopmentFeature;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.*;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.newevent.impl.other.BlockAABBEvent;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.newevent.impl.render.Render3DEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.SlotUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.value.impl.DragValue;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.pathfinding.alan.api.Path;
import com.alan.clients.util.pathfinding.alan.Pathfinder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import org.apache.commons.lang3.RandomUtils;
import util.time.StopWatch;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.impl.StringValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Rise
@DevelopmentFeature
@ModuleInfo(name = "Test", description = "module.other.test.description", category = Category.OTHER)
public final class Test extends Module {

    private final ArrayList<Integer> integerArrayList = new ArrayList<>();
    private final ArrayList<Double> doubleArrayList = new ArrayList<>();
    private final ArrayList<Float> floatArrayList = new ArrayList<>();
    private final ArrayList<Packet<?>> packetArrayOutList = new ArrayList<>();
    private final ArrayList<Packet<?>> packetArrayInList = new ArrayList<>();
    private int anInt, anInt2;
    private boolean aBoolean, aBoolean2, delay;
    private double aDouble, aDouble2, serverPosX, serverPosY, serverPosZ;
    private float aFloat, aFloat2;
    private Vec3 lastPosition, currentPosition, lastCurrentPosition;
    private Entity entity;
    private Packet<?> aPacket;
    private EntityOtherPlayerMP otherEntity;

    private final List<Packet<?>> packets = new ArrayList<>();
    private final ConcurrentLinkedQueue<PacketUtil.TimedPacket> timedPackets = new ConcurrentLinkedQueue<>();
    private final StopWatch timerUtil = new StopWatch();
    private double startPosY, distance, moveSpeed, y, lastX, lastY, lastZ;
    private int stage, bestBlockStack;
    public static boolean set, doFly;
    private BlockPos startPos;
    private final DecimalFormat format = new DecimalFormat("0.0");
    private Vec3 targetBlock;
    private BlockPos blockFace;
    private DragValue positionValue = new DragValue("Position", this, new Vector2d(255, 255));
    public Vec3 position = new Vec3(0, 0, 0);
    private final StringValue runIf = new StringValue("Run If () ->", this, "onGround");
    private Pathfinder pathfinder;
    private Path path;
    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<Vector3d> positions = new ArrayList<>();
    private World world;
    private boolean placeHolder;

    //    private final List<Double> jumpValues = new ArrayList<Double>() {{
//        add(0.42); +
//        add(0.33319999363422365);
//        add(0.24813599859094576);
//        add(0.16477328182606651);
//        add(0.08307781780646721);
//        add(0.0030162615090425808);
//        add(-0.0784000015258789);
//        add(-0.1552320045166016);
//        add(-0.230527368912964);
//        add(-0.30431682745754424);
//        add(-0.37663049823865513);
//        add(-0.44749789698341763);
//    }};
//
//    private final List<Double> speedValues = new ArrayList<Double>() {{
//        add(0.3023122842180768);
//        add(0.2982909636320639);
//        add(0.2946315653119493);
//        add(0.29130151576857954);
//        add(0.2882711731879136);
//        add(0.285513563574277);
//        add(0.283004140640921);
//        add(0.2807205673107283);
//        add(0.27864251688221625);
//        add(0.2767514920910006);
//        add(0.27503066045614655);
//        add(0.27346470444576965);
//    }};


    //    0.03684800296020513
//    -0.042288957922058
//    -0.11984318109609361
//    0.11760000228881837
//    0.03684800296020513
//    -0.042288957922058
//    -0.11984318109609361
//    0.11760000228881837
//    0.03684800296020513


    //0.40444491418477924

//
//                    case "1":
//    v = -0.0784000015258789;
//                                break;
//                            case "2":
//    v = -0.09800000190734864;
//                                break;
//                            case "3":
//    v = -0.09800000190735147;
//                                break;

//        for (int i = 0; i < 3; i++) {
//        double position = startingLocationY;
//        for (double value : jumpValues) {
//            if (value == 0.42) value = 0.42f;
//            position += value;
//
//            if (position < startingLocationY) position = startingLocationY;
//            PacketUtil.sendPacketWithoutEvent(new C03PacketPlayer.C04PacketPlayerPosition(startingLocationX, position, startingLocationZ, false));
//        }
//    }
//        PacketUtil.sendPacketWithoutEvent(new C03PacketPlayer.C04PacketPlayerPosition(startingLocationX,startingLocationY,startingLocationZ, true));
//

    Font font = FontManager.getNunito(15);

    @Override
    protected void onDisable() {
//        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }

    @Override
    protected void onEnable() {
        position = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        anInt = 0;
        moveSpeed = 0;

        aBoolean = false;
        mc.thePlayer.jump();
    }

    @EventLink
    public final Listener<PacketReceiveEvent> receive = event -> {
        Packet<?> packet = event.getPacket();

        placeHolder = true;
    };

    @EventLink
    public final Listener<PacketSendEvent> send = event -> {
        Packet<?> packet = event.getPacket();

        placeHolder = true;
    };

    @EventLink
    public final Listener<TeleportEvent> teleport = event -> {
        placeHolder = true;
    };

    @EventLink
    public final Listener<BlockAABBEvent> blockAABB = event -> {
        placeHolder = true;
    };

    @EventLink
    public final Listener<Render2DEvent> render2D = event -> {

//        FontManager.getProductSansMedium(16).drawString("Testing", 10, 10, Color.WHITE.getRGB());
//        FontManager.getProductSansMedium(16).drawString("çéâêîôûàèìòùëïü", 50, 50, Color.WHITE.getRGB());

        placeHolder = true;
    };

    @EventLink
    public final Listener<Render3DEvent> render3D = event -> {
        placeHolder = true;
    };

    @EventLink
    public final Listener<PreUpdateEvent> preUpdate = event -> {

        if (aBoolean) {
            mc.thePlayer.motionY = 0;
            MoveUtil.strafe(0.42);
        }

        placeHolder = true;
    };

    @EventLink
    public final Listener<WaterEvent> water = event -> {
        placeHolder = true;
    };

    @EventLink
    public final Listener<AttackEvent> attack = event -> {
        placeHolder = true;
    };

    @EventLink
    public final Listener<WorldChangeEvent> worldChange = event -> {
        placeHolder = true;
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        anInt++;


        placeHolder = true;
    };

    @EventLink()
    public final Listener<PostMotionEvent> onPostMotionEvent = event -> {

        placeHolder = true;
    };

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {
        placeHolder = true;
    };

    @EventLink()
    public final Listener<HitSlowDownEvent> onHitSlowDown = event -> {
        placeHolder = true;
    };
}
