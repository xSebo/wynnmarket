package com.sebo.wynnmarketserver.objects;

import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public class Item {

    public String getUrl() {
        return url;
    }

    protected String url;
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    protected String name;

    protected String category;

    protected String type;

    public String getRarity() {
        return rarity;
    }

    protected String rarity;

    protected HashMap<String,ArrayList<Double>> stats;

    public ArrayList<Double> getStat(String stat){
        try{
            this.stats.get(stat).get(0);
            return this.stats.get(stat);
        }
        catch(NullPointerException e){
            return new ArrayList<>(Arrays.asList(null,null));
        }
    }

    public HashMap<String, ArrayList<Double>> getStats() {
        return stats;
    }

    public Item(String name, String rarity, String category, String type, HashMap<String, ArrayList<Double>> stats) {
        this.name = name;
        this.rarity = rarity;
        this.category = category;
        this.type = type;
        this.stats = stats;

        this.url = "https://hppeng-wynn.github.io/item/#" + name;
    }

    public void setType(String type) {
    }
}
