# Health Indicator

**English** | [한국어](README.ko.md)

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

| Key | Default | Description |
|---|---|---|
| `enabled` | `true` | On/off. Can also be toggled with the "Toggle Health Indicator" key in Controls (unbound by default). |
| `look` | `NUMBER` | `NUMBER`, `NUMBER_MAX`, `HEARTS`, `BAR`, `PERCENT` |
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
