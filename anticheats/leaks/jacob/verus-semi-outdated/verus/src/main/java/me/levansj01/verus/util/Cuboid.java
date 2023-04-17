package me.levansj01.verus.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.levansj01.launcher.VerusLauncher;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.api.wrapper.Direction;
import me.levansj01.verus.data.transaction.tracker.BasicLocation;
import me.levansj01.verus.data.transaction.world.ChunkRef;
import me.levansj01.verus.data.transaction.world.IVerusBlock;
import me.levansj01.verus.data.transaction.world.IVerusChunk;
import me.levansj01.verus.data.transaction.world.IVerusWorld;
import me.levansj01.verus.data.transaction.world.LazyData;
import me.levansj01.verus.util.Cuboid.1;
import me.levansj01.verus.util.java.MathHelper;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.ILocation;
import me.levansj01.verus.util.location.IVector3d;
import me.levansj01.verus.util.location.PacketLocation;
import me.levansj01.verus.util.location.PlayerLocation;
import me.levansj01.verus.util.location.Vector3d;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Cuboid {
    private double z1;
    private double x2;
    private double y1;
    private double x1;
    private double y2;
    private static final Integer MIN_Y_LEVEL = NMSManager.getInstance().getMinYLevel();
    private double z2;

    public Cuboid setValues(ILocation var1) {
        return this.setValues(var1.getX(), var1.getY(), var1.getZ());
    }

    public boolean contains(Vector3d var1) {
        return var1 == null ? false : this.contains(var1.getX(), var1.getY(), var1.getZ());
    }

    public boolean checkBlocksInternal(Supplier<String> var1, int var2, BiFunction<Integer, Integer, Boolean> var3, Predicate<IBlockPosition> var4) {
        return this.checkBlocksInternal(var1, var2, var3, Boolean::booleanValue, (var1x, var2x) -> {
            return var4.test(var2x);
        });
    }

    public Cuboid setValues(double var1, double var3, double var5) {
        return this.setValues(var1, var1, var3, var3, var5, var5);
    }

    public boolean checkBlocksInternal(Player var1, World var2, Predicate<IBlockPosition> var3) {
        NMSManager var4 = NMSManager.getInstance();
        Objects.requireNonNull(var1);
        return this.checkBlocksInternal(var1::getName, var2.getMaxHeight(), (var2x, var3x) -> {
            return var4.isLoaded(var2, var2x, var3x);
        }, var3);
    }

    public Cuboid add(double var1, double var3, double var5, double var7, double var9, double var11) {
        this.x1 += var1;
        this.x2 += var3;
        this.y1 += var5;
        this.y2 += var7;
        this.z1 += var9;
        this.z2 += var11;
        return this;
    }

    public boolean containsXZ(PlayerLocation var1) {
        return this.containsXZ(var1.getX(), var1.getZ());
    }

    public double getWidthX() {
        return this.x2 - this.x1;
    }

    public void setY1(double var1) {
        this.y1 = var1;
    }

    public boolean checkBlocks(Player var1, World var2, Predicate<Material> var3) {
        return this.checkBlocksInternal(var1, var2, (var3x) -> {
            return var3.test(var3x.getType(var1, var2));
        });
    }

    public boolean rayTraceInternal(Player var1, Predicate<MutableBlockLocation> var2) {
        Objects.requireNonNull(var1);
        return this.rayTraceInternal(var1::getName, var2);
    }

    public boolean containsXY(Vector3d var1) {
        return var1 == null ? false : this.containsXY(var1.getX(), var1.getY());
    }

    public double getZ2() {
        return this.z2;
    }

    public boolean containsXZ(Vector3d var1) {
        return var1 == null ? false : this.containsXZ(var1.getX(), var1.getZ());
    }

    public double getY2() {
        return this.y2;
    }

    public void setX2(double var1) {
        this.x2 = var1;
    }

    public String toString() {
        return "Cuboid(x1=" + this.getX1() + ", x2=" + this.getX2() + ", y1=" + this.getY1() + ", y2=" + this.getY2() + ", z1=" + this.getZ1() + ", z2=" + this.getZ2() + ")";
    }

    public Cuboid hitbox() {
        return this.add(-0.3, 0.3, 0.0, 1.8, -0.3, 0.3).expand(0.1, 0.1, 0.1);
    }

    public void setX1(double var1) {
        this.x1 = var1;
    }

    public boolean contains(ILocation var1) {
        return this.contains(var1.getX(), var1.getY(), var1.getZ());
    }

    public static Cuboid withLimit(ILocation var0, ILocation var1, int var2) {
        return var0.distanceSquared(var1) < (double)var2 ? new Cuboid(var0, var1) : new Cuboid(var1);
    }

    public Cuboid to(ILocation var1, int var2) {
        if (this.distanceSquared(var1) > (double)var2) {
            return this;
        } else {
            this.include(var1);
            return this;
        }
    }

    public double distanceSquared(ILocation var1) {
        return this.contains(var1) ? 0.0 : Math.min(Math.pow(var1.getX() - this.x1, 2.0), Math.pow(var1.getX() - this.x2, 2.0)) + Math.min(Math.pow(var1.getY() - this.y1, 2.0), Math.pow(var1.getY() - this.y2, 2.0)) + Math.min(Math.pow(var1.getZ() - this.z1, 2.0), Math.pow(var1.getZ() - this.z2, 2.0));
    }

    public <C> boolean checkBlocksInternal(Supplier<String> var1, int var2, BiFunction<Integer, Integer, C> var3, Predicate<C> var4, BiPredicate<C, IBlockPosition> var5) {
        int var6 = (int)Math.floor(this.x1);
        int var7 = (int)Math.ceil(this.x2);
        int var8 = Math.max((int)Math.floor(this.y1), MIN_Y_LEVEL);
        int var9 = Math.min((int)Math.ceil(this.y2), var2);
        int var10 = (int)Math.floor(this.z1);
        int var11 = (int)Math.ceil(this.z2);
        int var12 = (1 + var7 - var6) * (1 + var11 - var10) * (1 + var9 - var8);
        if (var12 > 500) {
            VerusLauncher.getPlugin().getLogger().severe(String.format("Tried to check %s blocks for %s in (%s, %s, %s) -> (%s, %s, %s) ", var12, var1.get(), var6, var8, var10, var7, var9, var11));
            return false;
        } else {
            MutableBlockLocation var13 = new MutableBlockLocation(var6, var8, var10);

            while(var13.getX() < var7) {
                for(; var13.getZ() < var11; var13.incrementZ()) {
                    Object var14 = var3.apply(var13.getX(), var13.getZ());
                    if (var4.test(var14)) {
                        while(var13.getY() < var9) {
                            if (!var5.test(var14, var13)) {
                                return false;
                            }

                            var13.incrementY();
                        }

                        var13.setY(var8);
                    }
                }

                var13.setZ(var10);
                var13.setY(var8);
                var13.incrementX();
            }

            return true;
        }
    }

    public Cuboid move(double var1, double var3, double var5) {
        this.x1 += var1;
        this.x2 += var1;
        this.y1 += var3;
        this.y2 += var3;
        this.z1 += var5;
        this.z2 += var5;
        return this;
    }

    public double cZ() {
        return (this.z1 + this.z2) * 0.5;
    }

    public double distanceXZ(double var1, double var3) {
        if (this.containsXZ(var1, var3)) {
            return 0.0;
        } else {
            double var5 = Math.min(Math.pow(var1 - this.x1, 2.0), Math.pow(var1 - this.x2, 2.0));
            double var7 = Math.min(Math.pow(var3 - this.z1, 2.0), Math.pow(var3 - this.z2, 2.0));
            return (double)MathHelper.sqrt_double(var5 + var7);
        }
    }

    public boolean checkLoaded(Supplier<String> var1, IVerusWorld var2) {
        return this.checkBlocksInternal(var1, 256, (var0, var1x) -> {
            return true;
        }, (var1x) -> {
            IVerusBlock var2x = var2.get(var1x);
            return var2x != null && var2x.getData() != null;
        });
    }

    public boolean containsYZ(double var1, double var3) {
        return this.y1 <= var1 && this.y2 >= var1 && this.z1 <= var3 && this.z2 >= var3;
    }

    public Cuboid(IVector3d var1) {
        this(var1.getX(), var1.getY(), var1.getZ());
    }

    public Intersection calculateIntercept(Vector3d var1, Vector3d var2) {
        Vector3d var3 = var1.getIntermediateWithXValue(var2, this.x1);
        Vector3d var4 = var1.getIntermediateWithXValue(var2, this.x2);
        Vector3d var5 = var1.getIntermediateWithYValue(var2, this.y1);
        Vector3d var6 = var1.getIntermediateWithYValue(var2, this.y2);
        Vector3d var7 = var1.getIntermediateWithZValue(var2, this.z1);
        Vector3d var8 = var1.getIntermediateWithZValue(var2, this.z2);
        if (!this.containsYZ(var3)) {
            var3 = null;
        }

        if (!this.containsYZ(var4)) {
            var4 = null;
        }

        if (!this.containsXZ(var5)) {
            var5 = null;
        }

        if (!this.containsXZ(var6)) {
            var6 = null;
        }

        if (!this.containsXY(var7)) {
            var7 = null;
        }

        if (!this.containsXY(var8)) {
            var8 = null;
        }

        Vector3d var9 = null;
        if (var3 != null) {
            var9 = var3;
        }

        if (var4 != null && (var9 == null || var1.distanceSquared(var4) < var1.distanceSquared(var9))) {
            var9 = var4;
        }

        if (var5 != null && (var9 == null || var1.distanceSquared(var5) < var1.distanceSquared(var9))) {
            var9 = var5;
        }

        if (var6 != null && (var9 == null || var1.distanceSquared(var6) < var1.distanceSquared(var9))) {
            var9 = var6;
        }

        if (var7 != null && (var9 == null || var1.distanceSquared(var7) < var1.distanceSquared(var9))) {
            var9 = var7;
        }

        if (var8 != null && (var9 == null || var1.distanceSquared(var8) < var1.distanceSquared(var9))) {
            var9 = var8;
        }

        if (var9 == null) {
            return null;
        } else {
            Direction var10;
            if (var9 == var3) {
                var10 = Direction.WEST;
            } else if (var9 == var4) {
                var10 = Direction.EAST;
            } else if (var9 == var5) {
                var10 = Direction.DOWN;
            } else if (var9 == var6) {
                var10 = Direction.UP;
            } else if (var9 == var7) {
                var10 = Direction.NORTH;
            } else {
                var10 = Direction.SOUTH;
            }

            return new Intersection(var9, var9.copy().subtract(var1), var10);
        }
    }

    public double getWidthZ() {
        return this.z2 - this.z1;
    }

    public boolean containsBlock(World var1, int var2, int var3, int var4) {
        int var5 = (int)Math.floor(this.x1);
        int var6 = (int)Math.ceil(this.x2);
        int var7 = Math.max((int)Math.floor(this.y1), MIN_Y_LEVEL);
        int var8 = Math.min((int)Math.ceil(this.y2), var1.getMaxHeight());
        int var9 = (int)Math.floor(this.z1);
        int var10 = (int)Math.ceil(this.z2);
        return var5 <= var2 && var6 > var2 && var7 <= var3 && var8 > var3 && var9 <= var4 && var10 > var4;
    }

    public Cuboid expand(double var1, double var3, double var5) {
        this.x1 -= var1;
        this.x2 += var1;
        this.y1 -= var3;
        this.y2 += var3;
        this.z1 -= var5;
        this.z2 += var5;
        return this;
    }

    public Cuboid(IBlockPosition var1) {
        this((double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
    }

    public double getZ1() {
        return this.z1;
    }

    private Cuboid(IVector3d var1, IVector3d var2) {
        this(Math.min(var1.getX(), var2.getX()), Math.max(var1.getX(), var2.getX()), Math.min(var1.getY(), var2.getY()), Math.max(var1.getY(), var2.getY()), Math.min(var1.getZ(), var2.getZ()), Math.max(var1.getZ(), var2.getZ()));
    }

    public Cuboid face(Direction var1) {
        switch (1.$SwitchMap$me$levansj01$verus$compat$api$wrapper$Direction[var1.ordinal()]) {
            case 1:
                this.y1 = this.y2;
                break;
            case 2:
                this.y2 = this.y1;
                break;
            case 3:
                this.x1 = this.x2;
                break;
            case 4:
                this.x2 = this.x1;
                break;
            case 5:
                this.z1 = this.z2;
                break;
            case 6:
                this.z2 = this.z1;
        }

        return this;
    }

    public void setY2(double var1) {
        this.y2 = var1;
    }

    public Cuboid shrink(double var1, double var3, double var5) {
        this.x1 += var1;
        this.x2 -= var1;
        this.y1 += var3;
        this.y2 -= var3;
        this.z1 += var5;
        this.z2 -= var5;
        return this;
    }

    public Cuboid setValues(double var1, double var3, double var5, double var7, double var9, double var11) {
        this.x1 = var1;
        this.x2 = var3;
        this.y1 = var5;
        this.y2 = var7;
        this.z1 = var9;
        this.z2 = var11;
        return this;
    }

    public Cuboid(double var1, double var3, double var5, double var7, double var9, double var11) {
        this.x1 = var1;
        this.x2 = var3;
        this.y1 = var5;
        this.y2 = var7;
        this.z1 = var9;
        this.z2 = var11;
    }

    public double getX1() {
        return this.x1;
    }

    public boolean isSquare() {
        return this.x1 == 0.0 && this.x2 == 1.0 && this.y1 == 0.0 && this.y2 == 1.0 && this.z1 == 0.0 && this.z2 == 1.0;
    }

    public Cuboid copy() {
        return new Cuboid(this.x1, this.x2, this.y1, this.y2, this.z1, this.z2);
    }

    public boolean rayTraceInternal(Supplier<String> var1, Predicate<MutableBlockLocation> var2) {
        double var3 = MathUtil.hypot(new double[]{this.x2 - this.x1, this.y2 - this.y1, this.z2 - this.z1});
        double var5 = (this.x2 - this.x1) / var3;
        double var7 = (this.y2 - this.y1) / var3;
        double var9 = (this.z2 - this.z1) / var3;
        double var11 = MathUtil.lowest(new Number[]{var5, var7, var9});
        int var13 = (int)Math.ceil(var11 / var3);
        if (var13 > 100) {
            VerusLauncher.getPlugin().getLogger().severe(String.format("Tried to raytrace %s blocks for %s (%s, %s, %s) -> (%s, %s, %s) ", var13, var1.get(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2));
            return false;
        } else {
            Vector3d var14 = new Vector3d(this.x1, this.y1, this.z1);
            MutableBlockLocation var15 = MutableBlockLocation.from(var14);

            while(var13-- >= 0) {
                if (!var2.test(var15)) {
                    return false;
                }

                var14.add(var11);
                var15.andThen(var14);
            }

            return true;
        }
    }

    public double cX() {
        return (this.x1 + this.x2) * 0.5;
    }

    public boolean contains(double var1, double var3, double var5) {
        return this.x1 <= var1 && this.x2 >= var1 && this.y1 <= var3 && this.y2 >= var3 && this.z1 <= var5 && this.z2 >= var5;
    }

    public void setZ2(double var1) {
        this.z2 = var1;
    }

    public boolean checkBlocks(Supplier<String> var1, IVerusWorld var2, Predicate<Material> var3) {
        return this.checkBlocksInternal(var1, var2, (var1x) -> {
            Iterator var2 = var1x.iterator();

            LazyData var3x;
            do {
                if (!var2.hasNext()) {
                    return true;
                }

                var3x = (LazyData)var2.next();
            } while(var3.test((Material)var3x.getType().get()));

            return false;
        });
    }

    public double distanceXZ(PlayerLocation var1) {
        return this.distanceXZ(var1.getX(), var1.getZ());
    }

    public BasicLocation[] corners() {
        double[] var1 = this.x1 != this.x2 ? new double[]{this.x1, this.x2} : new double[]{this.x1};
        double[] var2 = this.y1 != this.y2 ? new double[]{this.y1, this.y2} : new double[]{this.y1};
        double[] var3 = this.z1 != this.z2 ? new double[]{this.z1, this.z2} : new double[]{this.z1};
        BasicLocation[] var4 = new BasicLocation[var1.length * var2.length * var3.length];
        int var5 = 0;
        double[] var6 = var1;
        int var7 = var1.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            double var9 = var6[var8];
            double[] var11 = var2;
            int var12 = var2.length;

            for(int var13 = 0; var13 < var12; ++var13) {
                double var14 = var11[var13];
                double[] var16 = var3;
                int var17 = var3.length;

                for(int var18 = 0; var18 < var17; ++var18) {
                    double var19 = var16[var18];
                    var4[var5++] = new BasicLocation(var9, var14, var19);
                }
            }
        }

        return var4;
    }

    public Cuboid(double var1, double var3, double var5) {
        this(var1, var1, var3, var3, var5, var5);
    }

    public void setZ1(double var1) {
        this.z1 = var1;
    }

    public double getWidthY() {
        return this.y2 - this.y1;
    }

    public Cuboid add(Cuboid var1) {
        return this.add(var1.getX1(), var1.getX2(), var1.getY1(), var1.getY2(), var1.z1, var1.z2);
    }

    public Cuboid combine(Cuboid var1) {
        return new Cuboid(Math.min(this.x1, var1.x1), Math.max(this.x2, var1.x2), Math.min(this.y1, var1.y1), Math.max(this.y2, var1.y2), Math.min(this.z1, var1.z1), Math.max(this.z2, var1.z2));
    }

    public boolean checkBlocksInternal(Supplier<String> var1, IVerusWorld var2, Predicate<IVerusBlock> var3) {
        ChunkRef var4 = new ChunkRef();
        return this.checkBlocksInternal(var1, 256, (var2x, var3x) -> {
            int var4x = var2x >> 4;
            int var5 = var3x >> 4;
            return var4.same(var4x, var5) ? (IVerusChunk)var4.getChunk() : (IVerusChunk)var4.set(var2.getChunk(var4x, var5), var4x, var5);
        }, (var0) -> {
            return true;
        }, (var1x, var2x) -> {
            return var3.test(var1x == null ? null : var1x.getType(var2x));
        });
    }

    public double getY1() {
        return this.y1;
    }

    public double cY() {
        return (this.y1 + this.y2) * 0.5;
    }

    public double getX2() {
        return this.x2;
    }

    public boolean containsXZ(double var1, double var3) {
        return this.x1 <= var1 && this.x2 >= var1 && this.z1 <= var3 && this.z2 >= var3;
    }

    public void include(IVector3d var1) {
        double var2 = var1.getX();
        double var4 = var1.getY();
        double var6 = var1.getZ();
        if (var2 < this.x1) {
            this.x1 = var2;
        }

        if (var2 > this.x2) {
            this.x2 = var2;
        }

        if (var4 < this.y1) {
            this.y1 = var4;
        }

        if (var4 > this.y2) {
            this.y2 = var4;
        }

        if (var6 < this.z1) {
            this.z1 = var6;
        }

        if (var6 > this.z2) {
            this.z2 = var6;
        }

    }

    public boolean containsYZ(Vector3d var1) {
        return var1 == null ? false : this.containsYZ(var1.getY(), var1.getZ());
    }

    public boolean containsBlock(World var1, BlockPosition var2) {
        return this.containsBlock(var1, var2.getX(), var2.getY(), var2.getZ());
    }

    public boolean overlaps(Cuboid var1) {
        return !(this.x1 > var1.x2) && !(var1.x1 > this.x1) && !(this.z1 > var1.z2) && !(var1.z1 > this.z1);
    }

    public Cuboid() {
        this(0.0, 0.0, 0.0);
    }

    public boolean containsBlock(World var1, PacketLocation var2) {
        int var3 = (int)Math.floor(var2.getX());
        int var4 = (int)Math.floor(var2.getY());
        int var5 = (int)Math.floor(var2.getZ());
        return this.containsBlock(var1, var3, var4, var5);
    }

    public boolean checkBlocksAndLoaded(Supplier<String> var1, IVerusWorld var2, Predicate<Material> var3) {
        return this.checkBlocksInternal(var1, var2, (var1x) -> {
            if (var1x != null && var1x.getData() != null) {
                Iterator var2 = var1x.iterator();

                LazyData var3x;
                do {
                    if (!var2.hasNext()) {
                        return true;
                    }

                    var3x = (LazyData)var2.next();
                } while(var3.test((Material)var3x.getType().get()));

                return false;
            } else {
                return false;
            }
        });
    }

    public boolean containsXY(double var1, double var3) {
        return this.x1 <= var1 && this.x2 >= var1 && this.y1 <= var3 && this.y2 >= var3;
    }
}