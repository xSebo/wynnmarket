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
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
                    put("Defence", "defensePoints");
                    put("Main Attack Damage", "damageBonusRaw");
                    put("✤ Main Attack Damage", "damageBonus");
                    put("✤ Spell Damage", "spellBonusRaw");
                    put("Spell Damage", "spellBonus");
                    /*
                    put("Health Regen", "healthRegenRaw");
                    put("Health Regen", "healthRegen");
                     */
                    put("Health", "healthBonus");
                    put("Poison", "poison");
                    put("Life Steal", "lifeSteal");
                    put("Mana Regen", "manaRegen");
                    put("Mana Steal", "manaSteal");
                    /*
                    put("1st Spell Cost", "spellCostPct1");
                    put("1st Spell Cost", "spellCostRaw1");
                    put("2nd Spell Cost", "spellCostPct2");
                    put("2nd Spell Cost", "spellCostRaw2");
                    put("3rd Spell Cost", "spellCostPct3");
                    put("3rd Spell Cost", "spellCostRaw3");
                    put("4th Spell Cost", "spellCostPct4");
                    put("4th Spell Cost", "spellCostRaw4");
                     */
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
                }
            };
    public static HashMap<String, String[]> allItems = new HashMap<>();

    public static void addItem(String name, String[] categoryType) {
        allItems.put(name, categoryType);
    }

    public static String updateLocal(){
        String carInfoJson= null;
        try {
            carInfoJson = new String(Files.readAllBytes(Paths.get("allItems.json")));
        } catch (IOException e) {
            return "Error reading file";
        }
        Gson gson = new Gson();
        Item[] allItems = gson.fromJson(carInfoJson, Item[].class);
        for (Item item : allItems) {
            String[] categoryType = {item.getCategory(),item.getType()};
            AllItemArray.addItem(item.getName(), categoryType);
        }
        return "HashMap Updated";
    }
    private static String genJsonToSave(JSONObject o,String nameType, String typeType) throws JSONException {
        String newJson = "{\"name\":\"";
        newJson+=o.getString(nameType) +
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
                        System.out.println("Starting DB update");
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
                    case 26:
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
                            newJson = genJsonToSave(o,"displayName","type");
                        } catch (JSONException e) {
                            try {
                                if (e.getMessage().equalsIgnoreCase("JSONObject[\"displayName\"] not found.")) {
                                    newJson = genJsonToSave(o,"name","type");
                                }
                            } catch (JSONException e1) {
                                try {
                                    newJson = genJsonToSave(o,"displayName","accessoryType");
                                    } catch (JSONException e2) {
                                    try {
                                        newJson = genJsonToSave(o,"name","accessoryType");

                                    } catch (JSONException ex) {
                                        System.out.println(o);
                                    }
                                }

                            }
                            //System.out.println(newJson);

                        }
                        hashSet.add(newJson);
                    }

                    } else{
                        System.out.println("Failed : HTTP error code : " + responseCode);
                    }
                }
                String[] jOA = hashSet.toArray(new String[hashSet.size()]);
                for (int i = 0; i < hashSet.size(); i++) {
                    writer.println(jOA[i].toString());
                    if (i != hashSet.size() - 1) {
                        writer.println(",");
                    }
                }
                writer.println("]");
                writer.close();
                System.out.println("Done\n");
                return "DB Updated";
            }catch(Exception e){
                e.printStackTrace();
                return "DB Update Failed";
            }
        }
        public static String updateAll () {
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


        public static String getMinValues (String itemName){
            try {
                String postDataRaw = "search=&name=" + itemName + "&category=all&tier=any&profession=any&min=1&max=130&order1=level&order2=null&order3=null&order4=null";
                byte[] postDataEncoded = postDataRaw.getBytes("UTF-8");
                int dataLength = postDataEncoded.length;

                URL url = new URL("https://www.wynndata.tk/items/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setDoOutput(true);
                con.setRequestProperty("charset", "utf-8");
                con.setRequestProperty("Content-Length", String.valueOf(dataLength));
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postDataEncoded);
                }

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Success");
                    String strResult = new BufferedReader(new InputStreamReader(con.getInputStream()))
                            .lines().collect(Collectors.joining("\n"));
                    Document doc = Jsoup.parse(strResult);
                    Elements td3 = doc.select("[class=td3]").select("tbody").select("tr").select("td");//.select("[class=positive], [class=negative]");
                    return td3.text();
                } else {
                    System.out.println("Failed: " + responseCode);
                    return "Failed: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }
    }
