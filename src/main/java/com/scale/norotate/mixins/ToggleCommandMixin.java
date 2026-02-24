package com.scale.norotate.mixins;

import com.scale.norotate.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;

@Mixin(ChatScreen.class)
public class ToggleCommandMixin {
    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    public void onChatInput(String string, boolean bl, CallbackInfo ci) {
        if (string.startsWith("/norotate")) {
            Main.isEnabled = !Main.isEnabled;

            ChatComponent chatComponent = Minecraft.getInstance().gui.getChat();
            chatComponent.addRecentChat("/norotate");
            chatComponent.addMessage(Component.literal("§6[§fNoRotate§6] " + (Main.isEnabled ? "§aEnabled" : "§cDisabled")));

            try {
                Files.write(Main.CONFIG_FILE.toPath(), String.valueOf(Main.isEnabled).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ci.cancel();
        }
    }
}
