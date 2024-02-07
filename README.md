# AreaSoundEvents Plugin Documentation

## Table of Contents

1. [Introduction](#introduction)
2. [Features](#features)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Commands](#commands)
6. [Permissions](#permissions)
7. [Usage](#usage)
    - [Creating Region Sound Events](#creating-region-sound-events)
    - [Viewing Region Sound Events](#viewing-region-sound-events)
    - [Deleting Region Sound Events](#deleting-region-sound-events)
8. [Troubleshooting](#troubleshooting)
9. [Credits](#credits)

## Introduction

AreaSoundEvents is a Bukkit/Spigot plugin designed to add region-based sound events to your Minecraft server. This plugin allows server administrators to define regions within the game world and associate specific sounds, sources, volumes, and pitches with those regions. Players within these regions will hear the defined sound effects, creating immersive audio experiences within your Minecraft server.

## Features

- Define regions within the game world.
- Associate sounds with specific regions.
- Adjust source, volume and pitch of sounds per region.
- Support for paginated commands.
- Interactive navigation for paginated commands.

## Installation

1. Download the latest version of the AreaSoundEvents plugin JAR file from [SpigotMC](https://www.spigotmc.org/resources/areasoundevents.12345/).
2. Place the downloaded JAR file in the `plugins` folder of your Bukkit/Spigot server.
3. Restart or reload your server to enable the plugin.

## Configuration

AreaSoundEvents provides a configuration file (`config.yml`) where you can customize various settings for the plugin:

- **Default Sound Settings**: Configure default sound settings such as volume and pitch.
- **Default Region Sound Events**: Define sound events for specific regions, including the sound to be played, its volume, and pitch.
- **Other Settings**: Configure default settings as silent mode, debug mode or language translation.

AreaSoundEvents also allows you to modify and add regions from the file (`regions.yml`), you will be able to reload the settings in the game without needing to restart the server:

#### YAML Regions Example

```yaml
regions: # Don't touch! is for plugin proposals
  areasoundevents: # The region's id
    name: areasoundevents # Region's name (has to be associate with the id)
    sound: minecraft:music.end # The sound which will be reproduced
    source: MUSIC # The source that sound plays (master, music, records...)
    volume: 0.5 # The volume of the sound (0~1)
    pitch: 1 # The pitch of the sound (0~1)
```

This region is defined to play the sound `minecraft:music.end` with a volume of `0.5` and a pitch of `1` from the source `MUSIC`.

## Commands

AreaSoundEvents plugin provides the following commands:

### Main Commands

- **/areasoundevents**: Main command for the plugin.

### Sub Commands

- **/areasoundevents help**: Display the available commands with descriptions and syntax.
- **/areasoundevents reload**: Reloads the plugin configuration.
- **/areasoundevents list**: Lists all defined regions and their associated sound events.
- **/areasoundevents save**: Save the current created configuration in 'regions.yml' file.
- **/areasoundevents create \<regionName> \<soundName> \<source> \<volume> \<pitch>**: Create a new region with the specified sound event.
- **/areasoundevents remove \<regionName>**: Deletes the specified region and its associated sound event.
- **/areasoundevents modify {region-name} name=\<regionName> sound=\<soundName> source=\<source> volume=\<volume> pitch=\<pitch>**: Adjusts the volume of the sound event for the specified region.

## Permissions

- **areasoundevents.admin**: Allows access to all plugin commands and features.
- **areasoundevents.create**: Permission to create new region sound events.
- **areasoundevents.delete**: Permission to delete existing region sound events.
- **areasoundevents.help**: Permission to display all commands descriptions and syntax.
- **areasoundevents.modify**: Permission to modify the region parameters.
- **areasoundevents.remove**: Permission to remove a region.
- **areasoundevents.reload**: Permission to reload the plugin configuration.
- **areasoundevents.list**: Permission to list defined regions and their associated sound events.

## Usage

### Creating Region Sound Events

To create a new region sound event, use the following command:

```css
/areasoundevents create <region> <sound> [source] [volume] [pitch]
```

- `<region>`: Name of the region where the sound event will be triggered.
- `<sound>`: ID of the Minecraft sound to be played.
- `[source]`: Optional. Sound source category (e.g., MASTER, MUSIC, AMBIENT).
- `[volume]`: Optional. Volume level of the sound (0.0 to 1.0).
- `[pitch]`: Optional. Pitch of the sound (0.0 to 1.0).

### Viewing Region Sound Events

To view existing region sound events, use the following command:

```bash
/areasoundevents list
```

This command will display a list of all defined region sound events.

### Deleting Region Sound Events

To delete a region sound event, use the following command:

```arduino
/areasoundevents remove <region>
```

- `<region>`: Name of the region sound event to delete.

## Troubleshooting

If you encounter any issues or have questions about using the AreaSoundEvents plugin, please refer to the following resources:

- [Plugin Documentation](https://pluginwebsite.com/documentation)
- [Support Forums](https://pluginwebsite.com/support)
- [Bug Reporting](https://pluginwebsite.com/bugs)
- [SpigotMC](https://www.spigotmc.org/resources/areasoundevents.12345/)

## Credits

AreaSoundEvents Plugin was developed by @LavinyTuttini and is available under the [MIT License](https://opensource.org/licenses/MIT).
