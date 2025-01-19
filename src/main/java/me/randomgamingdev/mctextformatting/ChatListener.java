package me.randomgamingdev.mctextformatting;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Stack;

public class ChatListener implements Listener {

    public ChatListener(MCTextFormatting plugin) {
    }

    private static class OpenMarker {
        int markerIndex;
        int position;
        OpenMarker(int markerIndex, int position) {
            this.markerIndex = markerIndex;
            this.position = position;
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String originalMessage = event.getMessage();

        if (originalMessage.trim().isEmpty()) {
            return;
        }

        String strippedFormatting = originalMessage.replaceAll("[*_~#]", "");
        if (strippedFormatting.trim().isEmpty()) {
            event.setMessage(originalMessage);
            return;
        }

        final String[] formatters = { "**", "__", "~~", "##", "*", "_", "~", "#" };
        final String[] replacements = { "§l", "§n", "§m", "§k", "§o", "§n", "§m", "§k" };

        final String[] placeholders = {
                "[ESCAPED_DOUBLE_ASTERISK]",
                "[ESCAPED_DOUBLE_UNDERSCORE]",
                "[ESCAPED_DOUBLE_TILDE]",
                "[ESCAPED_DOUBLE_HASH]",
                "[ESCAPED_SINGLE_ASTERISK]",
                "[ESCAPED_SINGLE_UNDERSCORE]",
                "[ESCAPED_SINGLE_TILDE]",
                "[ESCAPED_SINGLE_HASH]"
        };

        String msg = originalMessage.replace("&", "§");
        for (int f = 0; f < formatters.length; f++) {
            String escaped = "\\" + formatters[f];
            msg = msg.replace(escaped, placeholders[f]);
        }

        StringBuilder output = new StringBuilder();
        Stack<Integer> markerStack = new Stack<>();
        int i = 0;

        while (i < msg.length()) {
            boolean matchedMarker = false;

            for (int f = 0; f < formatters.length; f++) {
                String formatter = formatters[f];
                int fl = formatter.length();
                if (i + fl > msg.length()) continue;
                if (msg.substring(i, i + fl).equals(formatter)) {
                    if (!markerStack.isEmpty() && markerStack.peek() == f) {
                        markerStack.pop();
                        output.append("§r");
                        for (int m : markerStack) {
                            output.append(replacements[m]);
                        }
                    } else {
                        markerStack.push(f);
                        output.append(replacements[f]);
                    }
                    i += fl;
                    matchedMarker = true;
                    break;
                }
            }
            if (matchedMarker) continue;

            boolean handledPlaceholder = false;
            for (int p = 0; p < placeholders.length; p++) {
                String placeholder = placeholders[p];
                if (msg.startsWith(placeholder, i)) {

                    output.append(formatters[p]);
                    i += placeholder.length();

                    while(i < msg.length()) {
                        if(msg.startsWith(formatters[p], i)) {
                            output.append(formatters[p]);
                            i += formatters[p].length();
                            break;
                        }
                        boolean nestedFound = false;
                        for(int q = 0; q < placeholders.length; q++) {
                            String ph = placeholders[q];
                            if(msg.startsWith(ph, i)) {
                                output.append(formatters[q]);
                                i += ph.length();
                                nestedFound = true;
                                break;
                            }
                        }
                        if(nestedFound) continue;
                        output.append(msg.charAt(i));
                        i++;
                    }
                    handledPlaceholder = true;
                    break;
                }
            }
            if(handledPlaceholder) continue;

            output.append(msg.charAt(i));
            i++;
        }

        while (!markerStack.isEmpty()) {
            int markerIdx = markerStack.pop();
            String replacement = replacements[markerIdx];
            String formatter = formatters[markerIdx];
            int last = output.lastIndexOf(replacement);
            if (last != -1) {
                output.replace(last, last + replacement.length(), formatter);
            }
        }

        String finalMessage = output.toString();

        String visible = finalMessage.replaceAll("§[0-9a-fk-or]", "").trim();
        if (visible.isEmpty()) {
            event.setMessage(originalMessage);
        } else {
            event.setMessage(finalMessage);
        }
    }
}