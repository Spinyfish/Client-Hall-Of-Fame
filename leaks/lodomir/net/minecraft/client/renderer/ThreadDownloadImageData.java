/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.HttpPipeline;
import optifine.HttpRequest;
import optifine.HttpResponse;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadDownloadImageData
extends SimpleTexture {
    private static final Logger logger = LogManager.getLogger();
    private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
    private final File cacheFile;
    private final String imageUrl;
    private final IImageBuffer imageBuffer;
    private BufferedImage bufferedImage;
    private Thread imageThread;
    private boolean textureUploaded;
    private static final String __OBFID = "CL_00001049";
    public Boolean imageFound = null;
    public boolean pipeline = false;

    public ThreadDownloadImageData(File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, IImageBuffer imageBufferIn) {
        super(textureResourceLocation);
        this.cacheFile = cacheFileIn;
        this.imageUrl = imageUrlIn;
        this.imageBuffer = imageBufferIn;
    }

    private void checkTextureUploaded() {
        if (!this.textureUploaded && this.bufferedImage != null) {
            this.textureUploaded = true;
            if (this.textureLocation != null) {
                this.deleteGlTexture();
            }
            TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
        }
    }

    @Override
    public int getGlTextureId() {
        this.checkTextureUploaded();
        return super.getGlTextureId();
    }

    public void setBufferedImage(BufferedImage bufferedImageIn) {
        this.bufferedImage = bufferedImageIn;
        if (this.imageBuffer != null) {
            this.imageBuffer.skinAvailable();
        }
        this.imageFound = this.bufferedImage != null;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (this.bufferedImage == null && this.textureLocation != null) {
            super.loadTexture(resourceManager);
        }
        if (this.imageThread == null) {
            if (this.cacheFile != null && this.cacheFile.isFile()) {
                logger.debug("Loading http texture from local cache ({})", new Object[]{this.cacheFile});
                try {
                    this.bufferedImage = ImageIO.read(this.cacheFile);
                    if (this.imageBuffer != null) {
                        this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
                    }
                    this.imageFound = this.bufferedImage != null;
                }
                catch (IOException ioexception) {
                    logger.error("Couldn't load skin " + this.cacheFile, (Throwable)ioexception);
                    this.loadTextureFromServer();
                }
            } else {
                this.loadTextureFromServer();
            }
        }
    }

    protected void loadTextureFromServer() {
        this.imageThread = new Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet()){
            private static final String __OBFID = "CL_00001050";

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void run() {
                block13: {
                    HttpURLConnection httpurlconnection;
                    block11: {
                        block12: {
                            httpurlconnection = null;
                            logger.debug("Downloading http texture from {} to {}", new Object[]{ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.cacheFile});
                            if (ThreadDownloadImageData.this.shouldPipeline()) {
                                ThreadDownloadImageData.this.loadPipelined();
                                return;
                            }
                            httpurlconnection = (HttpURLConnection)new URL(ThreadDownloadImageData.this.imageUrl).openConnection(Minecraft.getMinecraft().getProxy());
                            httpurlconnection.setDoInput(true);
                            httpurlconnection.setDoOutput(false);
                            httpurlconnection.connect();
                            if (httpurlconnection.getResponseCode() / 100 == 2) break block11;
                            if (httpurlconnection.getErrorStream() != null) {
                                Config.readAll(httpurlconnection.getErrorStream());
                            }
                            if (httpurlconnection == null) break block12;
                            httpurlconnection.disconnect();
                        }
                        ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
                        return;
                    }
                    try {
                        BufferedImage bufferedimage;
                        if (ThreadDownloadImageData.this.cacheFile != null) {
                            FileUtils.copyInputStreamToFile((InputStream)httpurlconnection.getInputStream(), (File)ThreadDownloadImageData.this.cacheFile);
                            bufferedimage = ImageIO.read(ThreadDownloadImageData.this.cacheFile);
                        } else {
                            bufferedimage = TextureUtil.readBufferedImage(httpurlconnection.getInputStream());
                        }
                        if (ThreadDownloadImageData.this.imageBuffer != null) {
                            bufferedimage = ThreadDownloadImageData.this.imageBuffer.parseUserSkin(bufferedimage);
                        }
                        ThreadDownloadImageData.this.setBufferedImage(bufferedimage);
                        if (httpurlconnection == null) break block13;
                        httpurlconnection.disconnect();
                    }
                    catch (Exception exception) {
                        block14: {
                            try {
                                logger.error("Couldn't download http texture: " + exception.getClass().getName() + ": " + exception.getMessage());
                                if (httpurlconnection == null) break block14;
                                httpurlconnection.disconnect();
                            }
                            catch (Throwable throwable) {
                                if (httpurlconnection != null) {
                                    httpurlconnection.disconnect();
                                }
                                ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
                                throw throwable;
                            }
                        }
                        ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
                        return;
                    }
                }
                ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
                return;
            }
        };
        this.imageThread.setDaemon(true);
        this.imageThread.start();
    }

    private boolean shouldPipeline() {
        if (!this.pipeline) {
            return false;
        }
        Proxy proxy = Minecraft.getMinecraft().getProxy();
        return proxy.type() != Proxy.Type.DIRECT && proxy.type() != Proxy.Type.SOCKS ? false : this.imageUrl.startsWith("http://");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadPipelined() {
        HttpResponse httpresponse;
        block8: {
            HttpRequest httprequest = HttpPipeline.makeRequest(this.imageUrl, Minecraft.getMinecraft().getProxy());
            httpresponse = HttpPipeline.executeRequest(httprequest);
            if (httpresponse.getStatus() / 100 == 2) break block8;
            this.imageFound = this.bufferedImage != null;
            return;
        }
        try {
            BufferedImage bufferedimage;
            byte[] abyte = httpresponse.getBody();
            ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte);
            if (this.cacheFile != null) {
                FileUtils.copyInputStreamToFile((InputStream)bytearrayinputstream, (File)this.cacheFile);
                bufferedimage = ImageIO.read(this.cacheFile);
            } else {
                bufferedimage = TextureUtil.readBufferedImage(bytearrayinputstream);
            }
            if (this.imageBuffer != null) {
                bufferedimage = this.imageBuffer.parseUserSkin(bufferedimage);
            }
            this.setBufferedImage(bufferedimage);
            this.imageFound = this.bufferedImage != null;
        }
        catch (Exception exception) {
            try {
                logger.error("Couldn't download http texture: " + exception.getClass().getName() + ": " + exception.getMessage());
                this.imageFound = this.bufferedImage != null;
            }
            catch (Throwable throwable) {
                this.imageFound = this.bufferedImage != null;
                throw throwable;
            }
            return;
        }
    }
}

