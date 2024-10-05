package me.justahuman.vault_deck_cache.mixin;

import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.CardDeckGearData;
import iskallia.vault.item.CardDeckItem;
import me.justahuman.vault_deck_cache.VaultDeckCache;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttributeGearData.class)
public class AttributeGearDataMixin {
    @Inject(at = @At("HEAD"), method = "read*", cancellable = true, remap = false)
    private static <T extends AttributeGearData> void read(ItemStack stack, CallbackInfoReturnable<T> cir) {
        if (stack.getItem() instanceof CardDeckItem) {
            VaultDeckCache.DataCache cache = VaultDeckCache.DECK_DATA_CACHE.get(stack.hashCode());
            if (cache != null) {
                cir.setReturnValue((T) cache.data());
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "read*", remap = false)
    private static <T extends AttributeGearData> void readReturn(ItemStack stack, CallbackInfoReturnable<T> cir) {
        if (stack.getItem() instanceof CardDeckItem) {
            int hashCode = stack.hashCode();
            if (!VaultDeckCache.DECK_DATA_CACHE.containsKey(hashCode)) {
                VaultDeckCache.DataCache cache = new VaultDeckCache.DataCache((CardDeckGearData) cir.getReturnValue());
                VaultDeckCache.DECK_DATA_CACHE.put(hashCode, cache);
            }
        }
    }
}
