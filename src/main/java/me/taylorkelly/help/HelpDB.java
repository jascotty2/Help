/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: for loading, retrieving, and saving help entries
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import me.jascotty2.lib1.Str;
import me.taylorkelly.help.utils.HelpLogger;
import me.taylorkelly.help.utils.YmlFilter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;

public class HelpDB {

	final Help plugin;
	File helpSaveFolder = new File(HelpConfig.dataFolder, "ExtraHelp");
	final ArrayList<String> sortedPlugins = new ArrayList<String>();
	final HashMap<String, ArrayList<HelpEntry>> helplist = new HashMap<String, ArrayList<HelpEntry>>();
	final ArrayList<String> changed = new ArrayList<String>();
	private Timer saveTimer = new Timer(true);
	String lastRegisteredPlugin = null;
	int lastRegisteredNumChanged = 0;

	public HelpDB(Help helpPlugin) {
		this.plugin = helpPlugin;
	} // end default constructor

	public void load() {
		try {
			for (ArrayList<HelpEntry> e : helplist.values()) {
				e.clear();
			}
			helplist.clear();

			int nLoaded = 0;
			File helpFolder = new File(HelpConfig.dataFolder, "ExtraHelp");
			if (!helpFolder.exists()) {
				helpFolder.mkdirs();
			} else if (helpFolder.isDirectory()) {
				File files[] = YmlFilter.listRecursively(helpFolder, 1);
				final String folder = helpFolder.getAbsolutePath() + File.separator;
				if (files == null) {
					return;
				}
				// sort files
				Arrays.sort(files, new Comparator<File>() {

					public int compare(File o1, File o2) {
						if (o1.isDirectory() || o2.isDirectory()) {
							return (o1.isDirectory() && o2.isDirectory()) ? o1.getName().compareToIgnoreCase(o2.getName()) : 0; // ignore ordering of folders; file listing is what is special ordered
						}
						String fn1 = o1.getAbsolutePath().substring(folder.length());
						String d1 = fn1.substring(0, fn1.indexOf(File.separator));
						String fn2 = o2.getAbsolutePath().substring(folder.length());
						String d2 = fn2.substring(0, fn2.indexOf(File.separator));
						if (d1.equals(d2)) {
							String f1 = o1.getName().toLowerCase().replaceFirst("\\.yml$", "");
							if (fn1.contains(File.separator)) {
								// is name of dir
								if (f1.equalsIgnoreCase(d1)) {
									return -1;
								}
							}
							String f2 = o2.getName().toLowerCase().replaceFirst("\\.yml$", "");
							if (fn2.contains(File.separator)) {
								// is name of dir
								if (f2.equalsIgnoreCase(d2)) {
									return 1;
								}
							}
						}

						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});

				HashMap<String, Integer> filesLoaded = new HashMap<String, Integer>();
				for (File insideFile : files) {
					String fileName, fn = insideFile.getAbsolutePath();
					if (fn.length() > folder.length()) {
						fn = fn.substring(folder.length());
						// if is a directory, use that as the name instead
						if (fn.contains(File.separator)) {
							fileName = fn.substring(0, fn.indexOf(File.separator));
						} else {
							fileName = insideFile.getName().replaceFirst(".yml$", "");
						}
					} else {
						fileName = insideFile.getName().replaceFirst(".yml$", "");
					}

					final Yaml yaml = new Yaml(new SafeConstructor());
					Map<String, Object> root = null;
					FileInputStream input = null;
					try {
						input = new FileInputStream(insideFile);
						root = (Map<String, Object>) yaml.load(new UnicodeReader(input));
						if (root == null || root.isEmpty()) {
							System.out.println("The file " + fn + " is empty");
							continue;
						}
					} catch (Exception ex) {
						//HelpLogger.severe("Error!", ex);
						String err = Str.getStackStr(ex), er = ex.getStackTrace()[0].toString();
						if (err.contains(er)) {
							err = err.substring(0, err.indexOf(er));
						}
						if (err.contains("at")) {
							err = err.substring(0, err.lastIndexOf("at"));
							err = err.substring(0, err.lastIndexOf("\n"));
						}
						HelpLogger.Severe("Error loading " + fn + "\n" + err);
					} finally {
						if (input != null) {
							try {
								input.close();
							} catch (IOException ex) {
							}
						}
					}
					if (root != null) {
						int num = 0;
						ArrayList<HelpEntry> pluginhelp = getPluginHelpCreate(fileName);
						for (String helpKey : root.keySet()) {
							Map<String, Object> helpNode = (Map<String, Object>) root.get(helpKey);
							Object com, des;
							if ((com = helpNode.get("command")) == null || !(com instanceof String)) {
								HelpLogger.Warning("Help entry node \"" + helpKey + "\" is missing a command name in " + fn);
							} else if ((des = helpNode.get("description")) == null || !(des instanceof String)) {
								HelpLogger.Warning("Help entry node \"" + helpKey + "\" is missing a description in " + fn);
							} else {

								String command = (String) com;

								HelpEntry e = getCommand(pluginhelp, command);
								if (e != null) {
									// add filename as a category
									boolean n = false;
									Object cat = helpNode.get("categories");
									if (cat == null) {
										cat = helpNode.get("category");
										if (cat == null) {
											// plugin can also be the command category (legacy support..)
											cat = helpNode.get("plugin");
											if (cat == null) {
												cat = fn.replaceFirst("\\.yml$", "");
											}
										}
									}

									if (cat != null) {
										if (cat instanceof List) {
											for (Object c : (List) cat) {
												if (!e.hasCategory(c.toString())) {
													e.categories.add(c.toString());
													n = true;
												}
											}
										} else if (!e.hasCategory(cat.toString())) {
											e.categories.add(cat.toString());
											n = true;
										}
									}
									if (!n) {
										HelpLogger.Warning("Help entry node \"" + helpKey + "\" has a duplicate command name ('" + command + "') \n"
												+ "\tin " + fn + "  (first in " + e.getFilename() + " : " + e.key + ")");
									}
									continue;
								}

								String description = (String) des;

								String extra = null;
								boolean main = false,
										visible = true,
										auto = false;
								ArrayList<String> permissions = new ArrayList<String>(),
										categories = new ArrayList<String>();

								if (helpNode.containsKey("extra")) {
									extra = helpNode.get("extra").toString();
								}

								if (helpNode.containsKey("main")) {
									if (helpNode.get("main") instanceof Boolean) {
										main = (Boolean) helpNode.get("main");
									} else {
										HelpLogger.Warning(command + "'s Help entry has 'main' as a non-boolean in " + fn + ". Defaulting to false");
									}
								}

								if (helpNode.containsKey("visible")) {
									if (helpNode.get("visible") instanceof Boolean) {
										visible = (Boolean) helpNode.get("visible");
									} else {
										HelpLogger.Warning(command + "'s Help entry has 'visible' as a non-boolean in " + fn + ". Defaulting to true");
									}
								}

								if (helpNode.containsKey("auto")) {
									if (helpNode.get("auto") instanceof Boolean) {
										auto = (Boolean) helpNode.get("auto");
									} else {
										HelpLogger.Warning(command + "'s Help entry has 'auto' as a non-boolean in " + fn + ". Defaulting to false");
									}
								}

								if (helpNode.containsKey("permissions")) {
									if (helpNode.get("permissions") instanceof List) {
										for (Object permission : (List) helpNode.get("permissions")) {
											permissions.add(permission.toString());
										}
									} else {
										permissions.add(helpNode.get("permissions").toString());
									}
								}
								Object cat = helpNode.get("categories");
								if (cat == null) {
									cat = helpNode.get("category");
									if (cat == null) {
										// plugin can also be the command category (legacy support..)
										cat = helpNode.get("plugin");
									}
								}

								if (cat != null) {
									if (cat instanceof List) {
										for (Object c : (List) cat) {
											categories.add(c.toString());
										}
									} else {
										categories.add(cat.toString());
									}
								}
								e = new HelpEntry(command, description,
										main, visible, auto,
										permissions, categories,
										extra, helpKey, insideFile.getName());

								pluginhelp.add(e);

								++num;
								++nLoaded;
							}
						}
						if (filesLoaded.containsKey(fileName)) {
							filesLoaded.put(fileName, filesLoaded.get(fileName) + num);
						} else {
							filesLoaded.put(fileName, num);
						}
					}
				}
				String loaded = "";
				String ks[] = filesLoaded.keySet().toArray(new String[0]);
				Arrays.sort(ks, new Comparator<String>() {

					public int compare(String o1, String o2) {
						return o1.compareToIgnoreCase(o2);
					}
				});
				for (String f : ks) {
					loaded += String.format("%s(%d), ", f, filesLoaded.get(f));
				}
				//HelpLogger.info(nLoaded + " extra help entries loaded" + (filesLoaded.length()>2 ? " from files: " + filesLoaded.replaceFirst(", $", "") : ""));
				HelpLogger.Info(nLoaded + " extra help entries loaded" + (loaded.length() > 2 ? " from files: " + loaded.substring(0, loaded.length() - 2) : ""));
			} else {
				HelpLogger.Warning("Error: ExtraHelp is a file");
			}
		} catch (Throwable ex) {
			HelpLogger.Severe("Internal Error while loading help", ex);
		}
	}

	public void autoLoad() {
		for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
			if (p.isEnabled()) {
				autoRegisterPlugin(p);
			}
		}
		if (plugin.config.savePluginHelp) {
			delaySave();
		}
	}

	@SuppressWarnings("unchecked")
	public void autoRegisterPlugin(Plugin p) {
		int nLoaded = 0;
		// retrieve plugin description file for current plugin
		final PluginDescriptionFile descriptionFile = p.getDescription();

		final String pname = p.getDescription().getName();

		// try and get commands from plugin
		final Object commandObject = descriptionFile.getCommands();
		try {
			if (commandObject instanceof LinkedHashMap && !((LinkedHashMap) commandObject).isEmpty()) {
				// register the main command if there is no custom help file
				if (!helplist.containsKey(p.getDescription().getName())) {
					ArrayList<HelpEntry> pluginhelp = getPluginHelpCreate(pname);
					pluginhelp.add(new HelpEntry("help " + pname, "Help for all " + pname + " commands",
							true, true, true,
							null, new String[]{"plugins"},
							null, null, null));
					if (!changed.contains(pname)) {
						changed.add(pname);
					}
				}
				ArrayList<HelpEntry> pHelp = getPluginHelp(pname);
				if (pHelp == null) {
					pHelp = new ArrayList<HelpEntry>();
					helplist.put(pname, pHelp);
				}
				for (Map.Entry<String, LinkedHashMap<String, Object>> entry :
						((LinkedHashMap<String, LinkedHashMap<String, Object>>) commandObject).entrySet()) {
					LinkedHashMap<String, Object> commandInfo = entry.getValue();
					String commandName = entry.getKey();
					Object usage = commandInfo.get("usage");
					String desc = (String) commandInfo.get("description");

					for (String com : (usage instanceof String && ((String) usage).length() > 2 ? (String) usage : commandName).split("\n")) {
						if (com.toLowerCase().contains("/<command>")) {//.startsWith("/<command>")) {
							com = commandName + com.substring(com.toLowerCase().indexOf("/<command>") + 10);
						} else if (com.toLowerCase().startsWith("<command>")) {
							com = commandName + com.substring(9);
						}
						if (com.startsWith("/") && !commandName.startsWith("/")) {
							com = com.substring(1);
						}

						// some use the usage to display help, with delimiters
						if (com.contains(" | ")) {
							desc = com.substring(com.indexOf(" | ") + 3);
							com = com.substring(0, com.indexOf(" | "));
						} else if (com.contains(" - ")) {
							desc = com.substring(com.indexOf(" - ") + 3);
							com = com.substring(0, com.indexOf(" - "));
						}

						if (desc != null && !desc.isEmpty()) {
							HelpEntry e = getCommand(pHelp, com);
							boolean ch = false;
							if (e == null) {
								e = new HelpEntry();
								e.isAuto = true;
								e.command = com;
								pHelp.add(e);
								++nLoaded;
								ch = true;
							}
							if (e.isAuto) {
								if (!ch && (e.help == null || !e.help.equals(desc))) {
									++nLoaded;
									ch = true;
								}
								e.help = desc;
								ArrayList<String> permissions = new ArrayList<String>();
								if (commandInfo.containsKey("permission")) {
									permissions.add((String) commandInfo.get("permission"));
								} else if (commandInfo.containsKey("permissions")) {
									Object pr = commandInfo.get("permissions");
									if (pr instanceof String) {
										permissions.add((String) pr);
									} else if (pr instanceof List) {
										permissions.addAll((List) pr);
									}
								}
								if (!ch && (e.permissions != null && e.permissions.length != permissions.size())) {
									if (e.permissions != null) {
										// check if changed
										for (String pr : e.permissions) {
											if (!permissions.contains(pr)) {
												++nLoaded;
												ch = true;
												break;
											}
										}
									} else {
										++nLoaded;
										ch = true;
									}
								}
								e.clearPermissions();
								e.permissions = permissions.toArray(new String[0]);
								//++nLoaded;

								//System.out.println(com + " - " + e.help);
								if (!changed.contains(pname)) {
									changed.add(pname);
								}
							}
						}
					}
				}
			}// end if
			if (nLoaded > 0) {
				HelpLogger.Info(nLoaded + " Entries autoloaded for " + descriptionFile.getFullName());
				if (plugin.config.savePluginHelp) {
					delaySave();
				}
			}
		} catch (Exception e) {
			HelpLogger.Log("There is no help available for plugin '"
					+ descriptionFile.getFullName() + "'", e);
		}// end try/catch
	}

	/**
	 * Gets all of the commands registered with this plugin
	 * @param plugin plugin to lookup
	 * @return list of commands
	 */
	public ArrayList<String> getPluginCommands(String plugin) {
		ArrayList<String> ret = new ArrayList<String>();
		if (plugin != null) {
			ArrayList<HelpEntry> ph = getPluginHelp(plugin);
			if (ph != null) {
				for (HelpEntry entry : ph) {
					ret.add(entry.command);
				}
			}
		}
		return ret;
	}

	public ArrayList<HelpEntry> getPluginHelp(String plugin) {
		if (plugin != null && !helplist.containsKey(plugin)) {
			for (String k : helplist.keySet()) {
				if (k.equalsIgnoreCase(plugin)) {
					plugin = k;
					break;
				}
			}
		}
		return helplist.get(plugin);
	}

	protected ArrayList<HelpEntry> getPluginHelpCreate(String plugin) {
		if (plugin != null && !helplist.containsKey(plugin)) {
			for (String k : helplist.keySet()) {
				if (k.equalsIgnoreCase(plugin)) {
					plugin = k;
					break;
				}
			}
		}
		if (!helplist.containsKey(plugin)) {
			ArrayList<HelpEntry> n = new ArrayList<HelpEntry>();
			helplist.put(plugin, n);
			return n;
		}
		return helplist.get(plugin);
	}

	protected HelpEntry getCommand(ArrayList<HelpEntry> pluginHelp, String command) {
		for (HelpEntry e : pluginHelp) {
			if (safeCommName(e.command).equalsIgnoreCase(safeCommName(command))) {
				return e;
			}
		}
		return null;
	}

	protected String safeCommName(String command) {
		return command == null ? "null" : command.trim().replace("  ", " ").
				//				replace("<", "_").replace(">", "_").
				//				replace("[", "_").replace("]", "_").
				//				replace("(", "_").replace(")", "_")
				//replaceAll("[\\[\\(\\<].*?[\\]\\)\\>]", "[p]");
				replaceAll("\\[.*?\\]", "[p]").
				replaceAll("\\(.*?\\)", "[p]").
				replaceAll("\\<.*?\\>", "[p]");
	}

	public void delaySave() {
		//saveTimer.purge();
		saveTimer.cancel();
		saveTimer = new Timer(true);
		saveTimer.schedule(new TimerTask() {

			public void run() {
				save();
			}
		}, plugin.config.saveDelay);
	}

	public void save() {
		for (String pname : changed) {
			try {
				ArrayList<HelpEntry> listing = getPluginHelp(pname);
				if (listing != null) {
					File defFile = null;
					File saveFolder = new File(helpSaveFolder, pname + ".yml");
					if (!saveFolder.exists()) {
						saveFolder = new File(helpSaveFolder, pname.toLowerCase() + ".yml");
						if (!saveFolder.exists()) {
							saveFolder = new File(helpSaveFolder, pname.toLowerCase());
							if (!saveFolder.exists()) {
								saveFolder = new File(helpSaveFolder, pname);
							}
							if (!saveFolder.exists() && !saveFolder.mkdirs()) {
								HelpLogger.Severe("Failed to create directory: " + saveFolder.getAbsolutePath());
								continue;
							}
						} else {
							defFile = saveFolder;
						}
					} else {
						defFile = saveFolder;
					}
					// compile a file:entry map
					HashMap<File, ArrayList<HelpEntry>> fileListings = new HashMap<File, ArrayList<HelpEntry>>();
					if (defFile != null) {
						// if pluginname is a file, is where all is saved
						fileListings.put(defFile, listing);
					} else {
						for (HelpEntry e : listing) {
							File f = null;
							if (e.filename == null) {
								f = defFile != null ? defFile : (defFile = getSaveFileCreate(pname));
							} else {
								f = new File(saveFolder, e.filename);
							}
							ArrayList<HelpEntry> l = fileListings.get(f);
							if (l == null) {
								l = new ArrayList<HelpEntry>();
								fileListings.put(f, l);
							}
							l.add(e);
						}
					}
					// save files
					for (Map.Entry<File, ArrayList<HelpEntry>> e : fileListings.entrySet()) {
						File f = e.getKey();
						if (f.exists()) {
							f.delete();
						}
						Configuration c = new Configuration(f);
						for (HelpEntry he : e.getValue()) {
							String k = he.getKey();
							c.setProperty(k + ".command", he.command);
							c.setProperty(k + ".description", he.help);
							if (he.extrahelp != null) {
								c.setProperty(k + ".extra", he.extrahelp);
							}
							c.setProperty(k + ".main", he.isMain);

							if (!he.visible) {
								c.setProperty(k + ".visible", he.visible);
							}

							if (he.isAuto) {
								c.setProperty(k + ".auto", he.isAuto);
							}

							c.setProperty(k + ".categories", he.categories);
							c.setProperty(k + ".permissions", he.getPermissionList());

						}
						c.save();
					}
				}
			} catch (Exception e) {
				HelpLogger.Severe("Failed to save entries for " + pname, e);
			}
		}
		changed.clear();
	}

	protected File getSaveFileCreate(String pluginName) throws IOException {
		// getting the default filename:
		// if pluginname is a folder, scan the folder for files
		/// if there is only one, set as default
		/// else, look for (in order) pluginname, main, commands
		/// else, get the closest match to pluginname
		File pluginFolder = new File(helpSaveFolder, pluginName);
		File[] dir = pluginFolder.exists() ? YmlFilter.listRecursively(pluginFolder, 0) : null;
		if (!pluginFolder.exists() || dir.length == 0) {
			pluginFolder.mkdirs();
			File f = new File(pluginFolder, pluginName.toLowerCase() + ".yml");
			f.createNewFile();
			return f;
		}
		for (File f : dir) {
			if (f.getName().replaceFirst(".yml$", "").equalsIgnoreCase(pluginName)) {
				return f;
			}
		}
		for (File f : dir) {
			if (f.getName().replaceFirst(".yml$", "").equalsIgnoreCase("main")) {
				return f;
			}
		}
		for (File f : dir) {
			if (f.getName().replaceFirst(".yml$", "").equalsIgnoreCase("commands")) {
				return f;
			}
		}
		// now find closest match
		int mini = 0, ds, min;
		ds = min = Str.getLevenshteinDistance(dir[0].getName().replaceFirst(".yml$", ""), pluginName);
		for (int i = 1; i < dir.length; ++i) {
			ds = Str.getLevenshteinDistance(dir[i].getName().replaceFirst(".yml$", ""), pluginName);
			if (ds < min) {
				min = ds;
				mini = i;
			}
		}
		return dir[mini];
	}

	/**
	 * called when a plugin has a custom help register call
	 * @param p plugin to register
	 * @param command command
	 * @param description description
	 * @param isMain if is on main pages
	 * @param perms permissions to view the entry
	 * @param extraHelp extra help info
	 */
	public boolean registerCommandAPI(Plugin p, String command, String description,
			boolean isMain, boolean visible, String[] perms, String[] categories, String extraHelp) {
		if (plugin.config.allowPluginHelp) {
			try{
			if (p == null) {
				throw new IllegalArgumentException("Help: plugin cannot be null");
			} else if (command == null || description == null) {
				HelpLogger.Warning("Plugin Command Register Error: " + p.getDescription().getFullName() + " is trying to register a null command / description");
				return false;
			}
			String pname = p.getDescription().getName();
			if (!pname.equals(lastRegisteredPlugin)) {
				lastRegisteredPlugin = pname;
				lastRegisteredNumChanged = 0;
			}
			HelpEntry ne = new HelpEntry(command, description,
					isMain, visible, true,
					perms, categories,
					extraHelp, null, null);
			ArrayList<HelpEntry> pluginhelp = getPluginHelpCreate(pname);
			HelpEntry e = getCommand(pluginhelp, command);
			if (e == null) {
				pluginhelp.add(ne);
				++lastRegisteredNumChanged;
			} else if (e.isAuto || plugin.config.allowPluginOverride) {
				if (!e.isIdentical(ne)) {
					++lastRegisteredNumChanged;
				}
				e.set(ne);
			} else {
				return false;
			}

			if (!changed.contains(pname)) {
				changed.add(pname);
			}
			if (plugin.config.savePluginHelp) {
				delaySave();
			}
			}catch(Throwable t) {
				HelpLogger.Severe("Error Registering Plugin Help", t);
			}
		}
		return false;
	}
} // end class HelpDB

