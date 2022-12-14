// 
// Decompiled by Procyon v0.5.36
// 

package intent.AquaDev.aqua.cape;

import java.net.URL;
import java.io.FileInputStream;
import java.io.File;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.awt.Graphics2D;
import org.w3c.dom.NodeList;
import javax.imageio.metadata.IIOMetadata;
import java.util.Hashtable;
import java.awt.image.WritableRaster;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.metadata.IIOMetadataNode;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.InputStream;

public class GifLoader
{
    private ImageFrame[] readGif(final InputStream stream, final String name) throws FileNotFoundException, IOException {
        final ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(ImageIO.createImageInputStream(stream));
        return this.readGIF(reader, name);
    }
    
    private ImageFrame[] readGIF(final ImageReader reader, final String name) throws IOException {
        final ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>(2);
        int width = -1;
        int height = -1;
        final IIOMetadata metadata = reader.getStreamMetadata();
        if (metadata != null) {
            final IIOMetadataNode globalRoot = (IIOMetadataNode)metadata.getAsTree(metadata.getNativeMetadataFormatName());
            final NodeList globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");
            if (globalScreenDescriptor != null && globalScreenDescriptor.getLength() > 0) {
                final IIOMetadataNode screenDescriptor = (IIOMetadataNode)globalScreenDescriptor.item(0);
                if (screenDescriptor != null) {
                    width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                    height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
                }
            }
        }
        BufferedImage master = null;
        Graphics2D masterGraphics = null;
        int frameIndex = 0;
        while (true) {
            BufferedImage image;
            try {
                image = reader.read(frameIndex);
            }
            catch (IndexOutOfBoundsException io) {
                break;
            }
            if (width == -1 || height == -1) {
                width = image.getWidth();
                height = image.getHeight();
            }
            final IIOMetadataNode root = (IIOMetadataNode)reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
            final IIOMetadataNode gce = (IIOMetadataNode)root.getElementsByTagName("GraphicControlExtension").item(0);
            final int delay = Integer.valueOf(gce.getAttribute("delayTime"));
            final String disposal = gce.getAttribute("disposalMethod");
            int x = 0;
            int y = 0;
            if (master == null) {
                System.err.println("Width: " + width);
                System.err.println("Height: " + height);
                master = new BufferedImage(width, height, 2);
                masterGraphics = master.createGraphics();
                masterGraphics.setBackground(new Color(0, 0, 0, 0));
            }
            else {
                final NodeList children = root.getChildNodes();
                for (int nodeIndex = 0; nodeIndex < children.getLength(); ++nodeIndex) {
                    final Node nodeItem = children.item(nodeIndex);
                    if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                        final NamedNodeMap map = nodeItem.getAttributes();
                        x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
                        y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
                    }
                }
            }
            masterGraphics.drawImage(image, x, y, null);
            final BufferedImage copy = new BufferedImage(master.getColorModel(), master.copyData(null), master.isAlphaPremultiplied(), null);
            frames.add(new ImageFrame(copy, delay, disposal, name));
            if (disposal.equals("restoreToPrevious")) {
                BufferedImage from = null;
                for (int i = frameIndex - 1; i >= 0; --i) {
                    if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
                        from = frames.get(i).getImage();
                        break;
                    }
                }
                master = new BufferedImage(from.getColorModel(), from.copyData(null), from.isAlphaPremultiplied(), null);
                masterGraphics = master.createGraphics();
                masterGraphics.setBackground(new Color(0, 0, 0, 0));
            }
            else if (disposal.equals("restoreToBackgroundColor")) {
                masterGraphics.clearRect(x, y, image.getWidth(), image.getHeight());
            }
            ++frameIndex;
        }
        reader.dispose();
        return frames.toArray(new ImageFrame[frames.size()]);
    }
    
    public ImageFrame[] readGifFromAssets(final String GIFName, final String name) {
        try {
            return this.readGif(this.getClass().getResourceAsStream("/assets/minecraft/Aqua/gui/" + GIFName + ".gif"), name);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ImageFrame[] readGifFromFile(final File file, final String name) {
        try {
            return this.readGif(new FileInputStream(file), name);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ImageFrame[] readGifFromURL(final URL URL, final String name) {
        try {
            return this.readGif(URL.openStream(), name);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
