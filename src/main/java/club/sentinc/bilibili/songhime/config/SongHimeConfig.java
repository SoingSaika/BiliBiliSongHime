package club.sentinc.bilibili.songhime.config;

import club.sentinc.bilibili.songhime.song.SongAction;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class SongHimeConfig {

    @JSONField(name = "room")
    private Integer room;

    @JSONField(name = "connect_timeout")
    private Integer connectTimeout;

    @JSONField(name = "ui")
    private String ui;

    @JSONField(name = "download_path")
    private String downloadPath;

    @JSONField(name = "action")
    private List<Action> actions;

    @JSONField(name = "action_prefix")
    private String actionPrefix;

    @JSONField(name = "music_login_phone")
    private String loginPhone;

    @JSONField(name = "music_login_password")
    private String loginPassword;

    public SongHimeConfig() {
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getActionPrefix() {
        return actionPrefix;
    }

    public void setActionPrefix(String actionPrefix) {
        this.actionPrefix = actionPrefix;
    }

    public String getLoginPhone() {
        return loginPhone;
    }

    public void setLoginPhone(String loginPhone) {
        this.loginPhone = loginPhone;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public Optional<SongAction> containsAction(String actionCommand) {
        if(actionPrefix != null) {
            if(actionCommand.startsWith(actionPrefix)) {
                actionCommand = actionCommand.substring(actionPrefix.length());
            } else {
                return Optional.empty();
            }
        }
        for (Action action : actions) {
            if(actionCommand.startsWith(action.command)) {
                if (action.enabled) {
                    return Optional.of(new SongAction(action, actionCommand.substring(action.command.length())));
                } else {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    public static class Action {

        @JSONField(name = "command")
        private String command;
        @JSONField(name = "enabled")
        private Boolean enabled;
        @JSONField(name = "action")
        private String action;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}
