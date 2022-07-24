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
    private static ArrayList<AuctionItem> auctionItems = new ArrayList<>();

    public static void writeItems() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("items.json", "UTF-8");
        writer.print("");
        writer.println("[");
        for (int i = 0; i < auctionItems.size(); i++) {
            String bannedJson = "{\"name\":\"\",\"rarity\":\"null\",\"price\":\"0\",\"category\":\"Unknown\",\"type\":\"Unknown\",\"stats\":{}}";
            if(auctionItems.get(i).getRawJson().equals(bannedJson)){
                continue;
            }
            writer.println(auctionItems.get(i).getRawJson());
            if(i!= auctionItems.size()-1) {
                writer.println(",");
            }
        }
        writer.println("]");
        writer.close();
    }

    public static ArrayList<AuctionItem> getItems() {
        return auctionItems;
    }

    public static void clear(){
        auctionItems.clear();
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

    public static void add(AuctionItem i){
        auctionItems.add(i);
    }

    public static String updateLocal() {
        try {
            String wholeFile = new String(Files.readAllBytes(Paths.get("items.json")), StandardCharsets.UTF_8);
            JSONArray a = new JSONArray(wholeFile);
            for(int i = 0; i < a.length(); i++){
                ItemArray.add(new AuctionItem(a.getJSONObject(i)));
            }
            return "HashMap for auction items updated with "+ItemArray.auctionItems.size()+" items";
        } catch (IOException e) {
            return "No pre-existing items found";
        } catch (JSONException e) {
            e.printStackTrace();
            return "JSONException";
        }
    }

    public static ArrayList<AuctionItem> sortBy(String stat){
        ArrayList<AuctionItem> sorted = new ArrayList<>();
        for(AuctionItem i: auctionItems){

            if(i.getStat(stat) == null || i.getRarity().equals("null") || i.getRarity().equals("Crafted")
            || i.getName().contains("Unidentified")){
                continue;
            }else{
                sorted.add(i);
            }
        }
        try {
            Collections.sort(sorted, new Comparator<AuctionItem>() {
                @Override
                public int compare(AuctionItem o1, AuctionItem o2) {
                    double stat1 = 0;
                    double stat2 = 0;

                    try {
                        stat1 = o1.getStat(stat)[1];
                    }catch(NullPointerException e){

                    }
                    try {
                        stat2 = o2.getStat(stat)[1];
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
        return genJson(auctionItems);
    }
    public static String asJson(ArrayList<AuctionItem> tempAuctionItems){
        return genJson(tempAuctionItems);
    }

    private static String genJson(ArrayList<AuctionItem> tempAuctionItems) {
        String json = "[";
        for(int i = 0; i < tempAuctionItems.size(); i++){
            json += tempAuctionItems.get(i).getRawJson();
            if(i!= tempAuctionItems.size()-1){
                json += ",";
            }
        }
        json += "]";
        System.out.println("JSON Generated");
        return json;
    }


}
