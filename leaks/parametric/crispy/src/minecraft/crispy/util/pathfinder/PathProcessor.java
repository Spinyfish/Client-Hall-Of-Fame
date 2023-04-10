package crispy.util.pathfinder;

import crispy.util.MinecraftUtil;
import crispy.util.rotation.LookUtils;
import crispy.util.rotation.Rotation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

import java.util.ArrayList;

public abstract class PathProcessor implements MinecraftUtil {


    private static final KeyBinding[] CONTROLS = new KeyBinding[]{
            mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindSneak};
    protected final ArrayList<PathPos> path;
    protected int index;
    protected boolean done;
    protected int ticksOffPath;

    public PathProcessor(ArrayList<PathPos> path) {
        if (path.isEmpty())
            throw new IllegalStateException("There is no path!");

        this.path = path;
    }

    public static final void lockControls() {
        // disable keys
        for (KeyBinding key : CONTROLS)
            key.pressed = false;

        // disable sprinting
        mc.thePlayer.setSprinting(false);
    }

    public static final void releaseControls() {
        // reset keys
        for (KeyBinding key : CONTROLS)
            key.pressed = false;
    }

    public abstract void process();

    public final int getIndex() {
        return index;
    }

    public final boolean isDone() {
        return done;
    }

    public final int getTicksOffPath() {
        return ticksOffPath;
    }

    protected final void facePosition(BlockPos pos) {
        faceVectorClientIgnorePitch(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
    }

    public void faceVectorClientIgnorePitch(Vec3i vec) {
        Rotation rotation =
                LookUtils.getNeededRotations(vec);

        mc.thePlayer.rotationYaw = rotation.getYaw();
        mc.thePlayer.rotationPitch = 0;
    }
}