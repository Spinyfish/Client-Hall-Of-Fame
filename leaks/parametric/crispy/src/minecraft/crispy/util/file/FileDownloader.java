package crispy.util.file;

import crispy.util.render.CrispyProgressBar;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader extends Thread implements Runnable {

    //pOSSIBLE autoUpdatE?!
    private final File macDir;
    private final String url;
    private final String filename;
    private final ProgressBar progressBar;
    public FileDownloader(final File mcDir, final String uri, final String filen) {
        this.macDir = mcDir;
        this.url = uri;
        this.filename = filen;
        this.progressBar = new ProgressBar();


    }

    public String getExtn() {
        return this.url.substring(this.url.lastIndexOf(46));
    }

    @Override
    public void run() {

        try {
            int i = 0;

            long totalDownloadedSize = 0L;
            final HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            final long totalFileSize = con.getContentLengthLong();
            try (final InputStream is = con.getInputStream();
                 final FileOutputStream fos = new FileOutputStream(macDir)) {
                final byte[] buff = new byte[8192];
                int readedLen = 0;
                while ((readedLen = is.read(buff)) > -1) {
                    totalDownloadedSize += readedLen;
                    final int currentProgress = (int) ((((double) totalDownloadedSize) / ((double) totalFileSize)) * 100000d);
                    fos.write(buff, 0, readedLen);
                    progressBar.updateProgress(currentProgress);

                    if (i > 50) {
                        i = 0;
                    }
                    ++i;
                    if (totalDownloadedSize >= totalFileSize) {
                        System.exit(200);
                    }
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
            Minecraft.getMinecraft().crashed(new CrashReport("crashed", e));
        }
    }

    public class ProgressBar extends JPanel {
        public JFrame f;

        public JProgressBar b;

        public ProgressBar() {
            f = new JFrame("Crispy Update");
            JPanel p = new JPanel();
            b = new JProgressBar();
            b.setValue(0);
            b.setStringPainted(true);
            b.setUI(new CrispyProgressBar());
            p.add(b);
            f.add(p);
            f.setResizable(false);
            b.setBorderPainted(false);
            b.setBorder(null);
            p.setBackground(Color.BLACK);
            b.setBackground(Color.BLACK);
            f.setBackground(Color.BLACK);

            b.setBounds(26, 80, this.getWidth() - 50, 25);
            f.setSize(580, 100);
            f.setVisible(true);
            b.setMaximum(100000);

            b.setValue(0);
        }

        public void updateProgress(int progress) {
            b.setValue(progress);
        }
    }
}
