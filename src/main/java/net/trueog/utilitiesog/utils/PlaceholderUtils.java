// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.entity.Player;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.Tag;

public class PlaceholderUtils {

	// Map to store Expansion Builders per prefix.
	private static final Map<String, Expansion.Builder> expansionBuilders = new HashMap<>();

	// Map to store registered expansions.
	private static final Map<String, Expansion> expansions = new HashMap<>();

	private final String miniPlaceholderPrefix;
	private final String miniPlaceholderSuffix;

	// MiniPlaceholder functions.
	private Supplier<String> globalPlaceholder;
	private Function<Player, String> audiencePlaceholder;
	private BiFunction<Player, Player, String> relationalPlaceholder;

	// Constructor for prefix and suffix.
	public PlaceholderUtils(String placeholderPrefix, String placeholderSuffix) {

		this.miniPlaceholderPrefix = placeholderPrefix;
		this.miniPlaceholderSuffix = placeholderSuffix;

	}

	// Setters for global, audience, and relational MiniPlaceholders.
	public PlaceholderUtils setGlobalPlaceholder(Supplier<String> placeholder) {

		this.globalPlaceholder = placeholder;

		return this;

	}

	public PlaceholderUtils setAudiencePlaceholder(Function<Player, String> placeholder) {

		this.audiencePlaceholder = placeholder;

		return this;

	}

	public PlaceholderUtils setRelationalPlaceholder(BiFunction<Player, Player, String> placeholder) {

		this.relationalPlaceholder = placeholder;

		return this;

	}

	// Register the MiniPlaceholder with recursive expansion.
	public void register() {

		Expansion.Builder builder = expansionBuilders.get(miniPlaceholderPrefix);

		if (builder == null) {

			builder = Expansion.builder(miniPlaceholderPrefix);
			expansionBuilders.put(miniPlaceholderPrefix, builder);

		}

		// Global MiniPlaceholder with recursive expansion.
		if (globalPlaceholder != null) {

			builder.globalPlaceholder(miniPlaceholderSuffix, (queue, ctx) -> {

				TextComponent result = TextUtils.expandTextWithPlaceholders(globalPlaceholder.get());

				return Tag.inserting(result);

			});

		}

		// Audience MiniPlaceholder with recursive expansion.
		if (audiencePlaceholder != null) {

			builder.audiencePlaceholder(miniPlaceholderSuffix, (audience, queue, ctx) -> {

				if (audience instanceof Player player) {

					TextComponent result = TextUtils.expandTextWithPlaceholders(audiencePlaceholder.apply(player), player);

					return Tag.inserting(result);

				}

				return Tag.inserting(Component.empty());

			});

		}

		// Relational MiniPlaceholder with recursive expansion.
		if (relationalPlaceholder != null) {

			builder.relationalPlaceholder(miniPlaceholderSuffix, (audience, otherAudience, queue, ctx) -> {

				if (audience instanceof Player player && otherAudience instanceof Player targetPlayer) {

					TextComponent result = TextUtils.expandTextWithPlaceholders(relationalPlaceholder.apply(player, targetPlayer), player, targetPlayer);

					return Tag.inserting(result);

				}

				return Tag.inserting(Component.empty());

			});

		}

	}

	// Build and register all expansions.
	public static void buildAndRegisterExpansions() {

		for (Map.Entry<String, Expansion.Builder> entry : expansionBuilders.entrySet()) {

			Expansion expansion = entry.getValue().build();
			expansion.register();

			expansions.put(entry.getKey(), expansion);

		}

		// Clean up the builders after they're done being used.
		expansionBuilders.clear();

	}

	// API for registering the MiniPlaceholder (global, audience, or relational).
	public static void trueogRegisterMiniPlaceholder(String placeholderName, Consumer<PlaceholderUtils> placeholderConfigurator) {

		// Split placeholder name into prefix and suffix using the last underscore.
		int lastIndex = placeholderName.lastIndexOf('_');
		if (lastIndex == -1) {

			throw new IllegalArgumentException("Placeholder name must contain an underscore separating prefix and suffix.");

		}

		String placeholderPrefix = placeholderName.substring(0, lastIndex);
		String placeholderSuffix = placeholderName.substring(lastIndex + 1);

		// Create a new PlaceholderUtils instance.
		PlaceholderUtils placeholderUtils = new PlaceholderUtils(placeholderPrefix, placeholderSuffix);

		// Apply the provided configuration (lambda).
		placeholderConfigurator.accept(placeholderUtils);

		// Register the MiniPlaceholder and adds it to the builder.
		placeholderUtils.register();

	}

	public static void unregisterAll() {

		for (Expansion expansion : expansions.values()) {

			expansion.unregister();

		}

		expansions.clear();

	}

}