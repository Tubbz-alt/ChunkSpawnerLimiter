package com.cyprias.chunkspawnerlimiter;

import org.bukkit.ChatColor;


public class Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ChunkSpawnerLimiter.getInstance().getName());

    public static void info(String mess) {
        logger.info(getLogPrefix() + mess);
    }

    public static void info(String format, Object... args) {
        logger.info(getLogPrefix() + String.format(format, args));


    }

    public static void info(Object... args) {
        StringBuilder msg = new StringBuilder("%s");
        for (int i = 1; i < args.length; i++) {
            msg.append(", %s");
        }
        info(msg.toString(), args);
    }

    public static void debug(String mess) {
        if (ChunkSpawnerLimiter.getInstance().getConfig().getBoolean("properties.debug-messages"))
            logger.info(cleanColorCodes(getLogPrefix() + "[Debug] " + mess));
    }

    public static void infoRaw(String mess) {
        logger.info(mess);
    }

    public static void infoRaw(String format, Object... args) {
        logger.info(String.format(format, args));
    }

    public static void warning(String mess) {
        logger.warning(getLogPrefix() + mess);
    }

    public static void warning(String format, Object... args) {
        logger.warning(getLogPrefix() + String.format(format, args));
    }

    public static void warningRaw(String mess) {
        logger.warning(mess);
    }

    public static void warningRaw(String format, Object... args) {
        logger.warning(String.format(format, args));
    }

    public static void severe(String mess) {
        logger.severe(getLogPrefix() + mess);
    }

    public static void severe(String format, Object... args) {
        logger.severe(getLogPrefix() + String.format(format, args));
    }

    public static void severeRaw(String mess) {
        logger.severe(mess);
    }

    public static void severeRaw(String format, Object... args) {
        logger.severe(String.format(format, args));
    }

    public static String getLogPrefix() {
        return ChunkSpawnerLimiter.chatPrefix;
    }

    // replace color codes with the colors
    public static String replaceColorCodes(String mess) {
        return mess.replaceAll("(&([" + colorCodes + "]))", "\u00A7$2");
    }

    // get rid of color codes
    public static String cleanColorCodes(String mess) {
        return mess.replaceAll("(&([" + colorCodes + "]))", "");
    }

    private static final String colorCodes;

    static {
        StringBuilder string = new StringBuilder();
        for (ChatColor color : ChatColor.values()) {
            char c = color.getChar();
            if (!Character.isLetter(c)) {
                string.append(c);
            } else {
                string.append(Character.toUpperCase(c));
                string.append(Character.toLowerCase(c));
            }
        }
        colorCodes = string.toString();
    }
}