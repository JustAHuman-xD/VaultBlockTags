package me.justahuman.vaultblocktags;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.function.Function;

@Mod("vaultblocktags")
public class VaultBlockTags {
    public static final Logger LOGGER = LogUtils.getLogger();

    public VaultBlockTags() {
        // this doesn't go here but wip
        MinecraftForgeClient.registerTooltipComponentFactory(VaultCrateTooltips.CrateComponent.class, Function.identity());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void makeTooltip(RenderTooltipEvent.GatherComponents event) {
        VaultCrateTooltips.makeTooltip(event);
    }
}
