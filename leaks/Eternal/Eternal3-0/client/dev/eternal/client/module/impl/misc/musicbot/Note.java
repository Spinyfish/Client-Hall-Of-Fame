package dev.eternal.client.module.impl.misc.musicbot;

/**
 * <h3>Project Eternal-V3</h3>
 * <p>
 * created on 12/20/22
 *
 * @author Wazed
 */

import com.google.common.base.Objects;
import net.minecraft.util.BlockPos;

public class Note {
  private byte intonation;
  private short tickPlayed;
  private BlockPos noteBlockPos;
  private String instrument;

  public Note(byte intonation, String instrument, BlockPos noteBlockPos, short tickPlayed) {
    this.intonation = intonation;
    this.instrument = instrument;
    this.noteBlockPos = noteBlockPos;
    this.tickPlayed = tickPlayed;
  }

  public BlockPos getNoteBlockPos() {
    return noteBlockPos;
  }

  public byte getIntonation() {
    return intonation;
  }

  public void setIntonation(byte intonation) {
    this.intonation = intonation;
  }

  public short getTickPlayed() {
    return tickPlayed;
  }

  public void setTickPlayed(short tickPlayed) {
    this.tickPlayed = tickPlayed;
  }

  public String getInstrument() {
    return instrument;
  }

  public void setInstrument(String instrument) {
    this.instrument = instrument;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Note note = (Note) o;
    return intonation == note.intonation
        && Objects.equal(noteBlockPos, note.noteBlockPos)
        && Objects.equal(instrument, note.instrument);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(intonation, noteBlockPos, instrument);
  }
}
