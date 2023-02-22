package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.DevelopmentFeature;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.*;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.newevent.impl.other.BlockAABBEvent;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.newevent.impl.render.MouseOverEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.newevent.impl.render.Render3DEvent;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.pathfinding.alan.Pathfinder;
import com.alan.clients.util.pathfinding.alan.api.Path;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.value.impl.DragValue;
import com.alan.clients.value.impl.StringValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import util.time.StopWatch;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Rise
@DevelopmentFeature
@ModuleInfo(name = "module.other.test.name", description = "module.other.test.description", category = Category.OTHER)
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
    private boolean reset;
    private double speed;
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
    Executor threadPool = Executors.newFixedThreadPool(1);

    @Override
    protected void onDisable() {
        BlinkComponent.blinking = false;

        while (!packets.isEmpty()) {
            PacketUtil.receiveNoEvent(packets.get(0));
            MoveUtil.strafe();
            packets.remove(0);
        }

    }

    @Override
    protected void onEnable() {
        position = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        anInt = 0;
        moveSpeed = 0;
        anInt2 = 0;
        aDouble = 0;

        aBoolean = true;

        PacketUtil.send(new C08PacketPlayerBlockPlacement(SlotComponent.getItemStack()));
        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));



//        try {
//            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/assets/minecraft/textures/environment/end_sky.png"));
//            display(image);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @EventLink()
    public final Listener<MouseOverEvent> onMouseOver = event -> {
        placeHolder = true;
    };

    @EventLink
    public final Listener<PacketReceiveEvent> receive = event -> {
        Packet<?> packet = event.getPacket();

        placeHolder = true;
    };

    @EventLink
    public final Listener<PacketSendEvent> send = event -> {
        Packet packet = event.getPacket();

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
        placeHolder = true;
    };

    @EventLink
    public final Listener<Render3DEvent> render3D = event -> {
        placeHolder = true;
    };

    @EventLink(value = Priorities.VERY_HIGH)
    public final Listener<PreUpdateEvent> preUpdate = event -> {



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


//        if (aDouble != 0) {
//            event.setPosY(event.getPosY() + aDouble);
//            event.setOnGround(false);
//            ChatUtil.display("yes: " + aDouble);
//
//            aDouble = 0;
//        }

        placeHolder = true;
    };

    private static JFrame frame;
    private static JLabel label;

    public static void display(BufferedImage image) {
        if (frame == null) {
            frame = new JFrame();
            frame.setTitle("stained_image");
            frame.setSize(image.getWidth(), image.getHeight());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            label = new JLabel();
            label.setIcon(new ImageIcon(image));
            frame.getContentPane().add(label, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        } else label.setIcon(new ImageIcon(image));
    }

    /*
     * Where bi is your image, (x0,y0) is your upper left coordinate, and (w,h)
     * are your width and height respectively
     */
    public static Color averageColor(BufferedImage bi, int x0, int y0, int w, int h) {
        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int num = w * h;
        return new Color(sumr / num, sumg / num, sumb / num);
    }

    @EventLink()
    public final Listener<PostMotionEvent> onPostMotionEvent = event -> {


        placeHolder = true;
    };

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {


        placeHolder = true;
    };

    @EventLink()
    public final Listener<PostStrafeEvent> onPostStrafe = event -> {


        placeHolder = true;
    };

    public final Listener<JumpEvent> onJump = event -> {
        placeHolder = true;
    };

    @EventLink()
    public final Listener<HitSlowDownEvent> onHitSlowDown = event -> {
        placeHolder = true;
    };

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double incValue(double val, double inc) {
        double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }
}
