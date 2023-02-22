package net.minecraft.client.gui.spectator.categories;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Random;

public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
    private final List<ISpectatorMenuObject> field_178672_a = Lists.newArrayList();

    public TeleportToTeam() {
        final Minecraft minecraft = Minecraft.getMinecraft();

        for (final ScorePlayerTeam scoreplayerteam : minecraft.theWorld.getScoreboard().getTeams()) {
            this.field_178672_a.add(new TeleportToTeam.TeamSelectionObject(scoreplayerteam));
        }
    }

    public List<ISpectatorMenuObject> func_178669_a() {
        return this.field_178672_a;
    }

    public IChatComponent func_178670_b() {
        return new ChatComponentText("Select a team to teleport to");
    }

    public void func_178661_a(final SpectatorMenu menu) {
        menu.func_178647_a(this);
    }

    public IChatComponent getSpectatorName() {
        return new ChatComponentText("Teleport to team member");
    }

    public void func_178663_a(final float p_178663_1_, final int alpha) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.field_175269_a);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
    }

    public boolean func_178662_A_() {
        for (final ISpectatorMenuObject ispectatormenuobject : this.field_178672_a) {
            if (ispectatormenuobject.func_178662_A_()) {
                return true;
            }
        }

        return false;
    }

    class TeamSelectionObject implements ISpectatorMenuObject {
        private final ScorePlayerTeam field_178676_b;
        private final ResourceLocation field_178677_c;
        private final List<NetworkPlayerInfo> field_178675_d;

        public TeamSelectionObject(final ScorePlayerTeam p_i45492_2_) {
            this.field_178676_b = p_i45492_2_;
            this.field_178675_d = Lists.newArrayList();

            for (final String s : p_i45492_2_.getMembershipCollection()) {
                final NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(s);

                if (networkplayerinfo != null) {
                    this.field_178675_d.add(networkplayerinfo);
                }
            }

            if (!this.field_178675_d.isEmpty()) {
                final String s1 = this.field_178675_d.get((new Random()).nextInt(this.field_178675_d.size())).getGameProfile().getName();
                this.field_178677_c = AbstractClientPlayer.getLocationSkin(s1);
                AbstractClientPlayer.getDownloadImageSkin(this.field_178677_c, s1);
            } else {
                this.field_178677_c = DefaultPlayerSkin.getDefaultSkinLegacy();
            }
        }

        public void func_178661_a(final SpectatorMenu menu) {
            menu.func_178647_a(new TeleportToPlayer(this.field_178675_d));
        }

        public IChatComponent getSpectatorName() {
            return new ChatComponentText(this.field_178676_b.getTeamName());
        }

        public void func_178663_a(final float p_178663_1_, final int alpha) {
            int i = -1;
            final String s = FontRenderer.getFormatFromString(this.field_178676_b.getColorPrefix());

            if (s.length() >= 2) {
                i = Minecraft.getMinecraft().fontRendererObj.getColorCode(s.charAt(1));
            }

            if (i >= 0) {
                final float f = (float) (i >> 16 & 255) / 255.0F;
                final float f1 = (float) (i >> 8 & 255) / 255.0F;
                final float f2 = (float) (i & 255) / 255.0F;
                Gui.drawRect(1, 1, 15, 15, MathHelper.func_180183_b(f * p_178663_1_, f1 * p_178663_1_, f2 * p_178663_1_) | alpha << 24);
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(this.field_178677_c);
            GlStateManager.color(p_178663_1_, p_178663_1_, p_178663_1_, (float) alpha / 255.0F);
            Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
        }

        public boolean func_178662_A_() {
            return !this.field_178675_d.isEmpty();
        }
    }
}
