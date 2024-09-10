package me.justahuman.vaultblocktags.mixin;

import iskallia.vault.core.card.CardCondition;
import iskallia.vault.core.card.CardEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CardEntry.class)
public interface CardEntryAccessor {
    @Accessor(remap = false)
    CardCondition getCondition();
}
