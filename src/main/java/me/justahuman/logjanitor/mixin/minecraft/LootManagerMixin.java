package me.justahuman.logjanitor.mixin.minecraft;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTableReporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LootManager.class)
public class LootManagerMixin {
    @Unique
    private static final Multimap<String, String> DUMMY_MAP = ImmutableMultimap.of();

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTableReporter;getMessages()Lcom/google/common/collect/Multimap;"), method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V")
    public Multimap<String, String> getMessages(LootTableReporter instance) {
        return DUMMY_MAP;
    }
}
