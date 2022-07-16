package com.sebo.wynnmarketserver.objects;

import java.util.HashMap;

public class AllItemArray {
    public static HashMap<String, String[]> allItems = new HashMap<>();

    public static void addItem(String name, String[] categoryType) {
        allItems.put(name, categoryType);
    }
}
