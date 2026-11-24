package dev.cerba.healthindicator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class HealthIndicator implements ClientModInitializer {
    public static Config config;

    @Override
    public void onInitializeClient() {
        config = Config.load();
        KeyBinding toggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.healthindicator.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.healthindicator"));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (toggle.wasPressed()) {
                config.enabled = !config.enabled;
                config.save();
                if (mc.player != null) {
                    mc.player.sendMessage(Text.translatable(config.enabled ? "healthindicator.toggle.on" : "healthindicator.toggle.off"), true);
                }
            }
        });
    }
}
