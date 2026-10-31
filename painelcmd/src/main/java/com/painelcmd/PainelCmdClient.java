package com.painelcmd;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class PainelCmdClient implements ClientModInitializer {

    public static KeyMapping OPEN_KEY;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category categoria =
                KeyMapping.Category.register(
                        Identifier.fromNamespaceAndPath("painelcmd", "main")
                );

        OPEN_KEY = new KeyMapping(
                "key.painelcmd.open",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                categoria
        );

        KeyMappingHelper.registerKeyMapping(OPEN_KEY);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            NightVision.tick(client);

            while (OPEN_KEY.consumeClick()) {
                if (client.gui.screen() == null && client.player != null) {
                    client.gui.setScreen(new PainelScreen());
                }
            }
        });
    }
}
