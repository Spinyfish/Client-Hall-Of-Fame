package dev.eternal.client.module.impl.misc.musicbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

/**
 * <h3>Project Eternal-V3</h3>
 * created on 12/20/22
 *
 * @author Wazed
 */

@Getter
@Setter
public class Instrument {

  private final HashMap<Integer, Integer> clickMap = new HashMap<>();
  private final Queue<Note> toTune = new LinkedList<>();
  private final List<Note> notes = new ArrayList<>();
  private final String instrumentName;
  private boolean tuned;
  public int index;
  public int clicks;

  public Instrument(String instrumentName) {
    this.instrumentName = instrumentName;
    this.index = 0;
    this.clicks = 0;
    this.tuned = false;
  }


  public void setTuned(boolean tuned) {
    this.tuned = tuned;
  }
}
