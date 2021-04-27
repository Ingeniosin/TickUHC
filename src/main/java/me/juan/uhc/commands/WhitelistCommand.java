package me.juan.uhc.commands;

import me.juan.uhc.configuration.permissions.PermissionsConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhitelistCommand extends Command {

    public WhitelistCommand() {
        super(false, true, PermissionsConfiguration.WHITELIST_COMMAND);
    }

    @Override
    public void execute(String[] args, CommandSender commandSender, Player player, String label) {
        String syntaxMessage = " <add" + (player != null && PermissionsConfiguration.GENERAL_MOD.contains(player) ? "/remove/on/off" : "") + "> <player>";
        if (args.length < 1) {
            syntaxError(label + syntaxMessage, commandSender);
            return;
        }
        String arg = args[0].toLowerCase();
        switch (arg) {
            case "add":
            case "remove":
                if (args.length < 2) {
                    syntaxError(label + syntaxMessage, commandSender);
                    return;
                }
                boolean isAdd = arg.equals("add");
                String target = args[1].toLowerCase();
                if (!game.isWhitelist()) {
                    commandSender.sendMessage("§cThe whitelist is deactivated.");
                    return;
                }
                boolean isWhitelisted = game.getWhitelistNames().contains(target);
                if (!isWhitelisted && isAdd) game.getWhitelistNames().add(target);
                else if (isWhitelisted && !isAdd) game.getWhitelistNames().remove(target);
                String playerName = Bukkit.getOfflinePlayer(target).getName();
                String whitelistMsg = isWhitelisted ? "§cThe player §f'" + playerName + "' §cis already on the whitelist." : "§aThe player §f'" + playerName + "' §awas added to the whitelist correctly.";
                String unwhitelistMessage = !isWhitelisted ? "§cThe player §f'" + playerName + "' §cis not on the whitelist." : "§aThe player §f'" + playerName + "' §awas correctly removed from the whitelist.";
                commandSender.sendMessage(isAdd ? whitelistMsg : unwhitelistMessage);
                return;
            case "on":
            case "off":
                boolean isOff = arg.equals("off");
                if (!isOff && game.isWhitelist()) {
                    commandSender.sendMessage("§cWhitelist is already activated.");
                    return;
                } else if (isOff && !game.isWhitelist()) {
                    commandSender.sendMessage("§cWhitelist is already deactivated.");
                    return;
                }
                game.setWhitelist(!game.isWhitelist());
                commandSender.sendMessage(game.isWhitelist() ? "§aWhitelist is now enabled." : "§aWhitelist is now disabled.");
                return;
        }
        syntaxError(label + syntaxMessage, commandSender);
    }
}
