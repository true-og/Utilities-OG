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

	// Map to store Expansion Data per prefix.
	private static final Map<String, ExpansionData> expansionDataMap = new HashMap<>();

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

	// Register the MiniPlaceholder.
	public void register() {

		ExpansionData data = expansionDataMap.computeIfAbsent(miniPlaceholderPrefix, k -> new ExpansionData(k));

		// Add MiniPlaceholder to the builder.
		if (globalPlaceholder != null) {

			data.builder.globalPlaceholder(miniPlaceholderSuffix, (queue, ctx) -> {

				TextComponent result = TextUtils.expandTextWithPlaceholders(globalPlaceholder.get());

				return Tag.inserting(result);

			});

		}

		if (audiencePlaceholder != null) {

			data.builder.audiencePlaceholder(miniPlaceholderSuffix, (audience, queue, ctx) -> {

				if (audience instanceof Player player) {

					TextComponent result = TextUtils.expandTextWithPlaceholders(audiencePlaceholder.apply(player), player);

					return Tag.inserting(result);

				}

				return Tag.inserting(Component.empty());

			});

		}

		if (relationalPlaceholder != null) {

			data.builder.relationalPlaceholder(miniPlaceholderSuffix, (audience, otherAudience, queue, ctx) -> {

				if (audience instanceof Player player && otherAudience instanceof Player targetPlayer) {

					TextComponent result = TextUtils.expandTextWithPlaceholders(relationalPlaceholder.apply(player, targetPlayer), player, targetPlayer);

					return Tag.inserting(result);

				}

				return Tag.inserting(Component.empty());

			});

		}

		// Rebuild and re-register the expansion.
		if (data.registered) {

			data.expansion.unregister();

		}

		data.expansion = data.builder.build();
		data.expansion.register();
		data.registered = true;

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

		// Register the MiniPlaceholder and add it to the builder.
		placeholderUtils.register();

	}

	// Inner class to hold expansion data.
	private static class ExpansionData {

		Expansion.Builder builder;
		Expansion expansion;
		boolean registered = false;

		public ExpansionData(String prefix) {

			this.builder = Expansion.builder(prefix);

		}

	}

	public static void unregisterAll() {

		for (ExpansionData data : expansionDataMap.values()) {

			if (data.registered) {

				data.expansion.unregister();
				data.registered = false;

			}

		}

		expansionDataMap.clear();

	}

}