/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: ( TODO )
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

import me.taylorkelly.help.utils.HelpLogger;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

public class HelpAutoLoadListener extends ServerListener{

	final Help plugin;

    public HelpAutoLoadListener(Help helpPlugin) {
		this.plugin = helpPlugin;
    } // end default constructor

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if(plugin.helpEntries.lastRegisteredNumChanged > 0) {
			HelpLogger.Info(plugin.helpEntries.lastRegisteredNumChanged + " Entried registered by " + plugin.helpEntries.lastRegisteredPlugin);
		}
		if(plugin.config.autoHelp) {
			plugin.helpEntries.autoRegisterPlugin(event.getPlugin());
		}
	}

} // end class HelpAutoLoadListener
