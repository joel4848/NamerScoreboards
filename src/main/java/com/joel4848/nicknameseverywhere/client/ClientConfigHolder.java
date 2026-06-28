package com.joel4848.nicknameseverywhere.client;

public class ClientConfigHolder {
    private static boolean serverAllowNickFormatting = true;
    private static boolean serverUsePronounsEverywhere = true;
    private static boolean hasReceivedServerConfig = false;

    public static void setServerConfig(boolean allowNickFormatting, boolean usePronounsEverywhere) {
        serverAllowNickFormatting = allowNickFormatting;
        serverUsePronounsEverywhere = usePronounsEverywhere;
        hasReceivedServerConfig = true;
    }

    // Keep old method for backwards compatibility with existing call sites
    public static void setServerAllowNickFormatting(boolean value) {
        serverAllowNickFormatting = value;
        hasReceivedServerConfig = true;
    }

    public static boolean getServerAllowNickFormatting() { return serverAllowNickFormatting; }
    public static boolean getServerUsePronounsEverywhere() { return serverUsePronounsEverywhere; }
    public static boolean hasReceivedServerConfig() { return hasReceivedServerConfig; }

    public static void reset() {
        serverAllowNickFormatting = true;
        serverUsePronounsEverywhere = true;
        hasReceivedServerConfig = false;
    }
}