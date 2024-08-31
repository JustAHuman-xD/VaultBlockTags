package me.justahuman.vaultblocktags;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.function.Function;

@Mod("vaultblocktags")
public class VaultBlockTags {
    public static final Logger LOGGER = LogUtils.getLogger();

    public VaultBlockTags() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup(FMLClientSetupEvent event) {
        MinecraftForgeClient.registerTooltipComponentFactory(VaultCrateTooltips.CrateComponent.class, Function.identity());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void makeTooltip(RenderTooltipEvent.GatherComponents event) {
        VaultCrateTooltips.makeTooltip(event);
    }
}
