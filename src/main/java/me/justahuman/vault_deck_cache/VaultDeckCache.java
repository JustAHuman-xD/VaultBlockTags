package me.justahuman.vault_deck_cache;

import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import iskallia.vault.gear.data.CardDeckGearData;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod("vault_deck_cache")
public class VaultDeckCache {
    public static final Map<UUID, Multimap<EntityAttribute, EntityAttributeModifier>> DECK_MODIFIER_CACHE = new ConcurrentHashMap<>();
    public static final Map<Integer, DataCache> DECK_DATA_CACHE = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    public VaultDeckCache() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public void onChangeDeck(CurioChangeEvent event) {
        if ("deck".equals(event.getIdentifier())) {
            DECK_MODIFIER_CACHE.remove(event.getEntity().getUuid());
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            long time = System.currentTimeMillis();
            DECK_DATA_CACHE.entrySet().removeIf(entry -> time - entry.getValue().accessed() > 60000);
        }
    }

    public static class DataCache {
        private final CardDeckGearData data;
        private long lastAccessed;

        public DataCache(CardDeckGearData data, long lastAccessed) {
            this.data = data;
            this.lastAccessed = lastAccessed;
        }

        public CardDeckGearData data() {
            return data;
        }

        public long accessed() {
            return lastAccessed;
        }

        public void access() {
            lastAccessed = System.currentTimeMillis();
        }
    }
}
