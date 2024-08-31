package me.barny1094875.utilitiesog.internal;

import java.util.function.Function;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;

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

	/**
	 * Creates a placeholder function using the provided template.
	 *
	 * @param template The template string to be used for this placeholder.
	 * @return A function that applies the template to a player's name and returns a Tag.
	 */
	public Function<Player, Tag> createPlaceholderFunction(TextComponent template) {

		return player -> {

			String formattedString = template.content().replace("<player>", player.getName());

			Component formattedComponent = MiniMessage.miniMessage().deserialize(formattedString);

			return Tag.inserting(formattedComponent);

		};

	}

}