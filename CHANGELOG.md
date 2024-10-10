**1.6.1**
- Log API MiniPlaceholder creation to the console.
- trueogColorize API.

**1.6:**
- Add support for declaring MiniPlaceholders with arguments.
- stripFormatting API.

**1.5.2:**
- Fix declaring MiniPlaceholders from external plugins.

**1.5.1:**
- Improve API documentation.
- Fix trueogMessage().

**1.5:**
- Create audience, global, and relative MiniPlaceholders in one new, simple API.
- Expand and format MiniPlaceholders within MiniPlaceholders.
- First 1.19 Stable Release.

**1.4.7:**
- Expand global and relational placeholders.

**1.4.6:**
- logToConsole API.

**1.4.5:**
- LuckPerms colors for <player_display_name>.

**1.4.4:**
- Added UUID processing for API use outside of a bukkit context.

**1.4.3:**
- Revised APIs for better usability.
- Updated gradle / dependencies.

**1.4.2:**
- Implemented trueOGUnregisterMiniPlaceholder API.
- Implemented isMiniPlaceholderRegistered API.
- Tested/Fixed other APIs.

**1.4.1:**
- Implemented AUDIENCE type MiniPlaceholder registration API.
- Implemented MiniPlaceholder unregistration API.
- Implemented runTaskAsynchronously API.
- Implemented trueogColorize API.
- Refactored existing APIs.

**1.4:**

- Added TrueOG Message API (supports modern + legacy formatting in the same message, &* for rainbow, and MiniPlaceholder expansion).
- Added TrueOG MiniPlaceholderAPI (currently supports expansion. Creation coming later.)
- Added MiniPlaceholder <player_display_name> (LuckPerms colors coming later.)
- Added mock Bamboo Planks.
- Mock Bamboo Wood is now crafted from 2x2 mock Bamboo Planks.
- Added permissions for every command.
- Major Refactoring.
- Improved error handling.
- Improved documentation.
- Improved config file.

**1.3.3:**

- Update gradle build.

**1.3.2**:
- Refactor phantom toggle logic.
- Enable phantom spawning by default.
- Fix Jitpack build.

**1.3.1:**
- Make bamboo wood named bamboo wood.

**1.3:**

- Added new /ping command. Supports ping lookup for self and others.
- Added /bing command to replicate the functionality of the old /ping command.
- Renamed /buy to /ranks for CraftingStore compatibility.
- Copied sub-commands as root commands.
- New per-command permissions (described in README.md).
- Added ranks menu and ping API.

**1.2:**

- Add "bamboo" wood recipe.

**1.1:**

- Added about message.
- Fixed NullPointerException.
- Expanded documentation.
- Improved chat formatting.
- Block per-player commands from console.
- Merged in and deprecated Buy-OG.
