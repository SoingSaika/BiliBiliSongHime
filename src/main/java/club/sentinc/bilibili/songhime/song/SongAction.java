package club.sentinc.bilibili.songhime.song;

import club.sentinc.bilibili.songhime.config.SongHimeConfig.Action;

public class SongAction {

    public final Action action;

    public final String simplifyCommand;

    public SongAction(Action action, String simplifyCommand) {
        this.action = action;
        this.simplifyCommand = simplifyCommand;
    }
}
