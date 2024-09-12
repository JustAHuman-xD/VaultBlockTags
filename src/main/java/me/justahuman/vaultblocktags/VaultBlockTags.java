package me.justahuman.vaultblocktags;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
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
import iskallia.vault.core.world.template.data.IndirectTemplateEntry;
import iskallia.vault.core.world.template.data.TemplateEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mod("vaultblocktags")
public class VaultBlockTags {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public VaultBlockTags() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    public void setup(FMLClientSetupEvent event) {
        loadTags();
    }

    public static void loadTags() {
        File configFile = new File("config/vaultblocktags.json");
        if (!configFile.exists()) {
            LOGGER.warn("Tags file not found, generating tags");
            generateTags();
            return;
        }

        // Load tags from file
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            JsonObject config = GSON.fromJson(reader, JsonObject.class);
            String version = config.getAsJsonPrimitive("version").getAsString();
            if (!version.equals(Version.latest().getName())) {
                LOGGER.warn("Version mismatch, regenerating tags");
                generateTags();
                return;
            }

            JsonObject tags = config.getAsJsonObject("tags");
            for (String key : tags.keySet()) {
                if (!key.equals("all_themes") && VaultRegistry.THEME.getKey("the_vault:" + key) == null) {
                    LOGGER.warn("Unknown theme key: {}", key);
                    continue;
                }

                TagKey<Item> tagKey = ItemTags.create(VaultMod.id(key));
                HolderSet.Named<Item> tag = Registry.ITEM.getOrCreateTag(tagKey);
                List<Holder<Item>> holders = new ArrayList<>();
                for (JsonElement element : tags.getAsJsonArray(key)) {
                    Registry.ITEM.getOptional(new ResourceLocation(element.getAsString()))
                            .map(Registry.ITEM::getResourceKey).filter(Optional::isPresent)
                            .map(Optional::get).map(Registry.ITEM::getOrCreateHolder).ifPresent(holders::add);
                }
                tag.bind(holders);
            }

            LOGGER.info("Loaded tags from file");
        } catch (Exception e) {
            LOGGER.error("Failed to load tags from file", e);
            generateTags();
        }
    }

    public static void generateTags() {
        JsonObject config = new JsonObject();
        JsonObject tags = new JsonObject();
        config.addProperty("version", Version.latest().getName());

        List<Holder<Item>> allHolders = new ArrayList<>();
        for (ThemeKey themeKey : VaultRegistry.THEME.getKeys()) {
            if (!(themeKey.get(Version.latest()) instanceof ClassicVaultTheme theme)) {
                continue;
            }

            String id = getThemeId(themeKey);
            Set<Item> items = new HashSet<>();
            TagKey<Item> themeTagKey = ItemTags.create(VaultMod.id(id));
            handleTemplatePool(id, items, theme.getStarts());
            handleTemplatePool(id, items, theme.getRooms());
            handleTemplatePool(id, items, theme.getTunnels());

            JsonArray serialized = new JsonArray();
            for (Item item : items) {
                ResourceLocation registryName = item.getRegistryName();
                if (registryName != null) {
                    serialized.add(registryName.toString());
                }
            }
            tags.add(id, serialized);

            HolderSet.Named<Item> tag = Registry.ITEM.getOrCreateTag(themeTagKey);
            List<Holder<Item>> holders = items.stream().map(Registry.ITEM::getResourceKey).filter(Optional::isPresent).map(Optional::get).map(Registry.ITEM::getOrCreateHolder).toList();
            tag.bind(holders);
            allHolders.addAll(holders);
        }

        JsonArray serialized = new JsonArray();
        for (Item item : allHolders.stream().map(Holder::value).collect(Collectors.toSet())) {
            ResourceLocation registryName = item.getRegistryName();
            if (registryName != null) {
                serialized.add(registryName.toString());
            }
        }
        tags.add("all_themes", serialized);
        config.add("tags", tags);

        TagKey<Item> allThemesKey = ItemTags.create(VaultMod.id("all_themes"));
        HolderSet.Named<Item> allThemesTag = Registry.ITEM.getOrCreateTag(allThemesKey);
        allThemesTag.bind(List.copyOf(allHolders));

        File configFile = new File("config/vaultblocktags.json");
        try (final FileWriter fileWriter = new FileWriter(configFile)) {
            GSON.toJson(config, fileWriter);
            fileWriter.flush();
            LOGGER.info("Saved tags to file");
        } catch (IOException e) {
            LOGGER.error("Failed to save tags to file", e);
        }
    }

    public static void handleTemplatePool(String id, Set<Item> items, TemplatePoolKey key) {
        for (Object object : key.get(Version.latest()).getChildren().keySet()) {
            if (!(object instanceof TemplateEntry entry)) {
                continue;
            }

            if (entry instanceof IndirectTemplateEntry indirect) {
                handleTemplatePool(id, items, indirect.getReference());
            }

            for (PaletteKey paletteKey : entry.getPalettes()) {
                handlePaletteKey(id, items, paletteKey);
            }
        }
    }

    public static void handlePaletteKey(String id, Set<Item> items, PaletteKey key) {
        Palette palette = key.get(Version.latest());
        if (palette != null) {
            handleTileProcessors(id, items, palette.getTileProcessors());
        }
    }

    public static void handleTileProcessors(String id, Set<Item> items, Iterable<TileProcessor> processors) {
        for (TileProcessor tileProcessor : processors) {
            if (tileProcessor instanceof WeightedTileProcessor weighted) {
                handlePartialTiles(id, items, weighted.getOutput().keySet());
            } else if (tileProcessor instanceof BernoulliWeightedTileProcessor bernoulli) {
                handlePartialTiles(id, items, bernoulli.success.keySet());
                handlePartialTiles(id, items, bernoulli.failure.keySet());
            } else if (tileProcessor instanceof ReferenceTileProcessor reference) {
                PaletteKey referenceKey = VaultRegistry.PALETTE.getKey(reference.getId());
                if (referenceKey != null) {
                    handlePaletteKey(id, items, referenceKey);
                }
            } else if (tileProcessor instanceof VaultLootTileProcessor loot) {
                handleTileProcessors(id, items, loot.levels.values());
            } else if (tileProcessor instanceof LeveledTileProcessor leveled) {
                handleTileProcessors(id, items, leveled.levels.values());
            }
        }
    }

    public static void handlePartialTiles(String id, Set<Item> items, Iterable<PartialTile> tiles) {
        for (PartialTile tile : tiles) {
            tile.getState().getBlock().asWhole().map(Block::asItem).ifPresent(items::add);
        }
    }

    public static String getThemeId(ThemeKey themeKey) {
        String path = themeKey.getId().getPath();
        int start = 0;
        int end = path.length();

        if (path.contains("/")) {
            start = path.lastIndexOf('/' + 1);
        }

        if (path.contains(".")) {
            end = path.lastIndexOf(".");
        }

        return path.substring(start, end);
    }
}
