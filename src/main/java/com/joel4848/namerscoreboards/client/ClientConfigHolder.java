package com.joel4848.namerscoreboards.client;

/**
 * Holds the server's config values on the client side.
 * This is separate from the client's own config file.
 */
public class ClientConfigHolder {
    private static boolean serverAllowNickFormatting = true;
    private static boolean hasReceivedServerConfig = false;

    public static void setServerAllowNickFormatting(boolean value) {
        serverAllowNickFormatting = value;
        hasReceivedServerConfig = true;
    }

    public static boolean getServerAllowNickFormatting() {
        return serverAllowNickFormatting;
    }

    public static boolean hasReceivedServerConfig() {
        return hasReceivedServerConfig;
    }

    public static void reset() {
        serverAllowNickFormatting = true;
        hasReceivedServerConfig = false;
    }
}