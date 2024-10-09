// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import net.trueog.utilitiesog.UtilitiesOG;

public class PlaceholderUtils {

	// Map to store Expansion Data per prefix.
	private static final Map<String, ExpansionData> expansionDataMap = new HashMap<>();

	private final String miniPlaceholderPrefix;
	private final String miniPlaceholderSuffix;

	// MiniPlaceholder functions.
	private Consumer<Expansion.Builder> placeholderRegistration;

	// Constructor for prefix and suffix.
	public PlaceholderUtils(String placeholderPrefix, String placeholderSuffix) {

		this.miniPlaceholderPrefix = placeholderPrefix;
		this.miniPlaceholderSuffix = placeholderSuffix;

	}

	// Set global placeholder without arguments.
	public PlaceholderUtils setGlobalPlaceholder(Supplier<String> placeholder) {

		this.placeholderRegistration = builder -> builder.globalPlaceholder(miniPlaceholderSuffix, (queue, ctx) -> {

			TextComponent result = TextUtils.expandTextWithPlaceholders(placeholder.get());

			return Tag.inserting(result);

		});

		return this;

	}

	// Set global placeholder with arguments.
	public PlaceholderUtils setGlobalPlaceholder(Function<List<String>, String> valueFunction) {

		this.placeholderRegistration = builder -> builder.globalPlaceholder(miniPlaceholderSuffix, (queue, ctx) -> {

			List<String> args = new ArrayList<>();
			while (queue.hasNext()) {

				args.add(queue.pop().value());

			}

			String resultString = valueFunction.apply(args);

			TextComponent result = TextUtils.expandTextWithPlaceholders(resultString);

			return Tag.inserting(result);

		});

		return this;

	}

	// Set audience placeholder without arguments.
	public PlaceholderUtils setAudiencePlaceholder(Function<Player, String> placeholder) {

		this.placeholderRegistration = builder -> builder.audiencePlaceholder(miniPlaceholderSuffix, (audience, queue, ctx) -> {

			if (audience instanceof Player player) {

				TextComponent result = TextUtils.expandTextWithPlaceholders(placeholder.apply(player), player);

				return Tag.inserting(result);

			}

			return Tag.inserting(Component.text(""));

		});

		return this;

	}

	// Set audience placeholder with arguments.
	public PlaceholderUtils setAudiencePlaceholder(BiFunction<Player, List<String>, String> valueFunction) {

		this.placeholderRegistration = builder -> builder.audiencePlaceholder(miniPlaceholderSuffix, (audience, queue, ctx) -> {

			if (audience instanceof Player player) {

				List<String> args = new ArrayList<>();
				while (queue.hasNext()) {

					args.add(queue.pop().value());
				}

				String resultString = valueFunction.apply(player, args);

				TextComponent result = TextUtils.expandTextWithPlaceholders(resultString, player);

				return Tag.inserting(result);

			}

			return Tag.inserting(Component.text(""));

		});

		return this;

	}

	// Set relational placeholder without arguments.
	public PlaceholderUtils setRelationalPlaceholder(BiFunction<Player, Player, String> placeholder) {

		this.placeholderRegistration = builder -> builder.relationalPlaceholder(miniPlaceholderSuffix, (audience, otherAudience, queue, ctx) -> {
			if (audience instanceof Player player && otherAudience instanceof Player targetPlayer) {

				String resultString = placeholder.apply(player, targetPlayer);

				TextComponent result = TextUtils.expandTextWithPlaceholders(resultString, player, targetPlayer);

				return Tag.inserting(result);

			}

			return Tag.inserting(Component.text(""));

		});

		return this;

	}

	// Set relational placeholder with arguments.
	public PlaceholderUtils setRelationalPlaceholder(UtilitiesOG.TriFunction<Player, Player, List<String>, String> valueFunction) {

		this.placeholderRegistration = builder -> builder.relationalPlaceholder(miniPlaceholderSuffix, (audience, otherAudience, queue, ctx) -> {
			if (audience instanceof Player player && otherAudience instanceof Player targetPlayer) {

				List<String> args = new ArrayList<>();
				while (queue.hasNext()) {

					args.add(queue.pop().value());

				}

				String resultString = valueFunction.apply(player, targetPlayer, args);

				TextComponent result = TextUtils.expandTextWithPlaceholders(resultString, player, targetPlayer);

				return Tag.inserting(result);

			}

			return Tag.inserting(Component.text(""));

		});

		return this;

	}

	// Register the MiniPlaceholder.
	public void register() {

		ExpansionData data = expansionDataMap.computeIfAbsent(miniPlaceholderPrefix, k -> new ExpansionData(k));

		// Add the MiniPlaceholder registration.
		data.addPlaceholderRegistration(placeholderRegistration);

	}

	// API for registering the MiniPlaceholder (global, audience, relational, or with args).
	public static void trueogRegisterMiniPlaceholder(String placeholderName, Consumer<PlaceholderUtils> placeholderConfigurator) {

		// Split the MiniPlaceholder name into prefix and suffix using the last underscore.
		int lastIndex = placeholderName.lastIndexOf('_');
		if (lastIndex == -1) {

			throw new IllegalArgumentException("ERROR: MiniPlaceholder name must contain an underscore separating its prefix and suffix.");

		}

		String placeholderPrefix = placeholderName.substring(0, lastIndex);
		String placeholderSuffix = placeholderName.substring(lastIndex + 1);

		// Create a new PlaceholderUtils instance.
		PlaceholderUtils placeholderUtils = new PlaceholderUtils(placeholderPrefix, placeholderSuffix);

		// Apply the provided configuration (lambda).
		placeholderConfigurator.accept(placeholderUtils);

		// Register the MiniPlaceholder.
		placeholderUtils.register();

	}

	// Inner class to hold expansion data.
	private static class ExpansionData {

		String prefix;
		List<Consumer<Expansion.Builder>> placeholderRegistrations = new ArrayList<>();
		Expansion expansion;
		boolean registered = false;

		public ExpansionData(String prefix) {

			this.prefix = prefix;

		}

		public void addPlaceholderRegistration(Consumer<Expansion.Builder> placeholderRegistration) {

			this.placeholderRegistrations.add(placeholderRegistration);

			rebuildExpansion();

		}

		private void rebuildExpansion() {

			if (registered) {

				expansion.unregister();

			}

			Expansion.Builder builder = Expansion.builder(prefix);
			for (Consumer<Expansion.Builder> registration : placeholderRegistrations) {

				registration.accept(builder);

			}

			expansion = builder.build();
			expansion.register();

			registered = true;

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