package me.barny1094875.utilitiesog.utils;

import java.util.function.Function;

import org.bukkit.entity.Player;

import io.github.miniplaceholders.api.Expansion;
import me.barny1094875.utilitiesog.internal.PlaceholderType;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.Tag;

public class PlaceholderUtils {

	private final String miniPlaceholderPrefix;
	private final String miniPlaceholderSuffix;
	private final Function<Player, Tag> placeholderFunction;
	private Expansion expansion;

	public PlaceholderUtils(String miniPlaceholderPrefix, String miniPlaceholderSuffix, PlaceholderType placeholderType, TextComponent content) {

		if (miniPlaceholderPrefix == null || miniPlaceholderSuffix == null) {
			throw new IllegalArgumentException("[Utilities-OG] MiniPlaceholderAPI ERROR: Both the prefix and suffix parameters must be specified!");
		}

		this.miniPlaceholderPrefix = miniPlaceholderPrefix;
		this.miniPlaceholderSuffix = miniPlaceholderSuffix;
		this.placeholderFunction = placeholderType.createPlaceholderFunction(content);
	}

	public String getFullPlaceholderName() {
		return miniPlaceholderPrefix + "_" + miniPlaceholderSuffix;
	}

	public void register() {
		if (expansion == null) {
			expansion = Expansion.builder(miniPlaceholderPrefix)
					.filter(Player.class)
					.audiencePlaceholder(miniPlaceholderSuffix, (audience, ctx, queue) -> {
						Player player = (Player) audience;
						return placeholderFunction.apply(player);
					})
					.build();

			expansion.register();

			String placeholder = "<" + miniPlaceholderPrefix + "_" + miniPlaceholderSuffix + ">";
			TextUtils.logToConsole("TrueOG MiniPlaceholdersAPI expansion " + placeholder + " registered successfully.");
		}
	}

	public void unregister() {
		if (expansion != null) {
			expansion.unregister();

			// Simplified logging method to log the unregistration.
			String placeholder = "<" + getFullPlaceholderName() + ">";
			TextUtils.logToConsole("TrueOG MiniPlaceholdersAPI expansion " + placeholder + " unregistered successfully.");

			expansion = null;
		}
	}

}