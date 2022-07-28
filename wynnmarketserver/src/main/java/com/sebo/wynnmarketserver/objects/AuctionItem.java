package com.sebo.wynnmarketserver.objects;

import com.google.gson.Gson;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AuctionItem extends Item{

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public double getAvgStatPct() {
        return avgStatPct;
    }

    public String getRarity() {
        return rarity;
    }

    public int getPrice() {
        return price;
    }

    private double generateAveragePercentage(){
        final double[] sum = {0};
        stats.forEach((k,v) -> {
            sum[0] += getStatPct(k);
        });
        return Double.valueOf(df.format(sum[0]/stats.size()));
    }

    private HashMap<String,Double> statPercentages;

    public HashMap<String, Double> getStatPercentages() {
        return statPercentages;
    }


    private double avgStatPct;

    //TODO -> Fix this, some percentages are >100%, and some are negative.
    public double getStatPct(String stat){
        try{
            try{
                return statPercentages.get(stat);
            }catch(NullPointerException e) {
                double statVal = this.stats.get(stat).get(1);
                ArrayList<Double> statBounds = AllItemArray.allItems.get(this.getName()).getStats().get(stat);
                double percentage;
                if(statBounds.get(0) == null){
                    percentage = Double.valueOf("100.0");
                    statPercentages.put(stat,percentage);
                    return percentage;
                }

                percentage = ((statVal - statBounds.get(0)) / (statBounds.get(1) - statBounds.get(0))) * 100;
                percentage = Double.valueOf(df.format(percentage));
                statPercentages.put(stat,percentage);
                return percentage;
            }
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
        statPercentages = new HashMap<>();
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
        try{
            stats.get("health");
            stats.remove("health");
        }catch (Exception e){
            //Do nothing
        }
        avgStatPct = generateAveragePercentage();
        /*
        statPercentages.forEach((k,v) -> {
            System.out.println(k + ": " + v);
        });

         */
    }

}
