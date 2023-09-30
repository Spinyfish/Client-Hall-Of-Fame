package net.minecraft.crash;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

import java.util.List;
import java.util.concurrent.Callable;

public class CrashReportCategory {
    private final CrashReport crashReport;
    private final String name;
    private final List<CrashReportCategory.Entry> children = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportCategory(final CrashReport report, final String name) {
        this.crashReport = report;
        this.name = name;
    }

    public static String getCoordinateInfo(final double x, final double y, final double z) {
        return String.format("%.2f,%.2f,%.2f - %s", Double.valueOf(x), Double.valueOf(y), Double.valueOf(z), getCoordinateInfo(new BlockPos(x, y, z)));
    }

    public static String getCoordinateInfo(final BlockPos pos) {
        final int i = pos.getX();
        final int j = pos.getY();
        final int k = pos.getZ();
        final StringBuilder stringbuilder = new StringBuilder();

        try {
            stringbuilder.append(String.format("World: (%d,%d,%d)", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)));
        } catch (final Throwable var17) {
            stringbuilder.append("(Error finding world loc)");
        }

        stringbuilder.append(", ");

        try {
            final int l = i >> 4;
            final int i1 = k >> 4;
            final int j1 = i & 15;
            final int k1 = j >> 4;
            final int l1 = k & 15;
            final int i2 = l << 4;
            final int j2 = i1 << 4;
            final int k2 = (l + 1 << 4) - 1;
            final int l2 = (i1 + 1 << 4) - 1;
            stringbuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", Integer.valueOf(j1), Integer.valueOf(k1), Integer.valueOf(l1), Integer.valueOf(l), Integer.valueOf(i1), Integer.valueOf(i2), Integer.valueOf(j2), Integer.valueOf(k2), Integer.valueOf(l2)));
        } catch (final Throwable var16) {
            stringbuilder.append("(Error finding chunk loc)");
        }

        stringbuilder.append(", ");

        try {
            final int j3 = i >> 9;
            final int k3 = k >> 9;
            final int l3 = j3 << 5;
            final int i4 = k3 << 5;
            final int j4 = (j3 + 1 << 5) - 1;
            final int k4 = (k3 + 1 << 5) - 1;
            final int l4 = j3 << 9;
            final int i5 = k3 << 9;
            final int j5 = (j3 + 1 << 9) - 1;
            final int i3 = (k3 + 1 << 9) - 1;
            stringbuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", Integer.valueOf(j3), Integer.valueOf(k3), Integer.valueOf(l3), Integer.valueOf(i4), Integer.valueOf(j4), Integer.valueOf(k4), Integer.valueOf(l4), Integer.valueOf(i5), Integer.valueOf(j5), Integer.valueOf(i3)));
        } catch (final Throwable var15) {
            stringbuilder.append("(Error finding world loc)");
        }

        return stringbuilder.toString();
    }

    /**
     * Adds a Crashreport section with the given name with the value set to the result of the given Callable;
     */
    public void addCrashSectionCallable(final String sectionName, final Callable<String> callable) {
        try {
            this.addCrashSection(sectionName, callable.call());
        } catch (final Throwable throwable) {
            this.addCrashSectionThrowable(sectionName, throwable);
        }
    }

    /**
     * Adds a Crashreport section with the given name with the given value (convered .toString())
     */
    public void addCrashSection(final String sectionName, final Object value) {
        this.children.add(new CrashReportCategory.Entry(sectionName, value));
    }

    /**
     * Adds a Crashreport section with the given name with the given Throwable
     */
    public void addCrashSectionThrowable(final String sectionName, final Throwable throwable) {
        this.addCrashSection(sectionName, throwable);
    }

    /**
     * Resets our stack trace according to the current trace, pruning the deepest 3 entries.  The parameter indicates
     * how many additional deepest entries to prune.  Returns the number of entries in the resulting pruned stack trace.
     */
    public int getPrunedStackTrace(final int size) {
        final StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();

        if (astacktraceelement.length <= 0) {
            return 0;
        } else {
            this.stackTrace = new StackTraceElement[astacktraceelement.length - 3 - size];
            System.arraycopy(astacktraceelement, 3 + size, this.stackTrace, 0, this.stackTrace.length);
            return this.stackTrace.length;
        }
    }

    /**
     * Do the deepest two elements of our saved stack trace match the given elements, in order from the deepest?
     */
    public boolean firstTwoElementsOfStackTraceMatch(final StackTraceElement s1, final StackTraceElement s2) {
        if (this.stackTrace.length != 0 && s1 != null) {
            final StackTraceElement stacktraceelement = this.stackTrace[0];

            if (stacktraceelement.isNativeMethod() == s1.isNativeMethod() && stacktraceelement.getClassName().equals(s1.getClassName()) && stacktraceelement.getFileName().equals(s1.getFileName()) && stacktraceelement.getMethodName().equals(s1.getMethodName())) {
                if (s2 != null != this.stackTrace.length > 1) {
                    return false;
                } else if (s2 != null && !this.stackTrace[1].equals(s2)) {
                    return false;
                } else {
                    this.stackTrace[0] = s1;
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Removes the given number entries from the bottom of the stack trace.
     */
    public void trimStackTraceEntriesFromBottom(final int amount) {
        final StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - amount];
        System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
        this.stackTrace = astacktraceelement;
    }

    public void appendToStringBuilder(final StringBuilder builder) {
        builder.append("-- ").append(this.name).append(" --\n");
        builder.append("Details:");

        for (final CrashReportCategory.Entry crashreportcategory$entry : this.children) {
            builder.append("\n\t");
            builder.append(crashreportcategory$entry.getKey());
            builder.append(": ");
            builder.append(crashreportcategory$entry.getValue());
        }

        if (this.stackTrace != null && this.stackTrace.length > 0) {
            builder.append("\nStacktrace:");

            for (final StackTraceElement stacktraceelement : this.stackTrace) {
                builder.append("\n\tat ");
                builder.append(stacktraceelement.toString());
            }
        }
    }

    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }

    public static void addBlockInfo(final CrashReportCategory category, final BlockPos pos, final Block blockIn, final int blockData) {
        final int i = Block.getIdFromBlock(blockIn);
        category.addCrashSectionCallable("Block type", new Callable<String>() {
            public String call() throws Exception {
                try {
                    return String.format("ID #%d (%s // %s)", Integer.valueOf(i), blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName());
                } catch (final Throwable var2) {
                    return "ID #" + i;
                }
            }
        });
        category.addCrashSectionCallable("Block data value", new Callable<String>() {
            public String call() throws Exception {
                if (blockData < 0) {
                    return "Unknown? (Got " + blockData + ")";
                } else {
                    final String s = String.format("%4s", Integer.toBinaryString(blockData)).replace(" ", "0");
                    return String.format("%1$d / 0x%1$X / 0b%2$s", Integer.valueOf(blockData), s);
                }
            }
        });
        category.addCrashSectionCallable("Block location", new Callable<String>() {
            public String call() throws Exception {
                return CrashReportCategory.getCoordinateInfo(pos);
            }
        });
    }

    public static void addBlockInfo(final CrashReportCategory category, final BlockPos pos, final IBlockState state) {
        category.addCrashSectionCallable("Block", new Callable<String>() {
            public String call() throws Exception {
                return state.toString();
            }
        });
        category.addCrashSectionCallable("Block location", new Callable<String>() {
            public String call() throws Exception {
                return CrashReportCategory.getCoordinateInfo(pos);
            }
        });
    }

    static class Entry {
        private final String key;
        private final String value;

        public Entry(final String key, final Object value) {
            this.key = key;

            if (value == null) {
                this.value = "~~NULL~~";
            } else if (value instanceof Throwable) {
                final Throwable throwable = (Throwable) value;
                this.value = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            } else {
                this.value = value.toString();
            }
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }
}
