package net.minecraft.crash;

import com.google.common.collect.Lists;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import net.optifine.CrashReporter;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class CrashReport {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Description of the crash report.
     */
    private final String description;

    /**
     * The Throwable that is the "cause" for this crash and Crash Report.
     */
    private final Throwable cause;

    /**
     * Category of crash
     */
    private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
    private final List<CrashReportCategory> crashReportSections = Lists.newArrayList();

    /**
     * File of crash report.
     */
    private File crashReportFile;
    private boolean field_85059_f = true;
    private StackTraceElement[] stacktrace = new StackTraceElement[0];
    private boolean reported = false;

    public CrashReport(final String descriptionIn, final Throwable causeThrowable) {
        this.description = descriptionIn;
        this.cause = causeThrowable;
        this.populateEnvironment();
    }

    /**
     * Populates this crash report with initial information about the running server and operating system / java
     * environment
     */
    private void populateEnvironment() {
        this.theReportCategory.addCrashSectionCallable("Minecraft Version", new Callable<String>() {
            public String call() {
                return "1.8.9";
            }
        });
        this.theReportCategory.addCrashSectionCallable("Operating System", new Callable<String>() {
            public String call() {
                return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Java Version", new Callable<String>() {
            public String call() {
                return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Java VM Version", new Callable<String>() {
            public String call() {
                return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Memory", new Callable<String>() {
            public String call() {
                final Runtime runtime = Runtime.getRuntime();
                final long i = runtime.maxMemory();
                final long j = runtime.totalMemory();
                final long k = runtime.freeMemory();
                final long l = i / 1024L / 1024L;
                final long i1 = j / 1024L / 1024L;
                final long j1 = k / 1024L / 1024L;
                return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
            }
        });
        this.theReportCategory.addCrashSectionCallable("JVM Flags", new Callable<String>() {
            public String call() {
                final RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
                final List<String> list = runtimemxbean.getInputArguments();
                int i = 0;
                final StringBuilder stringbuilder = new StringBuilder();

                for (final String s : list) {
                    if (s.startsWith("-X")) {
                        if (i++ > 0) {
                            stringbuilder.append(" ");
                        }

                        stringbuilder.append(s);
                    }
                }

                return String.format("%d total; %s", Integer.valueOf(i), stringbuilder.toString());
            }
        });
        this.theReportCategory.addCrashSectionCallable("IntCache", new Callable<String>() {
            public String call() throws Exception {
                return IntCache.getCacheSizes();
            }
        });

        if (Reflector.FMLCommonHandler_enhanceCrashReport.exists()) {
            final Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
            Reflector.callString(object, Reflector.FMLCommonHandler_enhanceCrashReport, this, this.theReportCategory);
        }
    }

    /**
     * Returns the description of the Crash Report.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the Throwable object that is the cause for the crash and Crash Report.
     */
    public Throwable getCrashCause() {
        return this.cause;
    }

    /**
     * Gets the various sections of the crash report into the given StringBuilder
     */
    public void getSectionsInStringBuilder(final StringBuilder builder) {
        if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
            this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
        }

        if (this.stacktrace != null && this.stacktrace.length > 0) {
            builder.append("-- Head --\n");
            builder.append("Stacktrace:\n");

            for (final StackTraceElement stacktraceelement : this.stacktrace) {
                builder.append("\t").append("at ").append(stacktraceelement.toString());
                builder.append("\n");
            }

            builder.append("\n");
        }

        for (final CrashReportCategory crashreportcategory : this.crashReportSections) {
            crashreportcategory.appendToStringBuilder(builder);
            builder.append("\n\n");
        }

        this.theReportCategory.appendToStringBuilder(builder);
    }

    /**
     * Gets the stack trace of the Throwable that caused this crash report, or if that fails, the cause .toString().
     */
    public String getCauseStackTraceOrString() {
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        Throwable throwable = this.cause;

        if (throwable.getMessage() == null) {
            if (throwable instanceof NullPointerException) {
                throwable = new NullPointerException(this.description);
            } else if (throwable instanceof StackOverflowError) {
                throwable = new StackOverflowError(this.description);
            } else if (throwable instanceof OutOfMemoryError) {
                throwable = new OutOfMemoryError(this.description);
            }

            throwable.setStackTrace(this.cause.getStackTrace());
        }

        String s = throwable.toString();

        try {
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            throwable.printStackTrace(printwriter);
            s = stringwriter.toString();
        } finally {
            IOUtils.closeQuietly(stringwriter);
            IOUtils.closeQuietly(printwriter);
        }

        return s;
    }

    /**
     * Gets the complete report with headers, stack trace, and different sections as a string.
     */
    public String getCompleteReport() {
        if (!this.reported) {
            this.reported = true;
            CrashReporter.onCrashReport(this, this.theReportCategory);
        }

        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Crash Report ----\n");
        Reflector.call(Reflector.BlamingTransformer_onCrash, stringbuilder);
        Reflector.call(Reflector.CoreModManager_onCrash, stringbuilder);
        stringbuilder.append("// ");
        stringbuilder.append(getWittyComment());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time: ");
        stringbuilder.append((new SimpleDateFormat()).format(new Date()));
        stringbuilder.append("\n");
        stringbuilder.append("Description: ");
        stringbuilder.append(this.description);
        stringbuilder.append("\n\n");
        stringbuilder.append(this.getCauseStackTraceOrString());
        stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

        for (int i = 0; i < 87; ++i) {
            stringbuilder.append("-");
        }

        stringbuilder.append("\n\n");
        this.getSectionsInStringBuilder(stringbuilder);
        return stringbuilder.toString();
    }

    /**
     * Gets the file this crash report is saved into.
     */
    public File getFile() {
        return this.crashReportFile;
    }

    /**
     * Saves this CrashReport to the given file and returns a value indicating whether we were successful at doing so.
     */
    public boolean saveToFile(final File toFile) {
        if (this.crashReportFile != null) {
            return false;
        } else {
            if (toFile.getParentFile() != null) {
                toFile.getParentFile().mkdirs();
            }

            try {
                final FileWriter filewriter = new FileWriter(toFile);
                filewriter.write(this.getCompleteReport());
                filewriter.close();
                this.crashReportFile = toFile;
                return true;
            } catch (final Throwable throwable) {
                logger.error("Could not save crash report to " + toFile, throwable);
                return false;
            }
        }
    }

    public CrashReportCategory getCategory() {
        return this.theReportCategory;
    }

    /**
     * Creates a CrashReportCategory
     */
    public CrashReportCategory makeCategory(final String name) {
        return this.makeCategoryDepth(name, 1);
    }

    /**
     * Creates a CrashReportCategory for the given stack trace depth
     */
    public CrashReportCategory makeCategoryDepth(final String categoryName, final int stacktraceLength) {
        final CrashReportCategory crashreportcategory = new CrashReportCategory(this, categoryName);

        if (this.field_85059_f) {
            final int i = crashreportcategory.getPrunedStackTrace(stacktraceLength);
            final StackTraceElement[] astacktraceelement = this.cause.getStackTrace();
            StackTraceElement stacktraceelement = null;
            StackTraceElement stacktraceelement1 = null;
            final int j = astacktraceelement.length - i;

            if (j < 0) {
//                System.out.println("Negative index in crash report handler (" + astacktraceelement.length + "/" + i + ")");
            }

            if (astacktraceelement != null && 0 <= j && j < astacktraceelement.length) {
                stacktraceelement = astacktraceelement[j];

                if (astacktraceelement.length + 1 - i < astacktraceelement.length) {
                    stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - i];
                }
            }

            this.field_85059_f = crashreportcategory.firstTwoElementsOfStackTraceMatch(stacktraceelement, stacktraceelement1);

            if (i > 0 && !this.crashReportSections.isEmpty()) {
                final CrashReportCategory crashreportcategory1 = this.crashReportSections.get(this.crashReportSections.size() - 1);
                crashreportcategory1.trimStackTraceEntriesFromBottom(i);
            } else if (astacktraceelement != null && astacktraceelement.length >= i && 0 <= j && j < astacktraceelement.length) {
                this.stacktrace = new StackTraceElement[j];
                System.arraycopy(astacktraceelement, 0, this.stacktrace, 0, this.stacktrace.length);
            } else {
                this.field_85059_f = false;
            }
        }

        this.crashReportSections.add(crashreportcategory);
        return crashreportcategory;
    }

    /**
     * Gets a random witty comment for inclusion in this CrashReport
     */
    private static String getWittyComment() {
        final String[] astring = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

        try {
            return astring[(int) (System.nanoTime() % (long) astring.length)];
        } catch (final Throwable var2) {
            return "Witty comment unavailable :(";
        }
    }

    /**
     * Creates a crash report for the exception
     */
    public static CrashReport makeCrashReport(final Throwable causeIn, final String descriptionIn) {
        final CrashReport crashreport;

        if (causeIn instanceof ReportedException) {
            crashreport = ((ReportedException) causeIn).getCrashReport();
        } else {
            crashreport = new CrashReport(descriptionIn, causeIn);
        }

        return crashreport;
    }
}
