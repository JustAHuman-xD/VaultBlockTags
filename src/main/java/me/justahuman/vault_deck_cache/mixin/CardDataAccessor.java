package me.justahuman.vault_deck_cache.mixin;

import iskallia.vault.core.card.CardDeck;
import iskallia.vault.gear.data.CardDeckGearData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CardDeckGearData.class)
public interface CardDataAccessor {
    @Accessor(remap = false)
    CardDeck getDeck();
}
