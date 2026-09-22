/*
 *
 * MIT License - Copyright (c) 2025 TheFaRHaNiR
 * Permission is granted to use, copy, modify, and distribute this software,
 * provided the copyright notice and this permission notice are included.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 *
 * @Author: TheFaRHaNiR
 * @Link: https://github.com/TheFaRHaNiR
 *
 */

package TheFaRHaNiR.PersianChatFixer;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static final int MAX_SIGN_LINES = 4;
    private static final int SIGN_LINE_WIDTH = 14;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    // Paper fires the legacy chat event before AsyncChatEvent and builds the modern
    // message from it, so fixing the text here, at LOWEST, reaches every chat plugin:
    // ones that cancel and re-broadcast on the legacy event, and ones on AsyncChatEvent.
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        String processed = PersianTextEngine.process(message);
        if (!processed.equals(message)) {
            event.setMessage(processed);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSignChange(SignChangeEvent event) {
        List<String> wrappedLines = new ArrayList<>();
        for (Component line : event.lines()) {
            String text = line == null ? "" : LEGACY.serialize(line);
            if (text.codePointCount(0, text.length()) > SIGN_LINE_WIDTH) {
                int[] cps = text.codePoints().toArray();
                for (int start = 0; start < cps.length; start += SIGN_LINE_WIDTH) {
                    int end = Math.min(start + SIGN_LINE_WIDTH, cps.length);
                    wrappedLines.add(new String(cps, start, end - start));
                }
            } else {
                wrappedLines.add(text);
            }
        }

        List<String> processedLines = new ArrayList<>();
        int count = Math.min(MAX_SIGN_LINES, wrappedLines.size());
        for (int i = 0; i < count; i++) {
            processedLines.add(PersianTextEngine.process(wrappedLines.get(i)));
        }

        for (int i = 0; i < MAX_SIGN_LINES; i++) {
            event.line(i, i < processedLines.size() ? LEGACY.deserialize(processedLines.get(i)) : Component.empty());
        }
    }
}
