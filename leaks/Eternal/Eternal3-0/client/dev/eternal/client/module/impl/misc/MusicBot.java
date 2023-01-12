package dev.eternal.client.module.impl.misc;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex3d;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventNotePlayed;
import dev.eternal.client.event.events.EventRender3D;
import dev.eternal.client.event.events.EventTick;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.Module.Category;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.impl.misc.musicbot.Instrument;
import dev.eternal.client.module.impl.misc.musicbot.Note;
import dev.eternal.client.module.impl.misc.musicbot.Song;
import dev.eternal.client.module.impl.misc.musicbot.Tuner;
import dev.eternal.client.property.impl.TextSetting;
import dev.eternal.client.util.player.RotationUtil;
import dev.eternal.client.util.time.Stopwatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNote;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

/**
 * <h3>Project Eternal-V3</h3>
 * <p>
 * created on 12/20/22
 *
 * @author Wazed
 */
@ModuleInfo(name = "Musicbot", description = "Plays music in the world", category = Category.MISC)
public class MusicBot extends Module {

  private final List<Instrument> instruments = new ArrayList<>();
  private final List<Note> notes = new ArrayList<>();
  private final Stopwatch timeUtil = new Stopwatch();
  private final Stopwatch songSpeed = new Stopwatch();
  private final TextSetting fileName = new TextSetting(this, "File Name", "");

  private Tuner tuner;
  private short tick;
  private int index;
  private Song song;
  private boolean indexingDone;
  private boolean tuningDone;
  private float speed;

  private Note currentPlayingNote;

  @Subscribe
  public void onRender3D(EventRender3D event) {
    this.instruments.forEach(
        instrument ->
            instrument
                .notes()
                .forEach(
                    note -> {
                      GlStateManager.disableTexture2D();
                      var color =
                          switch (instrument.instrumentName()) {
                            case "bass" -> new float[]{0.0F, 0.0F, 1.0F};
                            case "snare" -> new float[]{0.0F, 1.0F, 0.0F};
                            case "hat" -> new float[]{1.0F, 1.0F, 0.0F};
                            case "bassattack" -> new float[]{1.0F, 0.0F, 0.0F};
                            default -> new float[]{1.0F, 1.0F, 1.0F};
                          };
                      glTranslated(
                          -RenderManager.renderPosX,
                          -RenderManager.renderPosY,
                          -RenderManager.renderPosZ);

                      RenderGlobal.drawOutlinedBoundingBox(
                          note.getNoteBlockPos().getBounds().expand(0.01, 0.01, 0.01),
                          (int) (color[0] * 255),
                          (int) (color[1] * 255),
                          (int) (color[2] * 255),
                          100);
                      GlStateManager.enableTexture2D();
                      glTranslated(
                          RenderManager.renderPosX,
                          RenderManager.renderPosY,
                          RenderManager.renderPosZ);

                      glColor4f(1f, 1f, 1f, 1f);
                    }));

    if (this.currentPlayingNote != null) {
      // Draw a line from the player to the note
      var playerPos = mc.thePlayer.getPositionVector();
      var notePos = this.currentPlayingNote.getNoteBlockPos().getCenter();
      glColor4f(1f, 1f, 1f, 1f);
      GlStateManager.disableTexture2D();
      glLineWidth(2.0f);
      GlStateManager.disableDepth();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();

      glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

      GlStateManager.glBegin(GL_LINES);
      glVertex3d(
          playerPos.xCoord, playerPos.yCoord + mc.thePlayer.getEyeHeight(), playerPos.zCoord);
      glVertex3d(notePos.xCoord, notePos.yCoord, notePos.zCoord);
      GlStateManager.glEnd();

      glTranslated(RenderManager.renderPosX, RenderManager.renderPosY, RenderManager.renderPosZ);
      GlStateManager.enableBlend();
      GlStateManager.enableCull();
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      glLineWidth(1.0f);
      GlStateManager.enableTexture2D();
      glColor4f(1f, 1f, 1f, 1f);
    }
  }

  @Subscribe
  public void onTick(EventTick event) {
    if (song != null && tuningDone) {
      try {
        if (songSpeed.hasElapsed(75)) {
          songSpeed.reset();
          tick++;
          for (Note note : song.getNotes()) {
            if (note.getTickPlayed() == tick) {
              int noteAsInt = note.getIntonation() - 33;
              for (Instrument instrument : instruments) {
                if (note.getInstrument()
                    .equals(
                        instrument.instrumentName().equals("harp")
                            ? "None"
                            : instrument.instrumentName())) {

                  instrument.notes().stream()
                      .filter(n -> noteAsInt == n.getIntonation() - 1)
                      .findFirst()
                      .ifPresent(
                          note1 -> {
                            currentPlayingNote = note1;
                            // print note
                            System.out.println(
                                "Playing note: "
                                    + note.getInstrument()
                                    + " "
                                    + note1.getIntonation()
                                    + " at tick: "
                                    + tick);
                            playNote(
                                note1.getNoteBlockPos(),
                                (byte) (note1.getIntonation()),
                                note1.getInstrument());
                          });
                }
              }
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    final int xzRange = 6;
    final int yRange = 6;
    BlockPos playerPos = mc.thePlayer.getPosition();

    List<BlockPos> posList = new ArrayList<>();

    for (int yOffset = -yRange; yOffset < yRange; yOffset++) {
      for (int xOffset = -xzRange; xOffset <= yRange; xOffset++) {
        for (int zOffset = -xzRange; zOffset <= xzRange; zOffset++) {
          final BlockPos bP = playerPos.add(xOffset, yOffset, zOffset);
          final Block block = mc.theWorld.getBlockState(bP).getBlock();
          if (block instanceof BlockNote) {
            posList.add(bP);
          }
        }
      }
    }

    if (posList.size() < 24) {
      System.out.println("Invalid platform!");
      return;
    }

    if (!indexingDone) {
      if (timeUtil.hasElapsed(80)) {
        timeUtil.reset();
        final BlockPos blockPos = posList.get(index);
        if (mc.playerController.clickBlock(blockPos, EnumFacing.NORTH)) {
          index++;
        }
        if (index >= posList.size()) {
          this.tuner = new Tuner(notes, instruments);
          indexingDone = true;
          tuningDone = false;
        }
      }
    }

    if (!tuningDone && indexingDone) {
      this.tuningDone = tuner.tune();
    }
  }

  @Subscribe
  public void onNotePlayed(EventNotePlayed event) {
    if (indexingDone) return;
    Note note =
        new Note(
            (byte) event.getNote(),
            getInstrumentString(event.getInstrument()),
            event.getPos(),
            (short) 0);
    if (!notes.contains(note)) {
      notes.add(note);
    }
  }

  // Create a method that plays a note by clicking the respective noteblock
  private void playNote(BlockPos pos, byte note, String instrument) {
    mc.playerController.clickBlock(pos, EnumFacing.NORTH);
    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
  }

  @Override
  public void onEnable() {
    instruments.clear();
    notes.clear();
    indexingDone = false;
    tuningDone = false;
    index = 0;
    tick = 0;
    song = new Song(new File(String.format("%s\\%s.nbs", System.getProperty("user.dir"), fileName.value())));
    instruments.addAll(
        Arrays.asList(
            new Instrument("harp"),
            new Instrument("bd"),
            new Instrument("snare"),
            new Instrument("hat"),
            new Instrument("bassattack")));
  }

  @Override
  protected void onDisable() {
  }

  private Instrument getInstrument(int id) {
    return instruments.stream()
        .filter(instrument -> instrument.instrumentName().equals(getInstrumentString(id)))
        .findFirst()
        .orElse(null);
  }

  public String getInstrumentString(int inst) {
    return switch (inst) {
      default -> "harp";
      case 1 -> "bd";
      case 2 -> "snare";
      case 3 -> "hat";
      case 4 -> "bassattack";
    };
  }
}
