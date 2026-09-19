# PersianChatFixer

[![Build](https://github.com/TheFaRHaNiR/PersianChatFixer-Paper/actions/workflows/build.yml/badge.svg)](https://github.com/TheFaRHaNiR/PersianChatFixer-Paper/actions/workflows/build.yml)
![Paper](https://img.shields.io/badge/Paper-26.1.2%2B-blue)
![Folia](https://img.shields.io/badge/Folia-supported-brightgreen)
![Java](https://img.shields.io/badge/Java-25-orange)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

The Minecraft client doesn't handle right-to-left text. Persian and Arabic show up with the letters disconnected and in reverse order. PersianChatFixer fixes chat messages and signs on the server, so every player sees them correctly with no client mod.

This is a Paper/Folia port of the PocketMine-MP plugin [PersianChatFixer](https://github.com/TheFaRHaNiR/PersianChatFixer).

## Features

- **Chat:** reshapes Persian and Arabic letters (isolated, initial, medial and final forms, including the `لا` ligature) and reverses the text so it displays correctly.
- **Signs:** fixes the same text on signs. Lines longer than 14 characters wrap automatically, up to 4 lines.
- **Keeps your formatting:** leaves `§` color codes, Latin words, numbers, brackets `() [] {} <>` and leading symbols such as `>` or `[` as they are.
- **No setup:** there are no commands, config or permissions. Drop the jar in and it works.
- **Folia-ready:** one jar runs on both Paper and Folia.

## Requirements

| | Version |
|---|---|
| Server | Paper or Folia **26.1.2+** |
| Java | **25+** |

## Installation

1. Download the latest jar from [Releases](https://github.com/TheFaRHaNiR/PersianChatFixer-Paper/releases). You can also get it from the artifacts of the latest successful [Build](https://github.com/TheFaRHaNiR/PersianChatFixer-Paper/actions/workflows/build.yml) run.
2. Put it in your server's `plugins/` folder.
3. Restart the server.

## Folia support

`plugin.yml` sets `folia-supported: true`. The plugin is safe on Folia's regionized threading for these reasons:

- Each listener only changes the event it receives. `AsyncChatEvent` runs on the chat thread and `SignChangeEvent` runs on the region thread that owns the sign.
- The text engine is stateless. Its glyph tables are built once when the class loads and are only read after that.
- It doesn't use the scheduler, doesn't touch the world and doesn't share any mutable state.

## Building from source

You need JDK 25 and Maven:

```bash
git clone https://github.com/TheFaRHaNiR/PersianChatFixer-Paper.git
cd PersianChatFixer-Paper
mvn -B package
```

The jar is written to `target/PersianChatFixer-<version>.jar`.

### CI

[GitHub Actions](.github/workflows/build.yml) builds the plugin on every push and pull request and uploads the jar as a build artifact. When you push a tag that starts with `v` (for example `v3.1.1`), it also creates a GitHub Release with the jar attached.

## Project layout

```
src/main/
├── java/TheFaRHaNiR/PersianChatFixer/
│   ├── Main.java               # Chat and sign listeners
│   └── PersianTextEngine.java  # Letter shaping and RTL reordering
└── resources/plugin.yml
```

## License

[MIT](LICENSE) © 2025 [TheFaRHaNiR](https://github.com/TheFaRHaNiR)
