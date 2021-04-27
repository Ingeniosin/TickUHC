package me.juan.uhc.commands;

import me.juan.uhc.configuration.permissions.PermissionsConfiguration;
import me.juan.uhc.manager.FrozenManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand extends Command {

    public FreezeCommand() {
        super(false, true, PermissionsConfiguration.MOD);
    }

    @Override
    public void execute(String[] args, CommandSender commandSender, Player player, String label) {
        if (args.length < 1) {
            syntaxError(label + " <player>", commandSender);
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("Player '" + args[0] + "' is not online right now!");
            return;
        }
        commandSender.sendMessage(FrozenManager.getFrozenManager().setFrozen(target) ? "§aThe player " + target.getDisplayName() + " is now frozen." : "§aThe player " + target.getDisplayName() + " is now un-frozen.");
    }
}
