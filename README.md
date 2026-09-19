# PersianChatFixer — Paper Port

Port of the PocketMine-MP plugin [PersianChatFixer](https://github.com/TheFaRHaNiR/PersianChatFixer) (v3.1.1) to **Paper** (latest, `26.2.build.121-stable`, api-version `26.2`, Java 25).

## What it does
- Fixes Persian/Arabic text in chat: applies correct contextual letter shaping (isolated/initial/medial/final forms, including the لا ligature) and reverses the text so it renders correctly in the Minecraft client.
- Does the same for signs, wrapping lines longer than 14 chars (max 4 lines).
- Preserves color codes (`§`), leading symbols like `><[]`, brackets `()[]{}<>`, Latin words, and numbers.

## Folia
Runs on both **Paper** and **Folia** (`folia-supported: true` in `plugin.yml`). Both listeners only rewrite the event they receive: `AsyncChatEvent` runs on the chat thread and `SignChangeEvent` on the sign's region thread. The text engine is stateless, so no scheduler is needed and the same jar works on both.

## Build
GitHub Actions builds it automatically (`.github/workflows/build.yml`) — artifact `PersianChatFixer` contains the jar.

To build locally instead (requires JDK 25 + Maven):

```
mvn -B package --file PersianChatFixer-Paper/pom.xml
```

Jar lands in `PersianChatFixer-Paper/target/`. Drop it into `plugins/`.

## Project layout
```
PersianChatFixer-Paper/
├── pom.xml
└── src/main/
    ├── java/TheFaRHaNiR/PersianChatFixer/
    │   ├── Main.java              # Chat + sign listeners (was Main.php)
    │   └── PersianTextEngine.java # Shaping + RTL engine (was PersianTextEngine.php)
    └── resources/plugin.yml
```

## Notes on the port
- Original: MIT License © 2025 TheFaRHaNiR.
- PHP `mb_str_split`/glyph logic ported 1:1, using the same glyph tables and the same `[isolated, initial, medial, final]` ordering.
- PocketMine `PlayerChatEvent` → Paper `AsyncChatEvent` (message round-tripped through the legacy `§` serializer so the engine handles colors exactly like PHP).
- PocketMine `SignChangeEvent` → Bukkit `SignChangeEvent` (Component `line(i, …)` API; lines >14 code points wrapped, first 4 kept, rest cleared — same as the PHP `SignText` behavior).
- Latin-phrase merging across spaces and bracket-token handling replicate the PHP behavior exactly.
