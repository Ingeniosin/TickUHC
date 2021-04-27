package me.juan.uhc.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class EnchantUtil {
    public static ItemStack enchant(ItemStack item) {
        //This seems messy but just contains the enchantments for each item type
        Material material = item.getType();

        if (EnchantmentTarget.TOOL.includes(material)) {
            return forceEnchants(item, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.LOOT_BONUS_BLOCKS});
        }

        if (EnchantmentTarget.WEAPON.includes(material)) {
            return forceEnchants(item, new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ARTHROPODS, Enchantment.DAMAGE_UNDEAD, Enchantment.DURABILITY, Enchantment.LOOT_BONUS_MOBS, Enchantment.FIRE_ASPECT});
        }

        if (EnchantmentTarget.ARMOR.includes(material)) {
            return forceEnchants(item, new Enchantment[]{Enchantment.DURABILITY, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE});
        }

        if (EnchantmentTarget.ARMOR_FEET.includes(material)) {
            return forceEnchants(item, new Enchantment[]{Enchantment.DURABILITY, Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE});
        }

        if (EnchantmentTarget.BOW.includes(material)) {
            return forceEnchants(item, new Enchantment[]{Enchantment.DURABILITY, Enchantment.ARROW_DAMAGE, Enchantment.ARROW_FIRE, Enchantment.ARROW_KNOCKBACK, Enchantment.ARROW_INFINITE});
        }

        if (EnchantmentTarget.FISHING_ROD.includes(material)) {
            return forceEnchants(item, new Enchantment[]{Enchantment.LUCK, Enchantment.LURE, Enchantment.DURABILITY});
        }

        return item;
    }

    public static ItemStack forceEnchants(ItemStack item, Enchantment[] enchantments) {
        Random random = new Random();

        ItemMeta meta = item.getItemMeta();

        //Max 3 enchants per item, Min 1
        int count = random.nextInt(3) + 1;

        for (int i = 0; i < count; i++) {
            //Find an enchantment that isn't already on the item
            Enchantment enchantment = null;
            while (enchantment == null || meta.hasEnchant(enchantment)) {
                enchantment = enchantments[random.nextInt(enchantments.length)];
            }

            //Get a valid random level and add the enchant
            int level = random.nextInt(enchantment.getMaxLevel()) + 1;
            meta.addEnchant(enchantment, level, true);
        }

        item.setItemMeta(meta);
        return item;
    }
}
