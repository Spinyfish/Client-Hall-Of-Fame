package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import net.minecraft.util.BlockPos;

/**
 * <h3>Project Eternal-V3</h3>
 * <p>
 * created on 12/20/22
 *
 * @author Wazed
 */
public class EventNotePlayed extends AbstractEvent {

  private final int note;
  private final BlockPos pos;
  private final int instrument;

  public EventNotePlayed(int note, BlockPos pos, int instrument) {
    this.note = note;
    this.pos = pos;
    this.instrument = instrument;
  }

  public int getNote() {
    return note;
  }

  public BlockPos getPos() {
    return pos;
  }

  public int getInstrument() {
    return instrument;
  }
}
