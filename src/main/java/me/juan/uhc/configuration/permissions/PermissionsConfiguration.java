package me.juan.uhc.configuration.permissions;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public enum PermissionsConfiguration {

    GENERAL_MOD(""),
    ADMIN("uhc.admin"),
    HOST("uhc.host"),
    MOD("uhc.mod"),
    MOD_BUILD("uhc.mod.build"),
    TRIAL_MOD("uhc.trial.mod"),
    VIP("uhc.vip"), //SPECTATE, WHITELIST AND COMMAND.
    SPECTATE("uhc.spectate"),
    WHITELIST_BYPASS("uhc.whitelist.bypass"),
    WHITELIST_COMMAND("uhc.whitelist.command");

    public static HashMap<String, List<PermissionsConfiguration>> hierarchy;
    private final String permission;

    PermissionsConfiguration(String permission) {
        this.permission = permission;
    }

    public String get() {
        return permission;
    }

    private void initHierarchy() {
        hierarchy = new HashMap<>();
        hierarchy.put(WHITELIST_BYPASS.permission, new ArrayList<>(Collections.singletonList(VIP)));
        hierarchy.put(WHITELIST_COMMAND.permission, new ArrayList<>(Collections.singletonList(VIP)));
        hierarchy.put(SPECTATE.permission, new ArrayList<>(Collections.singletonList(VIP)));
    }

    private boolean isMod(Player player) {
        return TRIAL_MOD.contains(player) || MOD.contains(player) || HOST.contains(player) || ADMIN.contains(player);
    }

    public boolean contains(Player player) {
        if (hierarchy == null) initHierarchy();
        if (permission.equals(GENERAL_MOD.permission)) return isMod(player);
        else if (permission.equals(MOD_BUILD.permission) && !TRIAL_MOD.contains(player)) return isMod(player);
        else if (hierarchy.containsKey(permission))
            return player.hasPermission(this.permission) || isMod(player) || hierarchy.get(permission).stream().anyMatch(permissions -> player.hasPermission(permissions.permission));
        return player.hasPermission(this.permission);
    }

}
