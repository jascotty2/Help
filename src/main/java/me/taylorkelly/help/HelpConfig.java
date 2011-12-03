/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: Config settings for Help
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import me.jascotty2.lib1.MinecraftChatStr;
import me.taylorkelly.help.enums.DisplayFormat;
import me.taylorkelly.help.utils.HelpLogger;
import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class HelpConfig {

	protected static File dataFolder = null;
	final Help plugin;
	public boolean allowPluginOverride = false, // allow a plugin to overwrite help entries
			allowPluginHelp = true, // if plugins can pass Help custom entries
			savePluginHelp = false, // if the help entries registered should be saved
			sortPluginHelp = true, // if added entries should also be sorted (by command string)
			checkActive = true,   // if should check if the entries go to a valid & enabled plugin
			autoHelp = true;	// if should look for custom help from plugin jars
	public DisplayFormat displayStyle = DisplayFormat.COLUMN;
	public int linesPerPage = 9;
	public long saveDelay = 10 * 1000; // 10 seconds
	ChatColor commandColor = ChatColor.RED,
			commandBracketColor = ChatColor.GRAY,
			descriptionColor = ChatColor.WHITE,
			introDashColor = ChatColor.GOLD,
			introTextColor = ChatColor.WHITE;

	public HelpConfig(Help helpPlugin) {
		this.plugin = helpPlugin;
		dataFolder = plugin.getDataFolder();
		if (dataFolder == null) {
			dataFolder = new File("plugins" + File.separatorChar + Help.name);
		}
		if (!legacyCheck()) {
			load();
		}
	} // end default constructor

	public final void load() {
		File configFile = new File(dataFolder, "config.yml");
		if (!configFile.exists()) {
			extractFile(configFile);
			return;
		}
		// else
		try {
			Configuration config = new Configuration(configFile);//plugin.getConfiguration();
			config.load();
			ConfigurationNode n = config.getNode("settings");
			if(n!=null){
				allowPluginOverride = n.getBoolean("allowPluginOverride", allowPluginOverride);
				allowPluginHelp = n.getBoolean("allowPluginHelp", allowPluginHelp);
				savePluginHelp = n.getBoolean("savePluginHelp", savePluginHelp);
				sortPluginHelp = n.getBoolean("sortPluginHelp", sortPluginHelp);
				autoHelp = n.getBoolean("autoHelp", autoHelp);
			}
			if ((n = config.getNode("display")) != null) {
				linesPerPage = n.getInt("linesPerPage", linesPerPage);
				String format = n.getString("style");
				if (format != null) {
					format = format.toLowerCase().trim();
					if (format.equals("oneline")) {
						displayStyle = DisplayFormat.ONE_LINE;
					} else if (format.equals("text")) {
						displayStyle = DisplayFormat.TEXT;
					} else if (format.equals("wrap")) {
						displayStyle = DisplayFormat.WRAP;
					} else if (format.equals("column") || format.equals("default")) {
						displayStyle = DisplayFormat.COLUMN;
					} else {
						HelpLogger.Warning("Invalid entry for display.style: " + format);
					}
				}

				checkActive = n.getBoolean("checkActive", checkActive);
				
				commandColor = getColor(config, "display.commandColor", commandColor);
				commandBracketColor = getColor(config, "display.commandBracketColor", commandBracketColor);
				descriptionColor = getColor(config, "display.descriptionColor", descriptionColor);
				introDashColor = getColor(config, "display.introDashColor", introDashColor);
				introTextColor = getColor(config, "display.introTextColor", introTextColor);
			}
		} catch (Exception e) {
			HelpLogger.Log(Level.SEVERE, "Error loading Settings", e);
		}
	}

	protected ChatColor getColor(Configuration config, String key, ChatColor err){
		String col = config.getString(key);
		if(col != null) {
			ChatColor c = MinecraftChatStr.getChatColor(col);
		}
		return err;
	}

	public final boolean legacyCheck() {
		File oldFile = new File(dataFolder, "Help.yml");
		if (oldFile.exists()) {
			try {
				Configuration config = new Configuration(oldFile);
				config.load();

				linesPerPage = config.getInt("entriesPerPage", linesPerPage);
				allowPluginOverride = config.getBoolean("allowPluginOverride", allowPluginOverride);
				allowPluginHelp = config.getBoolean("allowPluginHelp", allowPluginHelp);
				savePluginHelp = config.getBoolean("savePluginHelp", savePluginHelp);
				sortPluginHelp = config.getBoolean("sortPluginHelp", sortPluginHelp);

				boolean shortenEntries = config.getBoolean("shortenEntries", false);
				boolean useWordWrap = config.getBoolean("useWordWrap", true);
				boolean wordWrapRight = config.getBoolean("wordWrapRight", true);

				if (shortenEntries) {
					displayStyle = DisplayFormat.ONE_LINE;
				} else if (!useWordWrap) {
					displayStyle = DisplayFormat.TEXT;
				} else if (!wordWrapRight) {
					displayStyle = DisplayFormat.WRAP;
				} else {
					displayStyle = DisplayFormat.COLUMN;
				}
			} catch (Throwable t) {
			} finally {
				oldFile.renameTo(new File(dataFolder, "Help.yml.old"));
			}
			return true;
		}
		return false;
	}

	private void extractFile(File dest) {
		extractFile(dest, dest.getName());
	}

	private void extractFile(File dest, String fname) {
		try {
			dest.createNewFile();
			InputStream res = Help.class.getResourceAsStream("/" + fname);
			FileWriter tx = new FileWriter(dest);
			try {
				for (int i = 0; (i = res.read()) > 0;) {
					tx.write(i);
				}
			} finally {
				tx.flush();
				tx.close();
				res.close();
			}
		} catch (IOException ex) {
			HelpLogger.Log(Level.SEVERE, "Failed creating new file (" + fname + ")", ex);
		}
	}
} // end class Config

