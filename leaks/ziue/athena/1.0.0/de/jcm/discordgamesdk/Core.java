package de.jcm.discordgamesdk;

import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.nio.file.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import java.util.function.*;

public class Core implements AutoCloseable
{
    public static final Consumer<Result> DEFAULT_CALLBACK;
    public static final BiConsumer<LogLevel, String> DEFAULT_LOG_HOOK;
    private final long pointer;
    private final CreateParams createParams;
    private final AtomicBoolean open;
    private final ReentrantLock lock;
    private final ActivityManager activityManager;
    private final UserManager userManager;
    private final OverlayManager overlayManager;
    private final RelationshipManager relationshipManager;
    private final ImageManager imageManager;
    private final LobbyManager lobbyManager;
    private final NetworkManager networkManager;
    private final VoiceManager voiceManager;
    
    public static void init(final File discordLibrary) {
        final File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-discord-game-sdk-" + System.nanoTime());
        if ((!tempDir.exists() || !tempDir.isDirectory()) && !tempDir.mkdir()) {
            throw new RuntimeException(new IOException("Cannot create temporary directory"));
        }
        tempDir.deleteOnExit();
        init(discordLibrary, tempDir);
    }
    
    public static void init(final File discordLibrary, final File tempDir) {
        final String name = "discord_game_sdk_jni";
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        String objectName;
        if (osName.contains("windows")) {
            osName = "windows";
            objectName = name + ".dll";
            System.load(discordLibrary.getAbsolutePath());
        }
        else if (osName.contains("linux")) {
            osName = "linux";
            objectName = "lib" + name + ".so";
        }
        else {
            if (!osName.contains("mac os")) {
                throw new RuntimeException("cannot determine OS type: " + osName);
            }
            osName = "macos";
            objectName = "lib" + name + ".dylib";
        }
        if (arch.equals("x86_64")) {
            arch = "amd64";
        }
        final String path = "/native/" + osName + "/" + arch + "/" + objectName;
        final InputStream in = Core.class.getResourceAsStream(path);
        if (in == null) {
            throw new RuntimeException(new FileNotFoundException("cannot find native library at " + path));
        }
        final File temp = new File(tempDir, objectName);
        temp.deleteOnExit();
        try {
            Files.copy(in, temp.toPath(), new CopyOption[0]);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.load(temp.getAbsolutePath());
        initDiscordNative(discordLibrary.getAbsolutePath());
    }
    
    public static void init(final URL url) {
        final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        final String protocol = url.getProtocol();
        Label_0076: {
            if (protocol.equalsIgnoreCase("file")) {
                if (osName.contains("windows")) {
                    if (!url.getFile().endsWith("discord_game_sdk.dll")) {
                        break Label_0076;
                    }
                }
                try {
                    final File file = new File(url.toURI());
                    init(file);
                    return;
                }
                catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                final InputStream in = url.openStream();
                String objectName;
                if (osName.contains("windows")) {
                    objectName = "discord_game_sdk.dll";
                }
                else if (osName.contains("mac os")) {
                    objectName = "discord_game_sdk.dylib";
                }
                else {
                    if (!osName.contains("linux")) {
                        throw new RuntimeException("cannot determine OS type: " + osName);
                    }
                    objectName = "discord_game_sdk.so";
                }
                final File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-discord-game-sdk-" + System.nanoTime());
                if ((!tempDir.exists() || !tempDir.isDirectory()) && !tempDir.mkdir()) {
                    throw new RuntimeException(new IOException("Cannot create temporary directory"));
                }
                final File temp = new File(tempDir, objectName);
                temp.deleteOnExit();
                Files.copy(in, temp.toPath(), new CopyOption[0]);
                in.close();
                init(temp, tempDir);
            }
            catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }
    }
    
    public static void initFromClasspath() {
        final String name = "discord_game_sdk";
        final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        String suffix;
        if (osName.contains("windows")) {
            suffix = ".dll";
        }
        else if (osName.contains("linux")) {
            suffix = ".so";
        }
        else {
            if (!osName.contains("mac os")) {
                throw new RuntimeException("cannot determine OS type: " + osName);
            }
            suffix = ".dylib";
        }
        if (arch.equals("amd64")) {
            arch = "x86_64";
        }
        final String res = "/lib/" + arch + "/" + name + suffix;
        init(Objects.requireNonNull(Core.class.getResource(res)));
    }
    
    private static File downloadDiscordLibrary() throws IOException {
        final String name = "discord_game_sdk";
        final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        String suffix;
        if (osName.contains("windows")) {
            suffix = ".dll";
        }
        else if (osName.contains("linux")) {
            suffix = ".so";
        }
        else {
            if (!osName.contains("mac os")) {
                throw new RuntimeException("cannot determine OS type: " + osName);
            }
            suffix = ".dylib";
        }
        if (arch.equals("amd64")) {
            arch = "x86_64";
        }
        final String zipPath = "lib/" + arch + "/" + name + suffix;
        final URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip");
        final ZipInputStream zin = new ZipInputStream(downloadUrl.openStream());
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.getName().equals(zipPath)) {
                final File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-" + name + System.nanoTime());
                if (!tempDir.mkdir()) {
                    throw new IOException("Cannot create temporary directory");
                }
                tempDir.deleteOnExit();
                final File temp = new File(tempDir, name + suffix);
                temp.deleteOnExit();
                Files.copy(zin, temp.toPath(), new CopyOption[0]);
                zin.close();
                return temp;
            }
            else {
                zin.closeEntry();
            }
        }
        zin.close();
        return null;
    }
    
    public static void initDownload() throws IOException {
        final File f = downloadDiscordLibrary();
        if (f == null) {
            throw new FileNotFoundException("cannot find native library in downloaded zip file");
        }
        init(f);
    }
    
    public static native void initDiscordNative(final String p0);
    
    public Core(final CreateParams params) {
        this.open = new AtomicBoolean(true);
        this.lock = new ReentrantLock();
        this.createParams = params;
        final Object ret = this.create(params.getPointer());
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        this.pointer = (long)ret;
        this.setLogHook(LogLevel.DEBUG, Core.DEFAULT_LOG_HOOK);
        this.activityManager = new ActivityManager(this.getActivityManager(this.pointer), this);
        this.userManager = new UserManager(this.getUserManager(this.pointer), this);
        this.overlayManager = new OverlayManager(this.getOverlayManager(this.pointer), this);
        this.relationshipManager = new RelationshipManager(this.getRelationshipManager(this.pointer), this);
        this.imageManager = new ImageManager(this.getImageManager(this.pointer), this);
        this.lobbyManager = new LobbyManager(this.getLobbyManager(this.pointer), this);
        this.networkManager = new NetworkManager(this.getNetworkManager(this.pointer), this);
        this.voiceManager = new VoiceManager(this.getVoiceManager(this.pointer), this);
    }
    
    private native Object create(final long p0);
    
    private native void destroy(final long p0);
    
    private native long getActivityManager(final long p0);
    
    private native long getUserManager(final long p0);
    
    private native long getOverlayManager(final long p0);
    
    private native long getRelationshipManager(final long p0);
    
    private native long getImageManager(final long p0);
    
    private native long getLobbyManager(final long p0);
    
    private native long getNetworkManager(final long p0);
    
    private native long getVoiceManager(final long p0);
    
    private native void runCallbacks(final long p0);
    
    private native void setLogHook(final long p0, final int p1, final BiConsumer<LogLevel, String> p2);
    
    public ActivityManager activityManager() {
        return this.activityManager;
    }
    
    public UserManager userManager() {
        return this.userManager;
    }
    
    public OverlayManager overlayManager() {
        return this.overlayManager;
    }
    
    public RelationshipManager relationshipManager() {
        return this.relationshipManager;
    }
    
    public ImageManager imageManager() {
        return this.imageManager;
    }
    
    public LobbyManager lobbyManager() {
        return this.lobbyManager;
    }
    
    public NetworkManager networkManager() {
        return this.networkManager;
    }
    
    public VoiceManager voiceManager() {
        return this.voiceManager;
    }
    
    public void runCallbacks() {
        this.execute(() -> this.runCallbacks(this.pointer));
    }
    
    public void setLogHook(final LogLevel minLevel, final BiConsumer<LogLevel, String> logHook) {
        this.execute(() -> this.setLogHook(this.pointer, minLevel.ordinal(), Objects.requireNonNull(logHook)));
    }
    
    public boolean isOpen() {
        return this.open.get();
    }
    
    @Override
    public void close() {
        if (this.open.compareAndSet(true, false)) {
            this.lock.lock();
            try {
                this.destroy(this.pointer);
            }
            finally {
                this.lock.unlock();
            }
            this.createParams.close();
        }
    }
    
    public long getPointer() {
        return this.pointer;
    }
    
    void execute(final Runnable runnable) {
        this.execute(() -> {
            runnable.run();
            return null;
        });
    }
    
     <T> T execute(final Supplier<T> provider) {
        if (!this.isOpen()) {
            throw new CoreClosedException();
        }
        this.lock.lock();
        try {
            return provider.get();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    static {
        DEFAULT_CALLBACK = (result -> {
            if (result != Result.OK) {
                throw new GameSDKException(result);
            }
            else {
                return;
            }
        });
        DEFAULT_LOG_HOOK = ((level, message) -> System.out.printf("[%s] %s\n", level, message));
    }
}
