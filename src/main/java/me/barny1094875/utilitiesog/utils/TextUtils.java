// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package me.barny1094875.utilitiesog.utils;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.miniplaceholders.api.MiniPlaceholders;
import me.barny1094875.utilitiesog.internal.ColorCodeMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class TextUtils {

	// Regular expressions to match color codes starting with § or & in Strings.
	private static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("(?i)[§&][0-9A-FK-OR]");
	private static final Pattern MINIMESSAGE_TAG_PATTERN = Pattern.compile("<[^>]*>");

	// Sends a formatted message to the player.
	public static void utilitiesOGMessage(Player player, String message) {

		// First, process the message for color codes.
		String colorProcessedMessage = processColorCodes(message);

		// Then, expand the MiniPlaceholders.
		TextComponent finalMessage = expandPlayerMiniPlaceholders(player, colorProcessedMessage);

		// Send the final message to the player.
		player.sendMessage(finalMessage);

	}

	// Processes color codes in the message, converting Bukkit color codes to MiniMessage codes.
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
					message = replaceAtIndex(message, (i + 1), Character.toString(Character.toLowerCase(message.charAt(i + 1))));

				}

				try {

					// Convert the newly uniform legacy Bukkit color codes into modern MiniMessage color / formatting codes.
					message = replaceAtIndex(message, (i + 1), ColorCodeMap.toMiniMessage(message.charAt(i + 1)));

					// Delete the leftover ampersand.
					message = replaceAtIndex(message, i, "");

				}
				catch (NullPointerException error) {

					// Do nothing, ampersand is intended to be in the message, since it's not a valid color code.

				}

				// Move the metaphorical cursor forward by one.
				positions[index++] = i;

			}

		}

		// Return the processed message with modern color codes.
		return message;

	}

	// Replace MiniPlaceholders in a given String with their content from MiniPlaceholderAPI.
	public static TextComponent expandPlayerMiniPlaceholders(Player player, String message) {

		// Get the MiniPlaceholder content that is relevant for the specific player who is seeing the message.
		TagResolver placeholders = MiniPlaceholders.getAudiencePlaceholders(player);

		// Replace the MiniPlaceholders with the audience placeholder content and the modern color codes with actual colors.
		Component expandedMessage = MiniMessage.miniMessage().deserialize(message, placeholders);

		// Put the message back into a TextComponent and send it on.
		return (TextComponent) expandedMessage;

	}

	// Log a Component message to the console, stripping colors.
	public static void logToConsole(String message) {

		Bukkit.getLogger().info(message);

	}

	// Strips color codes and MiniMessage tags from a String.
	public static String stripColors(String input) {

		if (input == null) return null;

		// Remove MiniMessage tags
		String stripped = MINIMESSAGE_TAG_PATTERN.matcher(input).replaceAll("");

		// Remove legacy color codes
		stripped = LEGACY_COLOR_PATTERN.matcher(stripped).replaceAll("");

		return stripped;

	}

	// Detects if a Bukkit color code is uppercase.
	private static boolean isUpperBukkitCode(char input) {

		// An array of all potential uppercase Bukkit color code characters.
		char[] bukkitColorCodes = {'A', 'B', 'C', 'D', 'E', 'F', 'K', 'L', 'M', 'N', 'O', 'R'};
		// Keeps track of found uppercase Bukkit color codes.
		boolean match = false;
		// Loop through each character in the array.
		for (char c : bukkitColorCodes) {

			// Check if the current character in the array is equal to the input character.
			if (c == input) {

				// A match was found.
				match = true;

			}

		}

		// Return whether or not the character is an uppercase Bukkit color code.
		return match;

	}

	// Replaces a character in a String at a given index with a new String.
	private static String replaceAtIndex(String original, int index, String newString) {

		// Check if the index is valid.
		if (index >= 0 && index < original.length()) {

			// Create a new string with the replacement.
			return original.substring(0, index) + newString + original.substring(index + 1);

		}

		// If the index is invalid, return the original string. Soft error.
		return original;

	}

	// Sends an error message to the player if they don't have permission to run a given command.
	public static void permissionsErrorMessage(Player player, String command, String permission) {

		// Send a formatted error message using the TrueOG Message API.
		utilitiesOGMessage(player, ("&cERROR: You do not have permission to run the /" + command + " command! &6Required permission: &e" + permission + "&6."));

	}

	// Sends an error message to the console if the command which was run is only applicable to players.
	public static void consoleUseErrorMessage(CommandSender sender, String command, String permission) {

		// Send an unformatted error message directly to the console.
		sender.sendMessage(("ERROR: The server console cannot execute the command: " + command + "! To run it in-game, ensure you have the permission: " + permission + "."));		

	}

}
