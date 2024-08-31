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
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onClientSetup);
        bus.addListener(this::makeTooltip);
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForgeClient.registerTooltipComponentFactory(VaultCrateTooltips.CrateComponent.class, Function.identity());
    }

    public void makeTooltip(RenderTooltipEvent.GatherComponents event) {
        VaultCrateTooltips.makeTooltip(event);
    }
}
