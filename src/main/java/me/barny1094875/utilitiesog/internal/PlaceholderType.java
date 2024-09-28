package me.barny1094875.utilitiesog.internal;

import java.util.function.Function;

import org.bukkit.entity.Player;

import me.barny1094875.utilitiesog.UtilitiesOG;
import me.barny1094875.utilitiesog.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;

public enum PlaceholderType {

	AUDIENCE(player -> {

		// The placeholder logic will be provided externally.
		return Tag.inserting(null);

	}),

	/*GLOBAL(player -> {
        return Tag.inserting(null);
    }),

    RELATIVE(player -> {
        return Tag.inserting(null);
    })*/;

	private final Function<Player, Tag> placeholderFunction;

	PlaceholderType(Function<Player, Tag> placeholderFunction) {

		this.placeholderFunction = placeholderFunction;

	}

	public Function<Player, Tag> getPlaceholderFunction() {

		return placeholderFunction;

	}

	// Creates a placeholder function using the provided template.
	public Function<Player, Tag> createPlaceholderFunction(TextComponent template) {
		return player -> {

			// Get the LuckPerms API instance.
			LuckPerms luckPerms = LuckPermsProvider.get();

			// Get the user's LuckPerms User object.
			User user = luckPerms.getUserManager().getUser(player.getUniqueId());
			if (user == null) {

				// Log the missing User to the console.
				UtilitiesOG.getPlugin().getLogger().info("ERROR: MiniPlaceholder processing error. Player: " + player.getName() + "has no User object in LuckPerms!");

				// Fallback to no formatting if the LuckPerms User cannot be found.
				return Tag.inserting(Component.text(player.getName()));

			}

			// Get the player's primary group.
			String primaryGroup = user.getPrimaryGroup();

			// Fetch the LuckPerms group.
			Group group = luckPerms.getGroupManager().getGroup(primaryGroup);
			if (group == null) {

				// Log the missing Group to the console.
				UtilitiesOG.getPlugin().getLogger().info("ERROR: MiniPlaceholder processing error. User: " + player.getName() + "has no Group assignment in LuckPerms!");

				// Fallback to no formatting if the LuckPerms User's Group cannot be found.
				return Tag.inserting(Component.text(player.getName()));

			}

			// Find the prefix node within the group.
			String prefix = null;
			for (Node node : group.getNodes()) {

				if (node instanceof PrefixNode) {

					prefix = ((PrefixNode) node).getMetaValue();

					break;

				}

			}

			// If no prefix is found, default to using just the player's name.
			if (prefix == null) {

				return Tag.inserting(Component.text(player.getName()));

			}

			// Extract the color code from the prefix (after the "]").
			String colorCode = prefix.replaceAll(".*\\](.*)", "$1");

			// Use TextUtils to process the color codes in the player's name.
			String coloredPlayerName = TextUtils.processColorCodes(colorCode + " " + player.getName());

			// Replace <player> in the template with the colored player's name.
			String formattedString = template.content().replace("<player>", coloredPlayerName);

			// Convert the formatted string to an Adventure component using UtilitiesOG.
			Component formattedComponent = UtilitiesOG.trueogExpandMiniPlaceholders(player, formattedString);

			// Return the formatted Tag.
			return Tag.inserting(formattedComponent);

		};

	}

}