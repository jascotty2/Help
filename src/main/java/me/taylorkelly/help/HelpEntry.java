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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpEntry implements Cloneable {

	public String command, help, extrahelp = null, key = null, filename = null;
	public boolean isMain = true, isAuto = false, visible = true;
	String[] permissions;
	public final ArrayList<String> categories = new ArrayList<String>();

	public HelpEntry() {
	}

	public HelpEntry(String command, String description,
			boolean isMain, boolean visible, boolean auto,
			String[] perms, String[] categories,
			String extraHelp, String customKey, String filename) {
		this.command = command;
		this.help = description;
		this.isMain = isMain;
		this.isAuto = auto;
		this.permissions = perms;
		if (categories != null) {
			this.categories.addAll(Arrays.asList(categories));
		}
		this.extrahelp = extraHelp;
		this.key = customKey;
		this.filename = filename;
	}

	public HelpEntry(String command, String description,
			boolean isMain, boolean visible, boolean auto,
			List<String> perms, List<String> categories,
			String extraHelp, String customKey, String filename) {
		this.command = command;
		this.help = description;
		this.isMain = isMain;
		this.isAuto = auto;
		if (perms != null) {
			this.permissions = perms.toArray(new String[0]);
		}
		if (categories != null) {
			this.categories.addAll(categories);
		}
		this.extrahelp = extraHelp;
		this.key = customKey;
		this.filename = filename;
	}

	public void clearPermissions() {
		permissions = null;
	}

	public void setPermissions(String perm) {
		permissions = new String[]{perm};
	}

	public void setPermissions(String[] perm) {
		permissions = perm;
	}

	public void addPermission(String perm) {
		if (permissions == null || permissions.length == 0) {
			permissions = new String[]{perm};
		} else {
			permissions = Arrays.asList(permissions, perm).toArray(permissions);
		}
	}

	public List<String> getPermissionList() {
		return permissions != null ? Arrays.asList(permissions) : new ArrayList<String>();
	}

	public boolean playerCanUse(CommandSender player) {
		if (permissions == null || permissions.length == 0 || !(player instanceof Player)) {
			return true;
		}
		for (String permission : permissions) {
			if (permission != null
					&& ((permission.equalsIgnoreCase("OP") && player.isOp())
					|| (HelpPermissions.permission((Player) player, permission))
					|| (permission.equalsIgnoreCase(((Player) player).getName())))) {
				return true;
			}
		}
		return false;
	}

	public boolean hasCategory(String cat) {
		for (String c : categories) {
			if (c.equalsIgnoreCase(cat)) {
				return true;
			}
		}
		return false;
	}

	public String getKey() {
		return key == null || key.isEmpty() ? command.replace(" ", "").replace("...", "?").replace(".", "?") : key;
	}

	/** 
	 * @return the name of the file this key is to be saved under <br />
	 * if none defined (and no category) returns null
	 */
	public String getFilename() {
		return filename != null ? filename : (categories.isEmpty() ? null : categories.get(0).toLowerCase() + ".yml");
	}

	public void set(HelpEntry e) {
		this.command = e.command;
		this.help = e.help;
		this.extrahelp = e.extrahelp;
		this.key = e.key;
		this.isAuto = e.isAuto;
		this.isMain = e.isMain;
		this.visible = e.visible;
		this.permissions = e.permissions == null ? null : Arrays.copyOf(e.permissions, e.permissions.length);
		this.categories.clear();
		this.categories.addAll(e.categories);
	}

	@Override
	public HelpEntry clone() {
		HelpEntry clone = new HelpEntry();
		clone.command = command;
		clone.help = help;
		clone.extrahelp = extrahelp;
		clone.key = key;
		clone.isAuto = isAuto;
		clone.isMain = isMain;
		clone.visible = visible;
		if (permissions != null) {
			clone.permissions = Arrays.copyOf(permissions, permissions.length);
		}
		clone.categories.addAll(categories);
		return clone;
	}

	public boolean isIdentical(HelpEntry other) {
		if (other != null
				&& strEqual(other.command, command)
				&& strEqual(other.help, help)
				&& strEqual(other.extrahelp, extrahelp)
				&& strEqual(other.key, key)
				&& other.isAuto == isAuto
				&& other.isMain == isMain
				&& other.visible == visible
				&& other.categories.size() == categories.size()
				&& other.permissions.length == permissions.length) {
			for (String c : other.categories) {
				if (!categories.contains(c)) {
					return false;
				}
			}
			for (String p : other.permissions) {
				boolean has = false;
				for (String p2 : permissions) {
					if (p.equals(p2)) {
						has = true;
						break;
					}
				}
				if (!has) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	private static boolean strEqual(String s1, String s2) {
		return (s1 == null && s2 == null) || (s1 != null && s2 != null && s1.equals(s2));
	}
} // end class HelpEntry

