package me.justahuman.vaultblocktags.mixin;

import iskallia.vault.block.VaultCrateBlock;
import me.justahuman.vaultblocktags.VaultBlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vazkii.quark.content.client.tooltip.ShulkerBoxTooltips;

@Mixin(ShulkerBoxTooltips.ShulkerComponent.class)
public class ShulkerBoxTooltipMixin {

    @Shadow(remap = false) @Final private ItemStack stack;

    @ModifyConstant(method = "renderImage", constant = @Constant(stringValue = "minecraft:shulker_box"))
    public String renderImage(String constant) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof VaultCrateBlock) {
            ResourceLocation registryName = stack.getItem().getRegistryName();
            if (registryName != null) {
                return registryName.toString();
            } else {
                VaultBlockTags.LOGGER.info("null for some reason?");
            }
        }
        return constant;
    }
}
