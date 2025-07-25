// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.utils;

import io.github.miniplaceholders.api.MiniPlaceholders;
import java.util.List;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.trueog.utilitiesog.misc.ColorCodeMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TextUtils {

    // Regular expressions to match color codes starting with § or & in Strings.
    private static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("(?i)[§&][0-9A-FK-OR]");
    private static final List<String> MINIMESSAGE_TAGS = List.of(
            "<black>",
            "<dark_blue>",
            "<dark_green>",
            "<dark_aqua>",
            "<dark_red>",
            "<dark_purple>",
            "<gold>",
            "<gray>",
            "<dark_gray>",
            "<blue>",
            "<green>",
            "<aqua>",
            "<red>",
            "<light_purple>",
            "<yellow>",
            "<white>",
            "<obfuscated>",
            "<bold>",
            "<strikethrough>",
            "<underlined>",
            "<italic>",
            "<reset>",
            "<rainbow>");

    // Sends a formatted message to the player, expanding placeholders and color codes.
    public static void trueogMessage(Player player, String message) {

        player.sendMessage(expandTextWithPlaceholders(message, player));
    }

    // Expand global MiniPlaceholders recursively.
    public static TextComponent expandTextWithPlaceholders(String message) {

        // Process any legacy color codes.
        message = processColorCodes(message);

        // Use global MiniPlaceholders for the resolver.
        TagResolver resolver = MiniPlaceholders.getGlobalPlaceholders();

        // Recursively expand the MiniPlaceholders.
        return recursivelyExpandPlaceholders(message, resolver);
    }

    // Expand MiniPlaceholders with player-specific context recursively.
    public static TextComponent expandTextWithPlaceholders(String message, Player player) {

        // Process legacy color codes.
        message = processColorCodes(message);

        // Create a resolver for global and audience-specific MiniPlaceholders.
        TagResolver resolver = TagResolver.resolver(
                MiniPlaceholders.getGlobalPlaceholders(), MiniPlaceholders.getAudiencePlaceholders(player));

        // Recursively expand MiniPlaceholders using MiniMessage.
        return recursivelyExpandPlaceholders(message, resolver);
    }

    // Relational placeholder expansion between two players.
    public static TextComponent expandTextWithPlaceholders(String message, Player player, Player target) {

        // Process legacy color codes.
        message = processColorCodes(message);

        // Create a resolver for global, audience, and relational MiniPlaceholders.
        TagResolver resolver = TagResolver.resolver(
                MiniPlaceholders.getGlobalPlaceholders(),
                MiniPlaceholders.getAudiencePlaceholders(player),
                MiniPlaceholders.getRelationalPlaceholders(player, target));

        // Recursively expand MiniPlaceholders using MiniMessage.
        return recursivelyExpandPlaceholders(message, resolver);
    }

    // Recursive placeholder expansion using MiniMessage.
    private static TextComponent recursivelyExpandPlaceholders(String message, TagResolver resolver) {

        String previousMessage;
        Component expandedMessage;

        // Loop until no new MiniPlaceholders are found (to handle recursive expansions).
        do {

            previousMessage = message;
            expandedMessage = MiniMessage.miniMessage().deserialize(message, resolver);

            // Serialize back to string.
            message = MiniMessage.miniMessage().serialize(expandedMessage);

        } while (!message.equals(previousMessage));

        return (TextComponent) expandedMessage;
    }

    // Process color codes, converting legacy Bukkit color codes (e.g., &c) to MiniMessage-compatible codes.
    public static String processColorCodes(String message) {

        // Keeps track of the ampersand count in the loop.
        int count = 0;
        // Count the number of ampersand characters to determine how many there are for lowercase conversion.
        for (char c : message.toCharArray()) {

            // If the character is an ampersand, do this...
            if (c == '&') {

                // Increase the ampersand count by 1.
                count++;
            }
        }

        // Keeps track of the positions of '&' characters in the message String.
        int[] positions = new int[count];
        // Keep track of the position of the metaphorical cursor in the message String.
        int index = 0;
        // Iterate through the message String to find the positions of '&' characters for lowercase conversion.
        for (int i = 0; i < message.length(); i++) {

            // If the character is an ampersand, do this...
            if (message.charAt(i) == '&') {

                // If the character directly after the ampersand is a valid uppercase Bukkit color code, do this...
                if (isUpperBukkitCode(message.charAt(i + 1))) {

                    // Replace the valid uppercase Bukkit color code with the equivalent lowercase one.
                    message = replaceAtIndex(
                            message, (i + 1), Character.toString(Character.toLowerCase(message.charAt(i + 1))));
                }

                try {

                    // Convert the newly uniform legacy Bukkit color codes into modern MiniMessage color / formatting
                    // codes.
                    message = replaceAtIndex(message, (i + 1), ColorCodeMap.toMiniMessage(message.charAt(i + 1)));

                    // Delete the leftover ampersand.
                    message = replaceAtIndex(message, i, "");

                } catch (NullPointerException error) {

                    // Do nothing, ampersand is intended to be in the message, since it's not a valid color code.

                }

                // Move the metaphorical cursor forward by one.
                positions[index++] = i;
            }
        }

        // Return the processed message with modern color codes.
        return message;
    }

    // Strips color codes and MiniMessage tags from a String.
    public static String stripColors(String input) {

        if (input == null) {

            return null;
        }

        String stripped = input;

        for (String tag : MINIMESSAGE_TAGS) {

            stripped = stripped.replace(tag, "");
        }

        return LEGACY_COLOR_PATTERN.matcher(stripped).replaceAll("");
    }

    // Sends an error message to the player if they lack the required permission.
    public static void permissionsErrorMessage(Player player, String command, String permission) {

        trueogMessage(
                player,
                "&cERROR: You do not have permission to run /" + command + ". &6Required permission: &e" + permission);
    }

    // Logs a Component message to the console, stripping any colors.
    public static void logToConsole(String message) {

        Bukkit.getLogger().info(stripColors(message));
    }

    // Detects if a Bukkit color code is uppercase (e.g., &C, &L).
    private static boolean isUpperBukkitCode(char input) {

        return "ABCDEFKLMNOR".indexOf(Character.toUpperCase(input)) != -1;
    }

    // Replaces a character in a string at a given index with a new String.
    private static String replaceAtIndex(String original, int index, String newString) {

        if (index >= 0 && index < original.length()) {

            return original.substring(0, index) + newString + original.substring(index + 1);
        }

        return original;
    }
}
