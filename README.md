# Health Indicator

**English** | [한국어](README.ko.md)

> **Official release: [1.2.0](https://github.com/cerbagge/healthindicator/releases/tag/v1.2.0)**  
> `healthindicator-mc1.20.1-1.2.0.jar` SHA-256: `50b8eaaba9f40dbad738953f30b8b43ae81c70b894b04e8ff20361effd5d166d`  
> Any file with a different hash is not an official release.

A client-side Minecraft 1.20.1 Fabric mod that shows health above players and mobs.

**Health Indicator by cerbagge**

## Features

- Choose how health is shown: number (`❤ 15`), number with max (`15 / 20`), heart icons, bar, or percent.
- The position is fixed just above the name tag (or just above the head when there is no name tag) and cannot be moved.
- Configure absorption hearts, health-based color (green → red), text size, and view distance.
- Toggle players, mobs/animals, and server player-shaped NPCs separately.

## No ESP: it never reveals players you cannot see

This mod only shows the health of entities that are visible on your screen. Health is not shown in these cases:

| Case | Behavior |
|---|---|
| Behind walls | Text and hearts are drawn with depth testing, so they never show through walls. |
| Body blocked by blocks | If none of the head, chest or feet is visible from the camera, nothing is drawn, so the label floating above the head cannot peek over a wall. |
| Invisible (Potion of Invisibility) | Not drawn. |
| Name tag hidden by the server | Not drawn when the team rule (`nametagVisibility`) hides the name tag. |
| Sneaking players | Not drawn. |
| HUD hidden with F1 | Not drawn. |

Coordinates, distance and direction are never displayed.

> Server rules differ. Please check the rules of the server you play on.

## Installation

1. Install Fabric Loader 0.16.10 or newer and Fabric API.
2. Put the jar from [Releases](https://github.com/cerbagge/healthindicator/releases) into your `mods` folder.

## Configuration

`config/healthindicator.json` is created on first launch. Edit it while the game is closed, then start the game again.

### How to configure

1. Launch the game once and close it. This creates the config file.
2. Open the `config` folder in your game directory.
   - Vanilla launcher: `%appdata%\.minecraft\config` on Windows, `~/Library/Application Support/minecraft/config` on macOS, `~/.minecraft/config` on Linux
   - Other launchers (Prism, CurseForge, Modrinth, etc.): the `config` folder inside the instance folder
3. Open `healthindicator.json` in a text editor such as Notepad.
4. Change the values you want and save. Keep the quotes (`"`) and commas (`,`) as they are; change only the values.
5. Start the game again.

Example: show health as a number, also show mob health, and make the text a bit larger

```json
{
  "enabled": true,
  "look": "NUMBER",
  "scale": 120,
  "distance": 32,
  "players": true,
  "entities": true,
  "npcs": false,
  "self": false,
  "hideFull": false,
  "absorption": true,
  "gradient": true,
  "color": 16733525
}
```

What each `look` value shows:

| Value | Shows |
|---|---|
| `HEARTS` | Heart icons (10 per row, up to 2 rows). Large mobs needing more than 20 hearts are shown as text, e.g. `❤ × 25` |
| `NUMBER` | `❤ 15` |
| `NUMBER_MAX` | `15 / 20` |
| `BAR` | A 10-segment bar |
| `PERCENT` | `75%` |

Notes:

- If the file can't be read (for example, a typo), the mod starts with default values and rewrites the file with defaults. Making a backup before editing is a good idea.
- `scale` (50 to 200) and `distance` (4 to 64) are clamped to the nearest allowed value.
- An existing config file keeps its values. Files created by older versions still have `look` set to `NUMBER`; to get heart icons, change it to `HEARTS` or delete the file and launch again.
- You can also toggle the display in game: Options → Controls → Key Binds, then assign a key to "Toggle Health Indicator" (unbound by default).

### Keys

| Key | Default | Description |
|---|---|---|
| `enabled` | `true` | On/off. Can also be toggled with the "Toggle Health Indicator" key in Controls (unbound by default). |
| `look` | `HEARTS` | `NUMBER`, `NUMBER_MAX`, `HEARTS`, `BAR`, `PERCENT` |
| `scale` | `100` | Text size (%, 50 to 200) |
| `distance` | `32` | View distance (blocks, 4 to 64) |
| `players` | `true` | Player health |
| `entities` | `false` | Mob/animal health |
| `npcs` | `false` | Health of server player-shaped NPCs |
| `self` | `false` | Your own health in third person |
| `hideFull` | `false` | Hide when health is full |
| `absorption` | `true` | Show absorption hearts |
| `gradient` | `true` | Color by health |
| `color` | `16733525` | Text color when `gradient` is off (RGB integer, 16733525 = `#FF5555`) |

## Building

Run Gradle with JDK 21 (the mod itself targets Java 17).

```
./gradlew build
```

## License

Custom license. See [LICENSE](LICENSE) for the full terms.

- **Attribution required**: credit "Health Indicator by cerbagge" with the repository URL when introducing or mentioning it.
- **No redistribution**: original or modified, including re-uploads, modpacks and launchers.
- **No commercial use**
- **No modification**: no modifications, derivative works, or copying code into other projects.

Only viewing the source and personal, non-commercial use of files officially released in this repository are permitted.
