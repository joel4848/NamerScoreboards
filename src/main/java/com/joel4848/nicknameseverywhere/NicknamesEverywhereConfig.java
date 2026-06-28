package com.joel4848.nicknameseverywhere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NicknamesEverywhereConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("nicknameseverywhere.json");

    public int maxNickLength = 0;
    public int maxPronounsLength = 0;
    public boolean allowNickFormatting = true;
    public boolean allowSettingOwnNicknames = true;
    public boolean usePronounsEverywhere = true;

    public static NicknamesEverywhereConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, NicknamesEverywhereConfig.class);
            } catch (IOException e) {
                System.err.println("Failed to load NicknamesEverywhere config, using defaults");
                e.printStackTrace();
            }
        }
        NicknamesEverywhereConfig config = new NicknamesEverywhereConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            System.err.println("Failed to save NicknamesEverywhere config");
            e.printStackTrace();
        }
    }

    public int maxNickLength() { return maxNickLength; }
    public int maxPronounsLength() { return maxPronounsLength; }
    public boolean allowNickFormatting() { return allowNickFormatting; }
    public boolean allowSettingOwnNicknames() { return allowSettingOwnNicknames; }
    public boolean usePronounsEverywhere() { return usePronounsEverywhere; }

    public void setMaxNickLength(int value) { this.maxNickLength = value; save(); }
    public void setMaxPronounsLength(int value) { this.maxPronounsLength = value; save(); }
    public void setAllowNickFormatting(boolean value) { this.allowNickFormatting = value; save(); }
    public void setAllowSettingOwnNicknames(boolean value) { this.allowSettingOwnNicknames = value; save(); }
    public void setUsePronounsEverywhere(boolean value) { this.usePronounsEverywhere = value; save(); }
}