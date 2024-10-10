# Utilities-OG

A collection of utilities and APIs used by [TrueOG Network](https://true-og.net/). Developed by christianniehaus & NotAlexNoyle.

Current Target: Purpur 1.19.4

## Building:

./gradlew build

The resulting .jar file will be in build/libs

At the moment, any config change requires a server restart to take effect.

## Features:

* **Mock Bamboo:** Enables crafting mock bamboo planks from 2x2 bamboo and mock bamboo wood from 2x2 mock bamboo planks.
* **Chain Armor:** Enables crafting chain armor from chains.
* **Color Codes:** Displays information to players about Bukkit's chat color codes and their syntax.
* **MiniPlaceholderAPI:** Enables a custom, easy-to-use MiniPlaceholders API (**Note:** May require additional configuration for specific placeholders).
* **NoFlippy:** Prevents trapdoors from being flipped in WorldGuard regions where the "can-flippy" flag is set to DENY (Requires WorldGuard plugin).
* **TogglePhantoms:** Allows players to use the `/togglephantoms` command to turn phantom spawning on or off for themselves.
* **Ping:** Provides two commands: `/ping` (displays real player ping) and `/bing` (replicates vanilla /ping functionality).
* **Ranks Menu:** Displays information to players about available ranks at the [TrueOG Network Store](https://store.true-og.net).

**Warning:**

* **DisableEntityCramming (Default: false):** Disables entity cramming damage server-wide. This can cause lag if no other plugin manages entity limits. Enable this Module only if you have a separate plugin managing entity limitations.

## Permissions:

* **`utilities.about`**
    * **Description:** Allows players to use the base `/utilities` command to get information about the plugin.
    * **Default:** `op` (Server Operator)
* **`utilities.colorcodes`**
    * **Description:** Grants permission to use the `/colorcodes` command to view Bukkit chat color code information.
    * **Default:** `op` (Server Operator)
* **`utilities.togglephantoms`**
    * **Description:** Allows players to use the `/togglephantoms` command to enable or disable phantom spawning for themselves.
    * **Default:** `op` (Server Operator)
* **`utilities.togglecramming`**
    * **Description:** Grants permission to use the `/togglecramming` command (if enabled) to manage entity cramming damage.
    * **Default:** `op` (Server Operator)
* **`utilities.ranks`**
    * **Description:** Allows players to use the `/ranks` command to view information about available ranks on the TrueOG Network Store.
    * **Default:** `op` (Server Operator)
* **`utilities.ping`**
    * **Description:** Grants permission to use the `/ping` (real ping) and `/bing` (mimics vanilla ping) commands.
    * **Default:** `op` (Server Operator)

**Please note:** These permissions can be modified in the Utilities-OG configuration file config.yml.

### API Documentation:

**[void] trueogMessage(Player player, String message)**

Sends a message to the specified player with TrueOG formatting. Supports both modern color codes and legacy Bukkit color codes (case-insensitive). Automatically expands MiniPlaceholders within the message.

Kotlin:
```kotlin
val targetPlayer = Bukkit.getPlayer("USERNAME")
UtilitiesOG.trueogMessage(targetPlayer, "&6This is a &*message with <green>True&4OG <bold>formatting!")
```

Java:
```java
Player targetPlayer = Bukkit.getPlayer("USERNAME");
UtilitiesOG.trueogMessage(targetPlayer, "&6This is a &*message with <green>True&4OG <bold>formatting!");
```

**[void] trueogMessage(UUID playerUUID, String message)**

Sends a message to a player identified by their UUID with TrueOG formatting. Useful when the Bukkit Player object is not available. Supports modern color codes and legacy Bukkit color codes (case-insensitive), and expands MiniPlaceholders within the message.

Kotlin:
```kotlin
UUID playerUUID = UUID.fromString("player_uuid");
UtilitiesOG.trueogMessage(playerUUID, "&6Hello there!");
```

Java:
```java
UUID playerUUID = UUID.fromString("player_uuid");
UtilitiesOG.trueogMessage(playerUUID, "&6Hello there!");
```

**[TextComponent] trueogExpand(String message)**

Expands MiniPlaceholders within a message string without any player context. Supports color code processing via TrueOG's formatting.

Kotlin:
```kotlin
val message = "Welcome to the server, everyone!"
val expandedMessage = UtilitiesOG.trueogExpand(message)
```

Java:
```java
String message = "Welcome to the server, everyone!";
TextComponent expandedMessage = UtilitiesOG.trueogExpand(message);
```

**[TextComponent] trueogExpand(String message, Player player)**

Expands MiniPlaceholders within a message string using the provided player's context. Supports color code processing.

Kotlin:
```kotlin
val targetPlayer = Bukkit.getPlayer("SomePlayer")
val message = "Welcome back, <player_display_name>!"
val expandedMessage = UtilitiesOG.trueogExpand(message, targetPlayer)
UtilitiesOG.trueogMessage(targetPlayer, expandedMessage)
```

Java:
```java
Player targetPlayer = Bukkit.getPlayer("SomePlayer");
String message = "Welcome back, <player_display_name>!";
TextComponent expandedMessage = UtilitiesOG.trueogExpand(message, targetPlayer);
UtilitiesOG.trueogMessage(targetPlayer, expandedMessage);
```

**[TextComponent] trueogExpand(String message, Player player, Player target)**

Expands MiniPlaceholders within a message string using both the sender's and the target's player contexts, which is useful for relational placeholders. Supports color code processing.

Kotlin:
```kotlin
val player = Bukkit.getPlayer("Player")
val target = Bukkit.getPlayer("TargetPlayer")
val message = "<player_display_name> is sending a message to <target_display_name>"
val expandedMessage = UtilitiesOG.trueogExpand(message, player, target)
UtilitiesOG.trueogMessage(player, expandedMessage)
```

Java:
```java
Player player = Bukkit.getPlayer("Player");
Player target = Bukkit.getPlayer("TargetPlayer");
String message = "<player_display_name> is sending a message to <target_display_name>";
TextComponent expandedMessage = UtilitiesOG.trueogExpand(message, player, target);
UtilitiesOG.trueogMessage(player, expandedMessage);
```

**[TextComponent] trueogExpand(String message, UUID playerUUID)**

Expands MiniPlaceholders within a message string using a player's UUID, useful when the Player object isn't available. Supports color code processing.

Kotlin:
```kotlin
val playerUUID = UUID.fromString("player_uuid")
val message = "Welcome back, <player_display_name>!"
val expandedMessage = UtilitiesOG.trueogExpand(message, playerUUID)
UtilitiesOG.trueogMessage(playerUUID, expandedMessage)
```

Java:
```java
UUID playerUUID = UUID.fromString("player_uuid");
String message = "Welcome back, <player_display_name>!";
TextComponent expandedMessage = UtilitiesOG.trueogExpand(message, playerUUID);
UtilitiesOG.trueogMessage(playerUUID, expandedMessage);
```

**[void] registerGlobalPlaceholder(String placeholderName, {String})**

Registers a global MiniPlaceholder that can be used anywhere in your messages. The valueSupplier provides the placeholder's value at runtime. Prefixes are the values before the first underscore. For example, the prefix of server_name is server.

Kotlin:
```kotlin
UtilitiesOG.registerGlobalPlaceholder("server_name") { "&aTrue&cOG &eNetwork" }
```

Java:
```java
UtilitiesOG.registerGlobalPlaceholder("server_name", () -> "&aTrue&cOG &eNetwork");
```

**[void] registerAudiencePlaceholder(String name, player -> {String})**

Registers an audience-specific MiniPlaceholder. The valueFunction uses the player's context to generate a dynamic value for the placeholder. Prefixes are the values before the first underscore. For example, the prefix of player_display_name is player.

Kotlin:
```kotlin
UtilitiesOG.registerAudiencePlaceholder("player_display_name") { player ->
    val luckPerms = LuckPermsProvider.get()
    val user = luckPerms.getUserManager().getUser(player.uniqueId)
    val prefix = user?.cachedData?.metaData?.prefix ?: ""
    "$prefix ${player.name}"
}
```

Java:
```java
UtilitiesOG.registerAudiencePlaceholder("player_display_name", player -> {
    LuckPerms luckPerms = LuckPermsProvider.get();
    User user = luckPerms.getUserManager().getUser(player.getUniqueId());
    String prefix = user != null ? user.getCachedData().getMetaData().getPrefix() : "";
    return prefix + " " + player.getName();
});
```

**[void] registerRelationalPlaceholder(String name, player, target -> {String})**

Registers a relational MiniPlaceholder that depends on two players (e.g., viewer and target). The valueFunction provides a dynamic value based on both players' contexts. Prefixes are the values before the first underscore. For example, the prefix of distance_between is distance.

Kotlin:
```kotlin
UtilitiesOG.registerRelationalPlaceholder("distance_between") { player, target ->
    val distance = player.location.distance(target.location)
    "Distance: ${distance.toInt()} blocks"
}
```

Java:
```java
UtilitiesOG.registerRelationalPlaceholder("distance_between", (player, target) -> {
    double distance = player.getLocation().distance(target.getLocation());
    return "Distance: " + (int) distance + " blocks";
});
```

**[TextComponent] trueogColorize(String message)**

Formats a String into a TextComponent. Supports modern and legacy color codes. Does not include custom MiniPlaceholder Expansion logic.

Kotlin:
```kotlin
val colorized = UtilitiesOG.trueogColorize(message)
```

Java:
```java
TextComponent colorized = UtilitiesOG.trueogColorize(message);
```

**[String] stripFormatting(String content)**

Strips legacy and modern color codes and formatting from a String.

Kotlin:
```kotlin
val unformatted = UtilitiesOG.stripFormatting(content)
```

Java:
```java
String unformatted = UtilitiesOG.stripFormatting(content);
```

**[void] logToConsole(Plugin plugin, String message)**

Logs a message to the server console with the prefix of the calling plugin. The message will have color codes and formatting stripped.

Kotlin:
```kotlin
UtilitiesOG.logToConsole(plugin, "&6This is a &*message with <green>color codes!")
```

Java:
```java
UtilitiesOG.logToConsole(plugin, "&6This is a &*message with <green>color codes!");
```

**[BukkitTask] runTaskAsynchronously(final Runnable run)**

Schedules a task to run asynchronously using the Bukkit scheduler under the UtilitiesOG plugin.

Kotlin:
```kotlin
val asyncTask = UtilitiesOG.runTaskAsynchronously {
    // Your async code here
}
```

Java:
```java
BukkitTask asyncTask = UtilitiesOG.runTaskAsynchronously(() -> {
    // Your async code here
});
```