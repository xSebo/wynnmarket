package com.sebo.wynnmarket.auctionobjects;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item {

    private boolean isUseful = true;
    private static HashMap<String, String> regexes =
            new HashMap<String, String>() {
                {
                    put("Health: ", "health"); //Applicable to armour, top health value
                    put("\\d+\\s+Strength", "strengthPoints");
                    put("\\d+\\s+Dexterity", "dexterityPoints");
                    put("\\d+\\s+Intelligence", "intelligencePoints");
                    put("\\d+\\s+Agility", "agilityPoints");
                    put("\\d+\\s+Defence", "defensePoints");
                    put("\\d+\\s+Main Attack Neutral Damage", "damageBonusRaw");
                    put("\\d%+\\s+Main Attack Damage", "damageBonus");
                    put("\\d+\\s+Spell Damage", "spellBonusRaw");
                    put("\\d%+\\s+Spell Damage", "spellBonus");
                    put("\\d+\\s+Health Regen", "healthRegen");
                    put("\\d%+\\s+Health Regen", "healthRegenRaw");
                    put("\\d+\\s+Health", "healthBonus");
                    put("\\/3s Poison", "poison");
                    put("\\/3s Life Steal", "lifeSteal");
                    put("\\/5s Mana Regen", "manaRegen");
                    put("\\/3s Mana Steal", "manaSteal");
                    put("\\d%+\\s+1st Spell Cost", "spellCostPct1");
                    put("\\d+\\s+1st Spell Cost", "spellCostRaw1");
                    put("\\d%+\\s+2nd Spell Cost", "spellCostPct2");
                    put("\\d+\\s+2nd Spell Cost", "spellCostRaw2");
                    put("\\d%+\\s+3rd Spell Cost", "spellCostPct3");
                    put("\\d+\\s+3rd Spell Cost", "spellCostRaw3");
                    put("\\d%+\\s+4th Spell Cost", "spellCostPct4");
                    put("\\d+\\s+4th Spell Cost", "spellCostRaw4");
                    put("\\d%+\\s+Thorns", "thorns");
                    put("\\d%+\\s+Reflection", "reflection");
                    put("\\d+\\s+tier Attack Speed", "attackSpeedBonus");
                    put("\\d%+\\s+Speed", "speed");
                    put("\\d%+\\s+Exploding", "exploding");
                    put("\\d%+\\s+Soul Point Regen", "soulPoints");
                    put("\\d%+\\s+Sprint", "sprint");
                    put("\\d%+\\s+Sprint Regen", "sprintRegen");
                    put("\\d+\\s+Jump Height", "jumpHeight");
                    put("\\d%+\\s+XP Bonus", "xpBonus");
                    put("\\d%+\\s+Loot Bonus", "lootBonus");
                    put("\\d%+\\s+Loot Quality", "lootQuality");
                    put("\\d%+\\s+Stealing", "emeraldStealing");
                    put("\\d%+\\s+Gather XP Bonus", "gatherXpBonus");
                    put("\\d%+\\s+Gather Speed", "gatherSpeed");
                    put("\\d%+\\s+Earth Damage", "bonusEarthDamage");
                    put("\\d%+\\s+Fire Damage", "bonusFireDamage");
                    put("\\d%+\\s+Water Damage", "bonusWaterDamage");
                    put("\\d%+\\s+Air Damage", "bonusAirDamage");
                    put("\\d%+\\s+Thunder Damage", "bonusThunderDamage");
                    put("\\d%+\\s+Earth Defence", "bonusEarthDefense");
                    put("\\d%+\\s+Fire Defence", "bonusFireDefense");
                    put("\\d%+\\s+Water Defence", "bonusWaterDefense");
                    put("\\d%+\\s+Air Defence", "bonusAirDefense");
                    put("\\d%+\\s+Thunder Defence", "bonusThunderDefense");
                }
            };

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public HashMap<String, Integer> getStats() {
        return stats;
    }

    private String name;
    private String rarity;

    private int price;

    public int getPrice() {
        return price;
    }

    private HashMap<String, Integer> stats = new HashMap<>();

    private void formatBonusPoints(String loreLine, String key, int bonusSkillPoints) {
        if (String.valueOf(loreLine.charAt(0)).equalsIgnoreCase("-")) {
            bonusSkillPoints = (bonusSkillPoints * (-1));
        }
        if (key.equalsIgnoreCase("poison") ||
                key.equalsIgnoreCase("lifeSteal") ||
                key.equalsIgnoreCase("manaregen") ||
                key.equalsIgnoreCase("manaSteal") ||
                key.equalsIgnoreCase("spellCostPct1") ||
                key.equalsIgnoreCase("spellCostPct2") ||
                key.equalsIgnoreCase("spellCostPct3") ||
                key.equalsIgnoreCase("spellCostPct4") ||
                key.equalsIgnoreCase("spellCostRaw1") ||
                key.equalsIgnoreCase("spellCostRaw2") ||
                key.equalsIgnoreCase("spellCostRaw3") ||
                key.equalsIgnoreCase("spellCostRaw4")
        ) {

            String tempString = String.valueOf(bonusSkillPoints);
            bonusSkillPoints = Integer.valueOf(tempString.substring(0, tempString.length() - 1));

        }
        stats.put(key, bonusSkillPoints);
        //System.out.println("FROM HASHMAP: "+key+ ": " + stats.get(key) + " FOR: " + name);
    }

    private void loreBuilder(ItemStack item) {
        NBTTagCompound nbt = item.getTagCompound();
        if (nbt != null) {
            NBTTagCompound disp = nbt.getCompoundTag("display");
            if (disp != null) {
                NBTTagList lore = disp.getTagList("Lore", 8);
                if (lore != null) {
                    // lore is now a list of NBTTagStrings
                    for (int i = 0; i < lore.tagCount(); i++) {
                        String loreLine = StringUtils.stripControlCodes(lore.getStringTagAt(i));
                        loreLine = loreLine.replaceAll("\\*", "");
                        switch (loreLine.toLowerCase()) {
                            case "set item":
                                rarity = "Set";
                                break;
                            case "unique item":
                                rarity = "Unique";
                                break;
                            case "rare item":
                                rarity = "Rare";
                                break;
                            case "legendary item":
                                rarity = "Legendary";
                                break;
                            case "fabled item":
                                rarity = "Fabled";
                                break;
                            case "mythic item":
                                rarity = "Mythic";
                                break;

                            case "crafting":
                                isUseful = false;
                                break;
                        }
                        if (loreLine.toLowerCase().contains("crafted by")) {
                            rarity = "Crafted";
                        }

                        if(loreLine.equalsIgnoreCase("Price:")) {
                            String finalPrice = "";
                            String tempLoreLine = StringUtils.stripControlCodes(lore.getStringTagAt(i + 1));
                            for (int j = 0; j < tempLoreLine.length(); j++) {
                                if (tempLoreLine.charAt(j) == '(') {
                                    break;
                                }
                                finalPrice += tempLoreLine.charAt(j);
                            }
                            price = Integer.parseInt(finalPrice.replaceAll("[^0-9]", ""));
                        }
                        int bonusPoints;
                        try {
                            bonusPoints = Integer.parseInt(loreLine.replaceAll("[^0-9]", ""));
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        String finalLoreLine = loreLine;
                        regexes.forEach((k, v) -> {
                            {
                                Pattern pattern = Pattern.compile(k);
                                Matcher matcher = pattern.matcher(finalLoreLine);
                                while (matcher.find()) {
                                    formatBonusPoints(finalLoreLine, v, bonusPoints);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public Item(ItemStack item) {
        name = StringUtils.stripControlCodes(item.getDisplayName());
        name = name.substring(0, name.length() - 1);
        loreBuilder(item);
        //System.out.println(this);
    }

    @Override
    public String toString() {
        final String[] finalString = {name + "\n"};
        stats.forEach((k, v) -> {
            finalString[0] += k + ": " + v + "\n";
        });
        return finalString[0];
    }
}
