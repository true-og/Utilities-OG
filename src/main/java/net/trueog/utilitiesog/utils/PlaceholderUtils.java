package net.trueog.utilitiesog.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.trueog.utilitiesog.InternalFunctions;
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

        this.placeholderRegistration = builder -> builder.globalPlaceholder(miniPlaceholderSuffix,
                (queue, ctx) -> Tag.inserting(TextUtils.expandTextWithPlaceholders(placeholder.get())));

        // Log global placeholder registration without arguments.
        UtilitiesOG.logToConsole(InternalFunctions.getPrefix(), "Registered Global MiniPlaceholder: "
                + miniPlaceholderPrefix + "_" + miniPlaceholderSuffix + " without arguments");

        return this;

    }

    // Set global placeholder with arguments.
    public PlaceholderUtils setGlobalPlaceholder(Function<List<String>, String> valueFunction) {

        this.placeholderRegistration = builder -> builder.globalPlaceholder(miniPlaceholderSuffix, (queue, ctx) -> {

            final List<String> args = new ArrayList<>();
            while (queue.hasNext()) {

                args.add(queue.pop().value());

            }

            final String resultString = valueFunction.apply(args);

            final TextComponent result = TextUtils.expandTextWithPlaceholders(resultString);

            return Tag.inserting(result);

        });

        // Log global placeholder registration with arguments.
        UtilitiesOG.logToConsole(InternalFunctions.getPrefix(), "Registered Global MiniPlaceholder: "
                + miniPlaceholderPrefix + "_" + miniPlaceholderSuffix + " with arguments");

        return this;

    }

    // Set audience placeholder without arguments.
    public PlaceholderUtils setAudiencePlaceholder(Function<Player, String> placeholder) {

        this.placeholderRegistration = builder -> builder.audiencePlaceholder(miniPlaceholderSuffix,
                (audience, queue, ctx) ->
                {

                    if (audience instanceof Player player) {

                        final TextComponent result = TextUtils.expandTextWithPlaceholders(placeholder.apply(player),
                                player);

                        return Tag.inserting(result);

                    }

                    return Tag.inserting(Component.text(""));

                });

        // Log audience placeholder registration without arguments.
        UtilitiesOG.logToConsole(InternalFunctions.getPrefix(), "Registered Audience MiniPlaceholder: "
                + miniPlaceholderPrefix + "_" + miniPlaceholderSuffix + " without arguments");

        return this;

    }

    // Set audience placeholder with arguments.
    public PlaceholderUtils setAudiencePlaceholder(BiFunction<Player, List<String>, String> valueFunction) {

        this.placeholderRegistration = builder -> builder.audiencePlaceholder(miniPlaceholderSuffix,
                (audience, queue, ctx) ->
                {

                    if (audience instanceof Player player) {

                        final List<String> args = new ArrayList<>();
                        while (queue.hasNext()) {

                            args.add(queue.pop().value());

                        }

                        final String resultString = valueFunction.apply(player, args);

                        final TextComponent result = TextUtils.expandTextWithPlaceholders(resultString, player);

                        return Tag.inserting(result);

                    }

                    return Tag.inserting(Component.text(""));

                });

        // Log audience placeholder registration with arguments.
        UtilitiesOG.logToConsole(InternalFunctions.getPrefix(), "Registered Audience MiniPlaceholder: "
                + miniPlaceholderPrefix + "_" + miniPlaceholderSuffix + " with arguments");

        return this;

    }

    // Set relational placeholder without arguments.
    public PlaceholderUtils setRelationalPlaceholder(BiFunction<Player, Player, String> placeholder) {

        this.placeholderRegistration = builder -> builder.relationalPlaceholder(miniPlaceholderSuffix,
                (audience, otherAudience, queue, ctx) ->
                {

                    if (audience instanceof Player player && otherAudience instanceof Player targetPlayer) {

                        final String resultString = placeholder.apply(player, targetPlayer);

                        final TextComponent result = TextUtils.expandTextWithPlaceholders(resultString, player,
                                targetPlayer);

                        return Tag.inserting(result);

                    }

                    return Tag.inserting(Component.text(""));

                });

        // Log relational placeholder registration without arguments
        UtilitiesOG.logToConsole(InternalFunctions.getPrefix(), "Registered Relational MiniPlaceholder: "
                + miniPlaceholderPrefix + "_" + miniPlaceholderSuffix + " without arguments");

        return this;

    }

    // Set relational placeholder with arguments.
    public PlaceholderUtils setRelationalPlaceholder(
            UtilitiesOG.TriFunction<Player, Player, List<String>, String> valueFunction)
    {

        this.placeholderRegistration = builder -> builder.relationalPlaceholder(miniPlaceholderSuffix,
                (audience, otherAudience, queue, ctx) ->
                {

                    if (audience instanceof Player player && otherAudience instanceof Player targetPlayer) {

                        final List<String> args = new ArrayList<>();
                        while (queue.hasNext()) {

                            args.add(queue.pop().value());

                        }

                        final String resultString = valueFunction.apply(player, targetPlayer, args);

                        final TextComponent result = TextUtils.expandTextWithPlaceholders(resultString, player,
                                targetPlayer);

                        return Tag.inserting(result);

                    }

                    return Tag.inserting(Component.text(""));

                });

        // Log relational placeholder registration with arguments.
        UtilitiesOG.logToConsole(InternalFunctions.getPrefix(), "Registered Relational MiniPlaceholder: "
                + miniPlaceholderPrefix + "_" + miniPlaceholderSuffix + " with arguments");

        return this;

    }

    // Register the MiniPlaceholder.
    public void register() {

        final ExpansionData data = expansionDataMap.computeIfAbsent(miniPlaceholderPrefix, ExpansionData::new);

        data.addPlaceholderRegistration(placeholderRegistration);

    }

    // API for registering the MiniPlaceholder (global, audience, relational, or
    // with args).
    public static void trueogRegisterMiniPlaceholder(String placeholderName,
            Consumer<PlaceholderUtils> placeholderConfigurator)
    {

        final int lastIndex = placeholderName.lastIndexOf('_');
        if (lastIndex == -1) {

            throw new IllegalArgumentException(
                    "ERROR: MiniPlaceholder name must contain an underscore separating its prefix and suffix.");

        }

        final String placeholderPrefix = StringUtils.substring(placeholderName, 0, lastIndex);
        final String placeholderSuffix = StringUtils.substring(placeholderName, lastIndex + 1);

        final PlaceholderUtils placeholderUtils = new PlaceholderUtils(placeholderPrefix, placeholderSuffix);

        placeholderConfigurator.accept(placeholderUtils);

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

            final Expansion.Builder builder = Expansion.builder(prefix);

            placeholderRegistrations.forEach(registration -> registration.accept(builder));

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