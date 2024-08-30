package me.justahuman.vaultblocktags.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.justahuman.vaultblocktags.VaultBlockTags;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.content.client.tooltip.ShulkerBoxTooltips;

@Mixin(ShulkerBoxTooltips.ShulkerComponent.class)
public class ShulkerBoxTooltipMixin {
    @Shadow @Final private ItemStack stack;

    @Shadow @Final private static int[][] TARGET_RATIOS;

    @Inject(method = "lambda$renderImage$0", at = @At("HEAD"))
    public void renderImage(int tooltipX, int tooltipY, Minecraft mc, PoseStack pose, IItemHandler capability, CallbackInfo ci) {
        int size = capability.getSlots();
        int[] dims = new int[]{Math.min(size, 9), Math.max(size / 9, 1)};
        VaultBlockTags.LOGGER.info(" ");
        VaultBlockTags.LOGGER.info("bingo slots size: {}", size);
        VaultBlockTags.LOGGER.info("bingo slots dims: {}", dims);
        VaultBlockTags.LOGGER.info(" ");
        for (int[] ratio : TARGET_RATIOS) {
            VaultBlockTags.LOGGER.info("test ratio: {}", ratio);
            if (ratio[0] * ratio[1] == size) {
                VaultBlockTags.LOGGER.info("overriding dims with test");
                VaultBlockTags.LOGGER.info(" ");
                break;
            }
            VaultBlockTags.LOGGER.info(" ");
        }
    }
}
