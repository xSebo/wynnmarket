package com.sebo.wynnmarketserver.objects;

import com.google.gson.Gson;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AllItemArray {

    private static HashMap<String, String> regexes =
            new HashMap<String, String>() {
                {
                    put("Health: ", "health"); //Applicable to armour, top health value
                    put("Strength", "strengthPoints");
                    put("Dexterity", "dexterityPoints");
                    put("Intelligence", "intelligencePoints");
                    put("Agility", "agilityPoints");
                    put("Defense", "defensePoints");
                    put("Main Attack Damage", "mainAttackDamageBonus");
                    put(".+\\s+Main Attack Damage", "mainAttackDamageBonusRaw");
                    put(".+\\s+Spell Damage", "spellDamageBonusRaw");
                    put("Spell Damage", "spellDamageBonus");
                    put("Health Regen Raw", "healthRegenRaw");
                    put("Health Regen", "healthRegen");
                    put("Health", "healthBonus");
                    put("Poison", "poison");
                    put("Life Steal", "lifeSteal");
                    put("Mana Regen", "manaRegen");
                    put("Mana Steal", "manaSteal");
                    put("1st Spell Cost", "spellCostRaw1");
                    put("2nd Spell Cost", "spellCostRaw2");
                    put("3rd Spell Cost", "spellCostRaw3");
                    put("4th Spell Cost", "spellCostRaw4");
                    put("Thorns", "thorns");
                    put("Reflection", "reflection");
                    put("Attack Speed", "attackSpeedBonus");
                    put("Walk Speed", "speed");
                    put("Exploding", "exploding");
                    put("Soul Point Regen", "soulPoints");
                    put("Sprint", "sprint");
                    put("Sprint Regen", "sprintRegen");
                    put("Jump Height", "jumpHeight");
                    put("XP Bonus", "xpBonus");
                    put("Loot Bonus", "lootBonus");
                    put("Loot Quality", "lootQuality");
                    put("Stealing", "emeraldStealing");
                    put("Gather XP Bonus", "gatherXpBonus");
                    put("Gather Speed", "gatherSpeed");
                    put(".+\\s+Earth Damage", "earthDamageBonus");
                    put(".+\\s+Fire Damage", "fireDamageBonus");
                    put(".+\\s+Water Damage", "waterDamageBonus");
                    put(".+\\s+Air Damage", "airDamageBonus");
                    put(".+\\s+Thunder Damage", "thunderDamageBonus");
                    put(".+\\s+Earth Defense", "bonusEarthDefense");
                    put(".+\\s+Fire Defense", "bonusFireDefense");
                    put(".+\\s+Water Defense", "bonusWaterDefense");
                    put(".+\\s+Air Defense", "bonusAirDefense");
                    put(".+\\s+Thunder Defense", "bonusThunderDefense");
                }
            };
    public static HashMap<String, Item> allItems = new HashMap<>();

    public static void addItem(String name, Item item) {
        allItems.put(name, item);
    }

    public static String updateLocal() {
        String itemInfoJson = null;
        try {
            itemInfoJson = new String(Files.readAllBytes(Paths.get("allItems.json")));
        } catch (IOException e) {
            return "Error reading file";
        }
        Gson gson = new Gson();
        Item[] allItems = gson.fromJson(itemInfoJson, Item[].class);
        for (Item item : allItems) {
            AllItemArray.addItem(item.getName(), item);
        }
        return "HashMap Updated";
    }

    private static String genJsonToSave(JSONObject o, String nameType, String typeType) throws JSONException {
        String newJson = "{\"name\":\"";
        newJson += o.getString(nameType) +
                "\",\"category\":\"" + o.getString("category") + "\",\"type\":\"" + o.getString(typeType) + "\"}";
        return newJson;
    }

    //TODO -> Make this multithreaded
    public static String updateStored() {
        try {
            String dbUrl = "https://api.wynncraft.com/public_api.php?action=itemDB&category=all";

            HashSet<String> hashSet = new HashSet<>();
            try {
                URL url = new URL(dbUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JSONArray json = new JSONObject(response.toString()).getJSONArray("items");
                    for (int j = 0; j < json.length(); j++) {
                        String name = "";
                        String type = "";
                        String category = "";
                        String rarity = "";
                        HashMap<String, ArrayList<Double>> stats = new HashMap<>();
                        JSONObject o = json.getJSONObject(j);
                        String newJson = "{\"name\":\"\",\"category\":\"\",\"type\":\"\"}";
                        try {
                            name = o.getString("displayName");
                            type = o.getString("type");
                            category = o.getString("category");
                            rarity = o.getString("tier");
                        } catch (JSONException e) {
                            try {
                                if (e.getMessage().equalsIgnoreCase("JSONObject[\"displayName\"] not found.")) {
                                    name = o.getString("name");
                                    type = o.getString("type");
                                    category = o.getString("category");
                                    rarity = o.getString("tier");
                                }
                            } catch (JSONException e1) {
                                try {
                                    name = o.getString("displayName");
                                    type = o.getString("accessoryType");
                                    category = o.getString("category");
                                    rarity = o.getString("tier");
                                } catch (JSONException e2) {
                                    try {
                                        name = o.getString("name");
                                        type = o.getString("accessoryType");
                                        category = o.getString("category");
                                        rarity = o.getString("tier");

                                    } catch (JSONException ex) {
                                        System.out.println(o);
                                    }
                                }

                            }
                        }
                        //IMPLEMENT NEW STAT METHOD
                        Iterator<String> ids = o.keys();
                        while (ids.hasNext()) {
                            String key = ids.next();
                            for (Map.Entry<String, String> regexEntry : regexes.entrySet()) {
                                if (key.equals(regexEntry.getValue())) {
                                    if (o.getDouble(key) == 0) {
                                        continue;
                                    }
                                    boolean idd = false;
                                    Double statMin = 0.0;
                                    Double statMax = 0.0;
                                    try {
                                        if (o.getBoolean("identified")) {
                                            statMin = null;
                                            statMax = o.getDouble(key);
                                            idd = true;
                                        }
                                    } catch (JSONException e) {
                                        //
                                    }
                                    if (!idd) {
                                        if (o.getDouble(key) < 0) {
                                            statMax = Double.valueOf(Math.round(o.getDouble(key) * 0.7));
                                            statMin = Double.valueOf(Math.round(o.getDouble(key) * 1.3));
                                        } else if (o.getDouble(key) > 0) {
                                            statMin = Double.valueOf(Math.round(o.getDouble(key) * 0.3));
                                            statMax = Double.valueOf(Math.round(o.getDouble(key) * 1.3));
                                        }
                                    }
                                    ArrayList<Double> statRange = new ArrayList<>();
                                    statRange.add(statMin);
                                    statRange.add(statMax);
                                    stats.put(key, statRange);
                                }

                            }
                        }
                        if (!(name.equalsIgnoreCase("") ||
                                type.equalsIgnoreCase("") ||
                                category.equalsIgnoreCase(""))) {
                            AllItemArray.addItem(name, new Item(name, rarity, category, type, stats));
                        }
                    }

                } else {
                    System.out.println("Failed : HTTP error code : " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            FileWriter fileWriter = new FileWriter("allItems.json");
            gson.toJson(AllItemArray.allItems.values(), fileWriter);
            fileWriter.close();
            System.out.println("Done\n");
            return "DB Updated";
        } catch (Exception e) {
            e.printStackTrace();
            return "DB Update Failed";
        }
    }

    public static String updateAll() {
        String stored = updateStored();
        String local = updateLocal();
        if (local.equals("HashMap Updated") && stored.equals("DB Updated")) {
            return "HashMap and DB for all items updated with " + allItems.size() + " items";
        }
        if (local.equals("IOException")) {
            updateLocal();
            return "HashMap and DB for all items updated with " + allItems.size() + " items";
        }

        System.out.println(local);
        System.out.println(stored);
        return "Update failed";
    }

    public static String getMinMaxValues(String JSONToBuild) {
        try {
            String itemName = new JSONObject(JSONToBuild).getString("name");
            itemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8);
            URL url = new URL("https://www.wynndata.tk/i/" + itemName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String strResult = new BufferedReader(new InputStreamReader(con.getInputStream()))
                        .lines().collect(Collectors.joining("\n"));

                Document doc = Jsoup.parse(strResult);
                boolean singleStat = false;
                Elements statHtml;
                try {
                    statHtml = doc.select("[class=td3]").first().select("tbody").select("tr").select("td");
                } catch (Exception e) {
                    statHtml = doc.select("[class=td3]").select("tbody").select("tr").select("td");
                }
                if (statHtml.size() == 0) {
                    singleStat = true;
                    try {
                        statHtml = doc.select("[class=td2]").first().select("tbody").select("tr").select("td");
                    } catch (Exception e) {
                        statHtml = doc.select("[class=td2]").select("tbody").select("tr").select("td");
                    }
                }

                ArrayList<String> statNamesList = new ArrayList<>();
                ArrayList<String> statValuesList = new ArrayList<>();
                if (!singleStat) {
                    int miniCount = 0;
                    for (Element e : statHtml) {
                        if (miniCount == 1) {
                            statNamesList.add(e.text());
                            miniCount = -1;
                        } else {
                            statValuesList.add(e.text());
                            miniCount++;
                        }
                    }
                } else {
                    boolean isStat = false;
                    for (Element e : statHtml) {
                        if (isStat) {
                            statValuesList.add(e.text());
                            isStat = false;
                        } else {
                            statNamesList.add(e.text());
                            isStat = true;
                        }
                    }
                }
                int statLength = statNamesList.size();
                for (int i = 0; i < statLength; i++) {
                    int finalI = i;
                    final boolean[] isChanged = {false};
                    regexes.forEach((k, v) -> {
                        {
                            if (statNamesList.get(finalI).matches(k)) {
                                statNamesList.set(finalI, v);
                                isChanged[0] = true;
                                return;
                            }
                        }
                    });
                    if (!isChanged[0]) {
                        continue;
                    }
                }
                HashMap<String, Double[]> stats = new HashMap<>();

                int i = 0;
                while (i < statValuesList.size()) {
                    Double[] tempStats = new Double[2];
                    boolean hasPercentage = statValuesList.get(i).contains("%");
                    String statMin = (statValuesList.get(i).replaceAll("[^0-9\\-]", ""));
                    String statMax = "";
                    if (!singleStat) {
                        statMax = (statValuesList.get(i + 1).replaceAll("[^0-9\\-]", ""));
                    }
                    String statName;
                    if (singleStat) {
                        statName = statNamesList.get(i);
                    } else {
                        statName = statNamesList.get(i / 2);
                    }

                    if (statName.contains("spellCostRaw") && hasPercentage) {
                        char lastChar = statName.charAt(statName.length() - 1);
                        statName = "spellCostPct" + lastChar;
                    }
                    if (statName.equalsIgnoreCase("healthRegenRaw") && hasPercentage) {
                        statName = "healthRegen";
                    }
                    if (statName.equalsIgnoreCase("poison") ||
                            statName.equalsIgnoreCase("lifeSteal") ||
                            statName.equalsIgnoreCase("manaRegen") ||
                            statName.equalsIgnoreCase("manaSteal")) {

                        try {
                            tempStats[0] = Double.valueOf(statMin.substring(0, statMin.length() - 1));
                        } catch (Exception e) {
                            tempStats[0] = null;
                        }
                        if (!singleStat) {
                            try {
                                tempStats[1] = Double.valueOf(statMax.substring(0, statMax.length() - 1));
                            } catch (Exception e) {
                                tempStats[1] = null;
                            }
                        } else {
                            tempStats[1] = null;
                        }
                    } else {
                        try {
                            tempStats[0] = Double.valueOf(statMin);
                        } catch (NumberFormatException e) {
                            tempStats[0] = null;
                        }
                        if (!singleStat) {
                            try {
                                tempStats[1] = Double.valueOf(statMax);
                            } catch (NumberFormatException e) {
                                tempStats[1] = null;
                            }
                        } else {
                            tempStats[1] = null;
                        }
                    }
                    if (singleStat) {
                        Double swap = tempStats[0];
                        tempStats[0] = null;
                        tempStats[1] = swap;
                    }
                    stats.put(statName, tempStats.clone());
                    if (singleStat) {
                        i++;
                    } else {
                        i = i + 2;
                    }
                }
                String json = JSONToBuild;
                String name = new JSONObject(json).getString("name");
                json = json.substring(0, json.length() - 1);
                json += ",\"stats\":";
                json += new JSONObject(stats) + "}";
                return json;

            } else {
                System.out.println("Failed: " + responseCode);
                return "Failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }
}
