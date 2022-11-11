package com.sebo.wynnmarketserver.objects;

import com.google.gson.Gson;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuctionItem extends Item{

    public String getUrl(){
        return url;
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public double getAvgStatPct() {
        return avgStatPct;
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

    private transient HashMap<String,Double> statPercentages;

    public HashMap<String, Double> getStatPercentages() {
        return statPercentages;
    }


    private transient double avgStatPct;

    public String getFormattedPrice() {
        return formattedPrice;
    }

    private transient String formattedPrice = "";

    private transient static Map<String,String> rarityColour = Map.of(
            "Mythic", "#AA00AA",
            "Fabled", "#FF5555",
            "Legendary", "#55FFFF",
            "Rare", "#FF55FF",
            "Unique", "#FFFF55",
            "Normal","#FFFFFF",
            "Set","#55FF55"
            );
    ;

    private void setFormattedPrice(){
        int emeraldCost = price;
        int stx = emeraldCost / 262144;
        int le = (emeraldCost % 262144) / 4096;
        int eb = (emeraldCost % 4096) / 64;
        int e = emeraldCost % 64;

        if (stx !=0) formattedPrice += stx + "STX ";
        if (le != 0) formattedPrice += le+"LE ";
        if (eb != 0) formattedPrice += eb+"EB ";
        if (e != 0) formattedPrice += e+"E ";
    }

    public String getColour() {
        return colour;
    }

    private String colour;

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
                //Wynndata info wrong
                if (name.equalsIgnoreCase("Broken Balance")) {
                    statBounds.set(1, Double.valueOf(124));
                }
                if(statVal == statBounds.get(0) && statVal == statBounds.get(1)){
                    percentage = 100;
                }else {
                    percentage = ((statVal - statBounds.get(0)) / (statBounds.get(1) - statBounds.get(0))) * 100;
                }
                percentage = Double.valueOf(df.format(percentage));
                statPercentages.put(stat,percentage);
                return percentage;
            }
        }
        catch(NullPointerException e){
            return 0;
        }
    }

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
        super(o.getString("name"),"","","",new HashMap<>());
        statPercentages = new HashMap<>();

        price = Integer.valueOf(o.get("price").toString());
        try {
            category = AllItemArray.allItems.get(name).getCategory();
            type = AllItemArray.allItems.get(name).getType();
            rarity = AllItemArray.allItems.get(name).getRarity();
        }catch (Exception e){
            category = "Unknown";
            type = "Unknown";
            rarity = "Unknown";
        }
        stats = new Gson().fromJson(o.getString("stats"), HashMap.class);
        try{
            stats.get("health");
            stats.remove("health");
        }catch (Exception e){
            //Do nothing
        }
        avgStatPct = generateAveragePercentage();
        colour = rarityColour.get(rarity);
        setFormattedPrice();
        /*
        statPercentages.forEach((k,v) -> {
            System.out.println(k + ": " + v);
        });

         */
    }

}
