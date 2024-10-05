package me.justahuman.vault_deck_cache.mixin;

import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.CardDeckGearData;
import me.justahuman.vault_deck_cache.VaultDeckCache;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttributeGearData.class)
public class AttributeGearDataMixin {
    @Inject(at = @At("HEAD"), method = "lambda$read$0", cancellable = true, remap = false)
    private static void readWithBuf(ItemStack stack, BitBuffer buf, CallbackInfoReturnable<CardDeckGearData> cir) {
        VaultDeckCache.DataCache cache = VaultDeckCache.DECK_DATA_CACHE.get(stack.hashCode());
        if (cache != null) {
            cir.setReturnValue(cache.data());
        }
    }

    @Inject(at = @At("HEAD"), method = "lambda$read$1", cancellable = true, remap = false)
    private static void read(ItemStack stack, CallbackInfoReturnable<CardDeckGearData> cir) {
        VaultDeckCache.DataCache cache = VaultDeckCache.DECK_DATA_CACHE.get(stack.hashCode());
        if (cache != null) {
            cir.setReturnValue(cache.data());
        }
    }

    @Inject(at = @At("RETURN"), method = "lambda$read$0", remap = false)
    private static void readReturnWithBuffer(ItemStack stack, BitBuffer buf, CallbackInfoReturnable<CardDeckGearData> cir) {
        int hashCode = stack.hashCode();
        if (!VaultDeckCache.DECK_DATA_CACHE.containsKey(hashCode)) {
            VaultDeckCache.DataCache cache = new VaultDeckCache.DataCache(cir.getReturnValue());
            VaultDeckCache.DECK_DATA_CACHE.put(hashCode, cache);
        }
    }

    @Inject(at = @At("RETURN"), method = "lambda$read$1", remap = false)
    private static void readReturn(ItemStack stack, CallbackInfoReturnable<CardDeckGearData> cir) {
        int hashCode = stack.hashCode();
        if (!VaultDeckCache.DECK_DATA_CACHE.containsKey(hashCode)) {
            VaultDeckCache.DataCache cache = new VaultDeckCache.DataCache(cir.getReturnValue());
            VaultDeckCache.DECK_DATA_CACHE.put(hashCode, cache);
        }
    }
}
