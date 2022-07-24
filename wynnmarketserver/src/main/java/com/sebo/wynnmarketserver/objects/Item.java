package com.sebo.wynnmarketserver.objects;

import java.util.HashMap;

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

    protected HashMap<String,Double[]> stats;

    public Double[] getStat(String stat){
        if(this.stats.get(stat) == null){
            return new Double[]{null, null};
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

    public HashMap<String, Double[]> getStats() {
        return stats;
    }
}
