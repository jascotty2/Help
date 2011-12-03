package me.taylorkelly.oldhelp;

import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpEntry {

    public String command;
    public String description;
    public String[] permissions;
    public boolean main;
    public String plugin;
    public boolean visible;

    public HelpEntry(String command, String description, String plugin, boolean main, String[] permissions, boolean visible) {
        this.command = command;
        this.description = description;
        this.plugin = plugin;
        this.main = main;
        this.permissions = permissions;
        this.visible = visible;
    }

    public HelpEntry(String command, String description, String plugin) {
        this(command, description, plugin, false, new String[]{}, true);
    }

    public HelpEntry(String command, String description, String plugin, boolean main) {
        this(command, description, plugin, main, new String[]{}, true);
    }

    public HelpEntry(String command, String description, String plugin, String[] permissions) {
        this(command, description, plugin, false, permissions, true);
    }

    public boolean playerCanUse(CommandSender player) {
        if (permissions == null || permissions.length == 0 || !(player instanceof Player)) {
            return true;
        }
        for (String permission : permissions) {
            if (permission.equalsIgnoreCase("OP") && player.isOp()) {
                return true;
            } else if (HelpPermissions.permission((Player) player, permission)) {
                return true;
            }
        }
        return false;
    }

    public String message() {
        ChatColor commandColor = ChatColor.RED;
        ChatColor pluginColor = ChatColor.GREEN;
        ChatColor descriptionColor = ChatColor.WHITE;
        return String.format("%s/%s%s : (via %s%s%s) %s%s",
                commandColor.toString(), command, ChatColor.WHITE.toString(),
                pluginColor.toString(), plugin, ChatColor.WHITE.toString(),
                descriptionColor.toString(), description);
    }

    @Override
    public String toString() {
        return String.format("%s/%s%s : %s%s",
                Help.settings.commandColor.toString(), command, ChatColor.WHITE.toString(),
                Help.settings.descriptionColor.toString(), description).
                replace("[", Help.settings.commandBracketColor.toString() + "[").replace("]", "]" + Help.settings.commandColor.toString());
    }

    public String chatString() {
        String line = String.format("%s/%s%s : %s",
                Help.settings.commandColor.toString(), command, ChatColor.WHITE.toString(),
                Help.settings.descriptionColor.toString());

        int descriptionSize = JMinecraftFontWidthCalculator.getStringWidth(description);
        int sizeRemaining = JMinecraftFontWidthCalculator.chatwidth - JMinecraftFontWidthCalculator.getStringWidth(line);

        line += JMinecraftFontWidthCalculator.strPadLeftChat(
                description.replace("[", Help.settings.commandBracketColor.toString() + "[").
                replace("]", "]" + Help.settings.descriptionColor.toString()), sizeRemaining, ' ');

        if (Help.settings.shortenEntries) {
            return JMinecraftFontWidthCalculator.strChatTrim(line);
        } else if (sizeRemaining > descriptionSize || !Help.settings.useWordWrap) {
            return line;
        } else if (Help.settings.wordWrapRight) {
            return JMinecraftFontWidthCalculator.strChatWordWrapRight(line, 10, ' ', ':');
        } else {
            return JMinecraftFontWidthCalculator.strChatWordWrap(line, 10);
        }
    }

    public String consoleString(int width) {
        String line = String.format("%s/%s%s : %s",
                Help.settings.commandColor.toString(), command, ChatColor.WHITE.toString(),
                Help.settings.descriptionColor.toString());

        int descriptionSize = JMinecraftFontWidthCalculator.strLen(description);
        int sizeRemaining = width - JMinecraftFontWidthCalculator.strLen(line);

        line += JMinecraftFontWidthCalculator.unformattedPadLeft(
                description.replace("[", Help.settings.commandBracketColor.toString() + "[").
                replace("]", "]" + Help.settings.descriptionColor.toString()), sizeRemaining, ' ');

        if (Help.settings.shortenEntries) {
            return JMinecraftFontWidthCalculator.strTrim(line, width);
        } else if (sizeRemaining > descriptionSize || !Help.settings.useWordWrap) {
            return line;
        } else if (Help.settings.wordWrapRight) {
            return JMinecraftFontWidthCalculator.strWordWrapRight(line, width, 10, ' ', ':');
        } else {
            return JMinecraftFontWidthCalculator.strWordWrap(line, width, 10);
        }
    }

    public void setEntry(HelpEntry toCopy) {
        this.command = toCopy.command;
        this.description = toCopy.description;
        this.plugin = toCopy.plugin;
        this.main = toCopy.main;
        this.permissions = toCopy.permissions;
        this.visible = toCopy.visible;
    }

    public void save(File dataFolder) {
        File folder = new File(dataFolder, "ExtraHelp");
        File file = new File(folder, plugin + ".yml");//_orig
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
            }
        }
        BetterConfig config = new BetterConfig(file);
        config.load();

        String node = command.replace(" ", "");
        config.setProperty(node + ".command", command);
        config.setProperty(node + ".description", description);
        //config.setProperty(node + ".plugin", plugin);
        config.setProperty(node + ".main", main);
        if (permissions != null && permissions.length != 0) {
            config.setProperty(node + ".permissions", permissions);
        }
        config.setProperty(node + ".visible", visible);
        config.save();
    }
}
