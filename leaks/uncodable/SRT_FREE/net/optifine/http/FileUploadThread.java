package net.optifine.http;

import java.util.Map;

public class FileUploadThread extends Thread {
   private final String urlString;
   private final Map headers;
   private final byte[] content;
   private final IFileUploadListener listener;

   public FileUploadThread(String urlString, Map headers, byte[] content, IFileUploadListener listener) {
      this.urlString = urlString;
      this.headers = headers;
      this.content = content;
      this.listener = listener;
   }

   @Override
   public void run() {
      try {
         HttpUtils.post(this.urlString, this.headers, this.content);
         this.listener.fileUploadFinished(this.urlString, this.content, null);
      } catch (Exception var2) {
         this.listener.fileUploadFinished(this.urlString, this.content, var2);
      }
   }

   public byte[] getContent() {
      return this.content;
   }

   public IFileUploadListener getListener() {
      return this.listener;
   }
}
