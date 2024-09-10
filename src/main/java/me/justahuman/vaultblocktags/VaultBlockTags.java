package me.justahuman.vaultblocktags;

import com.mojang.logging.LogUtils;
import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.generator.theme.ClassicVaultTheme;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.processor.tile.BernoulliWeightedTileProcessor;
import iskallia.vault.core.world.processor.tile.LeveledTileProcessor;
import iskallia.vault.core.world.processor.tile.ReferenceTileProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.processor.tile.VaultLootTileProcessor;
import iskallia.vault.core.world.processor.tile.WeightedTileProcessor;
import iskallia.vault.core.world.template.data.TemplateEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mod("vaultblocktags")
public class VaultBlockTags {
    private static final Logger LOGGER = LogUtils.getLogger();

    public VaultBlockTags() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    public void setup(FMLClientSetupEvent event) {
        VaultRegistry.THEME.getKeys().forEach(themeKey -> {
            if (!(themeKey.get(Version.latest()) instanceof ClassicVaultTheme theme)) {
                return;
            }

            String id = getThemeId(themeKey);
            Set<Block> blocks = new HashSet<>();
            TagKey<Block> themeTagKey = BlockTags.create(VaultMod.id(id));
            for (Object object : theme.getRooms().get(Version.latest()).getChildren().keySet()) {
                if (!(object instanceof TemplateEntry entry)) {
                    continue;
                }

                for (PaletteKey paletteKey : entry.getPalettes()) {
                    handlePaletteKey(id, blocks, paletteKey);
                }
            }
            
            HolderSet.Named<Block> tag = Registry.BLOCK.getOrCreateTag(themeTagKey);
            List<Holder<Block>> holders = blocks.stream().map(Registry.BLOCK::getResourceKey).filter(Optional::isPresent).map(Optional::get).map(Registry.BLOCK::getOrCreateHolder).toList();
            tag.bind(holders);
        });
    }

    public static void handlePaletteKey(String id, Set<Block> blocks, PaletteKey key) {
        Palette palette = key.get(Version.latest());
        if (palette != null) {
            handleTileProcessors(id, blocks, palette.getTileProcessors());
        }
    }

    public static void handleTileProcessors(String id, Set<Block> blocks, Iterable<TileProcessor> processors) {
        for (TileProcessor tileProcessor : processors) {
            if (tileProcessor instanceof WeightedTileProcessor weighted) {
                for (PartialTile tile : weighted.getOutput().keySet()) {
                    tile.getState().getBlock().asWhole().ifPresent(blocks::add);
                }
            } else if (tileProcessor instanceof BernoulliWeightedTileProcessor bernoulli) {
                for (PartialTile tile : bernoulli.success.keySet()) {
                    tile.getState().getBlock().asWhole().ifPresent(blocks::add);
                }
                for (PartialTile tile : bernoulli.failure.keySet()) {
                    tile.getState().getBlock().asWhole().ifPresent(blocks::add);
                }
            } else if (tileProcessor instanceof ReferenceTileProcessor reference) {
                PaletteKey referenceKey = VaultRegistry.PALETTE.getKey(reference.getId());
                if (referenceKey != null) {
                    handlePaletteKey(id, blocks, referenceKey);
                }
            } else if (tileProcessor instanceof VaultLootTileProcessor loot) {
                handleTileProcessors(id, blocks, loot.levels.values());
            } else if (tileProcessor instanceof LeveledTileProcessor leveled) {
                handleTileProcessors(id, blocks, leveled.levels.values());
            }
        }
    }

    public static String getThemeId(ThemeKey themeKey) {
        int start = 0;
        int end = 0;
        String path = themeKey.getId().getPath();
        if (path.contains("/")) {
            start = path.lastIndexOf('/' + 1);
        }

        if (path.contains(".")) {
            end = path.lastIndexOf(".");
        }

        return path.substring(start, end);
    }
}
