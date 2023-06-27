package de.jcm.discordgamesdk.activity;

import java.util.*;
import java.lang.ref.*;

public class Activity implements AutoCloseable
{
    private static final ReferenceQueue<Activity> QUEUE;
    private static final ArrayList<ActivityReference> REFERENCES;
    private static final Thread QUEUE_THREAD;
    private final long pointer;
    private final ActivityTimestamps timestamps;
    private final ActivityAssets assets;
    private final ActivityParty party;
    private final ActivitySecrets secrets;
    
    public Activity() {
        this.pointer = this.allocate();
        this.timestamps = new ActivityTimestamps(this.getTimestamps(this.pointer));
        this.assets = new ActivityAssets(this.getAssets(this.pointer));
        this.party = new ActivityParty(this.getParty(this.pointer));
        this.secrets = new ActivitySecrets(this.getSecrets(this.pointer));
    }
    
    public Activity(final long pointer) {
        this.pointer = pointer;
        this.timestamps = new ActivityTimestamps(this.getTimestamps(pointer));
        this.assets = new ActivityAssets(this.getAssets(pointer));
        this.party = new ActivityParty(this.getParty(pointer));
        this.secrets = new ActivitySecrets(this.getSecrets(pointer));
        final ActivityReference reference = new ActivityReference(this, Activity.QUEUE);
        Activity.REFERENCES.add(reference);
    }
    
    public long getApplicationId() {
        return this.getApplicationId(this.pointer);
    }
    
    public String getName() {
        return this.getName(this.pointer);
    }
    
    public void setState(final String state) {
        if (state.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 127");
        }
        this.setState(this.pointer, state);
    }
    
    public String getState() {
        return this.getState(this.pointer);
    }
    
    public void setDetails(final String details) {
        if (details.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 127");
        }
        this.setDetails(this.pointer, details);
    }
    
    public String getDetails() {
        return this.getDetails(this.pointer);
    }
    
    public void setType(final ActivityType type) {
        this.setType(this.pointer, type.ordinal());
    }
    
    public ActivityType getType() {
        return ActivityType.values()[this.getType(this.pointer)];
    }
    
    public ActivityTimestamps timestamps() {
        return this.timestamps;
    }
    
    public ActivityAssets assets() {
        return this.assets;
    }
    
    public ActivityParty party() {
        return this.party;
    }
    
    public ActivitySecrets secrets() {
        return this.secrets;
    }
    
    public void setInstance(final boolean instance) {
        this.setInstance(this.pointer, instance);
    }
    
    public boolean getInstance() {
        return this.getInstance(this.pointer);
    }
    
    private native long allocate();
    
    private static native void free(final long p0);
    
    private native long getApplicationId(final long p0);
    
    private native String getName(final long p0);
    
    private native void setState(final long p0, final String p1);
    
    private native String getState(final long p0);
    
    private native void setDetails(final long p0, final String p1);
    
    private native String getDetails(final long p0);
    
    private native void setType(final long p0, final int p1);
    
    private native int getType(final long p0);
    
    private native long getTimestamps(final long p0);
    
    private native long getAssets(final long p0);
    
    private native long getParty(final long p0);
    
    private native long getSecrets(final long p0);
    
    private native void setInstance(final long p0, final boolean p1);
    
    private native boolean getInstance(final long p0);
    
    @Override
    public void close() {
        free(this.pointer);
    }
    
    public long getPointer() {
        return this.pointer;
    }
    
    @Override
    public String toString() {
        return "Activity@" + this.pointer + "{applicationId=" + this.getApplicationId() + ", name = " + this.getName() + ", state = " + this.getState() + ", details = " + this.getDetails() + ", type = " + this.getType() + ", timestamps=" + this.timestamps() + ", assets=" + this.assets() + ", party=" + this.party() + ", secrets=" + this.secrets() + '}';
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: invokespecial   java/lang/ref/ReferenceQueue.<init>:()V
        //     7: putstatic       de/jcm/discordgamesdk/activity/Activity.QUEUE:Ljava/lang/ref/ReferenceQueue;
        //    10: new             Ljava/util/ArrayList;
        //    13: dup            
        //    14: invokespecial   java/util/ArrayList.<init>:()V
        //    17: putstatic       de/jcm/discordgamesdk/activity/Activity.REFERENCES:Ljava/util/ArrayList;
        //    20: new             Ljava/lang/Thread;
        //    23: dup            
        //    24: invokedynamic   BootstrapMethod #0, run:()Ljava/lang/Runnable;
        //    29: ldc             "Activity-Cleaner"
        //    31: invokespecial   java/lang/Thread.<init>:(Ljava/lang/Runnable;Ljava/lang/String;)V
        //    34: putstatic       de/jcm/discordgamesdk/activity/Activity.QUEUE_THREAD:Ljava/lang/Thread;
        //    37: getstatic       de/jcm/discordgamesdk/activity/Activity.QUEUE_THREAD:Ljava/lang/Thread;
        //    40: iconst_1       
        //    41: invokevirtual   java/lang/Thread.setDaemon:(Z)V
        //    44: getstatic       de/jcm/discordgamesdk/activity/Activity.QUEUE_THREAD:Ljava/lang/Thread;
        //    47: invokevirtual   java/lang/Thread.start:()V
        //    50: return         
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Unsupported node type: com.strobel.decompiler.ast.Lambda
        //     at com.strobel.decompiler.ast.Error.unsupportedNode(Error.java:32)
        //     at com.strobel.decompiler.ast.GotoRemoval.exit(GotoRemoval.java:612)
        //     at com.strobel.decompiler.ast.GotoRemoval.exit(GotoRemoval.java:586)
        //     at com.strobel.decompiler.ast.GotoRemoval.exit(GotoRemoval.java:598)
        //     at com.strobel.decompiler.ast.GotoRemoval.exit(GotoRemoval.java:586)
        //     at com.strobel.decompiler.ast.GotoRemoval.transformLeaveStatements(GotoRemoval.java:625)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotosCore(GotoRemoval.java:57)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:53)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at us.deathmarine.luyten.FileSaver.doSaveJarDecompiled(FileSaver.java:192)
        //     at us.deathmarine.luyten.FileSaver.access$300(FileSaver.java:45)
        //     at us.deathmarine.luyten.FileSaver$4.run(FileSaver.java:112)
        //     at java.lang.Thread.run(Unknown Source)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static class ActivityReference extends PhantomReference<Activity>
    {
        private final long pointer;
        
        public ActivityReference(final Activity referent, final ReferenceQueue<? super Activity> q) {
            super(referent, q);
            this.pointer = referent.pointer;
        }
        
        public long getPointer() {
            return this.pointer;
        }
    }
}
