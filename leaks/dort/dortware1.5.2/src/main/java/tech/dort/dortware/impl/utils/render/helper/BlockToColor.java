package tech.dort.dortware.impl.utils.render.helper;

public class BlockToColor {

    public static float[] getBlockColor(final int id) {
        switch (id) {
            case 1:
            case 173:
            case 139:
            case 109:
            case 77:
            case 70:
            case 16: {
                return new float[]{0.3f, 0.3f, 0.3f};//stone				done
            }
            case 2:
            case 3: {
                return new float[]{0.59f, 0.29f, 0.00f};//grass			done
            }
            case 4: {
                return new float[]{0.30f, 0.30f, 0.30f};//cobblestone		done
            }
            case 5: {
                return new float[]{0.91f, 0.45f, 0.00f};//planks			done
            }
            case 6: {
                return new float[]{0.81f, 0.45f, 0.00f};//sapling			done
            }
            case 7: {
                return new float[]{0.00f, 0.00f, 0.00f};//bedrock			done
            }
            case 8: {
                return new float[]{0.00f, 0.00f, 0.90f};//water flowing	done
            }
            case 9: {
                return new float[]{0.00f, 0.00f, 0.90f};//water still		done
            }
            case 10: {
                return new float[]{1.0f, 0.00f, 0.00f};//lava				done
            }
            case 11: {
                return new float[]{1.0f, 0.00f, 0.00f};//lava				done
            }
            case 12: {
                return new float[]{0.4f, 0.4f, 0.00f};//sand				done
            }
            case 13: {
                return new float[]{0.2f, 0.18f, 0.15f};//gravel			done
            }
            case 14: {
                return new float[]{1.0f, 1.0f, 0.0f};//gold ore			done
            }
            case 15: {
                return new float[]{1.0f, 0.7f, 0.7f};//iron ore 		done
            }
            case 17: {
                return new float[]{0.65f, 0.33f, 0.00f};//wood block		done
            }
            case 18: {
                return new float[]{0.00f, 0.7f, 0.00f};//leaves			done
            }
            case 19: {
                return new float[]{0.86f, 0.86f, 0.07f};//sponge			done
            }
            case 20: {
                return new float[]{1f, 1f, 1f};//glass			done
            }
            case 21: {
                return new float[]{0.0f, 0.4f, 1.0f};//lapis ore			done
            }
            case 22: {
                return new float[]{0.0f, 0.4f, 1.0f};//lapis block 		done
            }
            case 23: {
                return new float[]{0.31f, 0.27f, 0.18f};//dispenser		done
            }
            case 24: {
                return new float[]{0.7f, 0.7f, 0.00f};//sandstone			done
            }
            case 25: {
                return new float[]{0.52f, 0.41f, 0.06f};//note block		done
            }
            case 26: {
                return new float[]{1.00f, 0.1f, 0.1f};//bed				done
            }
            case 27: {
                return new float[]{0.5f, 0.00f, 0.00f};//powered rail		done
            }
            case 28: {
                return new float[]{0.5f, 0.00f, 0.00f};//detector rail		done
            }
            case 29: {
                return new float[]{0.5f, 0.5f, 0.40f};//sticky pison		done
            }
            case 30: {
                return new float[]{1.0f, 1.0f, 1.00f};//cobweb				done
            }
            case 31: {
                return new float[]{0.2f, 0.7f, 0.2f};//grass/fern			done
            }
            case 32: {
                return new float[]{0.7f, 0.7f, 0.30f};//dead bush			done
            }
            case 33: {
                return new float[]{0.5f, 0.5f, 0.40f};//piston				done
            }
            case 34: {
                return new float[]{0.5f, 0.5f, 0.40f};//piston head		done
            }
            case 35: {
                return new float[]{1.0f, 1.0f, 1.00f};//wool				done
            }
            case 36: {
                return new float[]{0.9f, 0.9f, 0.00f};//there is no 36?
            }
            case 37: {
                return new float[]{0.9f, 0.9f, 0.00f};//yellow flower		done
            }
            case 38: {
                return new float[]{0.9f, 0.9f, 0.9f};//other flowers		done
            }
            case 39: {
                return new float[]{0.4f, 0.3f, 0.3f};//brown mushroom		done
            }
            case 40: {
                return new float[]{0.9f, 0.9f, 0.00f};//red mushroom		done
            }
            case 41: {
                return new float[]{1.0f, 1.0f, 0.0f}; //gold block		done
            }
            case 42: {
                return new float[]{1.0f, 0.5f, 0.5f}; //iron block		done
            }
            case 43: {
                return new float[]{0.7f, 0.7f, 0.7f};//slab				done
            }
            case 44: {
                return new float[]{0.7f, 0.7f, 0.00f};//sandstone slab		done
            }
            case 45: {
                return new float[]{0.7f, 0.0f, 0.00f};//bricks				done
            }
            case 46: {
                return new float[]{1.00f, 0.3f, 0.00f};//tnt				done
            }
            case 47: {
                return new float[]{0.65f, 0.33f, 0.00f};//bookshelf		done
            }
            case 48: {
                return new float[]{0.2f, 0.8f, 0.20f};//moss stone			done
            }
            case 49: {
                return new float[]{0.17f, 0.1f, 0.15f};//obsidian			done
            }
            case 50: {
                return new float[]{0.9f, 0.9f, 0.6f};//torch				done
            }
            case 51: {
                return new float[]{1.0f, 0.00f, 0.00f};//fire				done
            }
            case 52: {
                return new float[]{0.0f, 1.f, 0.00f};//spawner				done
            }
            case 53: {
                return new float[]{0.7f, 0.3f, 0.0f};//oak stairs		done
            }
            case 54: {
                return new float[]{1f, 1.f, 0.0f};// chest				done
            }
            case 55: {
                return new float[]{0.7f, 0.0f, 0.00f};//redstone dust		done
            }
            case 56: {
                return new float[]{0.0f, 1.0f, 1.0f};//diamond ore 		done
            }
            case 57: {
                return new float[]{0.0f, 0.7f, 0.7f}; //diamond block	done
            }
            case 58: {
                return new float[]{0.6f, 0.4f, 0.0f};//crafting table	done
            }
            case 59: {
                return new float[]{1.00f, 1.00f, 1.00f};//wheat crops		done
            }
            case 60: {
                return new float[]{0.59f, 0.29f, 0.00f};//farmland			done
            }
            case 61: {
                return new float[]{0.17f, 0.14f, 0.10f};//furnace			done
            }
            case 62: {
                return new float[]{0.17f, 0.14f, 0.10f};//furnace			done
            }
            case 63: {
                return new float[]{0.8f, 0.6f, 0.0f};//sign				done
            }
            case 64: {
                return new float[]{0.8f, 0.6f, 0.0f};//oak wood door		done
            }
            case 65: {
                return new float[]{0.8f, 0.6f, 0.0f};//ladder			done
            }
            case 66: {
                return new float[]{0.5f, 0.00f, 0.00f};//rail				done
            }
            case 67: {
                return new float[]{0.30f, 0.30f, 0.30f};//cobble stairs	done
            }
            case 68: {
                return new float[]{0.8f, 0.6f, 0.0f};//sign				done
            }
            case 69: {
                return new float[]{0.4f, 0.3f, 0.0f};//lever				done
            }
            case 71: {
                return new float[]{1.0f, 0.5f, 0.5f};//iron door			done
            }
            case 72: {
                return new float[]{0.91f, 0.45f, 0.00f};//wood plate		done
            }
            case 73: {
                return new float[]{1.0f, 0.3f, 0.2f};//redstone ore 	done
            }
            case 74: {
                return new float[]{1.0f, 0.3f, 0.2f};//redstone ore 	done
            }
            case 75: {
                return new float[]{0.7f, 0.0f, 0.00f};//redstone torch		done
            }
            case 76: {
                return new float[]{0.7f, 0.0f, 0.00f};//redstone torch		done
            }
            case 78: {
                return new float[]{1f, 1f, 1.00f};//snow layer				done
            }
            case 79: {
                return new float[]{0.0f, 0.0f, 1.00f};//ice				done
            }
            case 80: {
                return new float[]{1f, 1f, 1.00f};//snow block				done
            }
            case 81: {
                return new float[]{0.0f, 0.7f, 0.00f};//cactus				done
            }
            case 82: {
                return new float[]{0.34f, 0.19f, 0.06f};//clay				done
            }
            case 83: {
                return new float[]{0.0f, 1.0f, 0.00f};//sugar cane			done
            }
            case 84: {
                return new float[]{0.52f, 0.41f, 0.06f};//take his jukebox, fuck this kid.
            }
            case 85: {
                return new float[]{0.91f, 0.45f, 0.00f};//oak fence		done
            }
            case 86: {
                return new float[]{1.0f, 0.6f, 0.00f};//pumpkin			done
            }
            case 87: {
                return new float[]{0.9f, 0.1f, 0.00f};//netherrack			done
            }
            case 88: {
                return new float[]{0.21f, 0.15f, 0.10f};//soul sand		done
            }
            case 89: {
                return new float[]{0.0f, 1.00f, 0.00f};//glowstone			done
            }
            case 90: {
                return new float[]{0.20f, 0.05f, 0.20f};//nether portal	done
            }
            case 91: {
                return new float[]{1.0f, 0.6f, 0.00f};//jack o lantern		done
            }
            case 92: {
                return new float[]{1f, 1f, 1f};//cake						done
            }
            case 93: {
                return new float[]{0.70f, 0.17f, 0.06f};//repeater			done
            }
            case 94: {
                return new float[]{0.70f, 0.17f, 0.06f};//repeater			done
            }
            case 95: {
                return new float[]{1f, 1f, 1.00f};//stained glass			done
            }
            case 96: {
                return new float[]{0.91f, 0.45f, 0.00f};//wood trapdoor	done
            }
            case 97: {
                return new float[]{0.3f, 0.3f, 0.30f};//silverfish-stone   done
            }
            case 98: {
                return new float[]{0.3f, 0.3f, 0.30f};//stone bricks		done
            }
            case 99: {
                return new float[]{0.7f, 0.7f, 0.00f};//mushroom block		done
            }
            case 100: {
                return new float[]{0.7f, 0.7f, 0.00f};//mushroom block		done
            }
            case 101: {
                return new float[]{0.8f, 0.8f, 0.80f};//iron bars			done
            }
            case 102: {
                return new float[]{1f, 1f, 1.00f};//glass pane				done
            }
            case 103: {
                return new float[]{0.1f, 1.0f, 0.10f};//melon block		done
            }
            case 104: {
                return new float[]{0.2f, 0.2f, 0.00f};//pumpkin stem		done
            }
            case 105: {
                return new float[]{0.2f, 0.2f, 0.00f};//melon stem			done
            }
            case 106: {
                return new float[]{0f, 0f, 0.50f};//vines					done
            }
            case 107: {
                return new float[]{0.91f, 0.45f, 0.00f};//oak fence gate	done
            }
            case 108: {
                return new float[]{0.7f, 0.0f, 0.00f};//brick stairs		done
            }
            case 110: {
                return new float[]{0.4f, 0.4f, 0.00f};//Mycelium			done
            }
            case 111: {
                return new float[]{0f, 0f, 0.70f};//lily pad				done
            }
            case 112: {
                return new float[]{0.33f, 0.08f, 0.03f};//nether brick		done
            }
            case 113: {
                return new float[]{0.33f, 0.08f, 0.03f};//nether fence		done
            }
            case 114: {
                return new float[]{0.33f, 0.08f, 0.03f};//nether stairs	done
            }
            case 115: {
                return new float[]{0.8f, 0.0f, 0.00f};//nether wart		done
            }
            case 116: {
                return new float[]{0.0f, 0.0f, 0.20f};//enchant table		done
            }
            case 117: {
                return new float[]{0.8f, 0.8f, 0.00f};//brewing stand		done
            }
            case 119: {
                return new float[]{0.2f, 0.2f, 0.20f};//cauldron			done
            }
            case 120: {
                return new float[]{0.7f, 0.7f, 0.3f};//end portal frame	done
            }
            case 121: {
                return new float[]{0.7f, 0.7f, 0.0f};//end stone			done
            }
            case 122: {
                return new float[]{0f, 0f, 0f};//dragon egg				done
            }
            case 123: {
                return new float[]{0.7f, 0.1f, 0.1f};//redstone lamp		done
            }
            case 124: {
                return new float[]{0.7f, 0.1f, 0.1f};//redstone lamp		done
            }
            case 125: {
                return new float[]{0.91f, 0.45f, 0.00f};//wood slab		done
            }
            case 126: {
                return new float[]{0.91f, 0.45f, 0.00f};//wood slab		done
            }
            case 127: {
                return new float[]{0.5f, 0.5f, 0.f};//cocoa beans			done
            }
            case 128: {
                return new float[]{0.7f, 0.7f, 0.00f};//sand stairs		done
            }
            case 129: {
                return new float[]{0.2f, 1.0f, 0.2f};//emerald ore		done
            }
            case 130: {
                return new float[]{0.33f, 0.f, 0.33f};//ender chest		done
            }
            case 131: {
                return new float[]{0.91f, 0.45f, 0.00f};//tripwire hook	done
            }
            case 132: {
                return new float[]{1f, 1f, 1f};//string					done
            }
            case 133: {
                return new float[]{0.2f, 0.5f, 0.2f};//emerald block		done
            }
            case 134: {
                return new float[]{0.91f, 0.45f, 0.00f};//wood stairs		done
            }
            case 135: {
                return new float[]{0.91f, 0.45f, 0.00f};//wood stairs		done
            }
            case 136: {
                return new float[]{0.91f, 0.45f, 0.00f};//wood stairs		done
            }
            case 137: {
                return new float[]{1f, 1f, 1f};//command block				done
            }
            case 138: {
                return new float[]{0.6f, 0.6f, 1f};//beacon				done
            }
            case 140: {
                return new float[]{0.7f, 0.2f, 0.2f};//flower pot			done
            }
            case 141: {
                return new float[]{0.f, 1.f, 0.f};//carrots				done
            }
            case 142: {
                return new float[]{0.f, 1.f, 0.f};//potatos				done
            }
            case 143: {
                return new float[]{0.91f, 0.45f, 0.00f};//wood button		done
            }
            case 144: {
                return new float[]{0.5f, 0.5f, 0.5f};//skull				done
            }
            case 145: {
                return new float[]{0.1f, 0.1f, 0.1f};//anvil				done
            }
            case 146: {
                return new float[]{1f, 0.f, 0.0f};//trapped chest		done
            }
            case 147: {
                return new float[]{1f, 1f, 0.f};//gold plate				done
            }
            case 148: {
                return new float[]{0.7f, 0.7f, 0.7f};//iron plate			done
            }
            case 149: {
                return new float[]{0.4f, 0.4f, 0.4f};//comparator			done
            }
            case 150: {
                return new float[]{0.4f, 0.4f, 0.4f};//comparator			done
            }
            case 151: {
                return new float[]{0.5f, 0.1f, 0.4f};//daylight sensor		done
            }
            case 152: {
                return new float[]{1.0f, 0.3f, 0.2f};//redstone block	done
            }
            case 153: {
                return new float[]{1.0f, 0.5f, 0.5f};//quartz ore		done
            }
            case 154: {
                return new float[]{0.2f, 0.2f, 0.2f};//hopper				done
            }
            case 155: {
                return new float[]{1.f, 1.f, 1.f};//quartz block			done
            }
            case 156: {
                return new float[]{1.f, 1.f, 1.f};//quartz stairs			done
            }
            case 157: {
                return new float[]{05.f, 0.f, 0.f};//activator rail		done
            }
            case 158: {
                return new float[]{0.31f, 0.27f, 0.18f};//dropper			done
            }
            case 159: {
                return new float[]{1.f, 0.6f, 0.1f};//hardened clay		done
            }
            case 160: {
                return new float[]{1.f, 1.f, 1.f};//stained glass pane		done
            }
            case 161: {
                return new float[]{1.f, 0.7f, 0.2f};//acacia leaves		done
            }
            case 162: {
                return new float[]{1.f, 0.7f, 0.2f};//acacia wood			done
            }
            case 163: {
                return new float[]{1.f, 0.7f, 0.2f};//acacia stairs		done
            }
            case 164: {
                return new float[]{0.f, 0.7f, 0.f};//oak wood stairs		done
            }
            case 165: {
                return new float[]{0.f, 0.7f, 0.f};//slime block			done
            }
            case 166: {
                return new float[]{0.7f, 0.0f, 0.0f};//barrier				done
            }
            case 167: {
                return new float[]{0.9f, 0.9f, 0.9f};//iron trap door		done
            }
            case 168: {
                return new float[]{0.05f, 0.1f, 1.f};//prismarine			done
            }
            case 169: {
                return new float[]{0.7f, 0.7f, 1.f};//sea lantern			done
            }
            case 170: {
                return new float[]{0.8f, 0.8f, 0.f};//hay bale				done
            }
            case 171: {
                return new float[]{01.f, 01.f, 01.f};//carpet				done
            }
            case 172: {
                return new float[]{0.7f, 0.7f, 0.3f};//hard clay			done
            }
            case 174: {
                return new float[]{0.f, 0.f, 01.f};//packed ice			done
            }
            case 175: {
                return new float[]{01.f, 01.f, 01.f};//double plant		done
            }
            case 176: {
                return new float[]{01.f, 01.f, 01.f};//banner				done
            }
            case 177: {
                return new float[]{01.f, 01.f, 01.f};//banner on block		done
            }
            case 178: {
                return new float[]{01.f, 01.f, 01.f};//daylight sensor		done
            }
            case 179: {
                return new float[]{01.f, 0.6f, 0.f};//red sandstone		done
            }
            case 180: {
                return new float[]{1.f, 0.6f, 0.f};//red sandstone			done
            }
            case 181: {
                return new float[]{1.f, 0.6f, 0.f};//red sandstone			done
            }
            case 182: {
                return new float[]{1.f, 0.6f, 0.f};//red sandstone			done
            }
            case 183: {
                return new float[]{0.91f, 0.45f, 0.00f};//fence gate		done
            }
            case 184: {
                return new float[]{0.91f, 0.45f, 0.00f};//fence gate		done
            }
            case 186: {
                return new float[]{0.91f, 0.45f, 0.00f};//fence gate		done
            }
            case 187: {
                return new float[]{0.91f, 0.45f, 0.00f};//fence gate		done
            }
            case 188: {
                return new float[]{0.91f, 0.45f, 0.00f};//fence			done
            }
            case 189: {
                return new float[]{0.91f, 0.45f, 0.00f};//fence			done
            }
            case 190: {
                return new float[]{0.91f, 0.45f, 0.00f};//fence			done
            }
            case 191: {
                return new float[]{0.91f, 0.45f, 0.00f};//fence			done
            }
            case 192: {
                return new float[]{0.91f, 0.45f, 0.00f};//fence			done
            }
            default: {
                return new float[]{1.0f, 1.0f, 1.0f};
            }
        }
    }

}
