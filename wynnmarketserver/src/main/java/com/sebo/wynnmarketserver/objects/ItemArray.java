package com.sebo.wynnmarketserver.objects;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ItemArray {
    private static ArrayList<Item> items = new ArrayList<>();

    public static void writeItems() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("items.json", "UTF-8");
        writer.print("");
        writer.println("[");
        for (int i= 0; i < items.size(); i++) {
            String bannedJson = "{\"name\":\"\",\"rarity\":\"null\",\"price\":\"0\",\"category\":\"Unknown\",\"type\":\"Unknown\",\"stats\":{}}";
            if(items.get(i).getRawJson().equals(bannedJson)){
                continue;
            }
            writer.println(items.get(i).getRawJson());
            if(i!=items.size()-1) {
                writer.println(",");
            }
        }
        writer.println("]");
        writer.close();
    }

    public static ArrayList<Item> getItems() {
        return items;
    }

    public static void clear(){
        items.clear();
        try {
            PrintWriter writer = new PrintWriter("items.json", "UTF-8");
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void add(Item i){
        items.add(i);
    }

    public static String updateLocal() {
        try {
            String wholeFile = new String(Files.readAllBytes(Paths.get("items.json")), StandardCharsets.UTF_8);
            JSONArray a = new JSONArray(wholeFile);
            for(int i = 0; i < a.length(); i++){
                ItemArray.add(new Item(a.getJSONObject(i)));
            }
            return "HashMap for auction items updated with "+ItemArray.items.size()+" items";
        } catch (IOException e) {
            return "No pre-existing items found";
        } catch (JSONException e) {
            e.printStackTrace();
            return "JSONException";
        }
    }

    public static ArrayList<Item> sortBy(String stat){
        ArrayList<Item> sorted = new ArrayList<>();
        for(Item i:items){

            if(i.getStats().get(stat) == null || i.getRarity().equals("null") || i.getRarity().equals("Crafted")
            || i.getName().contains("Unidentified")){
                continue;
            }else{
                sorted.add(i);
            }
        }
        try {
            Collections.sort(sorted, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    double stat1 = 0;
                    double stat2 = 0;

                    try {
                        stat1 = o1.getStats().get(stat);
                    }catch(NullPointerException e){

                    }
                    try {
                        stat2 = o2.getStats().get(stat);
                    }catch (NullPointerException e){

                    }
                    if (stat.equalsIgnoreCase("spellCostPct1") ||
                            stat.equalsIgnoreCase("spellCostPct2") ||
                            stat.equalsIgnoreCase("spellCostPct3") ||
                            stat.equalsIgnoreCase("spellCostPct4") ||
                            stat.equalsIgnoreCase("spellCostRaw1") ||
                            stat.equalsIgnoreCase("spellCostRaw2") ||
                            stat.equalsIgnoreCase("spellCostRaw3") ||
                            stat.equalsIgnoreCase("spellCostRaw4")) {
                        return Double.compare(stat1, stat2);
                    }
                    return Double.compare(stat2, stat1);
                }
            });
            System.out.println("Sorted " +sorted.size() + " items by " + stat);
        }catch(Exception e){
            System.out.println("Error sorting by "+stat);
            e.printStackTrace();
        }
        return sorted;
    }

    public static String asJson(){
        return genJson(items);
    }
    public static String asJson(ArrayList<Item> tempItems){
        return genJson(tempItems);
    }

    private static String genJson(ArrayList<Item> tempItems) {
        String json = "[";
        for(int i = 0; i < tempItems.size(); i++){
            json += tempItems.get(i).getRawJson();
            if(i!=tempItems.size()-1){
                json += ",";
            }
        }
        json += "]";
        System.out.println("JSON Generated");
        return json;
    }


}
