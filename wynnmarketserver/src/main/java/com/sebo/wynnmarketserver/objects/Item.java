package com.sebo.wynnmarketserver.objects;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Item {
    private String name;

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    private String rarity;

    private String category;
    private String type;

    public HashMap<String, Double> getStats() {
        return stats;
    }

    private HashMap<String, Double> stats;
    private String rawJson;

    private int price;

    public String getRawJson() {
        return rawJson;
    }
    public Item(JSONObject o) throws JSONException {
        name = (String) o.get("name");
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
