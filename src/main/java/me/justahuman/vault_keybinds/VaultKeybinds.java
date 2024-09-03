package me.justahuman.vault_keybinds;

import com.mojang.logging.LogUtils;
import net.minecraft.client.option.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod("vault_keybinds")
public class VaultKeybinds {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final KeyBinding REROLL_BOUNTY_KEYBIND = new KeyBinding("vault_keybinds.reroll_bounty_keybind", GLFW.GLFW_KEY_R, KeyBinding.INVENTORY_CATEGORY);

    public VaultKeybinds() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup(FMLClientSetupEvent event) {

    }
}
