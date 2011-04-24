package me.taylorkelly.help;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class HelpList {

    private HashMap<String, HelpEntry> mainHelpList = new HashMap<String, HelpEntry>();
    private HashMap<String, HashMap<String, HelpEntry>> pluginHelpList = new HashMap<String, HashMap<String, HelpEntry>>();
    private LinkedList<HelpEntry> savedList = new LinkedList<HelpEntry>();

    public HelpList() {
    }

    public ArrayList<HelpEntry> getSortedHelp(CommandSender player, int start, int size) {
        ArrayList<HelpEntry> ret = new ArrayList<HelpEntry>();
        List<String> names = new ArrayList<String>(mainHelpList.keySet());
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.SECONDARY);
        Collections.sort(names, collator);


        if (player instanceof Player) {
            int index = 0;
            int currentCount = 0;
            while (index < names.size() && ret.size() < size) {
                String currName = names.get(index);
                HelpEntry entry = mainHelpList.get(currName);
                if (entry.playerCanUse((Player) player) && entry.visible) {
                    if (currentCount >= start) {
                        ret.add(entry);
                    } else {
                        currentCount++;
                    }
                }
                index++;
            }
        } else {
                for (int index = 0, currentCount = 0; index < names.size() && ret.size() < size; ++index) {
                    HelpEntry entry = mainHelpList.get(names.get(index));
                    if (entry.visible) {
                        if (currentCount >= start) {
                            ret.add(entry);
                        }else {
                            currentCount++;
                        }
                    }
                }
        }
        return ret;
    }

    public int getSize() {
        return mainHelpList.size();
    }

    public double getMaxEntries(CommandSender player) {
        if (player instanceof Player) {
            int count = 0;
            for (HelpEntry entry : mainHelpList.values()) {
                if (entry.playerCanUse((Player) player) && entry.visible) {
                    count++;
                }
            }
            return count;
        }
        return mainHelpList.size();
    }

    public ArrayList<HelpEntry> getSortedHelp(CommandSender player, int start, int size, String plugin) {
        ArrayList<HelpEntry> ret = new ArrayList<HelpEntry>();
        if (!pluginHelpList.containsKey(plugin)) {
            return ret;
        } else {
            List<String> names = new ArrayList<String>(pluginHelpList.get(plugin).keySet());
            Collator collator = Collator.getInstance();
            collator.setStrength(Collator.SECONDARY);
            Collections.sort(names, collator);

            if (player instanceof Player) {
                int index = 0;
                int currentCount = 0;
                int lineLength = 0;
                while (index < names.size() && ret.size() < size){// && lineLength < size) {
                    String currName = names.get(index);
                    HelpEntry entry = pluginHelpList.get(plugin).get(currName);
                    if (entry != null && entry.playerCanUse((Player) player) && entry.visible) {
                        if (currentCount >= start) {
                            ret.add(entry);
                            lineLength += entry.lineLength;
                        } else {
                            currentCount++;
                        }
                    }
                    index++;
                }
            } else {
                int index = 0;
                while (index < names.size() && ret.size() < size){
                    String currName = names.get(index);
                    HelpEntry entry = pluginHelpList.get(plugin).get(currName);
                    if (entry != null && entry.visible) {
                       ret.add(entry);
                    }
                    index++;
                }
            }
            return ret;
        }
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

        for (int i = 0; i < plugins.size(); i++) {
            String pluginName = plugins.get(i);
            HashMap<String, HelpEntry> pluginSet = pluginHelpList.get(pluginName);
            List<String> commands = new ArrayList<String>(pluginSet.keySet());
            Collections.sort(commands, collator);
            for (int j = 0; j < commands.size(); j++) {
                String command = commands.get(j);
                HelpEntry entry = pluginSet.get(command);
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

    public int getSize(String plugin) {
        if (pluginHelpList.containsKey(plugin)) {
            return pluginHelpList.get(plugin).size();
        } else {
            return 0;
        }
    }

    public double getMaxEntries(CommandSender player, String plugin) {
        if (pluginHelpList.containsKey(plugin)) {
            if (player instanceof Player) {
                int count = 0;
                for (HelpEntry entry : pluginHelpList.get(plugin).values()) {
                    if (entry.playerCanUse((Player) player) && entry.visible) {
                        count++;
                    }
                }
                return count;
            }
            return pluginHelpList.get(plugin).size();
        } else {
            return 0;
        }
    }

    public String matchPlugin(String plugin) {
        for (String pluginKey : pluginHelpList.keySet()) {
            if (pluginKey.equalsIgnoreCase(plugin)) {
                return pluginKey;
            }
        }
        return plugin;
    }

    public boolean registerCommand(String command, String description, String plugin, boolean main, String[] permissions, File dataFolder) {
        HelpEntry entry = new HelpEntry(command, description, plugin, main, permissions, true);
        entry.save(dataFolder);
        if (main && !mainHelpList.containsKey(command)) {
            mainHelpList.put(command, entry);
        }
        savePluginEntry(plugin, entry);
        return true;
    }

    private void savePluginEntry(String plugin, HelpEntry entry) {
        customSaveEntry(plugin, entry, false);
        permaSaveEntry(entry);
    }

    private void customSaveEntry(String plugin, HelpEntry entry, boolean priority) {
        if (pluginHelpList.containsKey(plugin)) {
            if (priority) {
                pluginHelpList.get(plugin).put(entry.command, entry);
            } else {
                if (!pluginHelpList.get(plugin).containsKey(entry.command)) {
                    pluginHelpList.get(plugin).put(entry.command, entry);
                }
            }
        } else {
            HashMap<String, HelpEntry> map = new HashMap<String, HelpEntry>();
            map.put(entry.command, entry);
            pluginHelpList.put(plugin, map);
        }
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

    public void reload(CommandSender player, File dataFolder) {
        mainHelpList = new HashMap<String, HelpEntry>();
        pluginHelpList = new HashMap<String, HashMap<String, HelpEntry>>();

        HelpLoader.load(dataFolder, this);

        for (HelpEntry entry : savedList) {
            if (entry.main && !mainHelpList.containsKey(entry.command)) {
                mainHelpList.put(entry.command, entry);
            }
            customSaveEntry(entry.plugin, entry, false);
        }

        player.sendMessage(ChatColor.AQUA + "Successfully reloaded Help");
    }

    private void permaSaveEntry(HelpEntry entry) {
        savedList.add(entry);
    }

    public boolean customRegisterCommand(String command, String description, String plugin, boolean main, String[] permissions, boolean visible) {
        HelpEntry entry = new HelpEntry(command, description, plugin, main, permissions, visible);
        if (main) {
            mainHelpList.put(command, entry);
        }
        customSaveEntry(plugin, entry, true);
        return true;
    }
}
