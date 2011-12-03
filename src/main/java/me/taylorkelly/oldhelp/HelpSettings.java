package me.taylorkelly.oldhelp;

import java.io.File;
import java.util.logging.Level;
import org.bukkit.ChatColor;

public final class HelpSettings {

    private static final String settingsFile = "Help.yml";
    public boolean allowPluginOverride = false,
            allowPluginHelp = true, // if plugins can pass Help custom entries
            savePluginHelp = false, // if the help entries registered should be saved
            sortPluginHelp = true, // if added entries should also be sorted (by command string)
            shortenEntries = false, // entries shown on only one line
            useWordWrap = true, // smart(er) word wrapping
            wordWrapRight = true; // wrap to the right
    public int entriesPerPage = 9;
    ChatColor commandColor = ChatColor.RED,
            commandBracketColor = ChatColor.GRAY,
            descriptionColor = ChatColor.WHITE,
            introDashColor = ChatColor.GOLD,
            introTextColor = ChatColor.WHITE;

    public HelpSettings() {
    }

    public HelpSettings(File dataFolder) {
        initialize(dataFolder);
    }

    public void initialize(File dataFolder) {
        try {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File configFile = new File(dataFolder, settingsFile);
            BetterConfig config = new BetterConfig(configFile);
            config.load();

            entriesPerPage = config.getInt("entriesPerPage", entriesPerPage);
            allowPluginOverride = config.getBoolean("allowPluginOverride", allowPluginOverride);
            allowPluginHelp = config.getBoolean("allowPluginHelp", allowPluginHelp);
            savePluginHelp = config.getBoolean("savePluginHelp", savePluginHelp);
            sortPluginHelp = config.getBoolean("sortPluginHelp", sortPluginHelp);

            shortenEntries = config.getBoolean("shortenEntries", shortenEntries);
            useWordWrap = config.getBoolean("useWordWrap", useWordWrap);
            wordWrapRight = config.getBoolean("wordWrapRight", wordWrapRight);

            config.save();
        } catch (Exception ex) {
            HelpLogger.Log(Level.SEVERE, "Error loading Settings", ex);
        }
    }
}
