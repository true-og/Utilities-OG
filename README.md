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

**[void] trueOGMessage(Player player, String message)**

Send a message to the player with TrueOG formatting. Supports modern color codes and legacy Bukkit color codes (case-insensitive).

Kotlin:
```kotlin
val targetPlayer = Bukkit.getPlayer("USERNAME")
UtilitiesOG.trueOGMessage(targetPlayer, "&6This is a &*message with <green>True&4OG <bold>formatting!")
```

Java:
```java
Player targetPlayer = Bukkit.getPlayer("USERNAME");
UtilitiesOG.trueOGMessage(targetPlayer, "&6This is a &*message with <green>True&4OG <bold>formatting!");
```

**[void] trueogMessage(UUID playerUUID, String message)**

Send a message to the player with TrueOG formatting in environments where the Bukkit API is not available. Supports modern color codes and legacy Bukkit color codes (case-insensitive).

Kotlin:
```kotlin
val playerUUID = UUID.fromString("player_uuid")
UtilitiesOG.trueogMessage(playerUUID, "&6Hello there!")
```

Java:
```java
UUID playerUUID = UUID.fromString("player_uuid");
UtilitiesOG.trueogMessage(playerUUID, "&6Hello there!");
```

**[TextComponent] trueOGExpandMiniPlaceholders(Player player, String input)**

Expands MiniPlaceholders within a string for the given player, with support for color code processing via trueogColorize().

Kotlin:
```kotlin
val targetPlayer = Bukkit.getPlayer("SomePlayer")
val message = "Welcome back, <player_display_name>!"
val expandedMessage = UtilitiesOG.trueOGExpandMiniPlaceholders(targetPlayer, message)
UtilitiesOG.trueOGMessage(targetPlayer, expandedMessage)
```

Java:
```java
Player targetPlayer = Bukkit.getPlayer("SomePlayer");
String message = "Welcome back, <player_display_name>!";
TextComponent expandedMessage = UtilitiesOG.trueOGExpandMiniPlaceholders(targetPlayer, message);
UtilitiesOG.trueOGMessage(targetPlayer, expandedMessage);
```

**[TextComponent] trueogExpandMiniPlaceholders(UUID playerUUID, String input)**

Expands MiniPlaceholders within a string for the given player, with support for color code processing via trueogColorize() in environments where the Bukkit API is not available.

Kotlin:
```kotlin
val playerUUID = UUID.fromString("player_uuid")
val input = "Welcome back, <player_display_name>!"
val expandedMessage = UtilitiesOG.trueogExpandMiniPlaceholders(playerUUID, input)
UtilitiesOG.trueOGMessage(playerUUID, expandedMessage)
```

Java:
```java
UUID playerUUID = UUID.fromString("player_uuid");
String input = "Welcome back, <player_display_name>!";
TextComponent expandedMessage = UtilitiesOG.trueogExpandMiniPlaceholders(playerUUID, input);
UtilitiesOG.trueOGMessage(playerUUID, expandedMessage);
```

**[TextComponent] trueogColorize(String message)**

Converts a message string into a formatted TextComponent using both legacy and modern color codes, along with MiniMessage processing.

Kotlin:
```kotlin
val myMessage = UtilitiesOG.trueogColorize("&6This is a &*message with <green>True&4OG <bold>formatting!")
```

Java:
```java
TextComponent myMessage = UtilitiesOG.trueogColorize("&6This is a &*message with <green>True&4OG <bold>formatting!");
```

**[void] trueOGRegisterMiniPlaceholder(String placeholderName, PlaceholderType placeholderType, String content)**

Creates and registers a MiniPlaceholder with the specified name and type. The content can include global MiniPlaceholders that will be expanded during registration. Placeholder Types include AUDIENCE, RELATIVE, and GLOBAL.

Kotlin:
```kotlin
val placeholderName = "example_placeholder"
val placeholderType = PlaceholderType.STRING
val content = "Hello, <player_name>!"
UtilitiesOG.trueOGRegisterMiniPlaceholder(placeholderName, placeholderType, content)
```

Java:
```java
String placeholderName = "example_placeholder";
PlaceholderType placeholderType = PlaceholderType.STRING;
String content = "Hello, <player_name>!";
UtilitiesOG.trueOGRegisterMiniPlaceholder(placeholderName, placeholderType, content);
```

**[void] trueOGUnregisterMiniPlaceholder(String placeholderName)**

Unregisters a previously registered MiniPlaceholder by its name.

Kotlin:
```kotlin
val placeholderName = "example_placeholder"
UtilitiesOG.trueOGUnregisterMiniPlaceholder(placeholderName)
```

Java:
```java
String placeholderName = "example_placeholder";
UtilitiesOG.trueOGUnregisterMiniPlaceholder(placeholderName);
```

**[boolean] isMiniPlaceholderRegistered(String placeholderName)**

Checks if a MiniPlaceholder is already registered based on its name.

Kotlin:
```kotlin
val placeholderName = "example_placeholder"
val isRegistered = UtilitiesOG.isMiniPlaceholderRegistered(placeholderName)
```

Java:
```java
String placeholderName = "example_placeholder";
boolean isRegistered = UtilitiesOG.isMiniPlaceholderRegistered(placeholderName);
```

**[BukkitTask] runTaskAsynchronously(final Runnable run)**

Runs a task asynchronously as UtilitiesOG using the BukkitTask API.

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

## TODO:

- LuckPerms colors for <player_display_name>.

- Console logging API with formatting stripping.

- Global Placeholder Registration.

- Relative Placeholder Registration.
