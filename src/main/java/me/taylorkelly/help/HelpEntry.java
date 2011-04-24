package me.taylorkelly.help;

import com.jascotty2.JMinecraftFontWidthCalculator;
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
    public int lineLength;
    public boolean visible;

    public HelpEntry(String command, String description, String plugin, boolean main, String[] permissions, boolean visible) {
        this.command = command;
        this.description = description;
        this.plugin = plugin;
        this.main = main;
        this.permissions = permissions;
        this.visible = visible;
        this.lineLength = processLength(command, description);//process(this);
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
        if (permissions.length == 0 || !(player instanceof Player)) {
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
    /*
    private static int process(HelpEntry entry) {
    ChatColor commandColor = ChatColor.RED;
    ChatColor descriptionColor = ChatColor.WHITE;
    int width = 325;

    StringBuilder entryBuilder = new StringBuilder();
    entryBuilder.append(commandColor.toString());
    entryBuilder.append("/");
    entryBuilder.append(entry.command);
    entryBuilder.append(ChatColor.WHITE.toString());
    entryBuilder.append(" : ");
    entryBuilder.append(descriptionColor.toString());
    //Find remaining length left
    int sizeRemaining = width - MinecraftFontWidthCalculator.getStringWidth(entryBuilder.toString());
    entryBuilder = new StringBuilder(entryBuilder.toString().replace("[", ChatColor.GRAY.toString() + "[").replace("]", "]" + commandColor.toString()));

    int descriptionSize = MinecraftFontWidthCalculator.getStringWidth(entry.description);
    if (sizeRemaining > descriptionSize) {
    return 1;
    } else {
    return 1 + (int) Math.ceil((double) MinecraftFontWidthCalculator.getStringWidth("  " + entry.description) / width);
    }
    }//*/

    protected int processLength() {
        return processLength(command, description);
    }

    private static int processLength(String command, String desc) {
        ChatColor commandColor = ChatColor.RED;
        ChatColor descriptionColor = ChatColor.WHITE;
        int width = 325;

        String entry = String.format("%s/%s%s : %s", commandColor.toString(),
                command, ChatColor.WHITE.toString(), descriptionColor.toString());
        //Find remaining length left
        int sizeRemaining = width - JMinecraftFontWidthCalculator.getStringWidth(entry);
        //entryBuilder = new StringBuilder(entryBuilder.toString().replace("[", ChatColor.GRAY.toString() + "[").replace("]", "]" + commandColor.toString()));

        int descriptionSize = JMinecraftFontWidthCalculator.getStringWidth(desc);
        if (sizeRemaining > descriptionSize) {
            return 1;
        } else {
            return 1 + (int) Math.ceil((double) JMinecraftFontWidthCalculator.getStringWidth("  " + desc) / width);
        }
    }

    public void save(File dataFolder) {
        File folder = new File(dataFolder, "ExtraHelp");
        File file = new File(folder, plugin + "_orig.yml");
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
        config.setProperty(node + ".plugin", plugin);
        config.setProperty(node + ".main", main);
        if (permissions.length != 0) {
            config.setProperty(node + ".permissions", permissions);
        }
        config.setProperty(node + ".visible", true);
        config.save();
    }
}
