// This is free and unencumbered software released into the public domain.
// Authors: NotAlexNoyle.
package net.trueog.utilitiesog.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.trueog.utilitiesog.UtilitiesOG;
import net.trueog.utilitiesog.utils.TextUtils;

// /phtest checks that other plugins' MiniPlaceholders resolve.
public class PlaceholderTestCommand implements TabExecutor {

    private static final String PERMISSION = "utilities.phtest";
    private static final String PLUGINS_KEY = "PlaceholderTestPlugins";
    private static final String PREFIX = "&8▍ &3PlaceholderTest &8▏ ";

    // Plugin name to its expected tags, in config order.
    private final Map<String, List<String>> expected;

    public PlaceholderTestCommand(Map<String, List<String>> expected) {

        this.expected = expected;

    }

    // Reads the list; an older config without it uses the bundled one.
    public static Map<String, List<String>> loadExpected(Configuration config) {

        final Map<String, List<String>> result = new LinkedHashMap<>();
        final Object own = config.get(PLUGINS_KEY, null);
        ConfigurationSection section = null;
        if (own instanceof ConfigurationSection ownSection) {

            section = ownSection;

        } else if (config.getDefaults() != null) {

            section = config.getDefaults().getConfigurationSection(PLUGINS_KEY);

        }

        if (section == null) {

            return result;

        }

        for (String pluginName : section.getKeys(false)) {

            final List<String> tags = section.getStringList(pluginName);
            if (!tags.isEmpty()) {

                result.put(pluginName, List.copyOf(tags));

            }

        }

        return result;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // The console may always run it; players need the node.
        if (sender instanceof Player player && !player.hasPermission(PERMISSION)) {

            TextUtils.permissionsErrorMessage(player, command.getName(), PERMISSION);
            return true;

        }

        if (args.length > 0 && args[0].equalsIgnoreCase("raw")) {

            return runRaw(sender, args);

        }

        final Player target = resolveTarget(sender, args.length > 0 ? args[0] : null);
        if (target == null) {

            return true;

        }

        if (expected.isEmpty()) {

            send(sender, "&cNothing to check: config.yml lists no plugins under " + PLUGINS_KEY + ".");
            return true;

        }

        send(sender, "&7Resolving placeholders for &f" + target.getName() + "&7.");

        final TagResolver global = MiniPlaceholders.getGlobalPlaceholders();
        final TagResolver audience = MiniPlaceholders.getAudiencePlaceholders(target);

        int missing = 0;
        for (Map.Entry<String, List<String>> entry : expected.entrySet()) {

            final String pluginName = entry.getKey();
            final Plugin owner = Bukkit.getPluginManager().getPlugin(pluginName);
            final String state = owner == null ? "&c(not installed)"
                    : owner.isEnabled() ? "&a(enabled)" : "&6(disabled)";
            send(sender, "&f" + pluginName + " " + state);

            for (String tag : entry.getValue()) {

                final boolean registered = audience.has(tag) || global.has(tag);
                if (!registered) {

                    missing++;

                }

                sender.sendMessage(describe(tag, registered, target));

            }

        }

        if (missing == 0) {

            send(sender, "&aAll placeholders registered.");

        } else {

            send(sender, "&c" + missing + " placeholder(s) not registered.");

        }

        return true;

    }

    // Expands any MiniMessage text, so unlisted tags can be checked too.
    private boolean runRaw(CommandSender sender, String[] args) {

        if (args.length < 2) {

            send(sender, "&cUsage: /phtest raw <text> [player]");
            return true;

        }

        // A trailing online player name picks the target.
        int end = args.length;
        Player target = null;
        if (args.length > 2) {

            final Player named = Bukkit.getPlayerExact(args[args.length - 1]);
            if (named != null) {

                target = named;
                end = args.length - 1;

            }

        }

        if (target == null) {

            target = resolveTarget(sender, null);
            if (target == null) {

                return true;

            }

        }

        final String text = String.join(" ", List.of(args).subList(1, end));
        final TextComponent expanded = UtilitiesOG.trueogExpand(text, target);

        send(sender, "&7Input: &f" + text);
        sender.sendMessage(UtilitiesOG.trueogColorize(PREFIX + "&7Output: ").append(expanded));
        send(sender, "&7Plain: &f\"" + UtilitiesOG.stripFormatting(expanded) + "\"");

        return true;

    }

    // One report line: <tag> -> rendered "plain" [status].
    private Component describe(String tag, boolean registered, Player target) {

        final String wrapped = "<" + tag + ">";
        final TextComponent expanded = UtilitiesOG.trueogExpand(wrapped, target);
        final String plain = UtilitiesOG.stripFormatting(expanded);

        final String status;
        if (!registered) {

            status = "&c[NOT REGISTERED]";

        } else if (plain.equals(wrapped)) {

            status = "&c[REGISTERED BUT UNRESOLVED]";

        } else if (plain.isEmpty()) {

            status = "&e[EMPTY]";

        } else {

            status = "&a[OK]";

        }

        // The escape keeps the tag itself from being expanded.
        return UtilitiesOG.trueogColorize("  &7\\" + wrapped + " &8-> &r").append(expanded)
                .append(UtilitiesOG.trueogColorize(" &8\"" + plain + "\" " + status));

    }

    // The named online player, else a player sender.
    private Player resolveTarget(CommandSender sender, String name) {

        if (name != null) {

            final Player named = Bukkit.getPlayerExact(name);
            if (named == null) {

                send(sender, "&cPlayer '" + name + "' is not online.");
                return null;

            }

            return named;

        }

        if (sender instanceof Player player) {

            return player;

        }

        send(sender, "&cConsole must name an online player: /phtest <player>");
        return null;

    }

    private void send(CommandSender sender, String message) {

        sender.sendMessage(UtilitiesOG.trueogColorize(PREFIX + message));

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        final List<String> out = new ArrayList<>();
        if (args.length != 1 || (sender instanceof Player player && !player.hasPermission(PERMISSION))) {

            return out;

        }

        final String typed = args[0].toLowerCase();
        if ("raw".startsWith(typed)) {

            out.add("raw");

        }

        for (Player online : Bukkit.getOnlinePlayers()) {

            if (online.getName().toLowerCase().startsWith(typed)) {

                out.add(online.getName());

            }

        }

        return out;

    }

}
