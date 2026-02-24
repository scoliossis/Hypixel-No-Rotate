package com.scale.norotate;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main implements ModInitializer {
    public static Vec2 lastServerRotation = null;
    public static Vec2 lastClientRotation = null;
    public static float extraYaw = 0;

    public static boolean isEnabled = true;

    public static final File CONFIG_FILE = new File("norotate/enabled.json");

    public static Vec2 getRotation() {
        return getPlayer().getRotationVector();
    }

    public static void setRotation(Vec2 rotation) {
        LocalPlayer player = getPlayer();
        if (player == null) return;

        // set ALL the rotations
        player.snapTo(getPlayer().position(), rotation.y, rotation.x);
    }

    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public void onInitialize() {
        if (!CONFIG_FILE.exists()) {
            new File(CONFIG_FILE.getParent()).mkdirs();
            System.out.println("[NoRotate] no rotate enabled by default.");
        }
        else {
            try {
                String noRotateConfig = Files.readString(CONFIG_FILE.toPath());
                isEnabled = noRotateConfig.equals("true");

                System.out.println("[NoRotate] " + isEnabled);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}