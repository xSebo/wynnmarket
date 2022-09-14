package com.sebo.wynnmarketserver.objects;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public class Item {
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

    public Item(String name, String category, String type, HashMap<String, ArrayList<Double>> stats) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.stats = stats;
    }
}
