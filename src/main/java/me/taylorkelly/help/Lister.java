package me.taylorkelly.help;

import com.jascotty2.JMinecraftFontWidthCalculator;
import java.util.ArrayList;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Lister {

    private HelpList helpList;
    private CommandSender player;
    private String plugin;
    private int maxPages;
    private int page;
    private ArrayList<HelpEntry> sortedEntries;

    public Lister(HelpList helpList, String plugin, CommandSender player) {
        this.helpList = helpList;
        this.player = player;
        this.plugin = helpList.matchPlugin(plugin);
    }

    public Lister(HelpList helpList, CommandSender player) {
        this(helpList, null, player);
    }

    public void setPage(int page) {
        this.page = page;
        int start = (page - 1) * HelpSettings.entriesPerPage;
        if (plugin == null) {
            sortedEntries = helpList.getSortedHelp(player, start, HelpSettings.entriesPerPage);
            maxPages = (int) Math.ceil(helpList.getMaxEntries(player) / (double) HelpSettings.entriesPerPage);
        } else {
            sortedEntries = helpList.getSortedHelp(player, start, HelpSettings.entriesPerPage, plugin);
            maxPages = (int) Math.ceil(helpList.getMaxEntries(player, plugin) / (double) HelpSettings.entriesPerPage);
        }
    }

    public void list() {
        ChatColor commandColor = ChatColor.RED;
        ChatColor descriptionColor = ChatColor.WHITE;
        ChatColor introDashColor = ChatColor.GOLD;
        ChatColor introTextColor = ChatColor.WHITE;
        if (player instanceof Player) {
            int width = 310;

            if (plugin == null) {
                player.sendMessage(introDashColor.toString() + JMinecraftFontWidthCalculator.strPadCenterChat(
                        introTextColor.toString() + " HELP (" + page + "/" + maxPages + ") " + introDashColor.toString(), width, '-'));
            } else {
                if (sortedEntries.isEmpty()) {
                    player.sendMessage(ChatColor.RED.toString() + plugin + " has no Help entries");
                } else {
                    player.sendMessage(introDashColor.toString() + JMinecraftFontWidthCalculator.strPadCenterChat(
                            introTextColor.toString() + " " + plugin.toUpperCase() + " HELP (" + page + "/" + maxPages + ") " + introDashColor.toString(), width, '-'));
                }
            }

            for (HelpEntry entry : sortedEntries) {
                String line = String.format("%s/%s%s : %s", commandColor.toString(),
                        entry.command, ChatColor.WHITE.toString(), descriptionColor.toString()).
                        replace("[", ChatColor.GRAY.toString() + "[").replace("]", "]" + commandColor.toString());

                //Find remaining length left
                int descriptionSize = JMinecraftFontWidthCalculator.getStringWidth(entry.description);
                int sizeRemaining = width - JMinecraftFontWidthCalculator.getStringWidth(line);

                if (sizeRemaining > descriptionSize) {
                    player.sendMessage(line + JMinecraftFontWidthCalculator.strPadLeftChat(
                            entry.description.replace("[", ChatColor.GRAY.toString() + "[").
                            replace("]", "]" + descriptionColor.toString()), sizeRemaining, ' '));
                } else {
                    player.sendMessage(line);
                    player.sendMessage(JMinecraftFontWidthCalculator.strPadLeftChat(
                            entry.description.replace("[", ChatColor.GRAY.toString() + "[").
                            replace("]", "]" + descriptionColor.toString()), ' '));
                }

            }
        } else {
            if (plugin == null) {
                player.sendMessage(introTextColor.toString() + "HELP (" + page + "/" + maxPages + ")");
            } else {
                if (sortedEntries.isEmpty()) {
                    player.sendMessage(ChatColor.RED.toString() + "Plugin " + plugin + " has no Help entries");
                } else {
                    player.sendMessage(introTextColor.toString() + plugin.toUpperCase() + " HELP (" + page + "/" + maxPages + ")");
                }
            }

            for (HelpEntry entry : sortedEntries) {
                String line = String.format("%s/%s%s : %s", commandColor.toString(),
                        entry.command, ChatColor.WHITE.toString(), descriptionColor.toString()).
                        replace("[", ChatColor.GRAY.toString() + "[").replace("]", "]" + commandColor.toString());

                    player.sendMessage( "   " + line +
                            entry.description.replace("[", ChatColor.GRAY.toString() + "[").replace("]", "]"
                            + descriptionColor.toString()));

                
            }
        }
    }

    public int getMaxPages() {
        if (plugin == null) {
            return (int) Math.ceil(helpList.getMaxEntries(player) / (double) HelpSettings.entriesPerPage);
        } else {
            return (int) Math.ceil(helpList.getMaxEntries(player, plugin) / (double) HelpSettings.entriesPerPage);
        }
    }

    public int getMaxPages(CommandSender player) {
        if (plugin == null) {
            return (int) Math.ceil(helpList.getMaxEntries(player) / (double) HelpSettings.entriesPerPage);
        } else {
            return (int) Math.ceil(helpList.getMaxEntries(player, plugin) / (double) HelpSettings.entriesPerPage);
        }
    }

    public String whitespace(int length) {
        int spaceWidth = JMinecraftFontWidthCalculator.getCharWidth(' ');

        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < length - spaceWidth; i += spaceWidth) {
            ret.append(" ");
        }

        return ret.toString();
    }

    public String dashes(int length) {
        int spaceWidth = JMinecraftFontWidthCalculator.getCharWidth('-');

        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < length - spaceWidth; i += spaceWidth) {
            ret.append("-");
        }

        return ret.toString();
    }
}
