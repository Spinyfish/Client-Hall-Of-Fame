package net.minecraft.item;

import java.util.*;
import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import com.google.common.collect.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.*;

public class ItemTool extends Item
{
    private Set<Block> effectiveBlocks;
    protected float efficiencyOnProperMaterial;
    private float damageVsEntity;
    protected ToolMaterial toolMaterial;
    
    protected ItemTool(final float attackDamage, final ToolMaterial material, final Set<Block> effectiveBlocks) {
        this.efficiencyOnProperMaterial = 4.0f;
        this.toolMaterial = material;
        this.effectiveBlocks = effectiveBlocks;
        this.maxStackSize = 1;
        this.setMaxDamage(material.getMaxUses());
        this.efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        this.damageVsEntity = attackDamage + material.getDamageVsEntity();
        this.setCreativeTab(CreativeTabs.tabTools);
    }
    
    @Override
    public float getStrVsBlock(final ItemStack stack, final Block block) {
        return this.effectiveBlocks.contains(block) ? this.efficiencyOnProperMaterial : 1.0f;
    }
    
    @Override
    public boolean hitEntity(final ItemStack stack, final EntityLivingBase target, final EntityLivingBase attacker) {
        stack.damageItem(2, attacker);
        return true;
    }
    
    @Override
    public boolean onBlockDestroyed(final ItemStack stack, final World worldIn, final Block blockIn, final BlockPos pos, final EntityLivingBase playerIn) {
        if (blockIn.getBlockHardness(worldIn, pos) != 0.0) {
            stack.damageItem(1, playerIn);
        }
        return true;
    }
    
    @Override
    public boolean isFull3D() {
        return true;
    }
    
    public ToolMaterial getToolMaterial() {
        return this.toolMaterial;
    }
    
    @Override
    public int getItemEnchantability() {
        return this.toolMaterial.getEnchantability();
    }
    
    public String getToolMaterialName() {
        return this.toolMaterial.toString();
    }
    
    @Override
    public boolean getIsRepairable(final ItemStack toRepair, final ItemStack repair) {
        return this.toolMaterial.getRepairItem() == repair.getItem() || super.getIsRepairable(toRepair, repair);
    }
    
    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
        final Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers();
        multimap.put((Object)SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), (Object)new AttributeModifier(ItemTool.itemModifierUUID, "Tool modifier", this.damageVsEntity, 0));
        return multimap;
    }
}
