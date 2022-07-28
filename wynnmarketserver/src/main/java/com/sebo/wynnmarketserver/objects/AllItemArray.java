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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
                    put("Main Attack Damage", "damageBonus");
                    put(".+\\s+Main Attack Damage", "damageBonusRaw");
                    put(".+\\s+Spell Damage", "spellBonusRaw");
                    put("Spell Damage", "spellBonus");
                    put("Health Regen", "healthRegenRaw");
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
                    put(".+\\s+Earth Damage", "bonusEarthDamage");
                    put(".+\\s+Fire Damage", "bonusFireDamage");
                    put(".+\\s+Water Damage", "bonusWaterDamage");
                    put(".+\\s+Air Damage", "bonusAirDamage");
                    put(".+\\s+Thunder Damage", "bonusThunderDamage");
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
            String dbUrl = "https://api.wynncraft.com/public_api.php?action=itemDB&search=";
            String alphabet = "abcdefghijklmnopqrstuvwxyz";

            PrintWriter writer = new PrintWriter("allItems.json", "UTF-8");
            writer.print("");
            writer.println("[");
            HashSet<String> hashSet = new HashSet<>();
            for (int i = 0; i < alphabet.length(); i++) {
                switch (i) {
                    case 0:
                        System.out.println("Starting DB update stage 1");
                        break;
                    case 7:
                        System.out.println("25% complete");
                        break;
                    case 13:
                        System.out.println("50% complete");
                        break;
                    case 20:
                        System.out.println("75% complete");
                        break;
                    case 25:
                        System.out.println("100% complete");
                        break;
                }
                String urlStr = dbUrl + alphabet.charAt(i);
                URL url = new URL(urlStr);
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
                        JSONObject o = json.getJSONObject(j);
                        String newJson = "{\"name\":\"\",\"category\":\"\",\"type\":\"\"}";
                        try {
                            newJson = genJsonToSave(o, "displayName", "type");
                        } catch (JSONException e) {
                            try {
                                if (e.getMessage().equalsIgnoreCase("JSONObject[\"displayName\"] not found.")) {
                                    newJson = genJsonToSave(o, "name", "type");
                                }
                            } catch (JSONException e1) {
                                try {
                                    newJson = genJsonToSave(o, "displayName", "accessoryType");
                                } catch (JSONException e2) {
                                    try {
                                        newJson = genJsonToSave(o, "name", "accessoryType");

                                    } catch (JSONException ex) {
                                        System.out.println(o);
                                    }
                                }

                            }
                        }
                        hashSet.add(newJson);
                    }

                } else {
                    System.out.println("Failed : HTTP error code : " + responseCode);
                }
            }

            String[] hashSetArray = hashSet.toArray(new String[hashSet.size()]);
            ArrayList<String> stats = new ArrayList();
            int threads = 200;
            Executor executor = Executors.newFixedThreadPool(threads);
            CompletionService<String> completionService =
                    new ExecutorCompletionService<>(executor);

            int totalSize = hashSet.size();
            for (int i = 0; i < totalSize; ) {
                System.out.println(i + "/" + totalSize);


                for (int j = 0; j < threads; j++, i++) {
                    int finalI = i;
                    completionService.submit(() -> getMinMaxValues(hashSetArray[finalI]));
                }
                int received = 0;
                boolean errors = false;
                while (received < threads && !errors) {
                    Future<String> resultFuture = completionService.take();
                    try {
                        String result = resultFuture.get();
                        received++;
                        stats.add(result);
                    } catch (Exception e) {
                        break;
                    }
                }
            }

            for (int i = 0; i < stats.size(); i++) {
                writer.println(stats.get(i));
                if (i != stats.size() - 1) {
                    writer.println(",");
                }
            }
            writer.println("]");
            writer.close();
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
                }catch (Exception e){
                    statHtml = doc.select("[class=td3]").select("tbody").select("tr").select("td");
                }
                if(statHtml.size() == 0) {
                    singleStat = true;
                    try {
                        statHtml = doc.select("[class=td2]").first().select("tbody").select("tr").select("td");
                    }catch (Exception e){
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
                while(i<statValuesList.size()) {
                    Double[] tempStats = new Double[2];
                    boolean hasPercentage = statValuesList.get(i).contains("%");
                    String statMin = (statValuesList.get(i).replaceAll("[^0-9\\-]", ""));
                    String statMax = "";
                    if (!singleStat) {
                        statMax = (statValuesList.get(i + 1).replaceAll("[^0-9\\-]", ""));
                    }
                    String statName;
                    if(singleStat) {
                        statName = statNamesList.get(i);
                    }
                    else {
                        statName = statNamesList.get(i/2);
                    }

                    if (statName.contains("spellCostRaw") && hasPercentage) {
                        char lastChar = statMin.charAt(statMin.length() - 1);
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
                        }else{
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
                        }else{
                            tempStats[1] = null;
                        }
                    }
                    if(singleStat){
                        Double swap = tempStats[0];
                        tempStats[0] = null;
                        tempStats[1] = swap;
                    }
                    stats.put(statName, tempStats.clone());
                    if(singleStat){
                        i++;
                    }else{
                        i = i+2;
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
