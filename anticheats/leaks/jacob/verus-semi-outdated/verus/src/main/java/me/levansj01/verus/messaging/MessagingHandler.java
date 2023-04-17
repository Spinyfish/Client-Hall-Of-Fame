package me.levansj01.verus.messaging;

import me.levansj01.verus.*;
import me.levansj01.verus.messaging.listener.*;
import org.bukkit.plugin.*;
import me.levansj01.verus.compat.*;
import me.levansj01.verus.storage.*;
import me.levansj01.launcher.*;
import java.util.logging.*;
import java.util.function.*;
import java.nio.file.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import org.bukkit.entity.*;
import com.google.common.io.*;
import org.bukkit.plugin.messaging.*;

public class MessagingHandler
{

    private static final String verusChannel;
    private ThreadLocal cipher;
    private static MessagingHandler instance;
    private VerusPlugin plugin;

    private BrandListener brandListener;

    private void unregisterChannels() {
        this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel((Plugin)this.plugin.getPlugin());
        this.plugin.getServer().getMessenger().unregisterIncomingPluginChannel((Plugin)this.plugin.getPlugin());
    }
    
    static {
        String verusChannel2;
        if (NMSManager.getInstance().getServerVersion().afterEq(ServerVersion.v1_13_R2)) {
            verusChannel2 = "bungeecord:verus";
        
        }
        else {
            verusChannel2 = "VerusBungee";
        }
        verusChannel = verusChannel2;
    }
    
    public static MessagingHandler getInstance() {
        MessagingHandler instance;
        if (MessagingHandler.instance == null) {
            instance = (MessagingHandler.instance = new MessagingHandler());
    
        }
        else {
            instance = MessagingHandler.instance;
        }
        return instance;
    }
    
    private void registerChannels() {
        final Messenger messenger = this.plugin.getServer().getMessenger();
        final VerusLauncher plugin = this.plugin.getPlugin();
        final String messagingChannel = StorageEngine.getInstance().getVerusConfig().getMessagingChannel();
        if (!messagingChannel.equals("BungeeCord")) {
            messenger.registerOutgoingPluginChannel((Plugin)plugin, "BungeeCord");
     
        }
        messenger.registerOutgoingPluginChannel((Plugin)plugin, MessagingHandler.verusChannel);
    
        messenger.registerOutgoingPluginChannel((Plugin)plugin, messagingChannel);
  
        this.registerBrandListener(messenger, plugin);
    }
    
    public void disable() {
        if (this.plugin == null) {
            return;
        }
        this.unregisterChannels();
        if (this.brandListener != null) {
            this.brandListener = null;
        }
    }
    
    public void enable(final VerusPlugin plugin) {
        this.plugin = plugin;
        this.registerChannels();
        final String secretKeyPath = StorageEngine.getInstance().getVerusConfig().getSecretKeyPath();
        final Path value = Paths.get(secretKeyPath, new String[0]);
        byte[] allBytes;
        if (Files.notExists(value, new LinkOption[0])) {
            allBytes = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(allBytes);
            Files.write(value, allBytes, new OpenOption[0]);
            plugin.getPlugin().getLogger().log(Level.INFO, "Secret key " + secretKeyPath + " not found, generating...");
       
        }
        else {
            allBytes = Files.readAllBytes(value);
        }
        plugin.getPlugin().getLogger().log(Level.WARNING, "Please configure VerusLink so it has access to this secret key (" + secretKeyPath + ").");
    
        this.cipher = ThreadLocal.withInitial((Supplier<?>)MessagingHandler::lambda.enable.0);
    }
    
    public BrandListener getBrandListener() {
        return this.brandListener;
    }
    
    private static Cipher lambda.enable.0(final byte[] array, final VerusPlugin verusPlugin) {
        final Cipher instance = Cipher.getInstance("AES");
        instance.init(1, new SecretKeySpec(array, "AES"));
      
        return instance;
    }
    
    public void handleBan(final Player player, final String s) {
        final ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("HandleBan");
        dataOutput.writeUTF(s);
        final byte[] byteArray = dataOutput.toByteArray();
        player.sendPluginMessage((Plugin)this.plugin.getPlugin(), "BungeeCord", byteArray);
        final byte[] doFinal = this.cipher.get().doFinal(byteArray);
  
        final ByteArrayDataOutput dataOutput2 = ByteStreams.newDataOutput();
        dataOutput2.writeUTF("verus");
        dataOutput2.writeInt(doFinal.length);
        dataOutput2.write(doFinal);
        player.sendPluginMessage((Plugin)this.plugin.getPlugin(), StorageEngine.getInstance().getVerusConfig().getMessagingChannel(), dataOutput2.toByteArray());
    }
    
    private void registerBrandListener(final Messenger messenger, final VerusLauncher verusLauncher) {
        String s;
        if (NMSManager.getInstance().getServerVersion().afterEq(ServerVersion.v1_13_R2)) {
            s = "minecraft:brand";
   
        }
        else {
            s = "MC|Brand";
        }
        messenger.registerIncomingPluginChannel((Plugin)verusLauncher, s, (PluginMessageListener)(this.brandListener = new BrandListener()));
  
    }
}
