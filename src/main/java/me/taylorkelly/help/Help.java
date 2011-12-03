/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: a refactored/rewrit version of Help, kept in the same namespace for compatibility
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.taylorkelly.help;

import java.util.ArrayList;
import me.taylorkelly.help.utils.HelpLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Help extends JavaPlugin {

	public final static String name = "Help";
	final HelpConfig config = new HelpConfig(this);
	final HelpDB helpEntries = new HelpDB(this);
	final HelpAutoLoadListener autoloader = new HelpAutoLoadListener(this);
	
	public Help() {
		helpEntries.load();
		HelpLogger.Info("Help Init Finished");
	}

	public void onEnable() {
		this.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, autoloader, Priority.Monitor, this);
		if(config.autoHelp) {
			helpEntries.autoLoad();
		}
		HelpLogger.Info("Help v" + getDescription().getVersion() + " enabled");
	}

	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

		return true;
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param plugin plugin that this command is for
	 * @return if the command was registered in Help
	 */
	public boolean registerCommand(String command, String description, Plugin plugin) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				false, true, null, null, null);
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param plugin plugin that this command is for
	 * @param main if this command should be listed on the main pages
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description,
			Plugin plugin, boolean main) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				main, true, null, null, null);
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param plugin plugin that this command is for
	 * @param permissions the permission(s) necessary to view this entry
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description,
			Plugin plugin, String... permissions) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				false, true, permissions, null, null);
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param plugin plugin that this command is for
	 * @param main if this command should be listed on the main pages
	 * @param permissions the permission(s) necessary to view this entry
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description,
			Plugin plugin, boolean main, String... permissions) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				main, true, permissions, null, null);
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param category category to classify the command under
	 * @param plugin plugin that this command is for
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description,
			String category, Plugin plugin) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				false, true, null, new String[]{category}, null);
	}
	
	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param category categories to classify the command under
	 * @param plugin plugin that this command is for
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description,
			String[] category, Plugin plugin) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				false, true, null, category, null);
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param category category to classify the command under
	 * @param plugin plugin that this command is for
	 * @param main if this command should be listed on the main pages
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description, String category,
			Plugin plugin, boolean main) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				main, true, null, new String[]{category}, null);
	}
	
	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param category categories to classify the command under
	 * @param plugin plugin that this command is for
	 * @param main if this command should be listed on the main pages
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description, String[] category,
			Plugin plugin, boolean main) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				main, true, null, category, null);
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param category category to classify the command under
	 * @param plugin plugin that this command is for
	 * @param main if this command should be listed on the main pages
	 * @param permissions the permission(s) necessary to view this entry
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description, String category,
			Plugin plugin, boolean main, String... permissions) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				main, true, permissions, new String[]{category}, null);
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param category categories to classify the command under
	 * @param plugin plugin that this command is for
	 * @param main if this command should be listed on the main pages
	 * @param permissions the permission(s) necessary to view this entry
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description, String[] category,
			Plugin plugin, boolean main, String... permissions) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				main, true, permissions, category, null);
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param category category to classify the command under
	 * @param extraHelp extra help info for looking up command help
	 * @param plugin plugin that this command is for
	 * @param main if this command should be listed on the main pages
	 * @param permissions the permission(s) necessary to view this entry
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description, String category,
			String extraHelp, Plugin plugin, boolean main, String... permissions) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				main, true, permissions, new String[]{category}, extraHelp);
	}

	/**
	 * Register a command with a plugin
	 * @param command the command string
	 * @param description command description
	 * @param category categories to classify the command under
	 * @param extraHelp extra help info for looking up command help
	 * @param plugin plugin that this command is for
	 * @param main if this command should be listed on the main pages
	 * @param permissions the permission(s) necessary to view this entry
	 * @return true if command was added to the help registry <br />
	 * false if not allowed, or if the command already registered
	 */
	public boolean registerCommand(String command, String description, String[] category,
			String extraHelp, Plugin plugin, boolean main, String... permissions) {
		return helpEntries.registerCommandAPI(plugin, command, description,
				main, true, permissions, category, extraHelp);
	}

	/**
	 * Gets the help text associated with this command
	 * @param command the command to lookup
	 * @return help text, or null if none
	 */
	public String getHelp(String command) {
		throw new UnsupportedOperationException("");
	}

	/**
	 * Gets all of the commands registered with this plugin
	 * @param plugin plugin to lookup
	 * @return list of commands
	 */
	public ArrayList<String> getPluginCommands(String plugin) {
		return helpEntries.getPluginCommands(plugin);
	}

	/**
	 * Get the help entries associated with this plugin
	 * @param plugin plugin to lookup
	 * @return a copy of the plugin help entries
	 */
	public ArrayList<HelpEntry> getPluginHelp(String plugin) {
		return helpEntries.getPluginHelp(plugin);
	}
	
} // end class Help

