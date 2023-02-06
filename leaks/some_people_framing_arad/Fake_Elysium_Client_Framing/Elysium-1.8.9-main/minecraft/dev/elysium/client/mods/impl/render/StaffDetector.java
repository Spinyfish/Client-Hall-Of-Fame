package dev.elysium.client.mods.impl.render;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventUpdate;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffDetector extends Mod {
    public String staff_names = "0Aix,0Da3s,0DrRep,0hPqnos,0h_Roby,1A7mad1,1Ahmvd,1Brhom,1Daykel,1Derex,1DeVilz,1F5aMH___3oo,1HeyImHasson_,1KhaleeD,1Kw3zfTea_,1Kweng,1L7NN,1LaB,1Levaai,1Loga_,1LoST_,1M0ha,1M7mdz,1M7mmD,1Mhmmd,1Mshari,1Narwhql,1Omxr,1Pepe_,1RE3,1RealFadi,1Sinqx,1Sweet,1Tz3bo,1_aq,1_ST,3rodi,7MZH,7re2a_YT,7sO,8mhh,90fa,9we,A5oShnBaT,abd0_369,Aboal3z14,Aboz3bl,AbuA7md506,AFG_progamer92,AfootDiamond117,AhmedPROGG,Ahmmd,Alaam_FG,Aliiyah,arbawii,AsgardOfEddard,AssassinTime,AwKTaM,Aymann_,Ba1z,baderr,Banderr,BaSiL_123,Bastic,beyondviolets,BinDontCare,BlackOurs,Blood_Artz,Bo3aShor,Bo6lalll,bota_69,Boviix,comsterr,Creegam,Cryslinq,CutieRana,cuz_himo,d5qq,DaBabyFan,DangPavel,DarkA5_,deathally,Dedz1k,DeeRx,DestroyerOnyc_,DestroyerTrUnKs,Dizibre,Dqrkfall,Draggn_,Driction,Du7ym,ebararh,Eissaa,Ev2n,EVanDoskI,EyesO_Diamond,F2rris,FexoraNEP,Flineer,Fta7,Futurezii,G3rryx,GoldenGapples,H2ris,Haifa_magic,HM___,iA11,iAhmedGG,iDhoom,IDoubIe,iiEsaTKing_,iikimo,iiRaivy,iLuvSG_,ilybb0,Ily_W6n,iMehdi_,ImXann,INFAMOUSEEE,InjjectoR,inVertice,IR3DX,iRxv,iS3od_,iSolom,Its_HighNoon,Ittekimasu,itzZa1D,ixBander,IxDjole,IxKimo,i_Ym5,Jarxay,Jinaaan,Jrx7,Just7MO,JustDrink_,KaaReeeM,KingHOYT,kostasidk,Kuhimitsu,lacvna,lareey,leeleeeleeeleee,Lemfs,lovelywords,lt1x,Lunching,Luvaa,lwra,M4rwaan,M7mmd,Maarcii,manuelmaster,Mark_Gamer_YT_,MaybeHeDoes,Mdqz,Mhmovd,MightyM7MD,Millsap,MindOfNasser,Mjdra_call_ME,MK_F16,mohmad_q8,mokgii,Mondoros,Mythiques,mzh,Neeres,obaida123445,ogm,OldAlone,Oxenaa,PerfectRod_,phxnomenal,PT7,qB6o6,qlxc,qMabel,qPito,Raceth,RADVN,rcski,RealWayne,real__happy,redcriper,Refolt,Requieem,ritclaw,rqnkk,s2lm,S3rvox,Saajed,Sadlly,SalemBayern_,SamoXS,sh5boo6,Sp0tzy_,SpecialAdam_,SpecialAdel_,STEEEEEVEEEE,Tabby_Bhau,Tetdeus,TheDaddyJames,TheDrag_Xx,Thenvra,TheOnlyM7MAD,Tibbz_BGamer,Tibbz_BGamer_,ToFy_,Tostiebramkaas,ttkshr_,tverdy,uh8e,vBursT_,vdhvm,vinnythebot,vM6r,vxom,w7r,WalriderTime,Wesccar,wishingdeath,wl3d,wzii,xDiaa_levo,xDupzHell,xIBerryPlayz,xiDayzer,xImMuntadher_,xIMonster_Rj,xImTaiG_,xL2d,xLePerfect,xMz7,Y2men,Yaazzeed,yff3,yosife_7Y,yQuack,Y_04,zAhmd,ZANAD,zayedk,zCroDanger,Zqvies,_0bX,_b_i,_iSkyla,_JustMix,_N3,_Ottawa,_R3,_SpecialSA_,_Vxpe,_xayu_,_z2_";
    public List<String> staff_list = Arrays.asList(staff_names.split(","));

    public List<EntityLivingBase> staff = new ArrayList<>();
    public List<EntityLivingBase> not_staff = new ArrayList<>();

    public StaffDetector() {
        super("StaffDetector","Warns you if staff activity is in your presence", Category.RENDER);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        for(EntityLivingBase en : Elysium.getInstance().getTargets()) {
            if(not_staff.contains(en) || staff.contains(en)) continue;

            if(staff_list.contains(en.getName())) {
                staff.add(en);
                Elysium.getInstance().addChatMessageConfig("STAFF DETECTED : " + en.getName());
            } else
                not_staff.add(en);
        }
    }
}
