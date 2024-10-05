package me.justahuman.vault_deck_cache.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import iskallia.vault.core.card.CardDeck;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.CardDeckGearData;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.item.CardDeckItem;
import me.justahuman.vault_deck_cache.VaultDeckCache;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Optional;
import java.util.UUID;

@Mixin(CardDeckItem.class)
public class CardDeckItemMixin {
    @Unique private static final Multimap<EntityAttribute, EntityAttributeModifier> vaultDeckCache$EMPTY = ImmutableMultimap.of();

    @Inject(at = @At("HEAD"), method = "getAttributeModifiers", cancellable = true, remap = false)
    public void getModifiers(SlotContext context, UUID uuid, ItemStack stack, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        if (context.entity() instanceof PlayerEntity player && player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            cir.setReturnValue(vaultDeckCache$EMPTY);
            return;
        }

        UUID entityUuid = context.entity().getUuid();
        var cache = VaultDeckCache.DECK_MODIFIER_CACHE.get(entityUuid);
        if (cache != null) {
            cir.setReturnValue(cache);
        } else if (AttributeGearData.read(stack) instanceof CardDeckGearData cardData) {
            cache = VaultGearHelper.getModifiers(cardData);
            VaultDeckCache.DECK_MODIFIER_CACHE.put(entityUuid, cache);
            cir.setReturnValue(cache);
        }
    }

    @Inject(at = @At("HEAD"), method = "getCardDeck", cancellable = true, remap = false)
    private static void getCardDeck(ItemStack stack, CallbackInfoReturnable<Optional<CardDeck>> cir) {
        int hashCode = stack.hashCode();
        VaultDeckCache.DataCache cache = VaultDeckCache.DECK_DATA_CACHE.get(hashCode);
        if (cache != null) {
            cir.setReturnValue(Optional.of(cache.deck()));
        }
    }

    @Inject(at = @At("RETURN"), method = "getCardDeck", remap = false)
    private static void getCardDeckReturn(ItemStack stack, CallbackInfoReturnable<Optional<CardDeck>> cir) {
        int hashCode = stack.hashCode();
        if (!VaultDeckCache.DECK_DATA_CACHE.containsKey(hashCode)) {
            cir.getReturnValue().ifPresent(cardDeck -> VaultDeckCache.DECK_DATA_CACHE.put(hashCode, new VaultDeckCache.DataCache(cardDeck)));
        }
    }

    @Inject(at = @At("HEAD"), method = "setCardDeck", remap = false)
    private static void setCardDeck(ItemStack stack, CardDeck card, CallbackInfoReturnable<CardDeck> cir) {
        VaultDeckCache.DECK_DATA_CACHE.remove(stack.hashCode());
    }
}
