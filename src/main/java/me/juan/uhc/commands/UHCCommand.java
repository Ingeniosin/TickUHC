package me.juan.uhc.commands;

import me.juan.uhc.configuration.lang.LangConfiguration;
import me.juan.uhc.manager.game.task.CountDownTask;
import me.juan.uhc.manager.game.task.GameTaskStatus;
import me.juan.uhc.menu.ConfigurationMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UHCCommand extends me.juan.uhc.commands.Command {

    public UHCCommand() {
        super(true, false, null);
    }

    @Override
    public void execute(String[] args, CommandSender commandSender, Player player, String label) {
        if (gameManager.isGenerating()) {
            LangConfiguration.NOT_AVAILABLE.get().sendPlayer(player);
            return;
        }
        if (args.length < 1) {
            syntaxError(label + " <start/clearcenter/forcedeathmatch/mobcount/config/mod/host/sethologram/removehologram/forceshrink>", player);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "configuration":
                ConfigurationMenu.configurationMenu(player);
                return;
            case "scenarios":
                ConfigurationMenu.scenarioManageMenu(player);
                return;
            case "startcount":
                new CountDownTask(120, GameTaskStatus.STARTING);
                return;
            case "start":

                return;
        }
        syntaxError(label + " <start/clearcenter/forcedeathmatch/mobcount/config/mod/host/sethologram/removehologram/forceshrink>", player);
    }


}
