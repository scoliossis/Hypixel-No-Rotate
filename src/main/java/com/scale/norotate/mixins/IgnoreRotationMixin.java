package com.scale.norotate.mixins;

import com.scale.norotate.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

// gets rotation from before packet, and sets it back after.
@Mixin(ClientboundPlayerPositionPacket.class)
public class IgnoreRotationMixin {
    @Unique
    private Vec2 rotation = null;

    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("HEAD"))
    public void beforePacketHandled(ClientGamePacketListener clientGamePacketListener, CallbackInfo ci) {
        // blehhhh boring, rotation is bad!!
        if (!Main.isEnabled) return;

        LocalPlayer player = Minecraft.getInstance().player;
        // you receive this packet on joining a server
        if (player == null) return;

        rotation = Main.getRotation();
    }

    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("TAIL"))
    public void afterPacketHandled(ClientGamePacketListener clientGamePacketListener, CallbackInfo ci) {
        // rotation should never be null, wtv
        if (rotation == null) return;

        // we should still set this rotation for a tick, or else the server WILL know
        Main.lastServerRotation = Main.getRotation();

        Main.setRotation(rotation);
        rotation = null;
    }
}