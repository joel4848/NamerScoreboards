package com.joel4848.namerscoreboards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NamerScoreboardsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("namerscoreboards.json");

    public int maxNickLength = 0;
    public boolean allowNickFormatting = true;
    public boolean allowSettingOwnNicknames = true;

    public static NamerScoreboardsConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, NamerScoreboardsConfig.class);
            } catch (IOException e) {
                System.err.println("Failed to load NamerScoreboards config, using defaults");
                e.printStackTrace();
            }
        }
        NamerScoreboardsConfig config = new NamerScoreboardsConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            System.err.println("Failed to save NamerScoreboards config");
            e.printStackTrace();
        }
    }

    public int maxNickLength() {
        return maxNickLength;
    }

    public boolean allowNickFormatting() {
        return allowNickFormatting;
    }

    public boolean allowSettingOwnNicknames() {
        return allowSettingOwnNicknames;
    }

    public void setMaxNickLength(int value) {
        this.maxNickLength = value;
        save();
    }

    public void setAllowNickFormatting(boolean value) {
        this.allowNickFormatting = value;
        save();
    }

    public void setAllowSettingOwnNicknames(boolean value) {
        this.allowSettingOwnNicknames = value;
        save();
    }
}