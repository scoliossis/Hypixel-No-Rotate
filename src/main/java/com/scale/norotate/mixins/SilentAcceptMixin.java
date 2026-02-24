package com.scale.norotate.mixins;

import com.scale.norotate.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class SilentAcceptMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    public void onTickHEAD(CallbackInfo ci) {
        if (Main.lastServerRotation == null) return;

        // store client rotation
        Main.lastClientRotation = Main.getRotation();

        // accept server rotation packet for this tick
        Main.setRotation(Main.lastServerRotation);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTickTAIL(CallbackInfo ci) {
        if (Main.lastClientRotation == null) return;

        // grim (and hypixel probably) check if u wrap ur yaw, which this mod flags without this.
        Vec2 newClientRotation = fixAimModule360(Main.lastServerRotation, Main.lastClientRotation);

        // restore client rotation
        Main.setRotation(new Vec2(newClientRotation.x, newClientRotation.y));

        // set the bobbing correctly, copied from LocalPlayer.applyInput()
        LocalPlayer player = Main.getPlayer();
        player.xBob = player.xBobO + ((newClientRotation.x - player.xBobO) * 0.5f);
        player.yBob = player.yBobO + ((newClientRotation.y - player.yBobO) * 0.5f);

        Main.extraYaw += newClientRotation.y - Main.lastClientRotation.y;

        Main.lastClientRotation = null;
        Main.lastServerRotation = null;
    }

    // hypixel basically uses a fork of this: https://github.com/GrimAnticheat/Grim/blob/2.0/common/src/main/java/ac/grim/grimac/checks/impl/aim/AimModulo360.java
    @Unique private Vec2 fixAimModule360(Vec2 serverRotation, Vec2 clientRotation) {
        float delta = clientRotation.y - serverRotation.y;

        while (delta <= -180) delta += 360;
        while (delta > 180) delta -= 360;

        return new Vec2(clientRotation.x, serverRotation.y + delta);
    }
}
