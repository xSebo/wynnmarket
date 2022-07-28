package com.sebo.wynnmarketserver.objects;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AuctionItem extends Item{

    private static final DecimalFormat df = new DecimalFormat("0.00");


    public String getRarity() {
        return rarity;
    }

    public int getPrice() {
        return price;
    }

    private double generateAveragePercentage(){
        final double[] sum = {0};
        stats.forEach((k,v) -> {
            sum[0] += generateStatPercentage(k);
        });
        return Double.valueOf(df.format(sum[0]/stats.size()));
    }

    public double getAvgStatPct() {
        return avgStatPct;
    }

    private double avgStatPct;

    //TODO -> Fix this, some percentages are >100%, and some are negative.
    private double generateStatPercentage(String stat){
        try{
            double statVal = this.stats.get(stat).get(1);
            ArrayList<Double> statBounds = AllItemArray.allItems.get(this.getName()).getStats().get(stat);

            double percentage = ((statVal-statBounds.get(0)) / (statBounds.get(1)-statBounds.get(0)))*100;
            return Double.valueOf(df.format(percentage));
        }
        catch(NullPointerException e){
            return 0;
        }
    }

    private String rarity;


    private int price;

    public String genRawJson() {
        String rawJson = "{\"name\":\""+name+
                "\",\"rarity\":\""+rarity+
                "\",\"price\":\""+price+
                "\",\"category\":\""+category+
                "\",\"type\":\""+type+
                "\",\"stats\":" +
                new Gson().toJson(stats) + "}"; // {"statName":value}
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
        avgStatPct = generateAveragePercentage();
    }

}
