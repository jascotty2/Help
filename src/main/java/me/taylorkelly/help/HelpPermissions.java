package me.taylorkelly.help;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;
import me.jascotty2.lib1.Str;
import me.taylorkelly.help.utils.HelpLogger;
import org.anjocaido.groupmanager.GroupManager;

public class HelpPermissions {

    private enum PermissionHandler {

        PERMISSIONS, GROUP_MANAGER, NONE
    }
    private static PermissionHandler handler;
    private static Plugin permissionPlugin;
    private static boolean permErr = false;

    public static void initialize(Server server) {
        Plugin groupManager = server.getPluginManager().getPlugin("GroupManager");
        Plugin permissions = server.getPluginManager().getPlugin("Permissions");

        if (groupManager != null/* && groupManager.isEnabled()*/) {
            permissionPlugin = groupManager;
            handler = PermissionHandler.GROUP_MANAGER;
            String version = groupManager.getDescription().getVersion();
            HelpLogger.Info("Permissions enabled using: GroupManager v" + version);
        } else if (permissions != null/* && permissions.isEnabled()*/) {
            permissionPlugin = permissions;
            handler = PermissionHandler.PERMISSIONS;
            String version = permissions.getDescription().getVersion();
            HelpLogger.Info("Permissions enabled using: Permissions v" + version);
        } else {
            handler = PermissionHandler.NONE;
            HelpLogger.Info("Using Bukkit for Permissions");
        }
    }

    public static boolean permission(Player player, String permission) {
        try {
            switch (handler) {
                case PERMISSIONS:
                    return ((Permissions) permissionPlugin).getHandler().has(player, permission);
                case GROUP_MANAGER:
                    return ((GroupManager) permissionPlugin).getWorldsHolder().getWorldPermissions(player).has(player, permission);
                case NONE:
					if (player.hasPermission(permission)) {
						return true;
					} else if (!permission.contains("*") && Str.count(permission, '.') >= 2) {
						return player.hasPermission(permission.substring(0, permission.lastIndexOf('.') + 1) + "*");
					}
					return false;
            }
        } catch (Exception ex) {
            if (!permErr) {
                HelpLogger.Severe("Unexpected Error checking permission: defaulting to true", ex);
                permErr = true;
            }
        }
		return true;
    }
}
