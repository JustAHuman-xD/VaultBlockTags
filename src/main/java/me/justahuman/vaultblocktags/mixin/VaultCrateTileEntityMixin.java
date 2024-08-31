package me.justahuman.vaultblocktags.mixin;

import iskallia.vault.block.entity.VaultCrateTileEntity;
import iskallia.vault.container.oversized.OverSizedItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VaultCrateTileEntity.class)
public class VaultCrateTileEntityMixin {
    @Shadow(remap = false) @Final private ItemStackHandler itemHandler;
    @Shadow(remap = false) @Final private List<OverSizedItemStack> items;

    @Inject(at = @At("TAIL"), method = "load")
    public void load(CompoundTag pTag, CallbackInfo ci) {
        if (!pTag.contains("inv") && (pTag.contains("Items") || pTag.contains("items"))) {
            List<OverSizedItemStack> overSizedItemStacks = this.items;
            for (int i = 0; i < overSizedItemStacks.size(); i++) {
                OverSizedItemStack stack = overSizedItemStacks.get(i);
                itemHandler.setStackInSlot(i, stack.overSizedStack());
            }
        }
    }
}
