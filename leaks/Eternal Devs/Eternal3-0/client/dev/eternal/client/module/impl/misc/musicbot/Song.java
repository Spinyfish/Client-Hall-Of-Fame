package dev.eternal.client.module.impl.misc.musicbot;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.minecraft.util.BlockPos;

/**
 * <h3>Project Eternal-V3</h3>
 * <p>
 * created on 12/20/22
 *
 * @author Wazed
 */
public class Song {

  private final File file;
  private final List<Note> notes = new ArrayList<>();
  private DataInputStream dataInputStream;

  @Getter
  private float speed;

  public Song(File file) {
    this.file = file;
    try {
      read();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static short readShort(DataInputStream dataInputStream) throws IOException {
    int byte1 = dataInputStream.readUnsignedByte();
    int byte2 = dataInputStream.readUnsignedByte();
    return (short) (byte1 + (byte2 << 8));
  }

  private static int readInt(DataInputStream dataInputStream) throws IOException {
    int byte1 = dataInputStream.readUnsignedByte();
    int byte2 = dataInputStream.readUnsignedByte();
    int byte3 = dataInputStream.readUnsignedByte();
    int byte4 = dataInputStream.readUnsignedByte();
    return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
  }

  private static String readString(DataInputStream dataInputStream) throws IOException {
    int length = readInt(dataInputStream);
    StringBuilder builder = new StringBuilder(length);
    for (; length > 0; --length) {
      char c = (char) dataInputStream.readByte();
      if (c == (char) 0x0D) {
        c = ' ';
      }
      builder.append(c);
    }
    return builder.toString();
  }

  public DataInputStream getInputStream() throws FileNotFoundException {
    if (dataInputStream == null)
      return dataInputStream = new DataInputStream(new FileInputStream(file));
    return dataInputStream;
  }

  public void read() throws Exception {

    DataInputStream dis = getInputStream();

    short length = readShort(dataInputStream);
    int firstcustominstrument =
        10; // Backward compatibility - most of songs with old structure are from 1.12
    int firstcustominstrumentdiff;
    int nbsversion = 0;
    if (length == 0) {
      nbsversion = dataInputStream.readByte();
      firstcustominstrument = dataInputStream.readByte();
      if (nbsversion >= 3) {
        length = readShort(dataInputStream);
      }
    }
    firstcustominstrumentdiff = 16 - firstcustominstrument;
    short songHeight = readShort(dataInputStream);
    String title = readString(dataInputStream);
    String author = readString(dataInputStream);
    readString(dataInputStream); // original author
    String description = readString(dataInputStream);
    this.speed = readShort(dataInputStream) / 100f;
    dataInputStream.readBoolean(); // auto-save
    dataInputStream.readByte(); // auto-save duration
    dataInputStream.readByte(); // x/4ths, time signature
    readInt(dataInputStream); // minutes spent on project
    readInt(dataInputStream); // left clicks (why?)
    readInt(dataInputStream); // right clicks (why?)
    readInt(dataInputStream); // blocks added
    readInt(dataInputStream); // blocks removed
    readString(dataInputStream); // .mid/.schematic file name
    if (nbsversion >= 4) {
      dataInputStream.readByte(); // loop on/off
      dataInputStream.readByte(); // max loop count
      readShort(dataInputStream); // loop start tick
    }
    short tick = -1;
    while (true) {
      short jumpTicks = readShort(dataInputStream); // jumps till next tick
      // System.out.println("Jumps to next tick: " + jumpTicks);
      if (jumpTicks == 0) {
        break;
      }
      tick += jumpTicks;
      // System.out.println("Tick: " + tick);
      short layer = -1;
      while (true) {
        short jumpLayers = readShort(dataInputStream); // jumps till next layer
        if (jumpLayers == 0) {
          break;
        }
        layer += jumpLayers;
        // System.out.println("Layer: " + layer);
        byte instrument = dataInputStream.readByte();

        if (firstcustominstrumentdiff > 0 && instrument >= firstcustominstrument) {
          instrument += firstcustominstrumentdiff;
        }

        byte key = dataInputStream.readByte();

        if (nbsversion >= 4) {
          dataInputStream.readByte(); // note block velocity
          dataInputStream.readByte(); // note block panning
          readShort(dataInputStream); // note block pitch
        }

        this.notes.add(new Note(key, getInstrumentByInt(instrument), BlockPos.ORIGIN, tick));
      }
    }
    dis.close();
  }

  public List<Note> getNotes() {
    return notes;
  }

  public String getInstrumentByInt(int inst) {
    switch (inst) {
      case 1:
        return "bassattack";
      case 2:
        return "bd";
      case 3:
        return "snare";
      case 4:
        return "hat";
      case 5:
        return "harp";
    }

    return "None";
  }
}
