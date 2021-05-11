package me.juan.uhc.commands;

import me.juan.uhc.Main;
import me.juan.uhc.configuration.lang.LangConfiguration;
import me.juan.uhc.configuration.permissions.PermissionsConfiguration;
import me.juan.uhc.manager.game.Game;
import me.juan.uhc.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Command implements CommandExecutor {

    public final boolean onlyPlayer, runAsyc;
    public final PermissionsConfiguration permission;
    public GameManager gameManager;
    public Game game;

    public Command(boolean onlyPlayer, boolean runAsyc, PermissionsConfiguration permission) {
        this.onlyPlayer = onlyPlayer;
        this.runAsyc = runAsyc;
        this.permission = permission;
    }

    private void init() {
        gameManager = GameManager.getGameManager();
        game = gameManager.getGame();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        init();
        if (onlyPlayer && !(commandSender instanceof Player)) {
            commandSender.sendMessage(LangConfiguration.COMMAND_ONLY_PLAYER.get().toString());
            return true;
        }
        Player player = getPlayer(commandSender);
        if (player != null && permission != null && !permission.contains(player)) {
            LangConfiguration.COMMAND_NO_PERMISSION.get().sendPlayer(player);
            return true;
        }
        if (runAsyc)
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> execute(strings, commandSender, player, s));
        else execute(strings, commandSender, player, s);
        return true;
    }

    public Player getPlayer(CommandSender commandSender) {
        return commandSender instanceof Player ? (Player) commandSender : null;
    }

    public abstract void execute(String[] args, CommandSender commandSender, Player player, String label);

    public void syntaxError(String arg, CommandSender commandSender) {
        commandSender.sendMessage("§cSyntax error: please use /" + arg);
    }

}
