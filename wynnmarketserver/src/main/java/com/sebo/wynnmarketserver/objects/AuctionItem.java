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

    private String rawJson;

    private int price;

    public String getRawJson() {
        return rawJson;
    }
    public AuctionItem(JSONObject o) throws JSONException {
        name = o.get("name").toString();
        rarity = (String) o.get("rarity");
        price = Integer.valueOf(o.get("price").toString());
        try {
            category = AllItemArray.allItems.get(name).getCategory();
            type = AllItemArray.allItems.get(name).getType();
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

    @Override
    public String toString() {
        return super.toString() + "\n" + price + " emeralds";
    }


}
