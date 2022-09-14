package com.sebo.wynnmarketserver.objects;

import java.util.*;

public class InfoMaps {
    public static Map<String, String> statMap = new HashMap<>(){
        {
            put("Strength", "strengthPoints");
            put("Dexterity", "dexterityPoints");
            put("Intelligence", "intelligencePoints");
            put("Agility", "agilityPoints");
            put("Defence", "defensePoints");
            put("Attack Damage Raw", "damageBonusRaw");
            put("ttack Damage", "damageBonus");
            put("Spell Damage Raw", "spellBonusRaw");
            put("Spell Damage", "spellBonus");
            put("Poison", "poison");
            put("Life Steal", "lifeSteal");
            put("Mana Regen", "manaRegen");
            put("Mana Steal", "manaSteal");
            put("1st Spell Cost", "spellCostPct1");
            put("1st Spell Cost Raw", "spellCostRaw1");
            put("2nd Spell Cost", "spellCostPct2");
            put("2nd Spell Cost Raw", "spellCostRaw2");
            put("3rd Spell Cost", "spellCostPct3");
            put("3rd Spell Cost Raw", "spellCostRaw3");
            put("4th Spell Cost", "spellCostPct4");
            put("4th Spell Cost Raw", "spellCostRaw4");
            put("Thorns", "thorns");
            put("Reflection", "reflection");
            put("Attack Speed Bonus", "attackSpeedBonus");
            put("Walk Speed", "speed");
            put("Exploding", "exploding");
            put("Soul Point Regen", "soulPoints");
            put("Jump Height", "jumpHeight");
            put("XP Bonus", "xpBonus");
            put("Loot Bonus", "lootBonus");
            put("Loot Quality", "lootQuality");
            put("Stealing", "emeraldStealing");
            put("Gather XP Bonus", "gatherXpBonus");
            put("Gather Speed", "gatherSpeed");
            put("Earth Damage", "bonusEarthDamage");
            put("Fire Damage", "bonusFireDamage");
            put("Water Damage", "bonusWaterDamage");
            put("Air Damage", "bonusAirDamage");
            put("Thunder Damage", "bonusThunderDamage");
            put("Earth Defence", "bonusEarthDefense");
            put("Fire Defence", "bonusFireDefense");
            put("Water Defence", "bonusWaterDefense");
            put("Air Defence", "bonusAirDefense");
            put("Thunder Defence", "bonusThunderDefense");
            put("Sprint", "sprint");
            put("Sprint Regen", "sprintRegen");
            put("Health", "healthBonus");
            put("Health Regen", "healthRegen");
            put("Health Regen Raw", "healthRegenRaw");
        }

    };

    public static Map<String, List<String>> categoryMap = new HashMap<>(){
        {
            put("Any", Arrays.asList("Any","Dagger","Wand","Spear","Bow","Relik","Helmet","Chestplate","Leggings","Boots","Ring","Necklace","Bracelet"));
            put("Weapon", Arrays.asList("Dagger","Wand","Spear","Bow","Relik"));
            put("Armor", Arrays.asList("Helmet","Chestplate","Leggings","Boots"));
            put("Accessory", Arrays.asList("Ring","Necklace","Bracelet"));
        }
    };

    public static List<String> getOrderedCategories(){
        List categories = new ArrayList();
        categories.add("Any");
        categories.add("Weapon");
        categories.add("Armor");
        categories.add("Accessory");
        return categories;
    }
}
