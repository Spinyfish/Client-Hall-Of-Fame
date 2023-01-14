package net.optifine.http;

import net.minecraft.client.Minecraft;

public class FileDownloadThread extends Thread {
   private final String urlString;
   private final IFileDownloadListener listener;

   public FileDownloadThread(String urlString, IFileDownloadListener listener) {
      this.urlString = urlString;
      this.listener = listener;
   }

   @Override
   public void run() {
      try {
         byte[] abyte = HttpPipeline.get(this.urlString, Minecraft.getMinecraft().getProxy());
         this.listener.fileDownloadFinished(this.urlString, abyte, null);
      } catch (Exception var2) {
         this.listener.fileDownloadFinished(this.urlString, null, var2);
      }
   }

   public IFileDownloadListener getListener() {
      return this.listener;
   }
}
