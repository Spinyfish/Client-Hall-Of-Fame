package dev.eternal.client.module.impl.misc.musicbot;

import dev.eternal.client.util.player.RotationUtil;
import dev.eternal.client.util.time.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class Tuner {
  public final List<Instrument> tunedInstruments = new ArrayList<>();
  public final List<Instrument> needTune;
  private final Queue<Note> notes = new LinkedList<>();
  private final Minecraft MC = Minecraft.getMinecraft();
  private final HashMap<Integer, Integer> clickMap = new HashMap<>();
  private final Stopwatch timeUtil = new Stopwatch();
  private final Map<Instrument, Integer> setupMap = new HashMap<>();
  private Iterator<Instrument> iterator;
  private int setupIndex;
  private Note currentNote;
  private Instrument currentInstrument;

  public Tuner(List<Note> notes, List<Instrument> instruments) {
    this.notes.addAll(notes);
    this.needTune = instruments;
    this.needTune.forEach(
        (instrument) -> {
          instrument
              .toTune()
              .addAll(
                  notes.stream()
                      .filter(note -> note.getInstrument().equals(instrument.instrumentName()))
                      .toList());
        });
  }

  public boolean tune() {
    for (Instrument instrument : needTune) {
      if (tuneNotesFor(instrument)) {
        instrument.setTuned(true);
      }
    }

    if (needTune.stream().allMatch(Instrument::tuned)) {
      for (Instrument instrument : needTune) {
        MC.ingameGUI
            .getChatGUI()
            .printChatMessage(
                new ChatComponentText(
                    "[MUSIC BOT] Tuned "
                        + instrument.notes().size()
                        + " notes for "
                        + instrument.instrumentName()));
      }
      return true;
    }

    return false;
  }

  public boolean tuneNotesFor(Instrument instrument) {
    if (instrument.toTune().isEmpty()) return true;
    var currentNote = instrument.toTune().peek();
    var setupIdx = instrument.index();

    if (timeUtil.hasElapsed(25)
        && currentNote.getInstrument().equals(instrument.instrumentName())) {
      timeUtil.reset();
      instrument.clickMap().putIfAbsent(setupIdx, 0);
      var intonation = currentNote.getIntonation();
      var neededClicks = intonation > setupIdx ? 24 - intonation + setupIdx : setupIdx - intonation;
      var clicks = instrument.clickMap().get(setupIdx);
      if (clicks < neededClicks) {
        if (this.incrementNote(currentNote)) {
          instrument.clickMap().put(setupIdx, clicks + 1);
        }
      } else {
        instrument.toTune().remove();
        currentNote.setIntonation((byte) (setupIdx + 1));
        instrument.notes().add(currentNote);
        System.out.println("Adding note " + currentNote.getIntonation() + " with index " + setupIdx);
        instrument.index(setupIdx + 1);
      }
      return instrument.index() > 24;
    } else {
      return false;
    }
  }

  private boolean incrementNote(Note note) {
    return MC.playerController.onPlayerRightClick(
        MC.thePlayer,
        MC.theWorld,
        MC.thePlayer.getHeldItem(),
        note.getNoteBlockPos(),
        EnumFacing.UP,
        note.getNoteBlockPos().getCenter());
  }
}
