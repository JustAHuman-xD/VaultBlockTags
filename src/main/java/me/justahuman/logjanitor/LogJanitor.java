package me.justahuman.logjanitor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.logging.LogUtils;
import me.justahuman.logjanitor.mixin.MixinConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

@Mod("logjanitor")
public class LogJanitor {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonObject config = new JsonObject();

    public static final Logger LOGGER = LogUtils.getLogger();

    public LogJanitor() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    public void setup(FMLClientSetupEvent event) {
        LOGGER.info("Log Janitor Loaded :D");
        if (config.keySet().isEmpty()) {
            saveDefaultConfig();
        }
    }

    public static void loadConfig() {
        File configFile = new File("config/logjanitor.json");
        if (!configFile.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            if (JsonParser.parseReader(reader) instanceof JsonObject parsedConfig) {
                parsedConfig.entrySet().forEach(entry -> config.add(entry.getKey(), entry.getValue()));
            }
        } catch (Exception e) {
            LOGGER.error("Something went wrong loading the config!", e);
            LOGGER.error("Please report this to JustAHuman!");
        }
    }

    public static boolean isMixinEnabled(String type, String mixin) {
        return !(config.get(type) instanceof JsonObject configSection)
                || !(configSection.get(mixin) instanceof JsonPrimitive configValue)
                || !configValue.isBoolean() || configValue.getAsBoolean();
    }

    public static void saveDefaultConfig() {
        Map<String, List<String>> sortedMixins = MixinConfig.getSortedMixins();
        for (String type : sortedMixins.keySet()) {
            JsonObject configSection = new JsonObject();
            for (String mixin : sortedMixins.get(type)) {
                configSection.addProperty(mixin, true);
            }
            config.add(type, configSection);
        }

        try(FileWriter writer = new FileWriter("config/logjanitor.json")) {
            GSON.toJson(config, writer);
            writer.flush();
        } catch (Exception e) {
            LOGGER.error("Something went wrong saving the default config!", e);
            LOGGER.error("Please report this to JustAHuman!");
        }
    }
}
