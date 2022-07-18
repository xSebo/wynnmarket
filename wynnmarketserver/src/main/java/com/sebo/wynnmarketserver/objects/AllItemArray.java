package com.sebo.wynnmarketserver.objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

public class AllItemArray {
    public static HashMap<String, String[]> allItems = new HashMap<>();

    public static void addItem(String name, String[] categoryType) {
        allItems.put(name, categoryType);
    }

    public static String updateLocal() {
        JSONParser parser = new JSONParser();
        try {
            org.json.simple.JSONArray a = (org.json.simple.JSONArray) parser.parse(new FileReader("allItems.json"));
            for (Object o : a) {
                String[] categoryType = {(String) ((org.json.simple.JSONObject) o).get("category"), (String) ((org.json.simple.JSONObject) o).get("type")};
                AllItemArray.addItem((String) ((org.json.simple.JSONObject) o).get("name"), categoryType);
            }
            return "HashMap Updated";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }
    }
    //TODO -> Make this multithreaded
    public static String updateStored(){
        try {
            String dbUrl = "https://api.wynncraft.com/public_api.php?action=itemDB&search=";
//        String alphabet = "aeiouy";
            String alphabet = "abcdefghijklmnopqrstuvwxyz";

            PrintWriter writer = new PrintWriter("allItems.json", "UTF-8");
            writer.print("");
            writer.println("[");
            HashSet<String> hashSet = new HashSet<>();
            for (int i = 0; i < alphabet.length(); i++) {
                switch(i){
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
                        hashSet.add(json.getJSONObject(j).toString());
                        if (i == alphabet.length() - 1 && j == json.length() - 1) {
                        } else {
                        }
                    }

                } else {
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
            return "DB Update Failed";
        }
    }
    public static String updateAll(){
        String local = updateLocal();
        String stored = updateStored();
        if(local.equals("HashMap Updated") && stored.equals("DB Updated")){
            return "HashMap and DB updated with "+allItems.size()+" items";
        }
        if(local.equals("IOException")){
            updateLocal();
            return "HashMap and DB updated with "+allItems.size()+" items";
        }
        return "Update failed";
    }
}
