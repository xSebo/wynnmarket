package com.sebo.wynnmarketserver.objects;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AuctionItem extends Item{

    public String getRarity() {
        return rarity;
    }

    public int getPrice() {
        return price;
    }

    private String rarity;

    public HashMap<String, Double> getStats() {
        return stats;
    }

    private HashMap<String, Double> stats;
    private String rawJson;

    private int price;

    public String getRawJson() {
        return rawJson;
    }
    public AuctionItem(JSONObject o) throws JSONException {
        try{
            name = o.get("displayName").toString();
        }catch(JSONException e){
            name = o.get("name").toString();
        }
        rarity = (String) o.get("rarity");
        price = Integer.valueOf(o.get("price").toString());
        try {
            category = (String) AllItemArray.allItems.get(name)[0];
            type = (String) AllItemArray.allItems.get(name)[1];
        }catch (Exception e){
            category = "Unknown";
            type = "Unknown";
        }
        stats = new Gson().fromJson(o.getString("stats"), HashMap.class);

        rawJson = "{\"name\":\""+name+
                "\",\"rarity\":\""+rarity+
                "\",\"price\":\""+price+
                "\",\"category\":\""+category+
                "\",\"type\":\""+type+
                "\",\"stats\":" +
                new Gson().toJson(stats) + "}"; // {"statName":value}
    }
    public AuctionItem(JSONObject o, boolean dbItem){

    }

    public double getStat(String stat){
        if(this.stats.get(stat) == null){
            return 0;
        }
        else{
            return this.stats.get(stat);
        }
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
