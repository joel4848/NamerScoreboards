package com.joel4848.namerscoreboards;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;

import java.util.function.Consumer;

public class NamerScoreboardsConfig extends ConfigWrapper<NamerScoreboardsConfig> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Integer> maxNickLength = this.optionForKey(this.keys.maxNickLength);
    private final Option<java.lang.Boolean> allowNickFormatting = this.optionForKey(this.keys.allowNickFormatting);
    private final Option<java.lang.Boolean> allowSettingOwnNicknames = this.optionForKey(this.keys.allowSettingOwnNicknames);

    private NamerScoreboardsConfig() {
        super(NamerScoreboardsConfig.class);
    }

    private NamerScoreboardsConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(NamerScoreboardsConfig.class, janksonBuilder);
    }

    public static NamerScoreboardsConfig createAndLoad() {
        var wrapper = new NamerScoreboardsConfig();
        wrapper.load();
        return wrapper;
    }

    public static NamerScoreboardsConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new NamerScoreboardsConfig(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public int maxNickLength() {
        return maxNickLength.value();
    }

    public void maxNickLength(int value) {
        maxNickLength.set(value);
    }

    public boolean allowNickFormatting() {
        return allowNickFormatting.value();
    }

    public void allowNickFormatting(boolean value) {
        allowNickFormatting.set(value);
    }

    public boolean allowSettingOwnNicknames() {
        return allowSettingOwnNicknames.value();
    }

    public void allowSettingOwnNicknames(boolean value) {
        allowSettingOwnNicknames.set(value);
    }


    public static class Keys {
        public final Option.Key maxNickLength = new Option.Key("maxNickLength");
        public final Option.Key allowNickFormatting = new Option.Key("allowNickFormatting");
        public final Option.Key allowSettingOwnNicknames = new Option.Key("allowSettingOwnNicknames");
    }
}

