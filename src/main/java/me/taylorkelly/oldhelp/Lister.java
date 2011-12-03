package me.taylorkelly.oldhelp;

import java.util.ArrayList;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Lister {

    private HelpList helpList;
    private CommandSender player;
    private String plugin;
    private int maxPages;
    private int page;
    private ArrayList<HelpEntry> sortedEntries;

    public Lister(HelpList helpList, String plugin, CommandSender player, int page) {
        this.helpList = helpList;
        this.player = player;
        this.plugin = helpList.matchPlugin(plugin);
        setPage(page);
    }

    public Lister(HelpList helpList, String plugin, CommandSender player) {
        this(helpList, plugin, player, 1);
    }

    public Lister(HelpList helpList, CommandSender player) {
        this(helpList, null, player, 1);
    }

    public Lister(HelpList helpList) {
        this(helpList, null, null, 1);
    }

    public Lister(HelpList helpList, CommandSender player, int page) {
        this(helpList, null, player, page);
    }

    public Lister(HelpList helpList, int page) {
        this(helpList, null, null, page);
    }

    public void setPage(int page) {
        this.page = page;
        int start = (page - 1) * Help.settings.entriesPerPage;
        if (plugin == null) {
            sortedEntries = helpList.getHelpEntries(player, start, Help.settings.entriesPerPage);
            maxPages = (int) Math.ceil(helpList.getMaxEntries(player) / (double) Help.settings.entriesPerPage);
        } else {
            sortedEntries = helpList.getHelpEntries(player, start, Help.settings.entriesPerPage, plugin);
            maxPages = (int) Math.ceil(helpList.getMaxEntries(player, plugin) / (double) Help.settings.entriesPerPage);
        }
    }

    public void list() {
        list(player);
    }

    public void list(CommandSender player) {
        if (player != null) {
            if (player instanceof Player) {

                if (plugin == null) {
                    player.sendMessage(Help.settings.introDashColor.toString()
                            + JMinecraftFontWidthCalculator.strPadCenterChat(Help.settings.introTextColor.toString()
                            + " HELP (" + page + "/" + maxPages + ") " + Help.settings.introDashColor.toString(), '-'));
                } else {
                    if (sortedEntries.isEmpty()) {
                        player.sendMessage(ChatColor.RED.toString() + plugin + " has no Help entries");
                    } else {
                        player.sendMessage(Help.settings.introDashColor.toString()
                                + JMinecraftFontWidthCalculator.strPadCenterChat(Help.settings.introTextColor.toString()
                                + " " + plugin.toUpperCase() + " HELP (" + page + "/" + maxPages + ") " + Help.settings.introDashColor.toString(), '-'));
                    }
                }

                for (HelpEntry entry : sortedEntries) {
                    for (String l : entry.chatString().split("\n")) {
                        //System.out.println("sending '" + l + "' (" + JMinecraftFontWidthCalculator.getStringWidth(l) + " pixels long)");
                        player.sendMessage(l);
                    }
                }
            } else {
                int width = System.getProperty("os.name").startsWith("Windows") ? 80 - 17 : 90;
                if (plugin == null) {
                    player.sendMessage(Help.settings.introDashColor.toString() + JMinecraftFontWidthCalculator.unformattedPadCenter(
                            Help.settings.introTextColor.toString() + " HELP (" + page + "/" + maxPages + ") " + Help.settings.introDashColor.toString(), width, '-'));
                } else {
                    if (sortedEntries.isEmpty()) {
                        player.sendMessage(ChatColor.RED.toString() + plugin + " has no Help entries");
                    } else {
                        player.sendMessage(Help.settings.introDashColor.toString() + JMinecraftFontWidthCalculator.unformattedPadCenter(
                                Help.settings.introTextColor.toString() + " " + plugin.toUpperCase() + " HELP (" + page + "/" + maxPages + ") " + Help.settings.introDashColor.toString(), width, '-'));
                    }
                }

                for (HelpEntry entry : sortedEntries) {
                    for (String l : entry.consoleString(width).split("\n")) {
                        player.sendMessage(l);
                    }
                }
            }
        }
    }

    public int getMaxPages() {
        return getMaxPages(player);
    }

    public int getMaxPages(CommandSender player) {
        if (plugin == null) {
            return (int) Math.ceil(helpList.getMaxEntries(player) / (double) Help.settings.entriesPerPage);
        } else {
            return (int) Math.ceil(helpList.getMaxEntries(player, plugin) / (double) Help.settings.entriesPerPage);
        }
    }
}
