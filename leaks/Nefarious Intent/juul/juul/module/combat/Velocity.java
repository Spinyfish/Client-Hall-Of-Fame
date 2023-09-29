package juul.module.combat;

import juul.event.Event;
import juul.event.EventPacket;
import juul.module.Module;
import juul.module.settings.BooleanSetting;
import juul.module.settings.NumberSetting;
import juul.module.settings.RangeSetting;
import juul.util.value.Random;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {

    public BooleanSetting sprinting, clicking;

    public RangeSetting vert, horz;

    public NumberSetting chance;

    public Velocity() {
        super("Velocity", "reduces knockback", 0, Category.COMBAT);
        addSetting(vert = new RangeSetting("Vertical", "", 90, 100, 0, 125, 1));
        addSetting(horz = new RangeSetting("Horizontal", "", 90, 100, 0, 125, 1));
        addSetting(sprinting = new BooleanSetting("While Sprinting", "only activates while player is sprinting", false));
        addSetting(clicking = new BooleanSetting("While Clicking", "only activates while player is clicking", false));
        addSetting(chance = new NumberSetting("Chance", "velocs", 100, 1, 100, 1));
    }

    public void onEvent(Event e) {
        if (e instanceof EventPacket) {

            EventPacket ev = (EventPacket) e;

            double dbChance = Random.dbRandom(1, 100);

            if (ev.incoming() && chance.value <= dbChance) {

                double dbHorizontal = Random.dbRandom(horz.getStart(), horz.getEnd());
                double dbVertical = Random.dbRandom(vert.getStart(), vert.getEnd());
                ;

                if (ev.getPacket() instanceof S27PacketExplosion) {
                    S27PacketExplosion packetExplosion = (S27PacketExplosion) ev.getPacket();
					/*
					 posX        private double field_149158_a;
    				 posY	     private double field_149156_b;
    				 posZ		 private double field_149157_c;
    				 strength	 private float field_149154_d;
					 motionX     private float field_149152_f;
    				 motionY	 private float field_149153_g;
   					 motionZ	 private float field_149159_h;
					 */

                    packetExplosion.field_149152_f *= dbHorizontal / 100;
                    packetExplosion.field_149153_g *= dbVertical / 100;
                    packetExplosion.field_149159_h *= dbHorizontal / 100;
                } else if (ev.getPacket() instanceof S12PacketEntityVelocity) {

                    S12PacketEntityVelocity packetEntityVelocity = (S12PacketEntityVelocity) ev.getPacket();

					/*
					 public int entityID, motionX, motionY, motionZ;

					  public int field_149417_a;
    					public int field_149415_b;
    					public int field_149416_c;
    					public int field_149414_d;
					 */
					// if (packetEntityVelocity.field_149417_a == thePlayer.getEntityId()) {
					packetEntityVelocity.field_149415_b *= dbHorizontal / 100;
					packetEntityVelocity.field_149416_c *= dbVertical / 100;
					packetEntityVelocity.field_149414_d *= dbHorizontal / 100;
					// }
                }
            }
        }
    }
}
