// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class TextUtils {

    // Cache a MiniMessage instance per each MessageFormat.
    private static final Map<MessageFormat, MiniMessage> MINI_CACHE = new ConcurrentHashMap<>();

    // Sends a formatted message with every supported tag active.
    public static void trueogMessage(Player player, String message) {

        trueogMessage(player, message, MessageFormat.full());

    }

    // Sends a formatted expanding message.
    public static void trueogMessage(Player player, String message, MessageFormat format) {

        player.sendMessage(expandTextWithPlaceholders(message, player, format));

    }

    // Sends a fully formatted non expanding message.
    public static void trueogRawMessage(Player player, String message) {

        trueogRawMessage(player, message, MessageFormat.full());

    }

    // Sends a selectively formatted non expanding message.
    public static void trueogRawMessage(Player player, String message, MessageFormat format) {

        player.sendMessage(miniMessage(format).deserialize(processColorCodes(message)));

    }

    // Sends a pre-built Component with every supported tag active.
    public static void trueogMessage(Player player, Component message) {

        player.sendMessage(message);

    }

    // Sends a selectively formatted pre-built Component.
    public static void trueogMessage(Player player, Component message, MessageFormat format) {

        player.sendMessage(applyFormat(message, format));

    }

    // Global-context MiniPlaceholder expansion with all tags active.
    public static TextComponent expandTextWithPlaceholders(String message) {

        return expandTextWithPlaceholders(message, MessageFormat.full());

    }

    // Global-context MiniPlaceholder expansion with the given format.
    public static TextComponent expandTextWithPlaceholders(String message, MessageFormat format) {

        message = processColorCodes(message);

        final TagResolver resolver = MiniPlaceholders.getGlobalPlaceholders();

        return recursivelyExpandPlaceholders(message, resolver, format);

    }

    // Audience-context MiniPlaceholder expansion with all tags active.
    public static TextComponent expandTextWithPlaceholders(String message, Player player) {

        return expandTextWithPlaceholders(message, player, MessageFormat.full());

    }

    // Audience-context MiniPlaceholder expansion with the given format.
    public static TextComponent expandTextWithPlaceholders(String message, Player player, MessageFormat format) {

        message = processColorCodes(message);

        final TagResolver resolver = TagResolver.resolver(MiniPlaceholders.getGlobalPlaceholders(),
                MiniPlaceholders.getAudiencePlaceholders(player));

        return recursivelyExpandPlaceholders(message, resolver, format);

    }

    // Relational-context MiniPlaceholder expansion with all tags active.
    public static TextComponent expandTextWithPlaceholders(String message, Player player, Player target) {

        return expandTextWithPlaceholders(message, player, target, MessageFormat.full());

    }

    // Relational-context MiniPlaceholder expansion with the given format.
    public static TextComponent expandTextWithPlaceholders(String message, Player player, Player target,
            MessageFormat format)
    {

        message = processColorCodes(message);

        final TagResolver resolver = TagResolver.resolver(MiniPlaceholders.getGlobalPlaceholders(),
                MiniPlaceholders.getAudiencePlaceholders(player),
                MiniPlaceholders.getRelationalPlaceholders(player, target));

        return recursivelyExpandPlaceholders(message, resolver, format);

    }

    // Recursive placeholder expansion driven by the format's MiniMessage
    // instance plus the supplied placeholder resolver.
    private static TextComponent recursivelyExpandPlaceholders(String message, TagResolver resolver,
            MessageFormat format)
    {

        final MiniMessage mm = miniMessage(format);

        String previousMessage;
        Component expandedMessage;

        do {

            previousMessage = message;
            expandedMessage = mm.deserialize(message, resolver);

            message = mm.serialize(expandedMessage);

        } while (!message.equals(previousMessage));

        return (TextComponent) expandedMessage;

    }

    private static final Map<String, String> LEGACY_TO_MM_MAP = Map.ofEntries(Map.entry("0", "<black>"),
            Map.entry("1", "<dark_blue>"), Map.entry("2", "<dark_green>"), Map.entry("3", "<dark_aqua>"),
            Map.entry("4", "<dark_red>"), Map.entry("5", "<dark_purple>"), Map.entry("6", "<gold>"),
            Map.entry("7", "<gray>"), Map.entry("8", "<dark_gray>"), Map.entry("9", "<blue>"),
            Map.entry("a", "<green>"), Map.entry("b", "<aqua>"), Map.entry("c", "<red>"),
            Map.entry("d", "<light_purple>"), Map.entry("e", "<yellow>"), Map.entry("f", "<white>"),
            Map.entry("k", "<obfuscated>"), Map.entry("l", "<bold>"), Map.entry("m", "<strikethrough>"),
            Map.entry("n", "<underlined>"), Map.entry("o", "<italic>"), Map.entry("r", "<reset>"),
            Map.entry("*", "<rainbow>"));

    private static final Pattern LEGACY_REGEX = Pattern.compile("[§&]([0-9a-fk-or*])", Pattern.CASE_INSENSITIVE);

    // Converts legacy Bukkit color / formatting codes (&c, &l, &*, §c, §l, §*) into
    // MiniMessage markup. Unknown codes are left alone (ampersand preserved).
    public static String processColorCodes(String message) {

        return LEGACY_REGEX.matcher(message).replaceAll(match -> {

            String key = match.group(1).toLowerCase();
            return Matcher.quoteReplacement(LEGACY_TO_MM_MAP.getOrDefault(key, match.group(0)));

        });

    }

    // Back-compat strip: delegates to stripFormatting.
    public static String stripColors(String input) {

        return stripFormatting(input);

    }

    // Removes every legacy Bukkit code and every MiniMessage tag supported
    // by Utilities-OG, returning plain text.
    public static String stripFormatting(String input) {

        if (input == null) {

            return null;

        }

        final String processed = processColorCodes(input);
        final Component parsed = miniMessage(MessageFormat.full()).deserialize(processed);

        return PlainTextComponentSerializer.plainText().serialize(parsed);

    }

    // Flattens a Component tree to plain text, discarding every style and
    // tag. Useful for passing player-visible messages into systems that only
    // accept raw strings (logs, databases, etc)
    public static String stripFormatting(Component component) {

        if (component == null) {

            return null;

        }

        return PlainTextComponentSerializer.plainText().serialize(component);

    }

    // Permissions error helper.
    public static void permissionsErrorMessage(Player player, String command, String permission) {

        trueogMessage(player,
                "&cERROR: You do not have permission to run /" + command + ". &6Required permission: &e" + permission);

    }

    // Console logger that strips every supported tag and legacy code.
    public static void logToConsole(String message) {

        Bukkit.getLogger().info(stripFormatting(message));

    }

    // Obtain or spawn the MiniMessage instance configured for a given format.
    public static MiniMessage miniMessage(MessageFormat format) {

        return MINI_CACHE.computeIfAbsent(format, fmt -> MiniMessage.builder().tags(fmt.buildResolver()).build());

    }

    // Clear formatting selectively.
    private static Component applyFormat(Component component, MessageFormat format) {

        final Style.Builder style = component.style().toBuilder();

        if (!format.has(MessageFormat.Tag.COLOR)) {

            style.color(null);

        }

        if (!format.has(MessageFormat.Tag.DECORATIONS)) {

            for (TextDecoration decoration : TextDecoration.values()) {

                style.decoration(decoration, TextDecoration.State.NOT_SET);

            }

        }

        final Component out = component.style(style.build());

        final List<Component> children = out.children();
        if (children.isEmpty()) {

            return out;

        }

        final List<Component> rewritten = new ArrayList<>(children.size());
        children.forEach(child -> rewritten.add(applyFormat(child, format)));

        return out.children(rewritten);

    }

    // Identifies uppercase Bukkit color codes (e.g. &C, &L).
    private static boolean isUpperBukkitCode(char input) {

        return StringUtils.indexOf("ABCDEFKLMNOR", Character.toUpperCase(input)) != -1;

    }

    // Replaces a character at a given index with a replacement string.
    private static String replaceAtIndex(String original, int index, String newString) {

        if (index >= 0 && index < original.length()) {

            return StringUtils.substring(original, 0, index) + newString + StringUtils.substring(original, index + 1);

        }

        return original;

    }

}