package net.minecraft.client.network;

import java.util.concurrent.atomic.*;
import org.apache.logging.log4j.*;
import net.minecraft.client.*;
import com.google.common.collect.*;
import net.minecraft.client.multiplayer.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class LanServerDetector
{
    private static final AtomicInteger field_148551_a;
    private static final Logger logger;
    
    static {
        field_148551_a = new AtomicInteger(0);
        logger = LogManager.getLogger();
    }
    
    public static class LanServer
    {
        private String lanServerMotd;
        private String lanServerIpPort;
        private long timeLastSeen;
        
        public LanServer(final String motd, final String address) {
            this.lanServerMotd = motd;
            this.lanServerIpPort = address;
            this.timeLastSeen = Minecraft.getSystemTime();
        }
        
        public String getServerMotd() {
            return this.lanServerMotd;
        }
        
        public String getServerIpPort() {
            return this.lanServerIpPort;
        }
        
        public void updateLastSeen() {
            this.timeLastSeen = Minecraft.getSystemTime();
        }
    }
    
    public static class LanServerList
    {
        private List<LanServer> listOfLanServers;
        boolean wasUpdated;
        
        public LanServerList() {
            this.listOfLanServers = (List<LanServer>)Lists.newArrayList();
        }
        
        public synchronized boolean getWasUpdated() {
            return this.wasUpdated;
        }
        
        public synchronized void setWasNotUpdated() {
            this.wasUpdated = false;
        }
        
        public synchronized List<LanServer> getLanServers() {
            return Collections.unmodifiableList((List<? extends LanServer>)this.listOfLanServers);
        }
        
        public synchronized void func_77551_a(final String p_77551_1_, final InetAddress p_77551_2_) {
            final String s = ThreadLanServerPing.getMotdFromPingResponse(p_77551_1_);
            String s2 = ThreadLanServerPing.getAdFromPingResponse(p_77551_1_);
            if (s2 != null) {
                s2 = p_77551_2_.getHostAddress() + ":" + s2;
                boolean flag = false;
                for (final LanServer lanserverdetector$lanserver : this.listOfLanServers) {
                    if (lanserverdetector$lanserver.getServerIpPort().equals(s2)) {
                        lanserverdetector$lanserver.updateLastSeen();
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    this.listOfLanServers.add(new LanServer(s, s2));
                    this.wasUpdated = true;
                }
            }
        }
    }
    
    public static class ThreadLanServerFind extends Thread
    {
        private final LanServerList localServerList;
        private final InetAddress broadcastAddress;
        private final MulticastSocket socket;
        
        public ThreadLanServerFind(final LanServerList p_i1320_1_) throws IOException {
            super("LanServerDetector #" + LanServerDetector.field_148551_a.incrementAndGet());
            this.localServerList = p_i1320_1_;
            this.setDaemon(true);
            this.socket = new MulticastSocket(4445);
            this.broadcastAddress = InetAddress.getByName("224.0.2.60");
            this.socket.setSoTimeout(5000);
            this.socket.joinGroup(this.broadcastAddress);
        }
        
        @Override
        public void run() {
            final byte[] abyte = new byte[1024];
            while (!this.isInterrupted()) {
                final DatagramPacket datagrampacket = new DatagramPacket(abyte, abyte.length);
                try {
                    this.socket.receive(datagrampacket);
                }
                catch (SocketTimeoutException var5) {
                    continue;
                }
                catch (IOException ioexception) {
                    LanServerDetector.logger.error("Couldn't ping server", (Throwable)ioexception);
                    break;
                }
                final String s = new String(datagrampacket.getData(), datagrampacket.getOffset(), datagrampacket.getLength());
                LanServerDetector.logger.debug(datagrampacket.getAddress() + ": " + s);
                this.localServerList.func_77551_a(s, datagrampacket.getAddress());
            }
            try {
                this.socket.leaveGroup(this.broadcastAddress);
            }
            catch (IOException ex) {}
            this.socket.close();
        }
    }
}
