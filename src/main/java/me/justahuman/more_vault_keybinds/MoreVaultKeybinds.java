package me.justahuman.more_vault_keybinds;

import com.mojang.logging.LogUtils;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod("more_vault_keybinds")
public class MoreVaultKeybinds {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String VAULT_KEYBINDS_CATEGORY = "key.categories.more_vault_keybinds";
    public static final KeyBinding REROLL_BOUNTY_KEYBIND = new KeyBinding("more_vault_keybinds.reroll_bounty_keybind", KeyConflictContext.GUI, InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_R), VAULT_KEYBINDS_CATEGORY);

    public MoreVaultKeybinds() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(REROLL_BOUNTY_KEYBIND);
    }
}
