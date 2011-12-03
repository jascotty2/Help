
package me.taylorkelly.oldhelp;

import java.util.ArrayList;

public class MatchList {

    public ArrayList<HelpEntry> commandMatches;
    public ArrayList<HelpEntry> pluginExactMatches;
    public ArrayList<HelpEntry> pluginPartialMatches;
    public ArrayList<HelpEntry> descriptionMatches;

    public MatchList(ArrayList<HelpEntry> commandMatches, ArrayList<HelpEntry> pluginExactMatches, ArrayList<HelpEntry> pluginPartialMatches, ArrayList<HelpEntry> descriptionMatches) {
        this.commandMatches = commandMatches;
        this.pluginExactMatches = pluginExactMatches;
        this.pluginPartialMatches = pluginPartialMatches;
        this.descriptionMatches = descriptionMatches;
    }

    public int size() {
        return commandMatches.size() + pluginExactMatches.size() + pluginPartialMatches.size() + descriptionMatches.size();
    }
}
