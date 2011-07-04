package me.taylorkelly.help;

import com.jascotty2.CheckInput;
import com.jascotty2.Str;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Help extends JavaPlugin {

    protected static String name = "Help";
    protected static String version = "?";
    protected static File dataFolder = null;
    protected static HelpList helpList = null;
    protected static HelpSettings settings = null;

    public Help() {
        dataFolder = this.getDataFolder();
        if (dataFolder == null) {
            dataFolder = new File("plugins" + File.separatorChar + "Help");
        }
    }

    @Override
    public void onEnable() {

        if (settings == null || helpList == null) {
            settings = new HelpSettings(dataFolder);
            helpList = new HelpList(dataFolder);
        }
        HelpLoader.load(dataFolder, helpList);

        version = this.getDescription().getVersion();

        HelpPermissions.initialize(getServer());

        HelpLogger.Log(version + " enabled");
    }

    @Override
    public void onDisable() {
        helpList = null;
        settings = null;
        HelpLogger.Log("disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String commandName = command.getName().toLowerCase();

        if (commandName.equals("help")) {
            /**
             * /help (#)
             */
            if (args.length == 0 || (args.length == 1 && CheckInput.IsInt(args[0]))) {
                Lister lister = new Lister(helpList, sender);
                if (args.length == 1) {
                    int page = Integer.parseInt(args[0]);
                    if (page < 1) {
                        sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
                        return true;
                    } else if (page > lister.getMaxPages()) {
                        sender.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages() + " pages of help");
                        return true;
                    } else if (page > 1) { // page is already at #1
                        lister.setPage(page);
                    }
                }
                lister.list();

                /**
                 * /help plugins
                 */
            } else if (args.length == 1 && args[0].equalsIgnoreCase("plugins")) {
                helpList.listPlugins(sender);

                /**
                 * /help reload
                 */
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.isOp()) {
                    settings.initialize(dataFolder);
                    helpList.reload(sender, dataFolder);
                } else {
                    sender.sendMessage(ChatColor.RED + " You don't have permission to do this");
                }
                /**
                 * /help search [query]
                 */
            } else if (args.length > 1 && args[0].equalsIgnoreCase("search")) {
                Searcher searcher = new Searcher(helpList);
                searcher.addPlayer(sender);
                searcher.setQuery(Str.argStr(args, 1));
                searcher.search();

                /**
                 * /help [plugin] (#)
                 */
            } else if (args.length == 1 || (args.length == 2 && CheckInput.IsInt(args[1]))) {
                Lister lister = new Lister(helpList, args[0], sender);
                //TODO: if plugin has no entry, check if this is actually a command
                if (args.length == 2) {
                    int page = Integer.parseInt(args[1]);
                    if (page < 1) {
                        sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
                        return true;
                    } else if (page > lister.getMaxPages(sender)) {
                        sender.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages(sender) + " pages of help");
                        return true;
                    }
                    lister.setPage(page);
                } else {
                    lister.setPage(1);
                }
                lister.list();
            } else {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Register a command with a plugin
     * @param command the command string
     * @param description command description
     * @param plugin plugin that this command is for
     * @return if the command was registered in Help
     */
    public boolean registerCommand(String command, String description, Plugin plugin) {
        if (helpList != null && (settings.allowPluginHelp || plugin == this) && plugin != null) {
            return helpList.registerCommand(command, description, plugin.getDescription().getName(), false, new String[]{}, this.getDataFolder());
        }
        return false;
    }

    /**
     * Register a command with a plugin
     * @param command the command string
     * @param description command description
     * @param plugin plugin that this command is for
     * @param main if this command should be listed on the main pages
     * @return
     */
    public boolean registerCommand(String command, String description, Plugin plugin, boolean main) {
        if (helpList != null && (settings.allowPluginHelp || plugin == this) && plugin != null) {
            return helpList.registerCommand(command, description, plugin.getDescription().getName(), main, new String[]{}, this.getDataFolder());
        }
        return false;
    }

    /**
     * Register a command with a plugin
     * @param command the command string
     * @param description command description
     * @param plugin plugin that this command is for
     * @param permissions the permission(s) necessary to view this entry
     * @return
     */
    public boolean registerCommand(String command, String description, Plugin plugin, String... permissions) {
        if (helpList != null && (settings.allowPluginHelp || plugin == this) && plugin != null) {
            return helpList.registerCommand(command, description, plugin.getDescription().getName(), false, permissions, this.getDataFolder());
        }
        return false;
    }

    /**
     * Register a command with a plugin
     * @param command the command string
     * @param description command description
     * @param plugin plugin that this command is for
     * @param main if this command should be listed on the main pages
     * @param permissions the permission(s) necessary to view this entry
     * @return
     */
    public boolean registerCommand(String command, String description, Plugin plugin, boolean main, String... permissions) {
        if (helpList != null && (settings.allowPluginHelp || plugin == this) && plugin != null) {
            return helpList.registerCommand(command, description, plugin.getDescription().getName(), main, permissions, this.getDataFolder());
        }
        return false;
    }

    /**
     * Gets the help text associated with this command
     * @param command the command to lookup
     * @return help text, or null if none
     */
    public String getHelp(String command) {
        return helpList.getCommandHelp(command);
    }
    
    
    /**
     * Gets all of the commands registered with this plugin
     * @param plugin plugin to lookup
     * @return list of commands
     */
    public ArrayList<String> getPluginCommands(String plugin) {
        return helpList.getPluginCommands(plugin);
    }
}
