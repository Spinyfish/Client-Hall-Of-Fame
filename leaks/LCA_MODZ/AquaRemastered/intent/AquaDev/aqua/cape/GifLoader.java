package intent.AquaDev.aqua.cape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GifLoader {
   private ImageFrame[] readGif(InputStream stream, String name) throws FileNotFoundException, IOException {
      ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
      reader.setInput(ImageIO.createImageInputStream(stream));
      return this.readGIF(reader, name);
   }

   private ImageFrame[] readGIF(ImageReader reader, String name) throws IOException {
      ArrayList<ImageFrame> frames = new ArrayList<>(2);
      int width = -1;
      int height = -1;
      IIOMetadata metadata = reader.getStreamMetadata();
      if (metadata != null) {
         IIOMetadataNode globalRoot = (IIOMetadataNode)metadata.getAsTree(metadata.getNativeMetadataFormatName());
         NodeList globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");
         if (globalScreenDescriptor != null && globalScreenDescriptor.getLength() > 0) {
            IIOMetadataNode screenDescriptor = (IIOMetadataNode)globalScreenDescriptor.item(0);
            if (screenDescriptor != null) {
               width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
               height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
            }
         }
      }

      BufferedImage master = null;
      Graphics2D masterGraphics = null;
      int frameIndex = 0;

      while(true) {
         BufferedImage image;
         try {
            image = reader.read(frameIndex);
         } catch (IndexOutOfBoundsException var21) {
            reader.dispose();
            return frames.toArray(new ImageFrame[frames.size()]);
         }

         if (width == -1 || height == -1) {
            width = image.getWidth();
            height = image.getHeight();
         }

         IIOMetadataNode root = (IIOMetadataNode)reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
         IIOMetadataNode gce = (IIOMetadataNode)root.getElementsByTagName("GraphicControlExtension").item(0);
         int delay = Integer.valueOf(gce.getAttribute("delayTime"));
         String disposal = gce.getAttribute("disposalMethod");
         int x = 0;
         int y = 0;
         if (master == null) {
            System.err.println("Width: " + width);
            System.err.println("Height: " + height);
            master = new BufferedImage(width, height, 2);
            masterGraphics = master.createGraphics();
            masterGraphics.setBackground(new Color(0, 0, 0, 0));
         } else {
            NodeList children = root.getChildNodes();

            for(int nodeIndex = 0; nodeIndex < children.getLength(); ++nodeIndex) {
               Node nodeItem = children.item(nodeIndex);
               if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                  NamedNodeMap map = nodeItem.getAttributes();
                  x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
                  y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
               }
            }
         }

         masterGraphics.drawImage(image, x, y, null);
         BufferedImage copy = new BufferedImage(master.getColorModel(), master.copyData(null), master.isAlphaPremultiplied(), null);
         frames.add(new ImageFrame(copy, delay, disposal, name));
         if (!disposal.equals("restoreToPrevious")) {
            if (disposal.equals("restoreToBackgroundColor")) {
               masterGraphics.clearRect(x, y, image.getWidth(), image.getHeight());
            }
         } else {
            BufferedImage from = null;

            for(int i = frameIndex - 1; i >= 0; --i) {
               if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
                  from = frames.get(i).getImage();
                  break;
               }
            }

            master = new BufferedImage(from.getColorModel(), from.copyData(null), from.isAlphaPremultiplied(), null);
            masterGraphics = master.createGraphics();
            masterGraphics.setBackground(new Color(0, 0, 0, 0));
         }

         ++frameIndex;
      }
   }

   public ImageFrame[] readGifFromAssets(String GIFName, String name) {
      try {
         return this.readGif(this.getClass().getResourceAsStream("/assets/minecraft/Aqua/gui/" + GIFName + ".gif"), name);
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public ImageFrame[] readGifFromFile(File file, String name) {
      try {
         return this.readGif(new FileInputStream(file), name);
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public ImageFrame[] readGifFromURL(URL URL, String name) {
      try {
         return this.readGif(URL.openStream(), name);
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }
}
