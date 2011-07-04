package me.taylorkelly.help;

import com.jascotty2.Str;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;

public class HelpLoader {

    public static void load(File dataFolder, HelpList list) {
        File helpFolder = new File(dataFolder, "ExtraHelp");
        if (!helpFolder.exists()) {
            helpFolder.mkdirs();
        } else if (helpFolder.isDirectory()) {
            int count = 0;
            //File files[] = helpFolder.listFiles(new YmlFilter());
            File files[] = YmlFilter.listRecursively(helpFolder, 1);
            String folder = helpFolder.getAbsolutePath() + File.separator;
            if (files == null) {
                return;
            }
            HashMap<String, Integer> filesLoaded = new HashMap <String, Integer>();
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
                    HelpLogger.severe("Error loading " + fn
                            + "\n" + err); //ex.getMessage()
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
                    for (String helpKey : root.keySet()) {
                        Map<String, Object> helpNode = (Map<String, Object>) root.get(helpKey);

                        if (!helpNode.containsKey("command")) {
                            HelpLogger.warning("Help entry node \"" + helpKey + "\" is missing a command name in " + fn);
                            continue;
                        }
                        if (!helpNode.containsKey("description")) {
                            HelpLogger.warning("Help entry node \"" + helpKey + "\" is missing a description in " + fn);
                            continue;
                        }

                        String command = helpNode.get("command").toString();
                        boolean main = false;
                        String description = helpNode.get("description").toString();
                        boolean visible = true;
                        ArrayList<String> permissions = new ArrayList<String>();

                        if (helpNode.containsKey("main")) {
                            if (helpNode.get("main") instanceof Boolean) {
                                main = (Boolean) helpNode.get("main");
                            } else {
                                HelpLogger.warning(command + "'s Help entry has 'main' as a non-boolean in " + fn + ". Defaulting to false");
                            }
                        }

                        if (helpNode.containsKey("visible")) {
                            if (helpNode.get("visible") instanceof Boolean) {
                                visible = (Boolean) helpNode.get("visible");
                            } else {
                                HelpLogger.warning(command + "'s Help entry has 'visible' as a non-boolean in " + fn + ". Defaulting to true");
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
                        list.customRegisterCommand(command, description, fileName, main, permissions.toArray(new String[0]), visible);
                        ++num;
                        ++count;
                    }
                    if(filesLoaded.containsKey(fileName)){
                        filesLoaded.put(fileName,filesLoaded.get(fileName)+num );
                    }else{
                        filesLoaded.put(fileName,num );
                    }
                    //filesLoaded += fileName + String.format("(%d), ", num);
                }
            }
            String loaded = "";
            for(String f : filesLoaded.keySet()){ //Arrays.sort()
                loaded += String.format("%s(%d), ", f, filesLoaded.get(f));
            }
            //HelpLogger.info(count + " extra help entries loaded" + (filesLoaded.length()>2 ? " from files: " + filesLoaded.replaceFirst(", $", "") : ""));
            HelpLogger.info(count + " extra help entries loaded" + (loaded.length() > 2 ? " from files: " + loaded.substring(0, loaded.length() - 2) : ""));
        } else {
            HelpLogger.warning("Error: ExtraHelp is a file");
        }
        LegacyHelpLoader.load(dataFolder, list);
    }
}
