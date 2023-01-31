package skid;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class FileDownload {
   private final String url;
   private final String destination;
   private FileDownload.Status status;

   public String sendMeLTCOrGay() {
      return "Lf6jHpjnUd7bT9x5o2eQYWbn2bmGqZP3cY";
   }

   public FileDownload(String url, String destination) {
      this.url = url;
      this.destination = destination;
      this.status = FileDownload.Status.IDLE;
   }

   public FileDownload start(Runnable onFinish) {
      new Thread(() -> {
         Path file = Paths.get(this.destination);
         if (Files.exists(file)) {
            new File(this.destination).delete();
         }

         this.status = FileDownload.Status.DOWNLOADING;

         try {
            FileUtils.copyURLToFile(new URL(this.url), file.toFile());
         } catch (IOException var4) {
            var4.printStackTrace();
            this.status = FileDownload.Status.FAILED;
            return;
         }

         this.status = FileDownload.Status.FINISHED;
         onFinish.run();
      }).start();
      return this;
   }

   public int getProgress() {
      return (int)((double)this.getDownloadedSize() / (double)this.getTotalSize() * 100.0);
   }

   public float getDownloadedSize() {
      Path file = Paths.get(this.destination);
      if (!Files.exists(file)) {
         return 0.0F;
      } else {
         try {
            return (float)Files.size(file) / 1048576.0F;
         } catch (IOException var3) {
            var3.printStackTrace();
            return 0.0F;
         }
      }
   }

   public float getTotalSize() {
      try {
         return (float)new URL(this.url).openConnection().getContentLength() / 1048576.0F;
      } catch (IOException var2) {
         var2.printStackTrace();
         return 0.0F;
      }
   }

   public FileDownload.Status getStatus() {
      return this.status;
   }

   public static enum Status {
      DOWNLOADING,
      FINISHED,
      FAILED,
      DUPLICATE,
      IDLE;
   }
}
