package net.minecraft.client.resources;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.texture.*;
import java.io.*;
import net.minecraft.client.resources.data.*;
import com.google.gson.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;

public class ResourcePackListEntryDefault extends ResourcePackListEntry
{
    private static final Logger logger;
    private final IResourcePack field_148320_d;
    private final ResourceLocation resourcePackIcon;
    
    public ResourcePackListEntryDefault(final GuiScreenResourcePacks resourcePacksGUIIn) {
        super(resourcePacksGUIIn);
        this.field_148320_d = this.mc.getResourcePackRepository().rprDefaultResourcePack;
        DynamicTexture dynamictexture;
        try {
            dynamictexture = new DynamicTexture(this.field_148320_d.getPackImage());
        }
        catch (IOException var4) {
            dynamictexture = TextureUtil.missingTexture;
        }
        this.resourcePackIcon = this.mc.getTextureManager().getDynamicTextureLocation("texturepackicon", dynamictexture);
    }
    
    @Override
    protected int func_183019_a() {
        return 1;
    }
    
    @Override
    protected String func_148311_a() {
        try {
            final PackMetadataSection packmetadatasection = this.field_148320_d.getPackMetadata(this.mc.getResourcePackRepository().rprMetadataSerializer, "pack");
            if (packmetadatasection != null) {
                return packmetadatasection.getPackDescription().getFormattedText();
            }
        }
        catch (JsonParseException jsonparseexception) {
            ResourcePackListEntryDefault.logger.error("Couldn't load metadata info", (Throwable)jsonparseexception);
        }
        catch (IOException ioexception) {
            ResourcePackListEntryDefault.logger.error("Couldn't load metadata info", (Throwable)ioexception);
        }
        return EnumChatFormatting.RED + "Missing pack.mcmeta :(";
    }
    
    @Override
    protected boolean func_148309_e() {
        return false;
    }
    
    @Override
    protected boolean func_148308_f() {
        return false;
    }
    
    @Override
    protected boolean func_148314_g() {
        return false;
    }
    
    @Override
    protected boolean func_148307_h() {
        return false;
    }
    
    @Override
    protected String func_148312_b() {
        return "Default";
    }
    
    @Override
    protected void func_148313_c() {
        this.mc.getTextureManager().bindTexture(this.resourcePackIcon);
    }
    
    @Override
    protected boolean func_148310_d() {
        return false;
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
