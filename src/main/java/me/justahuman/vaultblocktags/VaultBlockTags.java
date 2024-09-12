package me.justahuman.vaultblocktags;

import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod("vaultblocktags")
public class VaultBlockTags {
    public static final Map<UUID, Multimap<EntityAttribute, EntityAttributeModifier>> DECK_MODIFIER_CACHE = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    public VaultBlockTags() {
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
}
