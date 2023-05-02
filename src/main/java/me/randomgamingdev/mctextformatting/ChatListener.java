package me.randomgamingdev.mctextformatting;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.LinkedList;

public class ChatListener implements Listener {
    MCTextFormatting plugin;

    ChatListener(MCTextFormatting plugin) {
        this.plugin = plugin;
    }

    public static String IndRep(String str, String rep, int pos, int len) {
        return str.substring(0, pos) + rep + str.substring(pos + len);
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        final String[] formatters = new String[]{ "**", "*", "__", "~~", "##" };
        final String[] replacements = new String[]{ "§l", "§o", "§n", "§m", "§k" };
        boolean[] last = new boolean[]{ false, false, false, false, false };
        for (int i = 0; i < msg.length(); i++) {
            final char character = msg.charAt(i);
            if (character == '\\') {
                msg = IndRep(msg, "", i, 1);
                continue;
            }

            if (character == '&') {
                msg = IndRep(msg, "§", i, 1);
                continue;
            }

            for (int j = 0; j < formatters.length; j++) {
                final String formatter = formatters[j];
                final String replacement = replacements[j];

                if (msg.length() - formatter.length() < i)
                    continue;
                if (!msg.startsWith(formatter, i))
                    continue;

                if (last[j] == true) {
                    last[j] = false;
                    String resetStr = "§r";
                    for (int k = 0; k < last.length; k++)
                        if (last[k])
                            resetStr += replacements[k];
                    msg = IndRep(msg, resetStr, i, formatter.length());
                    break;
                }

                last[j] = true;
                msg = IndRep(msg, replacement, i, formatter.length());
                i += formatter.length() - 1;
                break;
            }
        }
        event.setMessage(msg);
    }
}
