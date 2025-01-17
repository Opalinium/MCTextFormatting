package me.randomgamingdev.mctextformatting;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    public ChatListener(MCTextFormatting plugin) {
    }

    private static String replaceSubstring(String original, String replacement, int position, int length) {
        return original.substring(0, position) + replacement + original.substring(position + length);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();

        final String[] formatters = { "**", "*", "__", "~~", "##" };
        final String[] replacements = { "§l", "§o", "§n", "§m", "§k" };

        boolean[] active = { false, false, false, false, false };

        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);

            if (c == '\\') {
                msg = replaceSubstring(msg, "", i, 1);
                i--;
                continue;
            }

            if (c == '&') {
                msg = replaceSubstring(msg, "§", i, 1);
                continue;
            }

            for (int j = 0; j < formatters.length; j++) {
                String formatter = formatters[j];
                String replacement = replacements[j];

                if (i + formatter.length() > msg.length()) {
                    continue;
                }

                if (!msg.startsWith(formatter, i)) {
                    continue;
                }

                if (active[j]) {
                    active[j] = false;

                    StringBuilder resetBuilder = new StringBuilder("§r");
                    for (int k = 0; k < active.length; k++) {
                        if (active[k]) {
                            resetBuilder.append(replacements[k]);
                        }
                    }
                    msg = replaceSubstring(msg, resetBuilder.toString(), i, formatter.length());

                } else {
                    active[j] = true;
                    msg = replaceSubstring(msg, replacement, i, formatter.length());
                    i += formatter.length() - 1;
                }
                break;
            }
        }

        event.setMessage(msg);
    }
}