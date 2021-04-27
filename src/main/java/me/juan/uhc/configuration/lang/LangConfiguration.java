package me.juan.uhc.configuration.lang;

import me.juan.uhc.Main;
import me.juan.uhc.configuration.ConfigurationFile;
import me.juan.uhc.utils.ConfigCursor;
import me.juan.uhc.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

import static me.juan.uhc.utils.PluginUtil.getElement;
import static me.juan.uhc.utils.PluginUtil.setGlobalPlaceholder;


public enum LangConfiguration {

    NOT_AVAILABLE(new Message("notAvailable")),
    WELCOME(new Message("welcome")),
    FAILED_LOAD(new Message("kick.failedLoad")),

    COMMAND_ONLY_PLAYER(new Message("command.onlyPlayer")),
    COMMAND_NO_PERMISSION(new Message("command.noPermission")),

    COUNTER_SCHEDULE_TIMER(new Message("counter.schedule.timer")),
    COUNTER_SCHEDULE_END(new Message("counter.schedule.end")),

    FREEZE_ALERT(new Message("freeze.alert")),
    FREEZE_ON_UN_FREEZE(new Message("freeze.onUnFreeze")),
    FREEZE_ON_FREEZE(new Message("freeze.onFreeze")),
    FREEZE_ON_PVP(new Message("freeze.onPvP")),
    PRACTICE_JOIN(new Message("practice.join")),
    PRACTICE_NOT_AVAILABLE(new Message("practice.notAvailable")),
    PRACTICE_KILL(new Message("practice.kill")),
    PRACTICE_DEAD_DEFAULT(new Message("practice.dead.default")),
    PRACTICE_DEAD_BY_PLAYER(new Message("practice.dead.byPlayer")),
    PRACTICE_LEAVE(new Message("practice.leave"));

    /*
        PARA ENVIAR UN MENSAJE A UN USUARIO SE USARA EL MÉTODO 'get().sendPlayer(Player player)'
        ESTE MÉTODO CHEQUEARA QUE ESTE ACTIVADO Y LO ENVIARA, POR LO TANTO NO ES NECESARIO REALIZAR UN CHECK
        DE IGUAL FORMA EL GLOBAL (get().sendGlobal()), ADEMAS REEMPLAZARA LAS VARIABLES EN 'PluginUntil.setGlobalPlaceholder'

             -> LEER PluginUntil.java <-
   */

    private final Message message;

    LangConfiguration(Message message) {
        this.message = message;
    }

    public Message get() {
        return message;
    }

    public static class Message {
        private String message;

        private Message(String path) {
            FileConfig fileConfig = ConfigurationFile.LANG.getConfig();
            ConfigCursor configCursor = fileConfig.getConfigCursor("Lang." + path);
            if (getElement(boolean.class, configCursor, "enable", true))
                message = String.join("\n&f", getElement(List.class, configCursor, "message", true));
        }

        public boolean isDisable() {
            return message == null;
        }

        public String toString() {
            return toString(null);
        }

        public String toString(String playerName) {
            return toString(playerName, null);
        }

        public String toString(String playerName, String[] customVar) {
            return setGlobalPlaceholder(message, playerName, customVar);
        }

        public void sendPlayer(CommandSender player) {
            if (isDisable()) return;
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> player.sendMessage(toString(player.getName())));
        }

        public void sendPlayer(CommandSender player, String[] customVar) {
            if (isDisable()) return;
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> player.sendMessage(toString(player.getName(), customVar)));
        }

        public void sendGlobal(String[] customVar) {
            if (isDisable()) return;
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> Bukkit.broadcastMessage(toString(null, customVar)));
        }

        public void sendGlobal() {
            if (isDisable()) return;
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> Bukkit.broadcastMessage(toString()));
        }

    }


}
