package com.sebo.wynnmarketserver.objects;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
    }

    public static void add(Item i){
        items.add(i);
    }



}
