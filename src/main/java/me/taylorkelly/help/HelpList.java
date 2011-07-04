package me.taylorkelly.help;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class HelpList {

    private HashMap<String, HelpEntry> mainHelpList = new HashMap<String, HelpEntry>();
    //private HashMap<String, HashMap<String, HelpEntry>> pluginHelpList = new HashMap<String, HashMap<String, HelpEntry>>();
    private HashMap<String, ArrayList<HelpEntry>> pluginHelpList = new HashMap<String, ArrayList<HelpEntry>>();
    private HashMap<String, HelpEntry> commandsList = new HashMap<String, HelpEntry>();
    private LinkedList<HelpEntry> savedList = new LinkedList<HelpEntry>();
    protected File dataFolder = null;

    public HelpList(File dataFolder) {
        this.dataFolder = dataFolder;
        customHelp();
    }

    public int getSize() {
        return mainHelpList.size();
    }

    public int getSize(String plugin) {
        if (pluginHelpList.containsKey(plugin)) {
            return pluginHelpList.get(plugin).size();
        } else {
            return 0;
        }
    }

    public double getMaxEntries(CommandSender player) {
        if (player instanceof Player) {
            int count = 0;
            for (HelpEntry entry : mainHelpList.values()) {
                if (entry.playerCanUse((Player) player) && entry.visible) {
                    ++count;
                }
            }
            return count;
        }
        return mainHelpList.size();
    }

    public double getMaxEntries(CommandSender player, String plugin) {
        if (pluginHelpList.containsKey(plugin)) {
            if (player instanceof Player) {
                int count = 0;
                for (HelpEntry entry : pluginHelpList.get(plugin)) {
                    if (entry.playerCanUse((Player) player) && entry.visible) {
                        ++count;
                    }
                }
                return count;
            }
            return pluginHelpList.get(plugin).size();
        } else {
            return 0;
        }
    }

    /**
     * gets the correctly-cased name of the given plugin title
     * @param plugin plugin to match
     * @return case matched plugin, or the input string if no match
     */
    public String matchPlugin(String plugin) {
        for (String pluginKey : pluginHelpList.keySet()) {
            if (pluginKey.equalsIgnoreCase(plugin)) {
                return pluginKey;
            }
        }
        return plugin;
    }

    public void listPlugins(CommandSender player) {
        StringBuilder list = new StringBuilder();
        for (String plugin : pluginHelpList.keySet()) {
            list.append(ChatColor.GREEN.toString());
            list.append(plugin);
            list.append(ChatColor.WHITE.toString());
            list.append(", ");
        }
        list.delete(list.length() - 2, list.length());
        player.sendMessage(ChatColor.AQUA + "Plugins with Help entries:");
        player.sendMessage(list.toString());
    }

    public ArrayList<HelpEntry> getHelpEntries(CommandSender player, int start, int size) {
        ArrayList<HelpEntry> ret = new ArrayList<HelpEntry>();

        List<String> names = new ArrayList<String>(mainHelpList.keySet());
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.SECONDARY);
        Collections.sort(names, collator);

        int currentCount = 0;
        for (String entryName : names) {
            HelpEntry entry = mainHelpList.get(entryName);
            if (entry != null && entry.playerCanUse(player) && entry.visible) {
                if (currentCount >= start) {
                    ret.add(entry);
                    if (ret.size() >= size) {
                        break;
                    }
                } else {
                    ++currentCount;
                }
            }
        }
        return ret;
    }

    public ArrayList<HelpEntry> getHelpEntries(CommandSender player, int start, int size, String plugin) {
        ArrayList<HelpEntry> ret = new ArrayList<HelpEntry>();
        if (pluginHelpList.containsKey(plugin)) {
            int currentCount = 0;
            for (HelpEntry entry : pluginHelpList.get(plugin)) {
                if (entry != null && entry.playerCanUse(player) && entry.visible) {
                    if (currentCount >= start) {
                        ret.add(entry);
                        if (ret.size() >= size) {
                            break;
                        }
                    } else {
                        ++currentCount;
                    }
                }
            }
        }
        return ret;
    }

    public MatchList getMatches(String query, CommandSender player) {
        ArrayList<HelpEntry> commandMatches = new ArrayList<HelpEntry>();
        ArrayList<HelpEntry> pluginExactMatches = new ArrayList<HelpEntry>();
        ArrayList<HelpEntry> pluginPartialMatches = new ArrayList<HelpEntry>();
        ArrayList<HelpEntry> descriptionMatches = new ArrayList<HelpEntry>();

        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.SECONDARY);

        List<String> plugins = new ArrayList<String>(pluginHelpList.keySet());
        Collections.sort(plugins, collator);

        for (String pluginName : plugins) {
            for (HelpEntry entry : pluginHelpList.get(pluginName)) {
                if (entry.playerCanUse(player) && entry.visible) {
                    //TODO Separate word matching
                    if (pluginName.equalsIgnoreCase(query)) {
                        pluginExactMatches.add(entry);
                    } else if (pluginName.toLowerCase().contains(query.toLowerCase())) {
                        pluginPartialMatches.add(entry);
                    }
                    if (entry.description.toLowerCase().contains(query.toLowerCase())) {
                        descriptionMatches.add(entry);
                    }
                    if (entry.command.toLowerCase().contains(query.toLowerCase())) {
                        commandMatches.add(entry);
                    }
                }
            }
        }

        return new MatchList(commandMatches, pluginExactMatches, pluginPartialMatches, descriptionMatches);
    }

    /**
     * Gets all of the commands registered with this plugin
     * @param plugin plugin to lookup
     * @return list of commands
     */
    public ArrayList<String> getPluginCommands(String plugin) {
        ArrayList<String> ret = new ArrayList<String>();
        if (plugin != null) {
            plugin = plugin.toLowerCase();
            if (pluginHelpList.containsKey(plugin)) {
                for (HelpEntry entry : pluginHelpList.get(plugin)) {
                    if (entry != null) {
                        ret.add(entry.command);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Gets the help text associated with this command
     * @param command the command to lookup
     * @return help text, or null if none
     */
    public String getCommandHelp(String command) {
        if(command == null) return null;
        HelpEntry t = commandsList.get(command.toLowerCase());
        return t != null ? t.description : null;
    }

    public void reload(CommandSender player, File dataFolder) {
        mainHelpList = new HashMap<String, HelpEntry>();
        pluginHelpList = new HashMap<String, ArrayList<HelpEntry>>(); //HashMap<String, HashMap<String, HelpEntry>>();

        HelpLoader.load(dataFolder, this);
        customHelp();

        for (HelpEntry entry : savedList) {
            if (entry.main && !mainHelpList.containsKey(entry.command)) {
                mainHelpList.put(entry.command, entry);
            }
            customSaveEntry(entry.plugin, entry, false);
        }

        if (player != null) {
            player.sendMessage(ChatColor.AQUA + "Successfully reloaded Help");
        }
    }

    public boolean registerCommand(String command, String description, String plugin, boolean main, String[] permissions) {
        return registerCommand(command, description, plugin, main, permissions, dataFolder);
    }

    public boolean registerCommand(String command, String description, String plugin, boolean main, String[] permissions, File folder) {
        if (command != null && description != null) {
            command = command.trim();
            if (!Help.settings.allowPluginOverride) {
                // check if the command (not plugin:command) is already registered
                if(commandsList.containsKey(command.toLowerCase())){
                    return false;
                }
            }
            HelpEntry entry = new HelpEntry(command, description.trim(), plugin, main, permissions, true);
            if (Help.settings.savePluginHelp) {
                entry.save(folder);
            }
            if (main && !mainHelpList.containsKey(command)) {
                mainHelpList.put(command, entry);
            }
            savePluginEntry(plugin, entry);
            sortPluginHelp(plugin);
            return true;
        }
        return false;
    }

    private void savePluginEntry(String plugin, HelpEntry entry) {
        customSaveEntry(plugin, entry, false);
        int i = savedList.lastIndexOf(entry);
        if (i >= 0) {
            savedList.get(i).setEntry(entry);
        } else {
            savedList.add(entry);
        }
        if (commandsList.containsKey(entry.command.toLowerCase())) {
            commandsList.get(entry.command.toLowerCase()).setEntry(entry);
        } else {
            commandsList.put(entry.command.toLowerCase(), entry);
        }
    }

    /**
     * saves a HelpEntry if it does not exist, or if given priority
     * @param plugin the plugin name
     * @param entry entry to save
     * @param priority if should overwrite the previous entry, if any
     */
    private void customSaveEntry(String plugin, HelpEntry entry, boolean priority) {
        if (plugin != null && entry != null) {
            plugin = plugin.toLowerCase();
            if (pluginHelpList.containsKey(plugin)) {
                ArrayList<String> plgs = getPluginCommands(plugin);
                int i = plgs.indexOf(entry.command);
                if (i >= 0) {
                    if (priority) {
                        pluginHelpList.get(plugin).get(i).setEntry(entry);
                    }
                } else {
                    pluginHelpList.get(plugin).add(entry);
                }
            } else {
                pluginHelpList.put(plugin, new ArrayList<HelpEntry>(Arrays.asList(entry)));
            }
        }
    }

    public boolean customRegisterCommand(String command, String description, String plugin, boolean main, String[] permissions, boolean visible) {
        HelpEntry entry = new HelpEntry(command, description, plugin, main, permissions, visible);
        if (main) {
            mainHelpList.put(command, entry);
        }
        customSaveEntry(plugin, entry, true);
        sortPluginHelp(plugin);
        return true;
    }

    private void customHelp() {
        registerCommand("help Help", "Displays more /help options", Help.name, true, null);
        registerCommand("help", "Displays the basic Help menu", Help.name, false, null);
        registerCommand("help [plugin]", "Displays the full help for [plugin]", Help.name, true, null);
        registerCommand("help plugins", "Show all the plugins with Help entries", Help.name, false, null);
        registerCommand("help search [query]", "Search the help entries for [query]", Help.name, false, null);
        registerCommand("help reload", "Reload the ExtraHelp.yml entries", Help.name, false, null);
    }

    public void sortPluginHelp(String plugin) {
        if (Help.settings.sortPluginHelp && plugin != null && pluginHelpList.containsKey(plugin)) {
            java.util.Collections.sort(pluginHelpList.get(plugin), new EntryComparator());
        }
    }

    public static class EntryComparator implements Comparator<HelpEntry> {

        boolean descending = true;

        public EntryComparator() {
        }

        public int compare(HelpEntry o1, HelpEntry o2) {
            return o1.command.compareTo(o2.command) * (descending ? 1 : -1);
        }
    }
}
