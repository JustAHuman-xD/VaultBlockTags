package me.justahuman.vault_deck_cache;

import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import iskallia.vault.core.card.CardDeck;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod("vault_deck_cache")
public class VaultDeckCache {
    public static final Map<Integer, ExpiringCache<Multimap<EntityAttribute, EntityAttributeModifier>>> DECK_MODIFIER_CACHE = new ConcurrentHashMap<>();
    public static final Map<Integer, ExpiringCache<CardDeck>> DECK_CACHE = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    public VaultDeckCache() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            DECK_MODIFIER_CACHE.entrySet().removeIf(entry -> entry.getValue().expired());
            DECK_CACHE.entrySet().removeIf(entry -> entry.getValue().expired());
        }
    }
}
