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
}
