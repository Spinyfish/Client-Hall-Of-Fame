package net.optifine.http;

import net.minecraft.client.Minecraft;

public class FileDownloadThread extends Thread {
    private String urlString = null;
    private IFileDownloadListener listener = null;

    public FileDownloadThread(final String urlString, final IFileDownloadListener listener) {
        this.urlString = urlString;
        this.listener = listener;
    }

    public void run() {
        try {
            final byte[] abyte = HttpPipeline.get(this.urlString, Minecraft.getMinecraft().getProxy());
            this.listener.fileDownloadFinished(this.urlString, abyte, null);
        } catch (final Exception exception) {
            this.listener.fileDownloadFinished(this.urlString, null, exception);
        }
    }

    public String getUrlString() {
        return this.urlString;
    }

    public IFileDownloadListener getListener() {
        return this.listener;
    }
}
