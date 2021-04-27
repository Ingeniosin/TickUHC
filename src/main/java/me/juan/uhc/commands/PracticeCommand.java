package me.juan.uhc.commands;

import me.juan.uhc.manager.PracticeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.juan.uhc.configuration.permissions.PermissionsConfiguration.HOST;

public class PracticeCommand extends Command {


    public PracticeCommand() {
        super(true, false, null);
    }

    @Override
    public void execute(String[] args, CommandSender commandSender, Player player, String label) {
        if (args.length >= 1 && HOST.contains(player) && args[0].equalsIgnoreCase("toggle")) {
            PracticeManager.toggle(player);
            return;
        }
        PracticeManager.handle(player);
    }
}
